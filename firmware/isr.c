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

// Mute alert
int mute = 0;

// Simulate critical patient?
int yellowCode = 0;

void yellowButton(){
  disableAlert();

  // Enable yellow led & disable green
  GP2DAT = (GP2DAT & ~(1 << 18)) | (1 << 17);

  // Yellow Code: If this is true,
    // then the patient stats will be "not so good"
  yellowCode = 1;
}

// ISRs
void timerISR(){
  // If red button is pressed => alert (P0.0)
  if(!(GP0DAT & 0x02)){
    enableAlert();
  } 
  
  // If Green button is pressed => no alert (P0.1)
  else if(!(GP0DAT & 0x04)){
    disableAlert();
  }

  // If Blue button is pressed => mute (P0.5)
  else if(!(GP0DAT & 0x20)){
    // Enable blue LED + Mute
    GP2DAT |= (1 << 19);

    mute = 1;
  }

  // If yellow button is pressed => simulate bad patient status (P0.3)
  else if(!(GP0DAT & 0x80)){
    yellowButton();
  }

  // Beeping
  static int beep = 0;
  static int beepDelay = BEEPDELAY;

  if(alertStatus){  
    if(beepDelay-- == 0){
      beepDelay = BEEPDELAY;

      if(!beep){

        // Disable green led
        GP2DAT &= ~(1 << 18);

        // Blink red led
        GP2DAT |= (1 << 16);

        // Enable beep if not muted
        if(!mute)
          GP2DAT |= (1 << 22);      
      } else {
        // Blink red led
        GP2DAT &= ~(1 << 16);

        // Disable beep
        GP2DAT &= ~(1 << 22);
      }
  
      beep = !beep;
    } 
  } else {
    // Disable beep
    GP2DAT &= ~(1 << 22);

    // Disable red led
    GP2DAT &= ~(1 << 16);

    if(!yellowCode)
      // Enable green led
      GP2DAT |= (1 << 18);
  }

  // Sending of commands
  static int counter = CLOCKMULTIPLIER;

  if(counter-- <= 0){
    counter = CLOCKMULTIPLIER;

    // Schedule sending of command status
    sendStatus = 1;
  }

  // Clear interrupt
  T0CLRI = 0;
}

void enableAlert(){
  // Toggle alert status
  alertStatus = 1; 

  // Set LED
  //GP4DAT |= (1 << 26);

  // Unmute
  mute = 0;

  // Disable blue LED
  GP2DAT &= ~(1 << 19);

  // Disable yellow code
  GP2DAT &= ~(1 << 17);
  yellowCode = 0;

  // immediatly signal change in alert status
  sendStatus  = 1;
}

void disableAlert(){
  // Toggle alert status
  alertStatus = 0; 

  // Disable LED
  //GP4DAT &= ~(1 << 26);

  // Disable yellow code
  GP2DAT &= ~(1 << 17);
  yellowCode = 0;

  // Unmute
  mute = 0;

  // Disable blue LED
  GP2DAT &= ~(1 << 19);

  // immediatly signal change in alert status
  sendStatus  = 1;
}

void buttonISR(){
  // Mask interrupt
  ctl_mask_isr(IRQ0_INT);
  
  if(alertStatus){
    disableAlert();
  } else {
    // Disable LED
    enableAlert();
  }

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
  } else if((COMSTA0 & COMSTA0_THRE_MASK) && (COMSTA1 & COMSTA1_CTS_MASK)) {    
    
    // Send a char from sendBuff
    if(sendBuffPos < sendBuffEnd){  
      char ch = sendBuffer[sendBuffPos++];
      COMTX = ch;
    } else {
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

  if(COMSTA1 & COMSTA1_CTS_MASK){
    GP4DAT |= (1 << 26);
  } else {
    // Disable LED
    GP4DAT &= ~(1 << 26);
  }

}

void dabort_handler(){
  debug_printf("ABORT HANDLER!\n");
}

void undef_handler(){
  debug_printf("UNDEF HANDLER!\n");
}
