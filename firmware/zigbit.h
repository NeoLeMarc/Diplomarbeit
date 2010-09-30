// Powersave modus:
// 1 - enabled
// 0 - disabled
#define POWERSAVE 0

int extractStatus(char *);
int waitForStatus(ringbuffer *);
void parseCommand(char *);
void zigBitLoop();
void initZigBit();
