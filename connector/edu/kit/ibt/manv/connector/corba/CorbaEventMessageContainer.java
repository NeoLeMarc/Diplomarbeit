package edu.kit.ibt.manv.connector.corba;
import CORBA_Server.*;
import Common.*;
import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NameComponent;

public class CorbaEventMessageContainer extends CorbaMessageContainer{
    private CORBA_EventMessage message;

    public CorbaEventMessageContainer (CORBA_EventMessage message){
        this.message = message;
    }

    public void send(Incoming serverIncoming){
        serverIncoming.notify_event(this.message);
    }

    public String toString(){
        return this.message.toString();
    }
}
