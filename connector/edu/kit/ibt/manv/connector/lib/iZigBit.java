package edu.kit.ibt.manv.connector.lib;
import edu.kit.ibt.manv.connector.commands.*;
import edu.kit.ibt.manv.connector.results.*;
import java.util.*;
import java.util.concurrent.*;

public interface iZigBit {
    public int getNodeID();
    public int getMacID();
    public MANVResult sendData(String data) throws InterruptedException;
    public MANVResult toggleAlertStatus();
}

