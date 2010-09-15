// Interface for ZigBit Device
#include <ctl_api.h>
#include <targets/ADuC7026.h>
#include <cross_studio_io.h>
#include "ringbuffer.h"
#include "isr.h"
#include "zigbit.h"

// Global Variables
int sendStatus = 0;
int alertStatus = 0;
extern int yellowCode;

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
      if(isCommand(line)){
        ringbufferPutLine(commandBuffer, line);
        //debug_printf("Got command: %s\n", line);
      } 
    }
  } while (status == -1);
  return status;
}

int isCommand(char * line){
  if(strlen(line) > 4 && line[0] == 'D' && line[1] == 'A' && line[2] == 'T' && line[3] == 'A')
    return 1;
  else
    return 0;
}

void parseCommand(char * commandline){
    //debug_printf("Entering parseCommand(%s)\n", commandline);

    // Count number of arguments
    int argc = 0;
    char * argv[MAXARGS];
    char argtemp[MAXLINE + 2];
    int i, argbegin = 0;

    // Move forward in commandline until we find the ":"
    while(*commandline != ':' && *commandline != '\0')
      commandline++;

    // Now move over the ":"
    if(*commandline == ':')
      commandline++;
    else if(*commandline == '\0')
      return;

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
    //debug_printf("Argument count: %i\n", argc);

    // Run command
    if(argc){
      //debug_printf("Command is: %s\n", argv[0]);
      switch(argv[0][0]){
        case 'e':
          // Enable alert
          enableAlert();
          break;

        case 'y':
          // Emulate Yellow code
          yellowButton();
          break;

        case 'd':
          // Disable alert
          disableAlert();
          break;

        case 't':
          // Simulate button request
          buttonISR();
          break;

        case 'm':
          // Mute
          muteAlert();
        default:
          ;
          //debug_printf("Unsupported command!\n");
      }
    }
    
    // Free Memory
    for(i = 0; i < argc; i++){
      free(argv[i]);
      //debug_printf("Freed arg[%i]\n", i);
    }
}

void sleep(int seconds){
  volatile long i;
  volatile long y;
  while(seconds--)
    for(i = 250000; i > 0; i--){    
        y = i - i;        
    }
}

void initZigBit(){
  // Inizialize ZigBit
  //debug_printf("Entering initializeZigBit...\n");
  /*
  // 1. Pull Reset line (Port 2.7)
  GP2DAT &= ~(1 << 23);

  // Sleep a little bit
  GP2DAT |= (1 << 18);
  sleep(1);
  GP2DAT &= ~(1 << 18);
*/
  // 2. Release Reset line
  GP2DAT |= (1 << 23);

  // Wait for ZigBit to become ready
  sleep(4);
  GP2DAT |= (1 << 18);
  sleep(4);
  GP2DAT &= ~(1 << 18);
  sleep(4);
  GP2DAT |= (1 << 18);
  sleep(4);
  GP2DAT &= ~(1 << 18);

  //debug_printf("ZigBit has been resetted...\n");

  // Now write settings to serial port
  putStringRaw("ATX\r\n");
  GP2DAT |= (1 << 17);
  readStringRaw();
  GP2DAT &= ~(1 << 17);
  sleep(4);
  putStringRaw("AT+GSN=3 +WROLE=2 +IFC=2,2 +WAUTONET=0 +WWAIT=100\r\n");
  GP2DAT |= (1 << 17);
  readStringRaw();
  GP2DAT &= ~(1 << 17);
  sleep(4);
  putStringRaw("AT+WPANID=1620 +WCHMASK=07FF800 +WPWR=35,0\r\n");
  GP2DAT |= (1 << 17);
  readStringRaw();
  GP2DAT &= ~(1 << 17);
  sleep(4);
  GP2DAT |= (1 << 18);
  putStringRaw("AT+WJOIN +WAUTONET=1\r\n");
  GP2DAT |= (1 << 16);
  readStringRaw();
  GP2DAT &= ~(1 << 18);
  sleep(1);
  GP2DAT |= (1 << 17);
  GP2DAT &= ~(1 << 16);
  sleep(1);
  GP2DAT &= ~(1 << 17);
  // ZigBit should now be initialized
}

void zigBitLoop(){
  // Commandbuffer
  ringbuffer commandBuffer;
  ringbuffer * commandBufferPointer = &commandBuffer;
  initRingbuffer(commandBufferPointer);

  int  status;
  char line[inBuffSize];

  while(1){
    int puls = 0;
    int atmung = 0;


    // If sendStatus is true, we have to send our status
    if(sendStatus){
        if(!alertStatus){
          if(yellowCode){
            // Simulate critical patient:
            puls   = randint(120, 180);
            atmung = randint(10, 30);
          } else {
            puls   = randint(60, 120);
            atmung = randint(30, 50);
          }

          printf("ATD 0,0\rSTATUS:OK:%i:%i\r\n", puls, atmung);

        } else {
          puls   = randint(0, 10);
          atmung = randint(0, 5);
          printf("ATD 0,0\rSTATUS:ALERT:%i:%i\r\n", puls, atmung);
        }
    
      sendStatus = 0;
      status = waitForStatus(&commandBuffer);
      //debug_printf("Got status, leaving loop!\n");
    } 

    // Fetch commands
    while(readLine(line)){
      if(isCommand(line))
        ringbufferPutLine(&commandBuffer, line);
    }
     

    // Now we execute all remaining commands
    while(ringbufferGetLine(&commandBuffer, line)){
      parseCommand(line);
    }

    // End of Duty cycle, put ZigBit Module to sleep
    if(POWERSAVE){
      printf("AT+WSLEEP\r\n");
      waitForStatus(&commandBuffer);
    }
  }
}

