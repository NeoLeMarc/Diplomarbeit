// Serial Test
// (C) Copyright 2010 by Marcel Noe <marcel@marcel-noe.de>
// based on UART example by Rowley Associates Limited.

// Comments by Marcel Noe <marcel@marcel-noe.de>
#include <targets/ADuC7000.h>

// Analog Devices rename common peripherals between ADuC7000 and ADuC7100...
#ifndef COMCON0
#define COMCON0 COM0CON0
#define COMDIV0 COM0DIV0
#define COMDIV1 COM0DIV1
#define COMSTA0 COM0STA0
#define COMTX   COM0TX
#define COMRX   COM0RX
#endif

#define PROCESSOR_CLOCK_FREQUENCY 41780000

// Maximum size of line
#define MAXLINE 255 
#define MAXARGS 10  // The maximal number of arguments we support

static void
UARTInitialize(unsigned int baud)
{
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

static void
UARTWriteChar(unsigned char ch)
{

  // 0x20 == Bit 5 set
  // This means: "THRE": - Bit is set if there is no Data in 
  // COMTX and COMRX, it is unset ("0") otherwise.

  // This is a spinlock, which waits until COMTX is empty
  while ((COMSTA0 & 0x20) == 0);

  // Write ch to comtx.
  COMTX = ch;
}

static unsigned char
UARTReadChar(void)
{
  // Spinlock waiting for DR, meaning: data to become ready.
  while ((COMSTA0 & 0x01) == 0);

  // Read & return data from COMRX.
  return COMRX;
}

static int
UARTReadAvailable(void)
{
  // Check DR, returns true if a byte can be fetch from
  // COMRX.
  return COMSTA0 & 0x01;
}

void 
__putchar(int ch)
{
  // __putchar is called by printf. Defining it enables us
  // to use printf to write to serial line.

  // \n has to be replaced by \r\n
  if (ch == '\n')
    UARTWriteChar('\r');

  // Call UARTWrite to write character
  UARTWriteChar(ch);
}

void parseCommand(char * commandline){
    printf("Entering parseCommand(%s)\n", commandline);

    // Count number of arguments
    int argc = 0;
    char * argv[MAXARGS];
    char argtemp[MAXLINE + 2];
    int i, argbegin = 0;

    for(i = 0; i < MAXLINE && commandline[i + 1] != '\0'; i++){

      if(commandline[i] == ' ' || commandline[i] == '\r'){
        // get size of argument
        int size =  i - argbegin + 1;

        // Request memory for storage of argument
        argv[argc] = (char *)malloc(sizeof(char) * size);

        // Coppy argument        
        snprintf(argv[argc], size, "%s", commandline + argbegin);
        
        // Save new argbegin
        argbegin = i + 1;
        argc++;
      }
    }

    // Print number of found arguments
    printf("Argument count: %i\n", argc);
    for(i = 0; i < argc; i++)
      printf("Argument %i: %s\n", i, argv[i]);
}

int
main(void)
{
  int i;
  int linepos = 0;
  char ch;
  char ch0;
  char commandline[MAXLINE + 1];
  int status = 0;

  // Set serial port to 38400 baud
  GP4DAT = 0x04000000;
  UARTInitialize(38400);
  GP4DAT ^= 0x04000000;

  // Begin read loop
  /*
  printf("ATZ\n");
  printf("ATR 0,0,s120=3\n");
  */
  
  while(1) {
      status = (status + 1) % 2;

      // Toggle status
      //printf("ATR 2,0,s120=3 s130=%i\n", status);
      // Second parameter can be set to Zero:
      // This will improve the performance but will lead to less reliable communication
      printf("ATD 0,1\rTest\r");

      if(status == 1){
        GP4DAT = 0x04000000;        
      } else {
        GP4DAT ^= 0x04000000;
      }

      // Read character -- Spinlock              
      do {
        commandline[0] = '\0';
        linepos        = 0;

        do{        
          //while(!UARTReadAvailable());                    
            ch = UARTReadChar();    
            commandline[linepos] = ch;
        } while (linepos++ < MAXLINE && ch != '\n');
        
        commandline[linepos] = '\0';      
      } while(strcmp("OK\r\n", commandline) != 0 && strcmp("ERROR\r\n", commandline) != 0);

      GP4DAT ^= 0x04000000;    
  }
}
