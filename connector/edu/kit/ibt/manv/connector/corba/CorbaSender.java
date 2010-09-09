package edu.kit.ibt.manv.connector.corba;
import edu.kit.ibt.manv.connector.events.*;
import java.util.concurrent.*;
import CORBA_Server.*;
import Common.*;
import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NameComponent;


public class CorbaSender extends Thread {
    private BlockingQueue<MANVEvent> eventQueue;
    private Incoming serverIncoming;

    public CorbaSender(BlockingQueue<MANVEvent> eventQueue, Incoming serverIncoming){
        this.eventQueue     = eventQueue;
        this.serverIncoming = serverIncoming;
    }

    @Override public void run(){
        // Send events to corba
        MANVEvent   event;
        try {
            while((event = this.eventQueue.take()) != null){
                if(event.isImportant()){
                    for(CorbaMessageContainer corbaMessage : event.createCorbaMessages()){
                        corbaMessage.send(this.serverIncoming);
                        System.out.println("!! Sent corba message: " + corbaMessage + " !!");
                    }
                }
            }
        } catch (InterruptedException e){
            System.out.println("Interrupted while processing event... discarding");
        }
    }
}
