package edu.kit.ibt.manv.connector.commands;
import edu.kit.ibt.manv.connector.lib.*;
import edu.kit.ibt.manv.connector.results.*;
import java.util.concurrent.*;

/* ** Commands ** */
public class MANVCommand extends MANVPrioritized {
    protected String command;
    private MANVResult result;
    protected int id = 0;
    protected static int maxId;
    private CountDownLatch resultLatch = new CountDownLatch(1);

    public MANVCommand(String command, int priority){
        this.command  = command;
        this.priority = priority;
        getUniqueId();
    }

    public String getCommand(){
        return this.command;
    }

    synchronized private int getUniqueId(){
        if(this.id == 0){
            this.id = ++this.maxId;
        } 
        return this.id;
    }

    public void setResult(MANVResult result){
        System.out.println("setResult(" + result + "-" + result.hashCode() + ")");
        this.result = result;

        // Open latch, so status can be fetched
        this.resultLatch.countDown();
    }

    public MANVResult getResult(){
        // Wait for result to become available
        boolean successful = false;
        while(!successful){
            try{
                System.out.println("Waiting for latch...");
                this.resultLatch.await();
                successful = true;
                System.out.println("... got latch!");
            } catch (InterruptedException e){
                System.out.println("Interrupted while waiting for latch...");
            }
        }

        return this.result;
    }

}

