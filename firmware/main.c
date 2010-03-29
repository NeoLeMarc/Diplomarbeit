// MANVFirmware
// (C) Copyright 2010 by Marcel Noe <marcel@marcel-noe.de>
// based on UART example by Rowley Associates Limited.

// Comments by Marcel Noe <marcel@marcel-noe.de>
#include <ctl_api.h>
#include <targets/ADuC7026.h>
#include <cross_studio_io.h>
#include "uart.h"
#include "isr.h"
#include "ringbuffer.h"
#include "random.h"
#include "zigbit.h"

// Defines 
#define BAUDRATE    38400   // Baudrate of serial interface

// Global variables
extern ringbuffer * inBufferRing;
extern ringbuffer * outBufferRing;

// Functions
int main(void){

  // Buffer for Serial interface
  inBufferRing  = newRingbuffer();
  outBufferRing = newRingbuffer();

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

  // Start ZigBit Loop
  zigBitLoop();
}
