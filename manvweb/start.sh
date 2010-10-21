#javac -Xlint:unchecked -cp ~/diplomarbeit/jan/manv/MANVServer/lib/jacorb.jar:idl/ MANVConnector.java CommandsImpl.java
#CLASSPATH=".:~/diplomarbeit/jan/manv/MANVCommon/lib/jacorb.jar:~/diplomarbeit/jan/manv/MANVCommon/lib/log4j-1.2.9.jar:~/diplomarbeit/jan/manv/MANVCommon/idl/:~/diplomarbeit/jan/manv/MANVCommon/idl/CORBA_Server:~/diplomarbeit/jan/manv/MANVCommon/lib/slf4j-api-1.5.8.jar:~/diplomarbeit/jan/manv/MANVCommon/MANVCommon.jar:~/diplomarbeit/jan/manv/MANVCommon/"
CLASSPATH=~/diplomarbeit/jan/manv/MANVServer/lib/jacorb.jar:/home/marcel/diplomarbeit/jan/manv/MANVCommon/idl/:~/diplomarbeit/jan/manv/MANVCommon/MANVCommon.jar:~/diplomarbeit/jan/manv/MANVCommon/lib/log4j-1.2.9.jar:~/diplomarbeit/jan/manv/MANVDevGUI/program/manvGui.jar:.
case $1 in 
    build) 
        CMD="javac -cp $CLASSPATH *.java"
        $CMD
        echo $CMD
    ;;

    start)
	    CMD="java -cp $CLASSPATH MANVWeb -ORBInitRef NameService=corbaloc::ibt-vm:1771/NameService -ORBiiop.port=1771"
        echo $CMD
        $CMD
    ;;

    *)
        echo "Usage: $0 [start|build]"
    ;;            
esac
