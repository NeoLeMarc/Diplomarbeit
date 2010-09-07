#!/bin/bash
# Der Inhalt dieser Datei wird ausgeführt, sobald die Erkennung abgeschlossen
# ist. Der Erste Parameter ist der Name des seriellen Devices.
echo "Hello World: $1"
python serial_to_socket.py /dev/$1
