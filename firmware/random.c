#include "random.h"
unsigned long currentRandomNumber = 12345;

int randint(int min, int max){
    unsigned long m = 4294967291;
    unsigned long a = 3141592653;
    unsigned long b = 56985;

    currentRandomNumber = (a * currentRandomNumber + b) % m;

    return (int)min + (currentRandomNumber % (max - min));
}


