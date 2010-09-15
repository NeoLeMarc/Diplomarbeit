package edu.kit.ibt.manv.connector.corba;
import edu.kit.ibt.manv.connector.lib.*;
import CORBA_Server.CommandsPOA;
import Common.CORBA_Node;

/* Corba implementation */
public class CommandsImpl extends CommandsPOA {

    @Override
    public void disableAlert(CORBA_Node node){
        System.out.println("*** CORBA Event received: disableAlert(" + node.node_id + ")");
        iZigBit zigBit = ZigBit.getByMacID(Integer.parseInt(node.node_id));
        zigBit.disableAlert();
    }

    @Override
    public void enableAlert(CORBA_Node node){
        System.out.println("*** CORBA Event received: enableAlert(" + node + ")");
        iZigBit zigBit = ZigBit.getByMacID(Integer.parseInt(node.node_id));
        zigBit.enableAlert();
    }

    @Override
    public void toggleAlert(CORBA_Node node){
        System.out.println("*** CORBA Event received: toggleAlert()");
        iZigBit zigBit = ZigBit.getByMacID(Integer.parseInt(node.node_id));
        zigBit.toggleAlertStatus();
    }

    @Override
    public void mute(CORBA_Node node){
        System.out.println("*** CORBA Event received: mute()");
        iZigBit zigBit = ZigBit.getByMacID(Integer.parseInt(node.node_id));
        zigBit.muteAlert();
    }
}

