#javac -Xlint:unchecked -cp ~/diplomarbeit/jan/manv/MANVServer/lib/jacorb.jar:idl/ MANVConnector.java CommandsImpl.java
case $1 in 
    build) 
        javac -cp ~/diplomarbeit/jan/manv/MANVServer/lib/jacorb.jar:idl/ -d ~/diplomarbeit/connector edu/kit/ibt/manv/connector/MANVConnector.java edu/kit/ibt/manv/connector/CommandsImpl.java
    ;;

    start)
	    java -cp .:~/diplomarbeit/jan/manv/MANVServer/lib/jacorb.jar:idl/:idl/Common/:idl/CORBA_Server/ edu.kit.ibt.manv.connector.MANVConnector -ORBInitRef NameService=corbaloc::ibt-vm:1771/NameService -ORBiiop.port=1771 
    ;;

    *)
        echo "Usage: $0 [start|build]"
    ;;            
esac
