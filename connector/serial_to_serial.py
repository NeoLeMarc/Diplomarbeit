#!/usr/bin/env python -OO
# MANV-Sensor - Serial to Serial 
# Connect two serial ports
# Used for snooping serial connection between ZigBit and Micro controller

import serial, select, sys, SocketServer, time


class Serial2Serial:
    def __init__(self, deviceA, deviceB):
        self.serialA = serial.Serial(deviceA, 38400, timeout=1)
        self.serialB = serial.Serial(deviceB, 38400, timeout=1)

    def tunnel(self):
        end = False
        print "Entering tunnel mode"

        while not end:
            readylist = select.select((self.serialA, self.serialB), (), ())

            for readysocket in readylist[0]:
                if readysocket == self.serialA:
                    data = self.serialA.read(1024)
                    if data[:6] != 'DEBUG:':
                        self.serialB.write(data)
                    print ">" + data
                elif readysocket == self.serialB:
                    data = self.serialB.read(1024)
                    self.serialA.write(data)
                    print "<" + data
                else:
                    data = ""
                    end = True
                    print "END!"

        ## Close serial port
        self.serialA.close()
        self.serialB.close()
        print "Leaving tunnel mode"

if __name__ == '__main__':
    s = Serial2Serial('/dev/ttyUSB0', '/dev/ttyUSB1')
    s.tunnel()

