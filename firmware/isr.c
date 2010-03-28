#include <ctl_api.h>
#include <targets/ADuC7026.h>
#include "ringbuffer.h"
#include "isr.h"
#include "uart.h"
#include "random.h"

// Gloabal variables
int                 clock0Count = 0;
int                 clock0Multiplier = CLOCKMULTIPLIER;
extern int          alertStatus;
extern ringbuffer * outBuffer;

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

void uartISR(){
  // This interrupt could either be "ready to send" or "data received"
  if((COMSTA0 && COMSTA0_DR_MASK) == COMSTA0_DR_MASK){
    doReceive();
  } else if ((COMSTA0 && COMSTA0_TEMT_MASK) == COMSTA0_TEMT_MASK){
    doSend();
  }
}

void doReceive(){
  // Try to acquire readlock:
  ctl_global_interrupts_disable();
  if(!recvLock){
    recvLock = 1;
    ctl_global_interrupts_enable();

    char line[RINGBUFFERMAXLEN + 1];
    char ch;
    int linepos = 0;
    
    // Read a line
    do{        
      ch = UARTReadChar();    
      line[linepos] = ch;
    } while (linepos++ < RINGBUFFERMAXLEN && ch != '\n');
        
    line[linepos] = '\0';      

    // Release lock
    recvLock = 0;

    // If line is status, we do callback
    int status;
    if(((status = parseStatus(line)) > -1) && callback != NULL)
      callback(status);
    // TODO: Else-Case: Parse command
  } else {
      ctl_global_interrupts_enable();
  }
}

void doSend(){
  // Try to acquire sendlock:
  ctl_global_interrupts_disable();
  if(!sendLock){
    sendLock = 1;
    ctl_global_interrupts_enable();

    char line[RINGBUFFERMAXLEN + 1];

    // Fetch data from ringBuffer
    if(ringbufferGetLine(outBuffer, line) > 0){
      // Set callback
      callback = &resumeSend;

      // Write to serial
      printf(line);
    }

    // Release sendLock
    sendLock = 0;
  } else {
    ctl_global_interrupts_enable();
  }
}

void resumeSend(int status){
  // Clear sendLock
  sendLock = 0;

  // reEnter doSend
  doSend();
}

void sendAlertStatus(){
  // Send status update
  // Second parameter can be set to Zero:
  // This will improve the performance but will lead to less reliable communication
  int puls   = 0;
  int atmung = 0;
  char line[RINGBUFFERMAXLEN + 1];

  if(!alertStatus){
    puls   = randint(60, 120);
    atmung = randint(30, 50);
    snprintf(line, RINGBUFFERMAXLEN, "ATD 0,1\rSTATUS:OK:%i:%i\r", puls, atmung);
  } else {
    puls   = randint(0, 10);
    atmung = randint(0, 5);
    snprintf(line, RINGBUFFERMAXLEN, "ATD 0,1\rSTATUS:ALERT:%i:%i\r", puls, atmung);
  }

  // Schedule for sending
  ringbufferPutLine(outBuffer, line);
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

int parseStatus(char * commandline){
  if(strcmp("OK\r\n", commandline) == 0)
    return 1;
  else if(strcmp("ERROR\r\n", commandline) == 0)
    return 0;
  else
    return -1;
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

