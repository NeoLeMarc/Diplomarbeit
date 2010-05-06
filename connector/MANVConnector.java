import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.*;
import CORBA_Server.*;
import Common.*;
import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NameComponent;

/* ** Meta classes ** */
class MANVPrioritized implements Comparable {
    protected int priority;

    public int compareTo(Object o){
        return 0;
    }

    public int compareTo(MANVPrioritized o){
        return this.priority - o.priority;
    }

}

/* ** Commands ** */
class MANVCommand extends MANVPrioritized {
    protected String command;
    private MANVResult result;
    protected int id = 0;
    protected static int maxId;
    private CountDownLatch resultLatch = new CountDownLatch(1);

    MANVCommand(String command, int priority){
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

    public void setResult(MANVResult result){
        this.result = result;

        // Open latch, so status can be fetched
        this.resultLatch.countDown();
    }

    public MANVResult getResult(){
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
class MANVEvent extends MANVPrioritized {
    protected String raw;
    
    MANVEvent(String raw){
        this.raw = raw;
    }

    public boolean isResult(){
        return false;
    }

    public boolean isImportant(){
        return false;
    }


    public static MANVEvent fromString(String raw){
        if(raw.equals("OK"))
            return new MANVResult(raw, true);
        else if(raw.equals("ERROR"))
            return new MANVResult(raw, false);
        else if(raw.matches("DATA .*:STATUS:.*:.*"))
            return new MANVStatusMessage(raw);
        else if(raw.matches("DATA .*:.*"))
            return new MANVDataReceived(raw);
        else if(raw.matches("EVENT:CHILD_JOINED .*"))
            return new MANVChildJoined(raw);
        else if(raw.matches("EVENT:CHILD_LOST .*"))
            return new MANVChildLost(raw);
        else if (raw.matches("\\+WCHILDREN:.*"))
            return new MANVChildrenList(raw);
        else 
            return new MANVEvent(raw);
    }

    public AbstractList<CorbaMessageContainer> createCorbaMessages(){
        return null; 
    }

    public String toString(){
        return "MANVEvent(" + this.raw + ")";
    }
}

class MANVChildJoined extends MANVEvent {
    protected ZigBit source;

    MANVChildJoined(String raw){
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
        eventMessage.nodeID    = String.valueOf(this.source.nodeID);
        eventMessage.eventType = event_join.value;
        eventMessage.connectorTimestamp = System.currentTimeMillis();

        ret.add(new CorbaEventMessageContainer(eventMessage));
        return ret;
    }

    public String toString(){
        return "MANVRChildJoind(" + this.raw + ")";
    }
};

class MANVChildLost extends MANVEvent {
    protected ZigBit source;

    MANVChildLost(String raw){
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
        eventMessage.nodeID    = String.valueOf(this.source.nodeID);
        eventMessage.eventType = event_lost.value;
        eventMessage.connectorTimestamp = System.currentTimeMillis();

        ret.add(new CorbaEventMessageContainer(eventMessage));
        return ret;
    }

    public String toString(){
        return "MANVRChildLost(" + this.raw + ")";
    }

};


class MANVDataReceived extends MANVEvent {
    protected ZigBit source;
    protected String data;


    MANVDataReceived(String raw){
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

class MANVStatusMessage extends MANVDataReceived {
    protected short pulse;
    protected short breathing;

    MANVStatusMessage(String raw){
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
        dataMessage.nodeID             = String.valueOf(this.source.nodeID);
        dataMessage.pulse              = this.pulse;
        dataMessage.breathing          = this.breathing; 
        dataMessage.connectorTimestamp = System.currentTimeMillis();
        ret.add(new CorbaDataMessageContainer(dataMessage));

        // If Status is error, we also have to create an event message
        if(this.data.matches("STATUS: ERROR:.*")){
            // Create alert message
            CORBA_EventMessage eventMessage = new CORBA_EventMessage();
            eventMessage.groupID            = 1;
            eventMessage.nodeID             = String.valueOf(this.source.nodeID);
            eventMessage.eventType          = event_alarm_breathing.value;
            eventMessage.connectorTimestamp = System.currentTimeMillis();

            ret.add(new CorbaEventMessageContainer(eventMessage));
         }

         return ret;
    }

    public String toString(){
        return "MANVRStatusMessage(" + this.raw + ")";
    }

}

/* ** Results ** */
class MANVResult extends MANVEvent {
    protected boolean status;
    protected MANVResult subResult;

    MANVResult(String raw, boolean status){
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

    public ZigBit[] getChildList(BlockingQueue<MANVCommand> commandQueue){
        return null;
    }

    public void addSubResult(MANVResult result){
        this.subResult = result;
    }

    public String toString(){
        return "MANVREsult(" + this.raw + ")";
    }

};

class MANVChildrenList extends MANVResult {

    MANVChildrenList(String raw){
        super(raw, true);
    }

    public boolean isComposite(){
        // Expecting an OK or ERROR to follow
        return true;
    }

    public ZigBit[] getChildList(BlockingQueue<MANVCommand> commandQueue){
        // Parse return value
        String[] panIdList = this.raw.split(":")[1].split(",");
        ZigBit[] childList = new ZigBit[panIdList.length];

        for(int pos = 0; pos < panIdList.length; pos++)
            childList[pos] = ZigBit.get(Integer.parseInt(panIdList[pos])); 

        return childList;
    }

    public void addSubResult(MANVResult result){
        this.status    = result.status;
        this.subResult = result;
    }
}

/* *** Entities ** */
class ZigBit {
    protected int nodeID;
    private int[] gpio = {0, 0, 0, 0};

    // Singleton
    private static HashMap<Integer, ZigBit> zigBitMap = new HashMap<Integer, ZigBit>();

    // Command queue
    private static BlockingQueue<MANVCommand> commandQueue;

    ZigBit(int nodeID){
        this.commandQueue = commandQueue;
        this.nodeID        = nodeID;
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
}

/* ** Threads ** */
class SocketReader extends Thread{
    private BlockingQueue<MANVEvent> eventQueue;
    private MANVResult               lastResult;
    private Semaphore                resultSemaphore = new Semaphore(1, true);
    private BufferedReader           serialIn; 

    SocketReader(BufferedReader serialIn, BlockingQueue<MANVEvent> eventQueue){
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

class SocketWriter extends Thread{
    private BlockingQueue<MANVCommand> commandQueue;
    private BlockingQueue<MANVCommand> resultQueue;
    private PrintWriter                serialOut;
    private SocketReader               reader;
    private MANVResult lastResult; 

    SocketWriter(PrintWriter serialOut, BlockingQueue<MANVCommand> commandQueue, BlockingQueue<MANVCommand> resultQueue, SocketReader reader){
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

class CorbaSender extends Thread {
    private BlockingQueue<MANVEvent> eventQueue;
    private Incoming serverIncoming;

    public CorbaSender(BlockingQueue<MANVEvent> eventQueue, Incoming serverIncoming){
        this.eventQueue     = eventQueue;
        this.serverIncoming = serverIncoming;
    }

    @Override public void run(){
        // Send events to corba
        MANVEvent   event;
        try {
            while((event = this.eventQueue.take()) != null){
                if(event.isImportant()){
                    for(CorbaMessageContainer corbaMessage : event.createCorbaMessages()){
                        corbaMessage.send(this.serverIncoming);
                        System.out.println("!! Sent corba message: " + corbaMessage + " !!");
                    }
                }
            }
        } catch (InterruptedException e){
            System.out.println("Interrupted while processing event... discarding");
        }
    }
}


/* ** Main class ** */
public class MANVConnector {
    private static Incoming serverIncoming;

    // Create Queues
    private static BlockingQueue<MANVCommand> commandQueue = new PriorityBlockingQueue<MANVCommand>();
    private static BlockingQueue<MANVEvent>   eventQueue   = new PriorityBlockingQueue<MANVEvent>();
    private static BlockingQueue<MANVCommand> resultQueue  = new PriorityBlockingQueue<MANVCommand>();

    public static void main(String[] args) throws IOException, InterruptedException{


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


        // Start Threads
        socketReader.start();
        socketWriter.start();

        // Start corba server loop
        initCORBA(args, properties);
    }

	/* Von Jan:
	 * CORBA initialisieren.
	 * Nachdem diese Methode ausgeführt wurde ist der Server bereit Nachrichten zu empfangen. 
	 */
	private static void initCORBA(String[] args, Properties properties)
	{
		System.err.println("initialisiere CORBA...");
		try
		{
	        // create and initialize the ORB
			ORB orb = ORB.init(args, properties);
		
			// get reference to rootpoa & activate the POAManager
			POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			rootpoa.the_POAManager().activate();
			
			// get the root naming context
			org.omg.CORBA.Object objRefNaming = orb.resolve_initial_references("NameService");

	        // Use NamingContextExt instead of NamingContext. This is 
	        // part of the Interoperable naming Service.  
	        NamingContextExt ncRef = NamingContextExtHelper.narrow(objRefNaming);
	
			// Use NamingContextExt which is part of the Interoperable
			// Naming Service (INS) specification.
			NamingContextExt refNaming = null;

			try {
				refNaming = NamingContextExtHelper.narrow(objRefNaming);
			} catch (Exception e) {
				System.err.println("NameService wurde nicht gefunden");
				System.exit(1);
			}

            // resolve the Object Reference in Naming
	        String name = "Server_Incoming";
	        serverIncoming = IncomingHelper.narrow(ncRef.resolve_str(name));
	        System.err.println("Obtained a handle on server object: " + serverIncoming);
	        System.err.println("...CORBA erfolgreich gestartet");

            // Servants erstellen
            CommandsPOA commands = new CommandsImpl();
            commands._this(orb);

            // References erstellen
            org.omg.CORBA.Object objRefCommands = rootpoa.servant_to_reference(commands);
			Commands refCommands = CommandsHelper.narrow(objRefCommands);

            // Im Nameservice registrieren
            String nameCommands = "Connector_Commands";
            NameComponent pathCommands[] = refNaming.to_name(nameCommands);
            refNaming.rebind(pathCommands, refCommands);

            // CorbaSender
            CorbaSender corbaSender = new CorbaSender(eventQueue, serverIncoming);
            corbaSender.start();

            // Auf Aufrufe warten
            System.out.println("... Server bereit.");
            orb.run();

		} catch (Exception e) {
			System.err.println("Fehler bei der CORBA-Initialisierung!\n" + e.toString());
            System.exit(1);
		}
	}
}
