#!/usr/bin/env python
import usb, os, serial

def searchUSB():
    for bus in usb.busses():
        for dev in bus.devices:
            if dev.idVendor == 0x10c4 and dev.idProduct == 0xea60:
                return (bus.dirname, dev.filename)
    
    return False

def searchSYS():
    devices = []
    for device in os.listdir('/sys/bus/usb-serial/devices'):
        devices.append(device)
    return devices

def testDevice(name):
    ret = []

    # Open with python serial
    ser = serial.Serial("/dev/%s" % name, 38400, timeout=1)
    ser.write("ATI\r\n")
    out = ser.read(74)
    if out:
        formatedOut = out.split("\r\n")
        if len(formatedOut) == 5:
            vendor  = formatedOut[1]
            name    = formatedOut[2]
            version = formatedOut[3]
            mac     = formatedOut[4]

            if vendor == 'ATMEL' and name == 'ZIGBIT':
                return (vendor, name, version, mac)
            else:
                return False
    else:
        return False

def findDevices():
    info    = searchUSB()
    devices = []
    if info:
        for device in searchSYS():
            info = testDevice(device)
            if info:
                devices.append((device, info))

        return devices
    else:
        return false


print findDevices()
