MARVConnector.class: MANVConnector.java CommandsImpl.java
	#javac -Xlint:unchecked -cp ~/diplomarbeit/jan/manv/MANVServer/lib/jacorb.jar:idl/ MANVConnector.java CommandsImpl.java
	javac -cp ~/diplomarbeit/jan/manv/MANVServer/lib/jacorb.jar:idl/ MANVConnector.java CommandsImpl.java


test: MANVConnector.class
	java -cp .:~/diplomarbeit/jan/manv/MANVServer/lib/jacorb.jar:idl/:idl/Common/:idl/CORBA_Server/ edu.kit.ibt.manv.connector.MANVConnector -ORBInitRef NameService=corbaloc::hermes:1771/NameService -ORBiiop.port=1771 

all: MANVConnector.class
