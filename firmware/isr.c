#include <ctl_api.h>
#include <targets/ADuC7026.h>
#include <cross_studio_io.h>
#include "ringbuffer.h"
#include "isr.h"
#include "uart.h"
#include "random.h"

// Gloabal variables
extern int sendStatus;
extern int alertStatus;

// In Buffer
char  inBuffer[inBuffSize];
int   inBuffPos = 0;
ringbuffer * inBufferRing;

// Out Buffer
char outBuffer[outBuffSize];
int  outBuffPos = 0;
ringbuffer * outBufferRing;

// Send Buffer
char sendBuffer[sendBuffSize];
int  sendBuffPos = 0;
int  sendBuffEnd = 0;

// ISRs
void timerISR(){
  static int counter = 25;

  if(counter-- <= 0){
    counter = 25;

    // Schedule sending of command status
    sendStatus = 1;
  }

  // Clear interrupt
  T0CLRI = 0;
}

void buttonISR(){
  // Mask interrupt
  ctl_mask_isr(IRQ0_INT);

  // Toggle alert status
  alertStatus ^= 1; 
  sendStatus  = 1;

  // Use LED to display alert Status
  GP4DAT = (alertStatus << 26);

  // immediatly signal change in alert status
  sendStatus  = 1;

  // Unmask interrupt
  ctl_unmask_isr(IRQ0_INT);
}

void uartISR(){
  if(COMSTA0 & COMSTA0_DR_MASK){
    // Read a character from COMRX
    char ch = COMRX;

    // Put in inBuffer
    if(inBuffPos < inBuffSize - 1){
      inBuffer[inBuffPos++] = ch;
      inBuffer[inBuffPos]   = '\0';

      // Copy to ringbuffer on EOL
      if(ch == '\n' || ch == '\r'){
        ringbufferPutLine(inBufferRing, inBuffer);

        // reset inBuffer
        inBuffer[0] = '\0';
        inBuffPos   = 0;
      }
    } else {
      // discard inBuffer
      inBuffer[0] = '\0';
      inBuffPos   = 0;
    }

    //debug_printf("Read character from COMRX: %i\n", ch);
  } else if(COMSTA0 & COMSTA0_TEMT_MASK) {

    // Send a char from sendBuff
    if(sendBuffPos < sendBuffEnd)
      COMTX = sendBuffer[sendBuffPos++];
    else {
      // Reset sendBuffPos
      sendBuffPos = 0;

      // Sendbuffer empty, try to fill from ringbuffer
      sendBuffEnd = ringbufferGetLine(outBufferRing, sendBuffer);

      if(sendBuffEnd < 0)
        sendBuffEnd = 0;

      if(sendBuffEnd < 1){
        // No data to send, disable interrupt
        COMIEN0 &= ~(1 << COMIEN0_ETBEI_BIT);      
      }
    }

  }

}

void dabort_handler(){
  debug_printf("ABORT HANDLER!\n");
}

void undef_handler(){
  debug_printf("UNDEF HANDLER!\n");
}
