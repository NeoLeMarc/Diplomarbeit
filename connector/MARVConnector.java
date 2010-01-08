import java.io.*;
import java.net.*;

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
