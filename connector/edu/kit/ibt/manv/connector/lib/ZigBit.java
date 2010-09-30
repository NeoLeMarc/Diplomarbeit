package edu.kit.ibt.manv.connector.lib;
import edu.kit.ibt.manv.connector.lib.*;
import edu.kit.ibt.manv.connector.commands.*;
import edu.kit.ibt.manv.connector.results.*;
import java.util.*;
import java.util.concurrent.*;

public class ZigBit implements iZigBit {
    private int nodeID  = 0;
    private int macID   = 0;

    private int[] gpio = {0, 0, 0, 0};

    // Singleton
    private static HashMap<Integer, ZigBit> zigBitMap = new HashMap<Integer, ZigBit>();
    private static HashMap<Integer, Integer> macMap = new HashMap<Integer, Integer>();

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

    public static iZigBit get(int nodeID){
        ZigBit z = zigBitMap.get(nodeID);
        if(z != null)
            return z;
        else {
            z = new ZigBit(nodeID);
            zigBitMap.put(nodeID, z);
            return z;
        }
    }

    public static iZigBit getByMacID(int macID){
        int nodeID = macMap.get(macID);
        if(nodeID != 0)
            return ZigBit.get(nodeID);
        else
            return new readonlyZigBit(nodeID); 
    }

    /*
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
    */

    public static iZigBit[] discover() throws InterruptedException{
        // Send discovery command
        MANVCommand command = new MANVCommand("ATS30=1+WCHILDREN?", 0);
        commandQueue.put(command);

        // Get result & return childList
        MANVResult result = command.getResult();
        return result.getChildList(ZigBit.commandQueue);
    }

    public MANVResult sendData(String data) throws InterruptedException{
        // Send data command
        MANVCommand command = new MANVCommand("ATD " + Integer.toHexString(this.nodeID) + ",0\r" + data + "\r", 10);
        commandQueue.put(command);

        // Get result & return status
        return command.getResult();
    }

    public void isendData(String data){
        // Send data command and do not fetch result
        try {
            MANVCommand command = new MANVCommand("ATD " + Integer.toHexString(this.nodeID) + ",0\r" + data + "\r", 10);
            commandQueue.put(command);
        } catch (InterruptedException e) {
        }
    }


    private MANVResult sendUntilSuccess(String data){
        boolean successful;
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

    public MANVResult toggleAlertStatus(){
        return this.sendUntilSuccess("t 1"); 
    }

    public MANVResult enableAlert(){
        return this.sendUntilSuccess("e 1"); 
    }

    public MANVResult disableAlert(){
        return this.sendUntilSuccess("d 1"); 
    }

    public MANVResult muteAlert(){
        return this.sendUntilSuccess("m 1"); 
    }

    public void itoggleAlertStatus(){
        this.isendData("t 1"); 
    }

    public void ienableAlert(){
        this.isendData("e 1"); 
    }

    public void idisableAlert(){
        this.isendData("d 1"); 
    }

    public void imuteAlert(){
        this.isendData("m 1"); 
    }



    private void requestMacID(){
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
                    this.macID = Integer.parseInt(data, 16);
                    successful = true;
                }

                System.out.println("Got MAC-Addr for Node " + this.nodeID + ": " + this.macID);

            } catch (InterruptedException e) {
                successful = false;
                System.err.println("Interrupted exception while requesting mac ID");
            }

        } while(!successful);

        // Update Hashmap
        macMap.put(this.macID, this.nodeID);
    }
}
