package edu.kit.ibt.manv.connector.corba;
import CORBA_Server.*;
import Common.*;
import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NameComponent;


public class CorbaDataMessageContainer extends CorbaMessageContainer{
    private CORBA_DataMessage message;

    public CorbaDataMessageContainer (CORBA_DataMessage message){
        this.message = message;
    }

    public void send(Incoming serverIncoming){
        serverIncoming.notify_data(this.message);
    }

    public String toString(){
        return this.message.toString();
    }
}
