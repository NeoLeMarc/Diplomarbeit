package edu.kit.ibt.manv.connector.events;
import edu.kit.ibt.manv.connector.lib.*;
import edu.kit.ibt.manv.connector.corba.*;
import java.util.*;
import CORBA_Server.*;
import Common.*;
import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NameComponent;

public class MANVChildJoined extends MANVEvent {
    protected iZigBit source;

    public MANVChildJoined(String raw){
        super(raw);
        this.source = new readonlyZigBit(Integer.parseInt(raw.split(" ")[1], 16));
    }

    public boolean isImportant(){
        return true; 
    }

    public AbstractList<CorbaMessageContainer> createCorbaMessages(){
        ArrayList<CorbaMessageContainer> ret = new ArrayList<CorbaMessageContainer>();

        // Create join message 
        CORBA_EventMessage eventMessage = new CORBA_EventMessage();
        eventMessage.groupID   = 1;
        eventMessage.nodeID    = String.valueOf(this.source.getMacID());
        eventMessage.eventType = event_join.value;
        eventMessage.connectorTimestamp = System.currentTimeMillis();

        ret.add(new CorbaEventMessageContainer(eventMessage));
        return ret;
    }

    public String toString(){
        return "MANVRChildJoind(" + this.raw + ")";
    }
};

