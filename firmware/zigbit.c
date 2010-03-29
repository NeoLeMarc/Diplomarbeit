// Interface for ZigBit Device
#include "ringbuffer.h"
#include "isr.h"
#include "zigbit.h"

// Global Variables
int sendStatus = 0;
int alertStatus = 0;

// Functions
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

void zigBitLoop(){
  // Commandbuffer
  ringbuffer commandBuffer;
  ringbuffer * commandBufferPointer = &commandBuffer;
  initRingbuffer(commandBufferPointer);

  int  status;
 
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

