// Serial Test
// (C) Copyright 2010 by Marcel Noe <marcel@marcel-noe.de>
// based on UART example by Rowley Associates Limited.

// Comments by Marcel Noe <marcel@marcel-noe.de>
#include <ctl_api.h>
#include <targets/ADuC7026.h>
#include "ringbuffer.h"
#include "uart.h"
#include "isr.h"

// Defines 
#define BAUDRATE        38400   // Baudrate of serial interface

// Global variables
int           alertStatus = 0;
ringbuffer *  outBuffer;         // Buffer for outgoing data

// Functions
int main(void){
  // Create a ringbuffer for incoming data 
  ringbuffer * outBuffer  = newRingbuffer();

  // Set Alert Status off
  alertStatus = 0;

  // Initialize Boards & turn the darn LED off
  ctl_board_init();
  GP4DAT = 0;

  // Enable Interrupts
  ctl_set_isr(IRQ0_INT, 1, CTL_ISR_TRIGGER_FIXED, buttonISR, 0);
  ctl_unmask_isr(IRQ0_INT);
  
  // Set serial port to 38400 baud
  UARTInitialize(BAUDRATE);

  // Begin read loop
  printf("ATZ\r\n");
  waitForStatus();
    
  // Enable periodic sending of status
  ctl_set_isr(TIMER0_INT, 0, CTL_ISR_TRIGGER_FIXED, timerISR, 0);
  ctl_unmask_isr(TIMER0_INT);

  T0LD   = (unsigned char)189000; // Every 10 seconds
  T0CON  = 0xc8;  // Set prescaler to CoreClock / 256 + Periodic mode

  // Enable UART Interrupt
  ctl_set_isr(UART_INT, 0, CTL_ISR_TRIGGER_FIXED, uartISR, 0);
  ctl_unmask_isr(UART_INT);

  COMIEN0 |= (1 << COMIEN0_ETBEI_BIT); // Enable "transmit buffer empty interrupt"
  COMIEN0 |= (1 << COMIEN0_ERBFI);     // Enable "receive buffer full interrupt"


  // Enable all interrupts
  ctl_global_interrupts_enable();
}
