// Serial Test
// (C) Copyright 2010 by Marcel Noe <marcel@marcel-noe.de>
// based on UART example by Rowley Associates Limited.

// Comments by Marcel Noe <marcel@marcel-noe.de>
#include <ctl_api.h>
#include <targets/ADuC7026.h>

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
#define IRQ0_INT IRQSTA_External_IRQ0_BIT
#define TIMER0_INT IRQSTA_Timer_0_BIT

// Maximum size of line
#define MAXLINE 255 
#define MAXARGS 10  // The maximal number of arguments we support
#define CLOCKMULTIPLIER 10;

// Global Variables
int alertStatus = 0;
int clock0Count = 0;
int clock0Multiplier = CLOCKMULTIPLIER;
unsigned long currentRandomNumber = 12345;

// Signatures
void waitForStatus();

int randint(int min, int max){
    unsigned long m = 4294967291;
    unsigned long a = 3141592653;
    unsigned long b = 56985;

    currentRandomNumber = (a * currentRandomNumber + b) % m;

    return (int)min + (currentRandomNumber % (max - min));
}

// Functions
static void UARTInitialize(unsigned int baud){
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

static void UARTWriteChar(unsigned char ch){

  // 0x20 == Bit 5 set
  // This means: "THRE": - Bit is set if there is no Data in 
  // COMTX and COMRX, it is unset ("0") otherwise.

  // This is a spinlock, which waits until COMTX is empty
  while ((COMSTA0 & 0x20) == 0);

  // Write ch to comtx.
  COMTX = ch;
}

static unsigned char UARTReadChar(void){
  // Spinlock waiting for DR, meaning: data to become ready.
  while ((COMSTA0 & 0x01) == 0);

  // Read & return data from COMRX.
  return COMRX;
}

static int UARTReadAvailable(void){
  // Check DR, returns true if a byte can be fetch from
  // COMRX.
  return COMSTA0 & 0x01;
}

void __putchar(int ch){
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

/* Interrupt service Routines */
void buttonISR(){
  int i;

  // Mask interrupt
  ctl_mask_isr(IRQ0_INT);

  // Toggle alert status
  alertStatus ^= 1;

  // Use LED to display alert Status
  GP4DAT = (alertStatus << 26);

  // immediatly signal change in alert status
  sendAlertStatus();

  // Spin some time to give user the chance to release button
  //for(i = PROCESSOR_CLOCK_FREQUENCY * 10; i > 0; i--);

  // Unmask interrupt
  ctl_unmask_isr(IRQ0_INT);
}

void timerISR(){
  if(clock0Multiplier-- > 0){ 
    // Clear interrupt
    T0CLRI = 0;
    return;

  } else
    clock0Multiplier = CLOCKMULTIPLIER;

  sendAlertStatus();

  // Clear interrupt
  T0CLRI = 0;
}

void sendAlertStatus(){
  // Send status update
  // Second parameter can be set to Zero:
  // This will improve the performance but will lead to less reliable communication
  int puls   = 0;
  int atmung = 0;

  if(!alertStatus){
    puls   = randint(60, 120);
    atmung = randint(10, 20);
    printf("ATD 0,1\rSTATUS:OK:%i:%i\r", puls, atmung);
  } else {
    puls   = randint(0, 10);
    atmung = randint(0, 5);
    printf("ATD 0,1\rSTATUS:ALERT:%i:%i\r", puls, atmung);
  }

  waitForStatus();
}

void waitForStatus(){
  int linepos = 0;
  char commandline[MAXLINE + 1];
  char ch;

  // Read character -- Spinlock              
  do {
    commandline[0] = '\0';
    linepos        = 0;

    do{        
      ch = UARTReadChar();    
      commandline[linepos] = ch;
    } while (linepos++ < MAXLINE && ch != '\n');
        
    commandline[linepos] = '\0';      
  } while(strcmp("OK\r\n", commandline) != 0 && strcmp("ERROR\r\n", commandline) != 0);
}

int main(void){
  // Set Alert Status off
  alertStatus = 0;

  // Initialize Boards & turn the darn LED off
  ctl_board_init();
  GP4DAT = 0;

  // Enable Interrupts
  ctl_set_isr(IRQ0_INT, 1, CTL_ISR_TRIGGER_FIXED, buttonISR, 0);
  ctl_unmask_isr(IRQ0_INT);
  
  // Set serial port to 38400 baud
  UARTInitialize(38400);

  // Begin read loop
  printf("ATZ\r\n");
  waitForStatus();
    
  // Enable periodic sending of status
  ctl_set_isr(TIMER0_INT, 0, CTL_ISR_TRIGGER_FIXED, timerISR, 0);
  ctl_unmask_isr(TIMER0_INT);

  T0LD   = 189000; // Every 10 seconds
  T0CON  = 0xc8;   // Set prescaler to CoreClock / 256 + Periodic mode

  // Enable all interrupts
  ctl_global_interrupts_enable();
}
