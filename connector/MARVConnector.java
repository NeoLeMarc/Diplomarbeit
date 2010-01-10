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
}

class ZigBit {
    int panID;
    SocketAdapter socket;
    int[] gpio = {0, 0, 0, 0};

    ZigBit(SocketAdapter socket, int panID){
        this.socket = socket;
        this.panID  = panID;
    }

    public void GPIOenable(int nr){
        this.gpio[nr] = 1;
    }

    public void GPIOdisable(int nr){
        this.gpio[nr] = 0;
    }

    public void update() throws java.io.IOException{
        this.socket.write("ATR " + this.panID + ",0,");
        for(int i = 0; i < gpio.length; i++)
            this.socket.write(" S13" + i + "=" + this.gpio[i]);
        this.socket.writeLine("");
        this.waitForStatus();
    }

    public boolean waitForStatus() throws java.io.IOException{
        String data = this.socket.readLine();

        while(!data.equals("OK") && !data.equals("ERROR")){
            System.out.println(data);
            data = this.socket.readLine();
        }


        if(data.equals("OK"))
            return true;
        else
            return false;
    }
}
/*
class SocketAdapter {
    private BufferedReader serialIn;
    private PrintWriter    serialOut;
    private Socket         socket;

    public SocketAdapter(Socket serialSocket) throws java.io.IOException{
        this.serialIn  = new BufferedReader(new InputStreamReader(serialSocket.getInputStream()));
        this.serialOut = new PrintWriter(serialSocket.getOutputStream(), true);
        this.socket    = serialSocket;
    }

    public void write(String in){
        this.serialOut.print(in);
    }

    public void writeLine(String line){
        this.serialOut.print(line + "\r\n");
        this.serialOut.flush();
    }

    public String readLine() throws java.io.IOException {
        return serialIn.readLine();
    }

    protected void finalize() throws java.io.IOException{
        this.socket.close();
    }
}
*/

class SocketReader extends Thread{
    private Queue<MARVEvent> eventQueue;
    private boolean            lastStatus;
    private Semaphore          statusSemaphore = new Semaphore(1, true);
    private BufferedReader     serialIn; 

    SocketReader(BufferedReader serialIn, Queue<MARVEvent> eventQueue){
        this.serialIn   = serialIn;
        this.eventQueue = eventQueue;
    }

    @Override public void run(){
        System.out.println("Starting SocketReader with BufferedReader: " + this.serialIn);
        String line;

        try {
            while((line = this.serialIn.readLine()) != null){
                System.out.println("SocketReader: " + line);
            }
        } catch (IOException e) {
            System.err.println("IOException in SocketReader");
            System.exit(1);
        }
    }

}

class SocketWriter extends Thread{
    private Queue<MARVCommand> commandQueue;
    private Queue<MARVCommand> resultQueue;
    private PrintWriter        serialOut;

    SocketWriter(PrintWriter serialOut, Queue<MARVCommand> commandQueue, Queue<MARVCommand> resultQueue){
        this.serialOut    = serialOut;
        this.commandQueue = commandQueue;
        this.resultQueue  = resultQueue;
    }

    private void writeLine(String line) throws IOException{
        this.serialOut.print(line + "\r\n");
        this.serialOut.flush();
    }

    @Override public void run(){
        System.out.println("Starting SocketWriter with PrintWriter: " + this.serialOut);

        try {
            // Initialize ZigBee adapter
            this.writeLine("AT+WAUTONET=1 +WWAIT=100 Z");
            this.writeLine("ATS30=1");
        } catch (IOException e) {
            System.err.println("IOException in SocketReader");
            System.exit(1);
        }

    }
}

public class MARVConnector {
    public static void main(String[] args) throws IOException{

        // Create Queues
        Queue<MARVCommand> commandQueue = new PriorityBlockingQueue<MARVCommand>();
        Queue<MARVEvent>   eventQueue   = new PriorityBlockingQueue<MARVEvent>();
        Queue<MARVCommand> resultQueue  = new PriorityBlockingQueue<MARVCommand>();

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
        SocketWriter socketWriter = new SocketWriter(new PrintWriter(serialSocket.getOutputStream(), true), commandQueue, resultQueue);

        // Start Threads
        socketReader.start();
        socketWriter.start();
    }
}

/*
public class MARVConnector {
    public static void main(String[] args) throws IOException {

        Socket serialSocket = null;
        String input;
       
        try {
            serialSocket = new Socket("localhost", 4711);

        } catch (IOException e){
            System.err.println("Error while creating socket: I/O-Error!");
            System.exit(1);
        }

        SocketAdapter sa = new SocketAdapter(serialSocket);
        sa.writeLine("AT+WAUTONET=1 +WWAIT=100 Z");
        sa.writeLine("ATS30=1");

        while(!(input = sa.readLine()).equals("EVENT:CHILD_JOINED 0000000000000003")){
            System.out.println(input);
        }

        System.out.println("Initialisatzion complete, entering test loop....");

        try { 
            Thread.currentThread().sleep(1000);
        } catch(InterruptedException e){
        };

        ZigBit z1 = new ZigBit(sa, 5);
        ZigBit z2 = new ZigBit(sa, 6);

        z1.GPIOdisable(1);
        z1.update();

        try { 
            Thread.currentThread().sleep(1000);
        } catch(InterruptedException e){
        };

        z1.GPIOenable(1);
        z1.update();


    }
}
*/

