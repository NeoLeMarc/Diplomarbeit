package edu.kit.ibt.manv.connector.results;
import edu.kit.ibt.manv.connector.corba.*;
import edu.kit.ibt.manv.connector.events.*;
import java.util.*;
import CORBA_Server.*;
import Common.*;
import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NameComponent;


public class MANVStatusMessage extends MANVDataReceived {
    protected short pulse;
    protected short breathing;

    public MANVStatusMessage(String raw){
        super(raw);

        // parse data
        this.parseRaw(raw);
    }

    private void parseRaw(String raw){
        String[] splitedRaw = raw.split(":", 5);
        this.pulse     = Short.parseShort(splitedRaw[3]);
        this.breathing = Short.parseShort(splitedRaw[4]);
        System.out.println("Pulse is: " + this.pulse + " - Breathing is: " + this.breathing);
    }

    public AbstractList<CorbaMessageContainer> createCorbaMessages(){
        ArrayList<CorbaMessageContainer> ret = new ArrayList<CorbaMessageContainer>();

        // Create Data Message
        CORBA_DataMessage dataMessage  = new CORBA_DataMessage();
        dataMessage.groupID            = 1;
        dataMessage.nodeID             = String.valueOf(this.source.getMacID());
        dataMessage.pulse              = this.pulse;
        dataMessage.breathing          = this.breathing; 
        dataMessage.connectorTimestamp = System.currentTimeMillis();
        ret.add(new CorbaDataMessageContainer(dataMessage));

        // If Status is error, we also have to create an event message
        if(this.data.matches("STATUS: ERROR:.*")){
            // Create alert message
            CORBA_EventMessage eventMessage = new CORBA_EventMessage();
            eventMessage.groupID            = 1;
            eventMessage.nodeID             = String.valueOf(this.source.getMacID());
            eventMessage.eventType          = event_alarm_breathing.value;
            eventMessage.connectorTimestamp = System.currentTimeMillis();

            ret.add(new CorbaEventMessageContainer(eventMessage));
         }

         return ret;
    }

    public String toString(){
        return "MANVRStatusMessage(" + this.raw + ")";
    }

}
