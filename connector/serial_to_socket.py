#!/usr/bin/env python -OO
# MANV-Sensor - Serial to socket
# for Java to ZigBit connector 

PORT = 4711
import serial, socket, select, sys, SocketServer

class MANVSerialToSocketHandler(SocketServer.BaseRequestHandler):

    def handle(self):
        print "Connection to socket opened"
        self.openSerial()
        print "Serial connected"
        self.tunnel()

    def openSerial(self):
        self.serial = serial.Serial('/dev/ttyUSB1', 38400, timeout=1)

    def shutdown(self):
        self.serial.close()

    def tunnel(self):
        end = False
        print "Entering tunnel mode"

        while not end:
            readylist = select.select((self.request, self.serial), (), ())

            for readysocket in readylist[0]:
                try:
                    if readysocket == self.serial:
                        self.request.send(self.serial.read(1024))
                    else:
                        self.serial.write(self.request.recv(1024))

                    sys.stdout.write(".")
                    sys.stdout.flush()


                except (socket.error):
                    end = True


        ## Close serial port
        self.shutdown()

        print "Leaving tunnel mode"

class ReusableServer(SocketServer.TCPServer):

    def server_bind(self):
        self.socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        self.socket.bind(self.server_address)

if __name__ == '__main__':

      server = ReusableServer(('localhost', PORT), MANVSerialToSocketHandler)
      server.serve_forever()

    


