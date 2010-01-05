import sys

class ZigBit:

    def __init__(self, serial, panID):
        self.panID  = panID
        self.serial = serial
        self.gpio   = [0, 0, 0, 0]

    def GPIOenable(self, nr):
        self.gpio[nr - 1] = 1

    def GPIOdisable(self, nr):
        self.gpio[nr - 1] = 0

    def update(self):
        self.serial.write("ATR %i,0," % self.panID)
        for i in xrange(0, len(self.gpio)):
            self.serial.write(' S13%i=%i' % (i, self.gpio[i]))
        self.serial.write("\r\n")
        return "UPDATE: %i" % self.wait()

    def wait(self):
        data = self.serial.readline()
        while data not in ['OK\r\n', 'ERROR\r\n']:
#            print data
            data = self.serial.readline()

        if data == 'OK\r\n':
            return True
        else:
            sys.stdout.write("ERROR!")
            sys.stdout.flush()
            return False

    def discover(self):
        self.serial.write('ats30=1+wchildren?\r\n')
        self.serial.readline()
        result = self.serial.readline()
        self.wait()
        children = []
        for child in result.split(':')[1].split(','):
            children.append(ZigBit(self.serial, int(child)))
        return children


import serial, time
sys.stdout.write("Beginning initialization... ")
sys.stdout.flush()
ser = serial.Serial('/dev/ttyUSB1', 38400, timeout=1)
ser.write("AT+WAUTONET=1 +WWAIT=100 Z\r\nATS30=1\r\n")
sys.stdout.write("Waiting for devices to join")
data = ser.readline()

while data[:18] != 'EVENT:CHILD_JOINED':
    sys.stdout.write(".")
    sys.stdout.flush()
    data = ser.readline()

sys.stdout.write("Beginning discovery...")
sys.stdout.flush()


z1 = ZigBit(ser, 5)
z2 = ZigBit(ser, 6)
z1.discover()
z1.update()
z2.update()

sys.stdout.write("... ready!\n")

print "Starting Test-Loop: "

while True:
    z1.GPIOdisable(1)
    z1.GPIOenable(2)
    z2.GPIOenable(1)
    z2.GPIOdisable(2)
    if z1.update() and z2.update():
        sys.stdout.write(".")
        sys.stdout.flush()

    z1.GPIOenable(1)
    z1.GPIOdisable(2)
    z2.GPIOdisable(1)
    z2.GPIOenable(2)
    if z1.update() and z2.update():
        sys.stdout.write(".")
        sys.stdout.flush()
