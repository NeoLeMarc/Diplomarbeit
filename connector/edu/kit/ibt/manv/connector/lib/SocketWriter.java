package edu.kit.ibt.manv.connector.lib;
import edu.kit.ibt.manv.connector.commands.*;
import edu.kit.ibt.manv.connector.results.*;
import java.util.concurrent.*;
import java.io.*;

public class SocketWriter extends Thread{
    private BlockingQueue<MANVCommand> commandQueue;
    private BlockingQueue<MANVCommand> resultQueue;
    private PrintWriter                serialOut;
    private SocketReader               reader;
    private MANVResult lastResult; 

    public SocketWriter(PrintWriter serialOut, BlockingQueue<MANVCommand> commandQueue, BlockingQueue<MANVCommand> resultQueue, SocketReader reader){
        this.serialOut    = serialOut;
        this.commandQueue = commandQueue;
        this.resultQueue  = resultQueue;
        this.reader       = reader;
    }

    private void writeLine(String line){
        this.serialOut.print(line + "\r\n");
        this.serialOut.flush();
//        System.out.println("Waiting for result");
        this.lastResult = this.reader.getLastResult();
//        System.out.println("Got result");
    }

    @Override 
    public void run(){
        MANVCommand command;
        System.out.println("Starting SocketWriter with PrintWriter: " + this.serialOut);

        // Initialize ZigBee adapter
        this.writeLine("AT+WLEAVE");
        this.writeLine("AT+WAUTONET=1 +WWAIT=100 Z");
        this.writeLine("ATS30=1");

        // Serve command Queue
        while(true){
            try {
                // Get next command & execute
                command = this.commandQueue.take();
                this.writeLine(command.getCommand());

                // Get result
                command.setResult(this.lastResult);

                // Write back to result queue
                resultQueue.put(command);

            } catch (InterruptedException e){
                // Try again
            }

        }

    }
}
