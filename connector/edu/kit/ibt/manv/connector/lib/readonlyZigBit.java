package edu.kit.ibt.manv.connector.lib;
import edu.kit.ibt.manv.connector.results.*;

public class readonlyZigBit implements iZigBit{
    private int id = 0;

    public readonlyZigBit(int id){
        this.id = id;
    }

    public int getNodeID(){
        return -1;
    }

    public int getMacID(){
        return this.id;
    }

    public MANVResult sendData(String data){
        return new MANVResult("ERROR", false);
    }

    public MANVResult toggleAlertStatus(){
        return new MANVResult("ERROR", false);
    }

    public MANVResult disableAlert(){
        return new MANVResult("ERROR", false);
    }

    public MANVResult enableAlert(){
        return new MANVResult("ERROR", false);
    }

    public MANVResult muteAlert(){
        return new MANVResult("ERROR", false);
    }
}
