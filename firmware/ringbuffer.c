#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "ringbuffer.h"
ringbuffer * newRingbuffer(){
    ringbuffer * bufferPointer = (ringbuffer *)malloc(sizeof(ringbuffer));
    int i = 0;

    // Set lines to \0
    for(i = 0; i < RINGBUFFERSIZE; i++)
        bufferPointer->lines[i][0] = '\0';

    // Set in pointer to first element
    bufferPointer->in = &bufferPointer->lines[0];

    // Out pointer is invalid
    bufferPointer->out = &bufferPointer->lines[0];

    // Return pointer to new ringbuffer
    return bufferPointer;
}

void debugRingbuffer(ringbuffer * bufferPointer){
    printf("In pointer: %ld, Out pointer: %ld, Last pointer: %ld\n", (long)bufferPointer->in, (long)bufferPointer->out, (long)&bufferPointer->lines[RINGBUFFERSIZE - 1]);
}

void ringbufferPutLine(ringbuffer * bufferPointer, char * line){

    // Get pointer for line to put string at
    ringbufferLine * destline = bufferPointer->in;

    // Copy string to ringbuffer
    snprintf((char *)destline, RINGBUFFERMAXLEN, "%s", line);

    // Set in pointer to next line
    bufferPointer->in = ringbufferGetNextPointer(bufferPointer, destline);
}

int ringbufferGetLine(ringbuffer * bufferPointer, char * line){
    // Check if element is valid
    if(bufferPointer->out[0] == '\0')
        return -1;
    else {
        // Copy line
        int size = snprintf(line, RINGBUFFERMAXLEN, "%s", *bufferPointer->out);

        // Erase element
        *bufferPointer->out[0] = '\0';

        // Set out pointer to next element
        bufferPointer->out = ringbufferGetNextPointer(bufferPointer, bufferPointer->out);

        return size;
    }

}

ringbufferLine * ringbufferGetNextPointer(ringbuffer * bufferPointer, ringbufferLine * bufferLine){
    ringbufferLine * retPointer = bufferLine + 1;

    if(retPointer > &bufferPointer->lines[RINGBUFFERSIZE - 1])
        // We've reached the end of the ringbuffer, wrap around 
        retPointer = &bufferPointer->lines[0];

    return retPointer;
}
