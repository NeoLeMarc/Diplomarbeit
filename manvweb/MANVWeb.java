/* Proof of concepft for CORBA<->AJAX Gateway
 * (C) Copyright 2010 by Marcel Noe <marcel@marcel-noe.de>
 */
import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.*;
import CORBA_Server.Commands;
import CORBA_Server.CommandsHelper;
import CORBA_Server.Publisher;
import CORBA_Server.PublisherHelper;
import CORBA_Server.Queries;
import CORBA_Server.QueriesHelper;
import CORBA_Server.Subscriber;
import CORBA_Server.SubscriberHelper;
import org.omg.CORBA.*;
import org.omg.CORBA.Object;
import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NameComponent;
import edu.kit.ibt.manv.common.CorbaUtil;
import edu.kit.ibt.manv.common.*;
import edu.kit.ibt.manv.client.corba.SubscriberImpl;
import edu.kit.ibt.manv.client.util.EnvironmentImpl;
import edu.kit.ibt.manv.client.Global;
import edu.kit.ibt.manv.client.corba.ToCommands;


public class MANVWeb extends MANV_CORBA_Component {

    public static void main(String[] args){
        MANVWeb web = new MANVWeb(args);
        web.startOrb();
    }

    public MANVWeb(String[] args){
        super(args);
        Global.setEnvironment(new EnvironmentImpl());
        initSubscriberServant();
        resolveNeededServerInterfaces();
    }

    @Override
    protected String getComponentName(){
        return "MANVGui";
    }

    @Override
    protected String getLoggingPath(){
        return "log/web.log";
    }

    private void initSubscriberServant(){
        Subscriber subscriber = null;
        try {
            org.omg.CORBA.Object objRef = registerCORBAServant(new SubscriberImpl(), "");
            subscriber = SubscriberHelper.narrow(objRef);
        } catch (Exception e) {
            System.err.println("Fehler beim Registrieren des SubscriberImpl Servants! " + e);
        }

        Global.getEnvironment().setSubscriber(subscriber);
    }

    private void resolveNeededServerInterfaces(){
        // Wir brauchen nur das Server-Query  und das Commands Interface
        Queries  queries  = null;
        Commands commands = null;

        try {
            org.omg.CORBA.Object objRef = resolveCORBAInterface("Server_Queries");
            queries = QueriesHelper.narrow(objRef);
        } catch (Exception e) {
            System.err.println("Fehler beim Auflösen des 'Server_Queries' Interface. Beende " + getComponentName());
            shutDown(1);
        }

        try {
            org.omg.CORBA.Object objRef = resolveCORBAInterface("Server_Commands");
            commands = CommandsHelper.narrow(objRef);
        } catch (Exception e){
            logger.fatal("Fehler beim Auflösen des 'Server_Commands' Interfaces. Beende " + getComponentName());
            shutDown(1);
        }

        ToQueries  toQueries  = new ToQueries(queries);
        ToCommands toCommands = new ToCommands(commands);

        // Starte Serverthread
        MANVWebserver webserver = new MANVWebserver(toQueries, toCommands);
        webserver.run();
    }
}
