import sys

import serial, time
ser = serial.Serial('/dev/ttyUSB2', 38400, timeout=1)

#if len(sys.argv) > 1:
#    if sys.argv[1] == 'k':
#        print "k..."
#        ser.write("K\r\n")
#    else:
#        ser.write("\n")
#else:
#    ser.write("OK\r\n")
#    ser.write("\n")

timelast = time.time()
while True:
    line = ''
    char = ''
    while(char != '\n'):
        char = ser.read()
        if char:
            print char
        line += char

    time.sleep(1)
    if len(line):
        print line
        ser.write("OK\r\n")
        print "OK"
        print "%i Seconds" % (time.time() - timelast)
        timelast = time.time()
