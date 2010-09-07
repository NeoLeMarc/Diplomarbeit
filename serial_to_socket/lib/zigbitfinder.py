#!/usr/bin/env python
import usb, os, serial

class UserInterface:

    def printStatus(self, text):
        pass


class TextUI(UserInterface):

    def printStatus(self, text):
        print "Status: %s" % text


class ZigBitFinder:

    def __init__(self, ui):

        ## User-Interface
        self.ui = ui

    def searchUSB(self):
        ## Search for device on USB-Bus
        for bus in usb.busses():
            for dev in bus.devices:
                if dev.idVendor == 0x10c4 and dev.idProduct == 0xea60:
                    
                    ## Debug Information
                    self.ui.printStatus("Found on USB-Bus: Bus: %s Device: %s ID: %04x:%04x" % (bus.dirname, dev.filename, dev.idVendor, dev.idProduct))

                    return (bus.dirname, dev.filename)
       
        self.ui.printStatus("No devices found on USB-Bus.")
        return False


    def searchSYS(self):
        ## Locate all usb-serial devices in /sys
        devices = []

        self.ui.printStatus("Searching in /sys")

        for device in os.listdir('/sys/bus/usb-serial/devices'):
            self.ui.printStatus("Found device: %s" % str(device))
            devices.append(device)

        return devices


    def testDevice(self, deviceName):
        ret = []

        # Open with python serial and test device-identification
        try:
            self.ui.printStatus("Testing device %s" % deviceName)
            ser = serial.Serial("/dev/%s" % deviceName, 38400, timeout=3)
            ser.write("ATI\r\n")
            out = ser.read(74)
            
            ## Compare return value for matching vendor strings etc.
            if out:

                formatedOut = out.split("\r\n")

                if len(formatedOut) == 5:
                    vendor  = formatedOut[1]
                    name    = formatedOut[2]
                    version = formatedOut[3]
                    mac     = formatedOut[4]

                    if vendor == 'ATMEL' and name == 'ZIGBIT':
                        self.ui.printStatus("Successfully tested device %s" % deviceName)
                        return (vendor, name, version, mac)
                    else:
                        self.ui.printStatus("Device %s does not match identification string" % name)
                        return False
            else:
                self.ui.printStatus("No response from device %s" % deviceName)
                return False

        except serial.SerialException:
            self.ui.printStatus("SerialException while opening device: %s", deviceName)
            return False

    def findDevices(self):

        ## Search for an USB-Device and return a list of all ZigBit Devices

        # 1. Search on USB-Bus
        info    = self.searchUSB()
        devices = []

        if info:
            for device in self.searchSYS():

                # 2. Test device for identificatin string
                info = self.testDevice(device)

                if info:

                    # Append the device to device list if test
                    # was successful.
                    devices.append((device, info))

            return devices
        else:
            return False


if __name__ == "__main__":
    z = ZigBitFinder(TextUI())
    print z.findDevices()
