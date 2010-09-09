package edu.kit.ibt.manv.connector.corba;
import CORBA_Server.*;
import Common.*;
import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NameComponent;

/* *** Corba message handling ****/
public abstract class CorbaMessageContainer {
    public abstract void send(Incoming serverIncoming);
}

