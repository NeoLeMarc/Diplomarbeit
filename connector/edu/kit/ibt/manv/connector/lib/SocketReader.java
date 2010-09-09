package edu.kit.ibt.manv.connector.lib;
import edu.kit.ibt.manv.connector.events.*;
import edu.kit.ibt.manv.connector.results.*;
import java.util.concurrent.*;
import java.util.*;
import java.io.*;

public class SocketReader extends Thread{
    private BlockingQueue<MANVEvent> eventQueue;
    private MANVResult               lastResult;
    private Semaphore                resultSemaphore = new Semaphore(1, true);
    private BufferedReader           serialIn; 

    public SocketReader(BufferedReader serialIn, BlockingQueue<MANVEvent> eventQueue){
        this.serialIn   = serialIn;
        this.eventQueue = eventQueue;
        this.resultSemaphore.acquireUninterruptibly();
    }

    private void setLastResult(MANVResult result){
        if(this.lastResult != null && this.lastResult.isComposite())
            this.lastResult.addSubResult(result);
        else
            this.lastResult = result; 

        if(!result.isComposite())
            resultSemaphore.release();
    }

    public MANVResult getLastResult(){
        this.resultSemaphore.acquireUninterruptibly();
        return this.lastResult;
    }

    @Override 
    public void run(){
        System.out.println("Starting SocketReader with BufferedReader: " + this.serialIn);
        String line;
        Boolean successful;
        MANVEvent event;

        try {
            while((line = this.serialIn.readLine()) != null){
                // Skip empty lines
                if(line.equals(""))
                    continue;

                event = MANVEvent.fromString(line);

                if(event.isResult()){
                    //System.out.println("** RESULT **: " + line);
                    this.setLastResult((MANVResult)event);
                } else if(event.isImportant()) {
                    System.out.println("** EVENT **: " + line);

                    // Do not lose events!
                    do {
                        successful = false;

                        try {
                            eventQueue.put(event);
                            successful = true;
                        } catch (InterruptedException e) {
                            successful = false;
                        }
                    } while (!successful);
                } else {
                    System.out.println("Discarding unimportant event: " + event);
                }
            }
        } catch (IOException e) {
            System.err.println("IOException in SocketReader");
            System.exit(1);
        }
    }

}
