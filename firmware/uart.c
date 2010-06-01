#include <ctl_api.h>
#include <targets/ADuC7026.h>
#include "ringbuffer.h"
#include "isr.h"
#include "uart.h"

// Global variables
extern char outBuffer[outBuffSize];
extern int outBuffPos;
extern ringbuffer * outBufferRing;
extern ringbuffer * inBufferRing;

void UARTInitialize(unsigned int baud){
  // Divisor: The baud rate of the serial interface is generated
  // by dividing the processor clock. This is the reason why it
  // is not possible to use all available baud-rates without the
  // use of an external clock generator.
  unsigned int divisor = PROCESSOR_CLOCK_FREQUENCY / (32 * baud);

  // Configuration of the serial port multiplexer:
  // The serial port mux is configured using GP1CON and GP2CON MMR.
  // The GPIO pins are grouped into 5 ports, ranging from 0 to 4.
  // Eache pin can be configured with 4 different configurations,
  // namely 00, 01, 10 and 11. The configuration register
  // for port X is called GPXCON, so GP1CON is the configuration
  // register for GPIO Port 1. In this configuration register,
  // every pin for this port has got 2 bits in which the configuration 
  // is stored.

  // The following line of code sets bit 0 and 4 to value 1, 
  // leaving all other bits to 0. This configures the GPIO-Port 1
  // as following:
  // Set bis 0 and 4: This disables Pull-Up for P1.0 and P1.1.
  // All ports are configured for GPIO. It is not clear
  // at this moment why serial port communication works here,
  // because this port should be configured to UART mode.
  // Also enable CTS + RTS 
  GP1CON = 0x1111;

  // Set bit 7 in COMCON 0. This is "DLAB": Divisor latch access:
  // "Set by user to enable access to COMDIV0 and COMDIV1).
  COMCON0 = 0x80;

  // Use registers COMDIV0 and COMDIV1 to set baud-rate divisor.
  COMDIV0 = (unsigned char)divisor;
  COMDIV1 = (unsigned char)(divisor >> 8);

  // Set bits 0, 1, 2 in COMCON0:
  // Bit [1:0]: Word-length select: This is set to 11, meaning 8 bits
  // Bit 2: STOP - Transmit stop bits.
  // As a side effect, DLAB is now deactived again, thus disabling
  // access to the COMDIV0 and COMDIV 1 registers.
  COMCON0 = 0x07;

}

// Helper function to put characters in outputbuffer
// Enables sending interrupt if outbuffer contains complete line
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

// Define __putchar, to be able to use 
// printf to write to serial line
void __putchar(int ch){
  // Put characters in buffer
  if (ch == '\n')
    putInOutbuffer('\r');

  if(ch != '\0')
    putInOutbuffer(ch);
}

// Write to serial port without interrupts
void putStringRaw(char * ch){
  while(*ch != '\0'){
    while(!(COMSTA0 & COMSTA0_THRE_MASK)); // Wait for serial port to become ready
    COMTX = *(ch++); // Write char
  }    
};

// Read a line without interrupts
void readStringRaw(char * out){
  char ch;
  do {
    while(!(COMSTA0 & COMSTA0_DR_MASK)); // Wait for character to arrive
    ch = COMRX;
    *(out++) = ch;
  } while(ch != '\n');
  *out = '\0';
  debug_printf("Read: %s\n", out);
};

// Read a single line from buffer
int readLine(char * line){
  return ringbufferGetLine(inBufferRing, line);
}
