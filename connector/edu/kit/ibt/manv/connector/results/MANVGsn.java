package edu.kit.ibt.manv.connector.results;
import edu.kit.ibt.manv.connector.commands.*;
import edu.kit.ibt.manv.connector.lib.*;
import java.util.concurrent.*;

public class MANVGsn extends MANVResult {
    String myRaw;

    public MANVGsn(String raw){
        super(raw, true);
        this.myRaw = raw;
    }

    public boolean isComposite(){
        // Expecting an OK or ERROR to follow
        return true;
    }

    public String getData(){
        // Extract gsn from raw data
        String[] splited = this.getRaw().split(":");

        if(splited.length == 2)
            return splited[1];
        else
            return "0"; 
    }

    public void addSubResult(MANVResult result){
        this.status    = result.status;
        this.subResult = result;
    }
}
