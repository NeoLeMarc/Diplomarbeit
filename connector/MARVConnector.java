import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.*;

class MARVPrioritized implements Comparable {
    protected int priority;

    public int compareTo(Object o){
        return 0;
    }

    public int compareTo(MARVPrioritized o){
        return this.priority - o.priority;
    }

}

class MARVCommand extends MARVPrioritized {
    protected String command;
    protected boolean result;
    protected int id = 0;
    static int maxId;

    MARVCommand(String command, int priority){
        this.command  = command;
        this.priority = priority;
        getUniqueId();
    }

    synchronized private int getUniqueId(){
        if(this.id == 0){
            this.id = ++this.maxId;
        } 
        return this.id;
    }

    public void setResult(boolean result){
        this.result = result;
    }

}

class MARVEvent extends MARVPrioritized {
    protected String raw;
    
    MARVEvent(String raw){
        this.raw = raw;
    }

    public static MARVEvent fromString(String raw){
        if(raw.matches("EVENT:CHILD_JOINED .*"))
            return new MARVChildJoined(raw);
        else
            return new MARVEvent(raw);
    }
}

class MARVChildJoined extends MARVEvent {

    MARVChildJoined(String raw){
        super(raw);
    }
};

class ZigBit {
    int panID;
    BlockingQueue<MARVCommand> commandQueue;
    int[] gpio = {0, 0, 0, 0};

    ZigBit(BlockingQueue<MARVCommand> commandQueue, int panID){
        this.commandQueue = commandQueue;
        this.panID        = panID;
    }

    public void GPIOenable(int nr){
        this.gpio[nr] = 1;
    }

    public void GPIOdisable(int nr){
        this.gpio[nr] = 0;
    }

    public void update() throws java.io.IOException{
        String commandString = "";
        commandString += "ATR " + this.panID + ",0,";

        for(int i = 0; i < gpio.length; i++)
            commandString += " S13" + i + "=" + this.gpio[i];

        try{
            this.commandQueue.put(new MARVCommand(commandString, 1));
        } catch (InterruptedException e) {
        }

    }
}

class SocketReader extends Thread{
    private BlockingQueue<MARVEvent> eventQueue;
    private boolean                  lastResult;
    private Semaphore                resultSemaphore = new Semaphore(1, true);
    private BufferedReader           serialIn; 

    SocketReader(BufferedReader serialIn, BlockingQueue<MARVEvent> eventQueue){
        this.serialIn   = serialIn;
        this.eventQueue = eventQueue;
        this.resultSemaphore.acquireUninterruptibly();
    }

    private void setLastResult(boolean result){
        this.lastResult = result; 
        resultSemaphore.release();
    }

    public boolean getLastResult(){
        this.resultSemaphore.acquireUninterruptibly();
        return this.lastResult;
    }

    @Override public void run(){
        System.out.println("Starting SocketReader with BufferedReader: " + this.serialIn);
        String line;
        Boolean successful;

        try {
            while((line = this.serialIn.readLine()) != null){
                System.out.println("SocketReader: " + line);

                if(line.equals("OK")){
                    System.out.println("Got result");
                    this.setLastResult(true);
                } else if (line.equals("ERROR")){
                    System.out.println("Got result");
                    this.setLastResult(false);
                } else if (line.matches("EVENT:.*")){
                    System.out.println("** EVENT **: " + line);

                    // Do not lose events!
                    do {
                        successful = false;

                        try {
                            eventQueue.put(MARVEvent.fromString(line));
                            successful = true;
                        } catch (InterruptedException e) {
                            successful = false;
                        }
                    } while (!successful);
                }
            }
        } catch (IOException e) {
            System.err.println("IOException in SocketReader");
            System.exit(1);
        }
    }

}

class SocketWriter extends Thread{
    private BlockingQueue<MARVCommand> commandQueue;
    private BlockingQueue<MARVCommand> resultQueue;
    private PrintWriter                serialOut;
    private SocketReader               reader;
    boolean lastResult; 

    SocketWriter(PrintWriter serialOut, BlockingQueue<MARVCommand> commandQueue, BlockingQueue<MARVCommand> resultQueue, SocketReader reader){
        this.serialOut    = serialOut;
        this.commandQueue = commandQueue;
        this.resultQueue  = resultQueue;
        this.reader       = reader;
    }

    private void writeLine(String line){
        this.serialOut.print(line + "\r\n");
        this.serialOut.flush();
        this.lastResult = this.reader.getLastResult();
    }

    @Override public void run(){
        MARVCommand command;
        System.out.println("Starting SocketWriter with PrintWriter: " + this.serialOut);

        // Initialize ZigBee adapter
        this.writeLine("AT+WAUTONET=1 +WWAIT=100 Z");
        this.writeLine("ATS30=1");

        // Serve command Queue
        while(true){
            try {
                // Get next command & execute
                command = this.commandQueue.take();
                this.writeLine(command.command);

                // Get result
                command.setResult(this.lastResult);

                // Write back to result queue
                resultQueue.put(command);

            } catch (InterruptedException e){
            }

        }

    }
}

public class MARVConnector {
    public static void main(String[] args) throws IOException, InterruptedException{

        // Create Queues
        BlockingQueue<MARVCommand> commandQueue = new PriorityBlockingQueue<MARVCommand>();
        BlockingQueue<MARVEvent>   eventQueue   = new PriorityBlockingQueue<MARVEvent>();
        BlockingQueue<MARVCommand> resultQueue  = new PriorityBlockingQueue<MARVCommand>();

        // Create Socket Adapter & Connect
        Socket serialSocket = null;
        String input;
       
        try {
            serialSocket = new Socket("localhost", 4711);

        } catch (IOException e){
            System.err.println("Error while creating socket: I/O-Error!");
            System.exit(1);
        }

        // Create Reader
        SocketReader socketReader = new SocketReader(new BufferedReader(new InputStreamReader(serialSocket.getInputStream())), eventQueue);

        // Create Writer
        SocketWriter socketWriter = new SocketWriter(new PrintWriter(serialSocket.getOutputStream(), true), commandQueue, resultQueue, socketReader);

        // Start Threads
        socketReader.start();
        socketWriter.start();

        // Wait for childs to join
        int count = 0;

        MARVEvent   event;
        while (count < 2) {
            event = eventQueue.take();
            if(event.getClass().getName() == "MARVChildJoined")
                count++;
        }

        ZigBit z1 = new ZigBit(commandQueue, 5);
        ZigBit z2 = new ZigBit(commandQueue, 6);

        z1.GPIOenable(1);
        z1.update();
        z1.GPIOenable(2);
        z1.update();
        z1.GPIOdisable(2);
        z1.update();
        z1.GPIOdisable(1);
        z1.update();


        // Fetch results
        MARVCommand result;

        for(int i = 0; i < 4; i++){
            result = resultQueue.take();
            System.out.println("Got Result for command " + result.id + ": " + result.result);
        }

    }
}
