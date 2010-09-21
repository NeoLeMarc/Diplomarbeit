package edu.kit.ibt.manv.connector.events;
import edu.kit.ibt.manv.connector.lib.*;
import edu.kit.ibt.manv.connector.corba.*;
import edu.kit.ibt.manv.connector.results.*;
import java.util.*;

/* *** Events *** **/
public class MANVEvent extends MANVPrioritized {
    private String raw;
    
    public MANVEvent(String raw){
        this.raw = raw;
    }

    public boolean isResult(){
        return false;
    }

    public boolean isImportant(){
        return false;
    }


    public static MANVEvent fromString(String raw){
        if(raw.equals("OK"))
            return new MANVResult(raw, true);
        else if(raw.equals("ERROR"))
            return new MANVResult(raw, false);
        else if(raw.matches("DATA .*:STATUS:.*:.*"))
            return new MANVStatusMessage(raw);
        else if(raw.matches("DATA .*:.*"))
            return new MANVDataReceived(raw);
        else if(raw.matches("EVENT:CHILD_JOINED .*"))
            return new MANVChildJoined(raw);
        else if(raw.matches("EVENT:CHILD_LOST .*"))
            return new MANVChildLost(raw);
        else if (raw.matches("\\+WCHILDREN:.*"))
            return new MANVChildrenList(raw);
        else if (raw.matches("\\+GSN:.*")){
            return new MANVGsn(raw);
        }
        else 
            return new MANVEvent(raw);
    }

    public AbstractList<CorbaMessageContainer> createCorbaMessages(){
        return null; 
    }

    public String toString(){
        return "MANVEvent(" + this.getRaw() + ")";
    }

    public String getRaw(){
        return this.raw;
    }
}

