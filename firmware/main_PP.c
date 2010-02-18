# 1 "/home/marcel/Ubuntu One/diplomarbeit/firmware/main.c"
# 1 "<built-in>"
# 1 "<command line>"
# 1 "/home/marcel/Ubuntu One/diplomarbeit/firmware/main.c"





# 1 "/opt/Rowley Associates Limited/CrossWorks for ARM 1.7/include/targets/ADuC7000.h" 1 3 4
# 41 "/opt/Rowley Associates Limited/CrossWorks for ARM 1.7/include/targets/ADuC7000.h" 3 4
# 1 "/opt/Rowley Associates Limited/CrossWorks for ARM 1.7/include/targets/ADuC7026.h" 1 3 4
# 42 "/opt/Rowley Associates Limited/CrossWorks for ARM 1.7/include/targets/ADuC7000.h" 2 3 4
# 7 "/home/marcel/Ubuntu One/diplomarbeit/firmware/main.c" 2
# 24 "/home/marcel/Ubuntu One/diplomarbeit/firmware/main.c"
static void
UARTInitialize(unsigned int baud)
{




  unsigned int divisor = 41780000 / (32 * baud);
# 50 "/home/marcel/Ubuntu One/diplomarbeit/firmware/main.c"
  (*(volatile unsigned long *)0xFFFFF404) = 0x11;



  (*(volatile unsigned char *)0xFFFF070C) = 0x80;


  (*(volatile unsigned char *)0xFFFF0700) = (unsigned char)divisor;
  (*(volatile unsigned char *)0xFFFF0704) = (unsigned char)(divisor >> 8);






  (*(volatile unsigned char *)0xFFFF070C) = 0x07;

}

static void
UARTWriteChar(unsigned char ch)
{






  while (((*(volatile unsigned char *)0xFFFF0714) & 0x20) == 0);


  (*(volatile unsigned char *)0xFFFF0700) = ch;
}

static unsigned char
UARTReadChar(void)
{

  while (((*(volatile unsigned char *)0xFFFF0714) & 0x01) == 0);


  return (*(volatile unsigned char *)0xFFFF0700);
}

static int
UARTReadAvailable(void)
{


  return (*(volatile unsigned char *)0xFFFF0714) & 0x01;
}

void
__putchar(int ch)
{




  if (ch == '\n')
    UARTWriteChar('\r');


  UARTWriteChar(ch);
}

void parseCommand(char * commandline){
    printf("Entering parseCommand(%s)\n", commandline);


    int argc = 0;
    char * argv[10];
    char argtemp[255 + 2];
    int i, argbegin = 0;

    for(i = 0; i < 255 && commandline[i + 1] != '\0'; i++){

      if(commandline[i] == ' ' || commandline[i] == '\r'){

        int size = i - argbegin + 1;


        argv[argc] = (char *)malloc(sizeof(char) * size);


        snprintf(argv[argc], size, "%s", commandline + argbegin);


        argbegin = i + 1;
        argc++;
      }
    }


    printf("Argument count: %i\n", argc);
    for(i = 0; i < argc; i++)
      printf("Argument %i: %s\n", i, argv[i]);
}

int
main(void)
{
  int i;
  int linepos = 0;
  char ch;
  char ch0;
  int status = 0;


  (*(volatile unsigned long *)0xFFFFF460) = 0x04000000;
  UARTInitialize(38400);
  (*(volatile unsigned long *)0xFFFFF460) ^= 0x04000000;


  while(1) {
      status = (status + 1) % 2;


      printf("ATR 0,0,s130=%i\n", status);

      if(status == 1){
        (*(volatile unsigned long *)0xFFFFF460) = 0x04000000;
      } else {
        (*(volatile unsigned long *)0xFFFFF460) ^= 0x04000000;
      }


        do{
          ch0 = UARTReadChar();

          while(!UARTReadAvailable());
            ch = UARTReadChar();

        } while (linepos++ < 255 && ch != '\r');

        (*(volatile unsigned long *)0xFFFFF460) ^= 0x04000000;
  }
}
