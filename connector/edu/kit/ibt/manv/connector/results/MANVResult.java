package edu.kit.ibt.manv.connector.results;
import edu.kit.ibt.manv.connector.events.*;
import edu.kit.ibt.manv.connector.lib.*;
import edu.kit.ibt.manv.connector.commands.*;
import java.util.*;
import java.util.concurrent.*;

/* ** Results ** */
public class MANVResult extends MANVEvent {
    protected boolean status;
    protected MANVResult subResult;

    public MANVResult(String raw, boolean status){
        super(raw);
        this.status = status;
    }

    public String getData(){
        return this.getRaw();
    }

    public boolean isResult(){
        return true;
    }

    public boolean isImportant(){
        return true;
    }

    public boolean isComposite(){
        return false;
    }

    public iZigBit[] getChildList(BlockingQueue<MANVCommand> commandQueue){
        return null;
    }

    public void addSubResult(MANVResult result){
        this.subResult = result;
    }

    public String toString(){
        return "MANVResult(" + this.getRaw() + ")";
    }

};
