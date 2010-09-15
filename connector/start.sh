#javac -Xlint:unchecked -cp ~/diplomarbeit/jan/manv/MANVServer/lib/jacorb.jar:idl/ MANVConnector.java CommandsImpl.java
case $1 in 
    build) 
        javac -cp ~/diplomarbeit/jan/manv/MANVServer/lib/jacorb.jar:/home/marcel/diplomarbeit/jan/manv/MANVCommon/idl/ edu/kit/ibt/manv/connector/MANVConnector.java  edu/kit/ibt/manv/connector/**/*.java 
    ;;

    start)
	    java -cp .:~/diplomarbeit/jan/manv/MANVServer/lib/jacorb.jar:idl/:idl/Common/:idl/CORBA_Server/:/home/ibt/diplomarbeit_remote/jan/manv/MANVCommon/idl/ edu.kit.ibt.manv.connector.MANVConnector -ORBInitRef NameService=corbaloc::ibt-vm:1771/NameService -ORBiiop.port=1771 
    ;;

    *)
        echo "Usage: $0 [start|build]"
    ;;            
esac
