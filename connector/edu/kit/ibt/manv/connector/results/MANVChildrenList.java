package edu.kit.ibt.manv.connector.results;
import edu.kit.ibt.manv.connector.commands.*;
import edu.kit.ibt.manv.connector.lib.*;
import java.util.concurrent.*;

public class MANVChildrenList extends MANVResult {

    public MANVChildrenList(String raw){
        super(raw, true);
    }

    public boolean isComposite(){
        // Expecting an OK or ERROR to follow
        return true;
    }

    public iZigBit[] getChildList(BlockingQueue<MANVCommand> commandQueue){
        // Parse return value
        String[] panIdList = this.getRaw().split(":")[1].split(",");
        iZigBit[] childList = new iZigBit[panIdList.length];

        for(int pos = 0; pos < panIdList.length; pos++)
            childList[pos] = ZigBit.get(Integer.parseInt(panIdList[pos], 16)); 

        return childList;
    }

    public void addSubResult(MANVResult result){
        this.status    = result.status;
        this.subResult = result;
    }
}
