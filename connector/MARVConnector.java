import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.*;
import CORBA_Server.*;
import Common.*;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

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

/* *** Corba message handling ****/
abstract class CorbaMessageContainer {
    public abstract void send(Incoming serverIncoming);
}

class CorbaDataMessageContainer extends CorbaMessageContainer{
    private CORBA_DataMessage message;

    CorbaDataMessageContainer (CORBA_DataMessage message){
        this.message = message;
    }

    public void send(Incoming serverIncoming){
        serverIncoming.notify_data(this.message);
    }

    public String toString(){
        return this.message.toString();
    }
}

class CorbaEventMessageContainer extends CorbaMessageContainer{
    private CORBA_EventMessage message;

    CorbaEventMessageContainer (CORBA_EventMessage message){
        this.message = message;
    }

    public void send(Incoming serverIncoming){
        serverIncoming.notify_event(this.message);
    }

    public String toString(){
        return this.message.toString();
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


    public static MARVEvent fromString(String raw){
        if(raw.equals("OK"))
            return new MARVResult(raw, true);
        else if(raw.equals("ERROR"))
            return new MARVResult(raw, false);
        else if(raw.matches("DATA .*:STATUS:.*:.*"))
            return new MARVStatusMessage(raw);
        else if(raw.matches("DATA .*:.*"))
            return new MARVDataReceived(raw);
        else if(raw.matches("EVENT:CHILD_JOINED .*"))
            return new MARVChildJoined(raw);
        else if(raw.matches("EVENT:CHILD_LOST .*"))
            return new MARVChildLost(raw);
        else if (raw.matches("\\+WCHILDREN:.*"))
            return new MARVChildrenList(raw);
        else 
            return new MARVEvent(raw);
    }

    public AbstractList<CorbaMessageContainer> createCorbaMessages(){
        return null; 
    }

    public String toString(){
        return "MARVEvent(" + this.raw + ")";
    }
}

class MARVChildJoined extends MARVEvent {
    protected ZigBit source;

    MARVChildJoined(String raw){
        super(raw);
        this.source = ZigBit.get(Integer.parseInt(raw.split(" ")[1]));
    }

    public boolean isImportant(){
        return true;
    }

    public AbstractList<CorbaMessageContainer> createCorbaMessages(){
        ArrayList<CorbaMessageContainer> ret = new ArrayList<CorbaMessageContainer>();

        // Create join message 
        CORBA_EventMessage eventMessage = new CORBA_EventMessage();
        eventMessage.groupID   = 1;
        eventMessage.nodeID    = String.valueOf(this.source.panID);
        eventMessage.eventType = event_join.value;
        eventMessage.connectorTimestamp = System.currentTimeMillis();

        ret.add(new CorbaEventMessageContainer(eventMessage));
        return ret;
    }

    public String toString(){
        return "MARVRChildJoind(" + this.raw + ")";
    }
};

class MARVChildLost extends MARVEvent {
    protected ZigBit source;

    MARVChildLost(String raw){
        super(raw);
        this.source = ZigBit.get(Integer.parseInt(raw.split(" ")[1]));
    }

    public boolean isImportant(){
        return true;
    }

    public AbstractList<CorbaMessageContainer> createCorbaMessages(){
        ArrayList<CorbaMessageContainer> ret = new ArrayList<CorbaMessageContainer>();

        // Create lost message 
        CORBA_EventMessage eventMessage = new CORBA_EventMessage();
        eventMessage.groupID   = 1;
        eventMessage.nodeID    = String.valueOf(this.source.panID);
        eventMessage.eventType = event_lost.value;
        eventMessage.connectorTimestamp = System.currentTimeMillis();

        ret.add(new CorbaEventMessageContainer(eventMessage));
        return ret;
    }

    public String toString(){
        return "MARVRChildLost(" + this.raw + ")";
    }

};


class MARVDataReceived extends MARVEvent {
    protected ZigBit source;
    protected String data;


    MARVDataReceived(String raw){
        super(raw);

        // parse data
        this.parseRaw(raw);
    }

    public boolean isImportant(){
        return true;
    }

    private void parseRaw(String raw){
        String[] splitedRaw = raw.split(":", 2);
        this.data   = splitedRaw[1];
        this.source = ZigBit.get(Integer.parseInt(splitedRaw[0].split(" ")[1].split(",")[0]));
        System.out.println("Data received: Source: " + this.source + " - " + this.data);
    }
};

class MARVStatusMessage extends MARVDataReceived {
    protected short pulse;
    protected short breathing;

    MARVStatusMessage(String raw){
        super(raw);

        // parse data
        this.parseRaw(raw);
    }

    private void parseRaw(String raw){
        String[] splitedRaw = raw.split(":", 5);
        this.pulse     = Short.parseShort(splitedRaw[3]);
        this.breathing = Short.parseShort(splitedRaw[4]);
        System.out.println("Pulse is: " + this.pulse + " - Breathing is: " + this.breathing);
    }

    public AbstractList<CorbaMessageContainer> createCorbaMessages(){
        ArrayList<CorbaMessageContainer> ret = new ArrayList<CorbaMessageContainer>();

        // Create Data Message
        CORBA_DataMessage dataMessage  = new CORBA_DataMessage();
        dataMessage.groupID            = 1;
        dataMessage.nodeID             = String.valueOf(this.source.panID);
        dataMessage.pulse              = this.pulse;
        dataMessage.breathing          = this.breathing; 
        dataMessage.connectorTimestamp = System.currentTimeMillis();
        ret.add(new CorbaDataMessageContainer(dataMessage));

        // If Status is error, we also have to create an event message
        if(this.data.matches("STATUS: ERROR:.*")){
            // Create alert message
            CORBA_EventMessage eventMessage = new CORBA_EventMessage();
            eventMessage.groupID            = 1;
            eventMessage.nodeID             = String.valueOf(this.source.panID);
            eventMessage.eventType          = event_alarm_breathing.value;
            eventMessage.connectorTimestamp = System.currentTimeMillis();

            ret.add(new CorbaEventMessageContainer(eventMessage));
         }

         return ret;
    }

    public String toString(){
        return "MARVRStatusMessage(" + this.raw + ")";
    }

}

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

    public String toString(){
        return "MARVREsult(" + this.raw + ")";
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
            childList[pos] = ZigBit.get(Integer.parseInt(panIdList[pos])); 

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
    int[] gpio = {0, 0, 0, 0};

    // Singleton
    private static HashMap<Integer, ZigBit> zigBitMap = new HashMap<Integer, ZigBit>();

    // Command queue
    private static BlockingQueue<MARVCommand> commandQueue;

    ZigBit(int panID){
        this.commandQueue = commandQueue;
        this.panID        = panID;
    }

    public static void setCommandQueue(BlockingQueue<MARVCommand> commandQueue){
        ZigBit.commandQueue = commandQueue;
    }

    public static ZigBit get(int panID){
        ZigBit z = zigBitMap.get(panID);
        if(z != null)
            return z;
        else {
            z = new ZigBit(panID);
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

    public static ZigBit[] discover() throws InterruptedException{
        // Send discovery command
        MARVCommand command = new MARVCommand("ATS30=1+WCHILDREN?", 0);
        commandQueue.put(command);

        // Get result & return childList
        MARVResult result = command.getResult();
        return result.getChildList(ZigBit.commandQueue);
    }

    public MARVResult sendData(String data) throws InterruptedException{
        // Send data command
        MARVCommand command = new MARVCommand("ATD " + this.panID + "\r" + data + "\r", 10);
        commandQueue.put(command);

        // Get result & return status
        return command.getResult();
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

    @Override 
    public void run(){
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
                    //System.out.println("** RESULT **: " + line);
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
//        System.out.println("Waiting for result");
        this.lastResult = this.reader.getLastResult();
//        System.out.println("Got result");
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
    private static Incoming serverIncoming;

    public static void main(String[] args) throws IOException, InterruptedException{

        // Create Queues
        BlockingQueue<MARVCommand> commandQueue = new PriorityBlockingQueue<MARVCommand>();
        BlockingQueue<MARVEvent>   eventQueue   = new PriorityBlockingQueue<MARVEvent>();
        BlockingQueue<MARVCommand> resultQueue  = new PriorityBlockingQueue<MARVCommand>();

        // Initialize ZigBit class with commandQueue
        ZigBit.setCommandQueue(commandQueue);

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

        // Initizalize CORBA connection
        System.out.println("Classpath: " + System.getProperty("java.class.path"));
        Properties properties = new Properties();
        //properties.put("org.omg.CORBA.ORBClass", "org.jacorb.orb.ORB");
        //properties.put("org.omg.CORBA.ORBSingletonClass", "org.jacorb.orb.ORBSingletonClass");
        initCORBA(args, properties);
        System.out.println("Corba initialized...");

        // Start Threads
        socketReader.start();
        socketWriter.start();

        // Send events to corba
        MARVEvent   event;
        while((event = eventQueue.take()) != null){
            if(event.isImportant())
                for(CorbaMessageContainer corbaMessage : event.createCorbaMessages()){
                    corbaMessage.send(serverIncoming);
                    System.out.println("!! Sent corba message: " + corbaMessage + " !!");
                }
        }
    }

	/* Von Jan:
	 * CORBA initialisieren.
	 * Nachdem diese Prozedur ausgeführt wurde ist der Server bereit Nachrichten zu empfangen. 
	 */
	private static void initCORBA(String[] args, Properties properties)
	{
		System.err.println("initialisiere CORBA...");
		try
		{
	        // create and initialize the ORB
			ORB orb = ORB.init(args, properties);
		
	        // get the root naming context
	        org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
	        // Use NamingContextExt instead of NamingContext. This is 
	        // part of the Interoperable naming Service.  
	        NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
	 
	        // resolve the Object Reference in Naming
	        String name = "Server_Incoming";
	        serverIncoming = IncomingHelper.narrow(ncRef.resolve_str(name));
	        System.err.println("Obtained a handle on server object: " + serverIncoming);
	        System.err.println("...CORBA erfolgreich gestartet");

		} catch (Exception e) {
			System.err.println("Fehler bei der CORBA-Initialisierung!\n" + e.toString());
            System.exit(1);
		}
	}
}
