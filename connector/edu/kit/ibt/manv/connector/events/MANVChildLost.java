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


public class MANVChildLost extends MANVEvent {
    protected iZigBit source;

    public MANVChildLost(String raw){
        super(raw);
        this.source = ZigBit.get(Integer.parseInt(raw.split(" ")[1]));
    }

    public boolean isImportant(){
        return true;
    }

    public AbstractList<CorbaMessageContainer> createCorbaMessages(){
        ArrayList<CorbaMessageContainer> ret = new ArrayList<CorbaMessageContainer>();

        // Create lost message 
        CORBA_EventMessage eventMessage = new CORBA_EventMessage();
        eventMessage.groupID   = 1;
        eventMessage.nodeID    = String.valueOf(this.source.getMacID());
        eventMessage.eType = event_lost.value;
        eventMessage.connectorTimestamp = System.currentTimeMillis();

        ret.add(new CorbaEventMessageContainer(eventMessage));
        return ret;
    }

    public String toString(){
        return "MANVRChildLost(" + this.raw + ")";
    }

};
