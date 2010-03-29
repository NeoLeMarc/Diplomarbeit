#define MAXLINE           80
#define RINGBUFFERSIZE    5
#define RINGBUFFERMAXLEN  MAXLINE 
#define RINGBUFFERINVALID 0

typedef char ringbufferLine[RINGBUFFERMAXLEN + 1];

typedef struct ringbuffer {
    ringbufferLine lines[RINGBUFFERSIZE];
    ringbufferLine * in;
    ringbufferLine * out; 
} ringbuffer;

ringbuffer * newRingbuffer(void);
ringbuffer * initRingbuffer(ringbuffer *);
void debugRingbuffer(ringbuffer *);
void ringbufferPutLine(ringbuffer *, char * line);
int  ringbufferGetLine(ringbuffer *, char * line);
ringbufferLine * ringbufferGetNextPointer(ringbuffer *, ringbufferLine *);
