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
  //inBufferRing  = newRingbuffer();
  //outBufferRing = newRingbuffer();

  // Create ringbuffers statically to save some heap space
  ringbuffer inBufferReal;
  ringbuffer outBufferReal;
  inBufferRing = &inBufferReal;
  outBufferRing = &outBufferReal;
  initRingbuffer(inBufferRing);
  initRingbuffer(outBufferRing);

  // Initialize UART
  UARTInitialize(BAUDRATE);

  // Enable UART Interrupt
  COMIEN0 = 0x0b; // enable ERBFI & ETBEI & EDSSI
  ctl_set_isr(UART_INT, 0, CTL_ISR_TRIGGER_FIXED, uartISR, 0);
  ctl_unmask_isr(UART_INT);

  // Enable Button Interrupt
  ctl_set_isr(IRQ0_INT, 1, CTL_ISR_TRIGGER_FIXED, buttonISR, 0);
  ctl_unmask_isr(IRQ0_INT);

  // Enable Timer Interrupt
  ctl_set_isr(TIMER0_INT, 0, CTL_ISR_TRIGGER_FIXED, timerISR, 0);
  ctl_unmask_isr(TIMER0_INT);

  T0LD  = (unsigned long)0x0FFF;
  T0CON = 0xc8;

  // Set GPIO 0.0 to output, GPIO 0.1 as input
  GP0CON = 0x0;
  GP0DAT = 0x01000000;

  // Use GIO 2.0, 2.1, 2.2, 2.3, 2.6, 2.7 as output
  GP2CON = 0x0;
  GP2DAT = 0xcf000000;

  // Use 4.2 as Output
  GP4DAT = 0x0;

  // Initialize ZigBit
  initZigBit();

  // Enable all interrupts
  ctl_global_interrupts_enable();

  // Start ZigBit Loop
  zigBitLoop();
}
