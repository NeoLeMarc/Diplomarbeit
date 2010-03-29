#include <ctl_api.h>
#include <targets/ADuC7026.h>
#include "uart.h"

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
  // because this port shoul be configured to UART mode.
  GP1CON = 0x11;

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

void UARTWriteChar(unsigned char ch){

  // 0x20 == Bit 5 set
  // This means: "THRE": - Bit is set if there is no Data in 
  // COMTX and COMRX, it is unset ("0") otherwise.

  // This is a spinlock, which waits until COMTX is empty
  while ((COMSTA0 & 0x20) == 0);

  // Write ch to comtx.
  COMTX = ch;
}

unsigned char UARTReadChar(void){
  // Spinlock waiting for DR, meaning: data to become ready.
  while ((COMSTA0 & 0x01) == 0);

  // Read & return data from COMRX.
  return COMRX;
}

int UARTReadAvailable(void){
  // Check DR, returns true if a byte can be fetch from
  // COMRX.
  return COMSTA0 & 0x01;
}

/*
void __putchar(int ch){
  // __putchar is called by printf. Defining it enables us
  // to use printf to write to serial line.

  // \n has to be replaced by \r\n
  if (ch == '\n')
    UARTWriteChar('\r');

  // Call UARTWrite to write character
  UARTWriteChar(ch);
}
*/
