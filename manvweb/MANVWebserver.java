// MANVWebserver 
// (C) Copyright 2010 by Marcel Noe <marcel@marcel-noe>


import java.net.*;
import java.io.*;
import Common.CORBA_Node; 
import Common.CORBA_StatusMessage;
import Common.CORBA_DataMessage;
import edu.kit.ibt.manv.client.corba.ToCommands;

class MANVJsonContainer {
    private CORBA_StatusMessage statusMessage;
    private ToQueries           toQueries;

    public MANVJsonContainer(CORBA_StatusMessage statusMessage, ToQueries toQueries){
        this.statusMessage = statusMessage;
        this.toQueries     = toQueries;
    }

    public String jsonifyData(CORBA_DataMessage dataMessage){
        // Data message jsonifizieren
        String curval = "";

        curval += "nodeID: "    + dataMessage.nodeID;
        curval += ", pulse:  "    + dataMessage.pulse;
        curval += ", breathing: " + dataMessage.breathing;

        return curval;
    }

    public String jsonify(){
        // Node jsonifizieren.
        String curval = "";

        // Zusätzlich müssen wir zu jeder Node die passenden Daten auslesen
        CORBA_DataMessage[] dataMessages = this.toQueries.getDataFromNode(this.statusMessage.serverTimestamp - 10000, new CORBA_Node(this.statusMessage.nodeID, (short)1));

        curval += "nodeID: "                + this.statusMessage.nodeID;
        curval += ", alarm_breathing: \""   + this.statusMessage.alarm_breathing + "\"";
        curval += ", alarm_pulse: \""       + this.statusMessage.alarm_pulse + "\"";
        curval += ", serverTimestamp: \""   + this.statusMessage.serverTimestamp + "\"";

        // Jetzt die Datenmessages hinzufügen
        String dataMessageValues = "";

        for(CORBA_DataMessage dataMessage: dataMessages){
            dataMessageValues += "{";
            dataMessageValues += this.jsonifyData(dataMessage);
            dataMessageValues += "},";
        }

        if(dataMessageValues.length() > 0)
            curval += ", dataMessages: [" + dataMessageValues.substring(0, dataMessageValues.length() - 1) + "]";
        else
            curval += ", dataMessages : []";


        curval  = "{" + curval + "},";
        return curval;
    }

    public static String jsonifyList(CORBA_StatusMessage[] statusList, ToQueries toQueries){
        String retval = "";
        for(CORBA_StatusMessage statusMessage : statusList)
            retval += new MANVJsonContainer(statusMessage, toQueries).jsonify();
                
        // Todo: letztes Komma entfernen. 
        retval = "[" + retval.substring(0, retval.length() - 1) + "]";

        return retval;
    }
}

class MANVWebWorker extends Thread {
    private Socket clientSocket;

    public MANVWebWorker(Socket clientSocket){
        this.clientSocket = clientSocket;
    }

    public void run(ToQueries toQueries, ToCommands toCommands){

        // Socket annehmen und Java-Klimbim drumherum bauen
        PrintWriter    out = null;
        BufferedReader in  = null;

        try {
            out = new PrintWriter(this.clientSocket.getOutputStream(), true);
            in  = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
        } catch (IOException e) {
            System.err.println("Accessing Socket failed!");
            System.exit(1);
        }

        // Input lesen
        String urlRequest = "";
        try {
            urlRequest = in.readLine();
        } catch (IOException e){
        };

        // Bestimmen, welche Methode aufgerufen werden soll.
        String command   = null;
        String parameter = null;

        String[] splittedReq = urlRequest.split(" ");

        // Parameter rausparsen
        if(splittedReq.length >= 3){
            String[] actionReq = splittedReq[1].split("/");
            
            if(actionReq.length >= 3){
                command   = actionReq[1];
                parameter = actionReq[2];
            }
        } 
       
        // Befehl ausführen
        if(command == null)
            // Kein Befehl übergeben, 
            out.println(MANVJsonContainer.jsonifyList(toQueries.getAllNodesStatus(), toQueries));

        else if(command.equals("e")){
            // Enable Alert
            toCommands.enableAlert(new CORBA_Node(parameter, (short)1));    
            out.println("OK\n");
        } else if(command.equals("d")){
            // Disable Alert
            toCommands.disableAlert(new CORBA_Node(parameter, (short)1));    
            out.println("OK\n");
        } else {
            // Mute
            toCommands.mute(new CORBA_Node(parameter, (short)1));    
            out.println("-" + command + "-");
            out.println("OK\n");
        }

        // Verbindung beenden
        try {
            this.clientSocket.close();
        } catch (IOException e) {
            System.err.println("Error shuting down socket!");
            System.exit(1);
        }
    }
}

public class MANVWebserver {
    private ToQueries toQueries;
    private ToCommands toCommands;

    public MANVWebserver(ToQueries toQueries, ToCommands toCommands) {
        this.toQueries  = toQueries;
        this.toCommands = toCommands;
    }

    public void sleep(){
        try{
            Thread.currentThread().sleep(1000);
        } catch (InterruptedException e) {
        };
    }

    public void run(){
        CORBA_Node[]            nodes;
        CORBA_StatusMessage[]   statusMessages;
        ServerSocket            serverSocket = null;

        // Serversocket erstellen
        try {
            serverSocket = new ServerSocket(8080);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 8080.");
        }

        // Verbindungen annehmen 
        while(true){
            Socket clientSocket = null;
            try {
                clientSocket = serverSocket.accept();
            } catch (IOException e){
                System.err.println("Accept failed.");
                System.exit(1);
            }

            // Angenommene Verbindung in eigenen Thread dispatchen
            MANVWebWorker webWorker = new MANVWebWorker(clientSocket);
            webWorker.run(toQueries, toCommands);
        }
    }
}
