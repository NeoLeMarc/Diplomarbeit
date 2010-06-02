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

// Function signatures
void UARTInitialize(unsigned int baud);
void putInOutbuffer(char ch);
void __putchar(int ch);
int readLine(char *);
void readStringRaw();
void putStringRaw(char *);
