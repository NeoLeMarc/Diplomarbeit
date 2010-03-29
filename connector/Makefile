MARVConnector.class: MARVConnector.java
	javac -Xlint:unchecked -cp ~/diplomarbeit/jan/manv/MANVServer/lib/jacorb.jar:idl/ MARVConnector.java

test: MARVConnector.class
	java -cp .:~/diplomarbeit/jan/manv/MANVServer/lib/jacorb.jar:idl/:idl/Common/:idl/CORBA_Server/ MARVConnector -ORBInitRef NameService=corbaloc::hermes:1771/NameService -ORBiiop.port=1771 

all: MARVConnector.class
