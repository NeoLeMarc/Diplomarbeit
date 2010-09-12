package edu.kit.ibt.manv.connector.lib;
import edu.kit.ibt.manv.connector.commands.*;
import edu.kit.ibt.manv.connector.results.*;
import java.util.*;
import java.util.concurrent.*;

public class ZigBit {
    private int nodeID  = 0;
    private int macID   = 0;

    private int[] gpio = {0, 0, 0, 0};

    // Singleton
    private static HashMap<Integer, ZigBit> zigBitMap = new HashMap<Integer, ZigBit>();

    // Command queue
    private static BlockingQueue<MANVCommand> commandQueue;

    ZigBit(int nodeID){
        this.commandQueue = commandQueue;
        this.nodeID        = nodeID;
    }

    public int getNodeID(){
        return this.nodeID;
    }

    public int getMacID(){
        if(this.macID == 0)
            this.requestMacID();

        return this.macID;
    }

    public static void setCommandQueue(BlockingQueue<MANVCommand> commandQueue){
        ZigBit.commandQueue = commandQueue;
    }

    public static ZigBit get(int nodeID){
        ZigBit z = zigBitMap.get(nodeID);
        if(z != null)
            return z;
        else {
            z = new ZigBit(nodeID);
            zigBitMap.put(nodeID, z);
            return z;
        }
    }


    public void GPIOenable(int nr){
        this.gpio[nr] = 1;
    }

    public void GPIOdisable(int nr){
        this.gpio[nr] = 0;
    }

    public void update() throws java.io.IOException{
        boolean successful   = false;
        String commandString = "";
        commandString += "ATR " + this.nodeID + ",0,";

        for(int i = 0; i < gpio.length; i++)
            commandString += " S13" + i + "=" + this.gpio[i];

        // Do not lose commands!
        do {
            successful = false;

            try {
                this.commandQueue.put(new MANVCommand(commandString, 1));
                successful = true;
            } catch (InterruptedException e) {
                successful = false;
            }
        } while (!successful);
    }

    public static ZigBit[] discover() throws InterruptedException{
        // Send discovery command
        MANVCommand command = new MANVCommand("ATS30=1+WCHILDREN?", 0);
        commandQueue.put(command);

        // Get result & return childList
        MANVResult result = command.getResult();
        return result.getChildList(ZigBit.commandQueue);
    }

    public MANVResult sendData(String data) throws InterruptedException{
        // Send data command
        MANVCommand command = new MANVCommand("ATD " + this.nodeID + "\r" + data + "\r", 10);
        commandQueue.put(command);

        // Get result & return status
        return command.getResult();
    }

    public MANVResult toggleAlertStatus(){
        String  data = "t 123";
        boolean successful = false;
        MANVResult result = null;

        do {
            try {
                result = this.sendData(data);
                successful = true;
            } catch (InterruptedException e) {
                successful = false;
            }
        } while(!successful);

        return result;
    }

    public void requestMacID(){
        // If we only have the short ID but need to have the MAC 
        // address, we have to ask the node
        //
        boolean successful = false;
        
        do {
            try {
                System.out.println("Requesting MAC-Addr for Node " + this.nodeID);
                MANVCommand command = new MANVCommand("ATR " + Integer.toHexString(this.nodeID) + ",0,+GSN?\r", 0);
                commandQueue.put(command);
                MANVResult result = command.getResult();
                String data = result.getData();

                if(result.isComposite() && data != "0"){
                    this.macID = Integer.parseInt(data);
                }

                this.macID = 3;
                successful = true;
                System.out.println("Got MAC-Addr for Node " + this.nodeID + ": " + this.macID);

            } catch (InterruptedException e) {
                successful = false;
                System.err.println("Interrupted exception while requesting mac ID");
            }

        } while(!successful);
    }
}
