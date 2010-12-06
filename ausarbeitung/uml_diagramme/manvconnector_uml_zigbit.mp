input metauml;
beginfig(1);

    % ZigBee Krempel
    Class.iZigBit("iZigBit")
            ()
            ("+getNodeID: int",
             "+getMacID: int",
             "+sendData(data: String): MANVResult",
             "+isendData(data: String)",
             "+toggleAlertStatus: MANVResult",
             "+disableAlert: MANVResult",
             "+enableAlert: MANVResult",
             "+muteAlert: MANVResult",
             "+itoggleAlertStatus: void",
             "+idisableAlert: void",
             "+ienableAlert: void",
             "+imuteAlert: void");
    classStereotypes.iZigBit("<<Interface>>");

    Class.readonlyZigBit("readonlyZigBit")
            ("-id: int")
            ("+readOnlyZigBit(nodeID: int)",
            "+getNodeID: int",
            "+getMacID: int",
            "+sendData(data: String): MANVResult",
            "+toggleAlertStatus: void",
            "+enableAlertStatus: void",
            "+disableAlert: void",
            "+muteAlert: void");

    Class.ZigBit("ZigBit")
            ("-zigBitMap: HashMap<Integer, ZigBit>",
             "-macMap: HashMap<Integer, Integer>")
            ("+ZigBit(nodeID : int)",
            "+getNodeID: int",
            "+getMacID: int",
            "+setCommandQueue(commandQueue: BlockingQueue<MANVCommand>)",
            "+get(id: int): iZigBit",
            "+getByMacID(macID: int): iZigBit",
            "+discover: iZigBit",
            "+sendData(data: String): MANVResult",
            "+sendUntilSuccess(data: String): MANVResult",
            "+toggleAlertStatus: MANVResult",
            "+enableAlertStatus: MANVResult",
            "+disableAlert: MANVResult",
            "+mutAlert: MANVResult",
            "-requestMacID: int");


    
    topToBottom(45)(iZigBit, ZigBit);
    leftToRight.top(45)(ZigBit, readonlyZigBit);

    drawObjects(iZigBit, readonlyZigBit, ZigBit);
    link(inheritance)(pathStepY(readonlyZigBit.n, iZigBit.s, 20));
    link(inheritance)(pathStepY(ZigBit.n, iZigBit.s, 20));
endfig;
end


















