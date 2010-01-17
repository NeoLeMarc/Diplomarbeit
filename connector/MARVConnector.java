import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.*;

/* ** Meta classes ** */
class MARVPrioritized implements Comparable {
    protected int priority;

    public int compareTo(Object o){
        return 0;
    }

    public int compareTo(MARVPrioritized o){
        return this.priority - o.priority;
    }

}

/* ** Commands ** */
class MARVCommand extends MARVPrioritized {
    protected String command;
    private MARVResult result;
    protected int id = 0;
    static int maxId;
    private CountDownLatch resultLatch = new CountDownLatch(1);

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

    public void setResult(MARVResult result){
        this.result = result;

        // Open latch, so status can be fetched
        this.resultLatch.countDown();
    }

    public MARVResult getResult(){
        // Wait for result to become available
        boolean successful = false;
        while(!successful){
            try{
                this.resultLatch.await();
                successful = true;
            } catch (InterruptedException e){
                // Try again
            }
        }

        return this.result;
    }

}


/* *** Events *** **/
class MARVEvent extends MARVPrioritized {
    protected String raw;
    
    MARVEvent(String raw){
        this.raw = raw;
    }

    public boolean isResult(){
        return false;
    }

    public boolean isImportant(){
        return false;
    }


    public String toString(){
        return this.raw;
    }

    public static MARVEvent fromString(String raw){
        if(raw.equals("OK"))
            return new MARVResult(raw, true);
        else if(raw.equals("ERROR"))
            return new MARVResult(raw, false);
        else if(raw.matches("EVENT:CHILD_JOINED .*"))
            return new MARVChildJoined(raw);
        else if (raw.matches("\\+WCHILDREN:.*"))
            return new MARVChildrenList(raw);
        else
            return new MARVEvent(raw);
    }
}

class MARVChildJoined extends MARVEvent {

    MARVChildJoined(String raw){
        super(raw);
    }

    public boolean isImportant(){
        return true;
    }
};

/* ** Results ** */
class MARVResult extends MARVEvent {
    protected boolean status;
    protected MARVResult subResult;

    MARVResult(String raw, boolean status){
        super(raw);
        this.status = status;
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

    public ZigBit[] getChildList(BlockingQueue<MARVCommand> commandQueue){
        return null;
    }

    public void addSubResult(MARVResult result){
        this.subResult = result;
    }

};

class MARVChildrenList extends MARVResult {

    MARVChildrenList(String raw){
        super(raw, true);
    }

    public boolean isComposite(){
        // Expecting an OK or ERROR to follow
        return true;
    }

    public ZigBit[] getChildList(BlockingQueue<MARVCommand> commandQueue){
        // Parse return value
        String[] panIdList = this.raw.split(":")[1].split(",");
        ZigBit[] childList = new ZigBit[panIdList.length];

        for(int pos = 0; pos < panIdList.length; pos++)
            childList[pos] = ZigBit.get(commandQueue, Integer.parseInt(panIdList[pos])); 

        return childList;
    }

    public void addSubResult(MARVResult result){
        this.status    = result.status;
        this.subResult = result;
    }
}

/* *** Entities ** */
class ZigBit {
    int panID;
    BlockingQueue<MARVCommand> commandQueue;
    int[] gpio = {0, 0, 0, 0};

    // Singleton
    private static HashMap<Integer, ZigBit> zigBitMap = new HashMap<Integer, ZigBit>();

    ZigBit(BlockingQueue<MARVCommand> commandQueue, int panID){
        this.commandQueue = commandQueue;
        this.panID        = panID;
    }

    public static ZigBit get(BlockingQueue<MARVCommand> commandQueue, int panID){
        ZigBit z = zigBitMap.get(panID);
        if(z != null)
            return z;
        else {
            z = new ZigBit(commandQueue, panID);
            zigBitMap.put(panID, z);
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
        commandString += "ATR " + this.panID + ",0,";

        for(int i = 0; i < gpio.length; i++)
            commandString += " S13" + i + "=" + this.gpio[i];

        // Do not lose commands!
        do {
            successful = false;

            try {
                this.commandQueue.put(new MARVCommand(commandString, 1));
                successful = true;
            } catch (InterruptedException e) {
                successful = false;
            }
        } while (!successful);
    }

    public static ZigBit[] discover(BlockingQueue<MARVCommand> commandQueue) throws InterruptedException{
        // Send discovery command
        MARVCommand command = new MARVCommand("ATS30=1+WCHILDREN?", 0);
        commandQueue.put(command);

        // Get result & return childList
        MARVResult result = command.getResult();
        return result.getChildList(commandQueue);
    }
}

/* ** Threads ** */
class SocketReader extends Thread{
    private BlockingQueue<MARVEvent> eventQueue;
    private MARVResult               lastResult;
    private Semaphore                resultSemaphore = new Semaphore(1, true);
    private BufferedReader           serialIn; 

    SocketReader(BufferedReader serialIn, BlockingQueue<MARVEvent> eventQueue){
        this.serialIn   = serialIn;
        this.eventQueue = eventQueue;
        this.resultSemaphore.acquireUninterruptibly();
    }

    private void setLastResult(MARVResult result){
        if(this.lastResult != null && this.lastResult.isComposite())
            this.lastResult.addSubResult(result);
        else
            this.lastResult = result; 

        if(!result.isComposite())
            resultSemaphore.release();
    }

    public MARVResult getLastResult(){
        this.resultSemaphore.acquireUninterruptibly();
        return this.lastResult;
    }

    @Override public void run(){
        System.out.println("Starting SocketReader with BufferedReader: " + this.serialIn);
        String line;
        Boolean successful;
        MARVEvent event;

        try {
            while((line = this.serialIn.readLine()) != null){
                // Skip empty lines
                if(line.equals(""))
                    continue;

                event = MARVEvent.fromString(line);

                if(event.isResult()){
                    System.out.println("** RESULT **: " + line);
                    this.setLastResult((MARVResult)event);
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

class SocketWriter extends Thread{
    private BlockingQueue<MARVCommand> commandQueue;
    private BlockingQueue<MARVCommand> resultQueue;
    private PrintWriter                serialOut;
    private SocketReader               reader;
    private MARVResult lastResult; 

    SocketWriter(PrintWriter serialOut, BlockingQueue<MARVCommand> commandQueue, BlockingQueue<MARVCommand> resultQueue, SocketReader reader){
        this.serialOut    = serialOut;
        this.commandQueue = commandQueue;
        this.resultQueue  = resultQueue;
        this.reader       = reader;
    }

    private void writeLine(String line){
        this.serialOut.print(line + "\r\n");
        this.serialOut.flush();
        System.out.println("Waiting for result");
        this.lastResult = this.reader.getLastResult();
        System.out.println("Got result");
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
                // Try again
            }

        }

    }
}

/* ** Main class ** */
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
            if(event instanceof MARVChildJoined)
                count++;
        }

        ZigBit[] zigBitList = ZigBit.discover(commandQueue);

        while(true){
            for(ZigBit zigBit: zigBitList){
                zigBit.GPIOdisable(0);
                zigBit.GPIOenable(1);
                zigBit.update();
            }

            for(ZigBit zigBit: zigBitList){
                zigBit.GPIOenable(0);
                zigBit.GPIOdisable(1);
                zigBit.update();
            }
        }

        // Fetch results
        //MARVCommand result;

        //for(int i = 0; i < 4; i++){
        //    result = resultQueue.take();
        //    System.out.println("Got Result for command " + result.id + ": " + result.result);
       // }

    }
}
