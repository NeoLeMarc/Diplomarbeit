#!/usr/bin/env python
import usb

for bus in usb.busses():
    for dev in bus.devices:
        print "Bus %s Device %s, ID: %04x:%04x %s" % (bus.dirname, dev.filename, dev.idVendor, dev.idProduct, dev.open().getString(1, 30))

