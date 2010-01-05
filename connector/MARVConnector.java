import java.io.*;
import java.net.*;

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

        // Create Input + Output streams
        BufferedReader serialIn  = new BufferedReader(new InputStreamReader(serialSocket.getInputStream()));
        PrintWriter    serialOut = new PrintWriter(serialSocket.getOutputStream(), true);


        // Send ATZ & Read response
        serialOut.print("ATZ\r\n");
        serialOut.flush();
        while ((input = serialIn.readLine()) != null){
            System.out.println("Socket: " + input);
        }

        serialSocket.close();

    }
}
