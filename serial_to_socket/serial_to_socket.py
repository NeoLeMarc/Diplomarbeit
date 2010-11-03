#!/usr/bin/env python -OO
# -*- coding: utf-8
# MANV-Sensor - Serial to socket
# for Java to ZigBit connector 

PORT = 4711 ## Port für Socketverbindung.
import serial, socket, select, sys, SocketServer, time

class MANVSerialToSocketHandler(SocketServer.BaseRequestHandler):

    def handle(self):
        ## Handler-Hook, in den der SocketServer einsteigt
        print "Connection to socket opened"
        self.openSerial()

        print "Serial connected"
        self.tunnel()

    def openSerial(self):
        ## Serielle Schnittstelle öffnen.
        ## Welche das genau ist, wird auf der Kommandozeile als 
        ## Parameter übergeben.
        self.serial = serial.Serial(sys.argv[1], 38400, timeout=1)

    def shutdown(self):
        ## Socket-Verbidung wird geschlossen -> aufräumen
        self.serial.close()

    def tunnel(self):
        ## Beginn des Tunnelmodus.
        ## Weiterleiten von Daten:
        ## Socket -> Serial
        ## Serial -> Socket
        end = False
        print "Entering tunnel mode"

        while not end:
            ## Select verwenden, um socket und seriellen Port ressourcensparend
            ## zu überwachen. Funktioniert leider nur unter UNIX.
            readylist = select.select((self.request, self.serial), (), ())

            ## Behandeln alle empfangenen Daten
            for readysocket in readylist[0]:

                ## Fallunterscheidung zwischen seriellem Port und Socket,
                ## da hier die Semantik unterschiedlich ist.
                if readysocket == self.serial:

                    ## Daten  wurden auf seriellem Port empfangen
                    ## -> auf socket schreiben.
                    data = self.serial.read(1024)
                    self.request.send(data)

                elif readysocket == self.request:

                    ## Daten wurden auf Socket empfangen
                    ## -> auf seriellen Port schreiben
                    data = self.request.recv(1024)

                    if data:
                        self.serial.write(data)

                    else:
                        ## EOF empfangen -> shutdown
                        end = True

                ## select hat etwas zurück geliefert, das weder Socket noch
                ## serieller Port ist -> shutdown
                else:
                    data = ""
                    end = True

                ## Daten zu Debuggingzwecken auf Konsole ausgeben.
                print data

        ## Aufräumen: Seriellen Port schliessen 
        self.serial.close()
        self.request.close()
        print "Leaving tunnel mode"


class ReusableServer(SocketServer.TCPServer):

    def server_bind(self):
        ## Socket wiederverwendbar machen.
        ## Wenn man das nicht tut, ist er nach Beenden des Programms
        ## für 5 Minuten nicht ansprechbar.
        self.socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        self.socket.bind(self.server_address)

## Einstiegspunkt 
if __name__ == '__main__':
      server = ReusableServer(('localhost', PORT), MANVSerialToSocketHandler)
      server.serve_forever()
