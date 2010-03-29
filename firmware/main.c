// Serial Test - Interupt Proof of conecpt
// (C) Copyright 2010 by Marcel Noe <marcel@marcel-noe.de>
// based on UART example by Rowley Associates Limited.

// Comments by Marcel Noe <marcel@marcel-noe.de>
#include <ctl_api.h>
#include <targets/ADuC7026.h>
#include <cross_studio_io.h>
#include "uart.h"
#include "ringbuffer.h"
#include "random.h"

// Defines 
#define BAUDRATE        38400   // Baudrate of serial interface
#define IRQ0_INT    IRQSTA_External_IRQ0_BIT
#define TIMER0_INT  IRQSTA_Timer_0_BIT
#define UART_INT    IRQSTA_UART_BIT

// Global variables
int sendStatus = 0;
int alertStatus = 0;

#define inBuffSize 80
char  inBuffer[inBuffSize];
int   inBuffPos = 0;
ringbuffer * inBufferRing;

#define outBuffSize 80
char outBuffer[outBuffSize];
int  outBuffPos = 0;
ringbuffer * outBufferRing;

#define sendBuffSize 80
char sendBuffer[sendBuffSize];
int  sendBuffPos = 0;
int  sendBuffEnd = 0;

void dabort_handler(){
  debug_printf("ABORT HANDLER!\n");
}

void undef_handler(){
  debug_printf("UNDEF HANDLER!\n");
}

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

void putInOutbuffer(char ch){
  if(outBuffPos < outBuffSize - 1){
    outBuffer[outBuffPos++] = ch;
    outBuffer[outBuffPos]   = '\0';

    if(ch == '\n'){
      // Put data in Ringbuffer
      ringbufferPutLine(outBufferRing, outBuffer);

      // Reset outBuffer_*
      outBuffer[0] = '\0';
      outBuffPos   = 0;

      // Enable sending interrupt
      COMIEN0 |= (1 << COMIEN0_ETBEI_BIT);
    }
  } else {    
    outBuffer[0] = '\0';
    outBuffPos   = 0;
  }
}

void __putchar(int ch){
  // Put characters in buffer
  if (ch == '\n')
    putInOutbuffer('\r');

  if(ch != '\0')
    putInOutbuffer(ch);
}

int readLine(char * line){
  return ringbufferGetLine(inBufferRing, line);
}

int extractStatus(char * commandline){
  if( (strcmp("OK\r\n", commandline) == 0) || (strcmp("OK\n", commandline) == 0) || (strcmp("OK\r", commandline) == 0))
    return 1;
  else if( (strcmp("ERROR\r\n", commandline) == 0) || (strcmp("ERROR\n", commandline) == 0) || (strcmp("ERROR\r", commandline) == 0))
    return 0;
  else
    return -1;

};

int waitForStatus(ringbuffer * commandBuffer){
  char line[inBuffSize];
  int  status;

  // Fetch rows for ok Status:
  do {
    readLine(line);
    status = extractStatus(line);
    if(status == -1 && line[0] != '\0'){
      // We've encountered an command,
      // that we put into the commandbuffer
      ringbufferPutLine(commandBuffer, line);
      debug_printf("Got command: %s\n", line);
      debug_printf("Char: %i", line[0]);
    }
  } while (status == -1);
  return status;
}

// Functions
int main(void){

  // Create ringbuffer for input
  ringbuffer commandBuffer;
  ringbuffer * commandBufferPointer = &commandBuffer;
  initRingbuffer(commandBufferPointer);
  inBufferRing  = newRingbuffer();
  outBufferRing = newRingbuffer();
  int  status;
  

  // Initialize UART
  UARTInitialize(BAUDRATE);

  // Enable UART Interrupt
  COMIEN0 = 0x01; // enable ERBFI & ETBEI
  ctl_set_isr(UART_INT, 0, CTL_ISR_TRIGGER_FIXED, uartISR, 0);
  ctl_unmask_isr(UART_INT);

  // Enable Button Interrupt
  ctl_set_isr(IRQ0_INT, 1, CTL_ISR_TRIGGER_FIXED, buttonISR, 0);
  ctl_unmask_isr(IRQ0_INT);

  // Enable Timer Interrupt
  ctl_set_isr(TIMER0_INT, 0, CTL_ISR_TRIGGER_FIXED, timerISR, 0);
  ctl_unmask_isr(TIMER0_INT);

  T0LD  = 0xFFFF;
  T0CON = 0xc8;

  // Enable all interrupts
  ctl_global_interrupts_enable();

  while(1){
    int puls = 0;
    int atmung = 0;

    // If sendStatus is true, we have to send our status
    if(sendStatus){
        if(!alertStatus){
          puls   = randint(60, 120);
          atmung = randint(30, 50);
          printf("ATD 0,1\rSTATUS:OK:%i:%i\r\n", puls, atmung);
        } else {
          puls   = randint(0, 10);
          atmung = randint(0, 5);
          printf("ATD 0,1\rSTATUS:ALERT:%i:%i\r\n", puls, atmung);
        }
    
      sendStatus = 0;
      status = waitForStatus(&commandBuffer);
      debug_printf("Got status, leaving loop!\n");
    }
  }
}
