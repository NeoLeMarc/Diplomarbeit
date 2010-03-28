// Settings 
#define CLOCKMULTIPLIER 10      // Multiplier for clock
#define MAXARGS         10      // The maximal number of arguments we support

// Interrupts
#define IRQ0_INT    IRQSTA_External_IRQ0_BIT
#define TIMER0_INT  IRQSTA_Timer_0_BIT
#define UART_INT    IRQSTA_UART_BIT

// Callbacks & Locks
void (*callback)(int status);   // Function pointer for status callback
int sendLock;                   // Sendlock
int recvLock;                   // Receivelock

// Signatures
void buttonISR();
void timerISR();
void uartISR();
void waitForStatus();
int  parseStatus(char *);
void parseCommand(char *);
void sendAlertStatus();
void resumeSend();
void doSend();
void doReceive();
