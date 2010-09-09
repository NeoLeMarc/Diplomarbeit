package edu.kit.ibt.manv.connector;
import edu.kit.ibt.manv.connector.commands.*;
import edu.kit.ibt.manv.connector.events.*;
import edu.kit.ibt.manv.connector.lib.*;
import edu.kit.ibt.manv.connector.corba.*;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.*;
import CORBA_Server.*;
import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NameComponent;

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

            // Register to server
            String nameControl = "Server_Control";
            Control serverControl = ControlHelper.narrow(ncRef.resolve_str(nameControl));
            serverControl.registerConnector();

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
