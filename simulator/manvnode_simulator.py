#!/usr/bin/env python -OO
# -*- coding: utf-8

## MANV-Node Simulator
## (C) Copyright 2010 by Marcel Noe

import serial, sys, random, time, select


class MANVNodeSimulator(object):


    def __init__(self, port, nodeID = 10):
        ## Verbindung herstellen
        self.serial = serial.Serial(port,      ## Serieller Port wird als Parameter übergeben
                                    38400,     ## Baudrate
                                    timeout=0) ## Kein Timeout
        self.nodeID = nodeID
        self.commandBuffer = []
        self.linebuffer    = ""
        self.alertStatus   = False

        self.commands = {'t' : self.toggleAlert,
                         'd' : self.disableAlert,
                         'e' : self.enableAlert}



    def run(self):

        # 1. ZigBit Modul initialisieren
        print "Beginnning Initialization..."
        self.sendline("ATZ")
        self.sendline("AT+WCHMASK=07FF800")             ## Alle Kanäle
        self.sendline("AT+IFC=0,0")                     ## RTS/CTS deaktivieren
        self.sendline("AT+GSN=%x" % int(self.nodeID))   ## GSN == Global Subscriber Number ~= Mac-Adresse
        self.sendline("AT+WPANID=1620")                 ## Netzwerk 0x1620 verwenden
        self.sendline("AT+WROLE=2")                     ## Rolle 2 == Endnode
        self.sendline("AT+WSRC=%x" % int(self.nodeID))  ## WSRC ~= IP-Adresse
        self.sendline("AT+WAUTONET=1")                  ## Automatisch mit dem Netzwerk Verbinden
        self.sendline("AT+WPWR=100,0")                  ## Powermanagement deaktivieren
        self.sendline("ATX")                            ## Transceiver einschalten
        self.sendline("ATZ")                            ## Reboot
        print "... complete"

        # 2. Status senden
        while True:
            print "Sending Status..."

            if self.alertStatus:
                self.sendline("ATD 0,0\rSTATUS:ALERT:%i:%i\r\n" % (random.randint(0, 10), random.randint(0, 5)))
            else:
                self.sendline("ATD 0,0\rSTATUS:OK:%i:%i\r\n" % (random.randint(60, 120), random.randint(30, 50)))

    def sendline(self, line):
        self.serial.write(line + "\r\n")
        return self.getStatus()

    def recvline(self):
        
        ## Komplette Zeilen auslesen 
        while True:
            try:
                line = self.linebuffer + self.serial.readline()
                line, self.linebuffer = line.split("\n", 1)
                return line
            except:
                self.linebuffer += self.serial.readline()

                if self.linebuffer.find('\n') == -1:
                    ## Warten, bis wirklich Daten vorliegen
                    select.select((self.serial,), (), ())

    def getStatus(self):
        while True:

            line = self.recvline()

            if line:
                print "--> Got Line: " + line

                if line == "OK\r":
                    return True
                elif line == "ERROR\r":
                    return False
                elif line[0:4] == "DATA":
                    self.commandBuffer.append(line)
                    self.handleCommands()

    def handleCommands(self):
        ## Commandbuffer abarbeiten
        command = " "
        while command:
            try:
                command = self.commandBuffer.pop()
                print "**> Command: " + command
            except:
                command = False

            if command:
                muell, command = command.split(':')
                argv = command.split(" ")

                if self.commands.has_key(argv[0]):
                    self.commands[argv[0]](*argv[1:])

    def toggleAlert(self, *args):
        print "+++ Toggling Alert +++"
        self.alertStatus = (self.alertStatus + 1) % 2 

    def disableAlert(self, *args):
        print "+++ Disabling Alert +++"
        self.alertStatus = 0 

    def enableAlert(self, *args):
        print "+++ Enabling Alert +++"
        self.alertStatus = 1


if __name__ == "__main__":

    if len(sys.argv) == 1:
        sys.stderr.write("Error: Not enough arguments\n")
        sys.stderr.write("Usage: %s [port] [nodeID]" % sys.argv[0])
        sys.exit(1)

    else:
        sim = MANVNodeSimulator(sys.argv[1], sys.argv[2])
        sim.run()
