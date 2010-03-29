// Settings 
#define CLOCKMULTIPLIER 10      // Multiplier for clock
#define MAXARGS         10      // The maximal number of arguments we support

// Sizes of Buffers
#define inBuffSize      80
#define outBuffSize     80
#define sendBuffSize    80

// Interrupts
#define IRQ0_INT    IRQSTA_External_IRQ0_BIT
#define TIMER0_INT  IRQSTA_Timer_0_BIT
#define UART_INT    IRQSTA_UART_BIT

// Signatures
void buttonISR();
void timerISR();
void uartISR();
void dabort_handler();
void undef_handler();
