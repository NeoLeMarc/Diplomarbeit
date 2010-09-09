package edu.kit.ibt.manv.connector.results;
import edu.kit.ibt.manv.connector.events.*;
import edu.kit.ibt.manv.connector.lib.*;

public class MANVDataReceived extends MANVEvent {
    protected ZigBit source;
    protected String data;


    public MANVDataReceived(String raw){
        super(raw);

        // parse data
        this.parseRaw(raw);
    }

    public boolean isImportant(){
        return true;
    }

    private void parseRaw(String raw){
        String[] splitedRaw = raw.split(":", 2);
        this.data   = splitedRaw[1];
        this.source = ZigBit.get(Integer.parseInt(splitedRaw[0].split(" ")[1].split(",")[0]));
        System.out.println("Data received: Source: " + this.source + " - " + this.data);
    }
};

