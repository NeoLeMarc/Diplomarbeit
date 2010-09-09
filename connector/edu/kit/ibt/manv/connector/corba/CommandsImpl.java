package edu.kit.ibt.manv.connector.corba;
import CORBA_Server.CommandsPOA;
import Common.CORBA_Node;

/* Corba implementation */
public class CommandsImpl extends CommandsPOA {

    @Override
    public void disableAlert(CORBA_Node node){
        System.out.println("*** CORBA Event received: disableAlert()");
    }

    @Override
    public void enableAlert(CORBA_Node node){
        System.out.println("*** CORBA Event received: enableAlert()");
    }

    @Override
    public void toggleAlert(CORBA_Node node){
        System.out.println("*** CORBA Event received: toggleAlert()");
    }

    @Override
    public void mute(CORBA_Node node){
        System.out.println("*** CORBA Event received: mute()");
    }
}

