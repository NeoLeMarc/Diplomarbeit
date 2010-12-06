input metauml;
beginfig(1);

    % Hauptklasse
    % Events
    Class.Comparable("Comparable")
            ()
            ("+compareTo(o: object): int");
    classStereotypes.Comparable("<<interface>>");
    drawObjects(Comparable);


    Class.MANVPrioritized("MANVPrioritized")
            ("#priority: int")
            ("+compareTo(o: MANVPrioritized): int",
            "+compareTo(o: object): int");

    topToBottom(45)(Comparable, MANVPrioritized);
    drawObject(MANVPrioritized);
    link(inheritance)(pathStepY(MANVPrioritized.n, Comparable.s, 20));

    Class.MANVEvent("MANVEvent")
            ("-raw: String")
            ("+MANVEvent(raw: String)",
            "+isResult: boolean",
            "+isImportant: boolean",
            "+fromString(raw: String): MANVEvent",
            "+createCorbaMessages: AbstractList<CorbaMessageContainer>",
            "+toString: String",
            "+getRaw: String");

    topToBottom(45)(MANVPrioritized, MANVEvent);
    drawObject(MANVEvent);
    link(inheritance)(pathStepY(MANVEvent.n, MANVPrioritized.s, 20));

    
    Class.MANVResult("MANVResult")
            ("#status: boolean",
            "#subResult: MANVResult")
            ("+MANVResult(raw: String, subResult: MANVResult)",
            "+getData: String",
            "+isResult: boolean",
            "+isImportant: boolean",
            "+isComposite: boolean",
            "+getChildList(commandQueue: BlockingQueue<MANVCommand>): AbstractList<CorbaMessageContainer>",
            "+getSubResult(result: MANVResult, toString: String): MANVResult");

    Class.MANVCommand("MANVCommand")
            ("#command: String",
            "-result: MANVResult",
            "#id: int",
            "#maxId: static int",
            "-resultLatch: CountDownLatch")
            ("+MANVCommand(command: String, priority: int)",
            "+getCommand: String",
            "-getUniqueId: int",
            "+setResult(result: MANVResult)",
            "+getResult: MANVResult");

    topToBottom(45)(MANVEvent, MANVResult);
    link(inheritance)(pathStepY(MANVResult.n, MANVEvent.s, 20));

    Class.MANVDataReceived("MANVDataReceived")
            ("-source: iZigBit",
            "-data: String")
            ("+MANVDataReceived(raw: String)",
            "+isImportant: boolean",
            "+parseRaw(raw: String)");

    Class.MANVChildLost("MANVChildLost")
            ("#source: iZigBit")
            ("+MANVChildLost(raw: String)",
            "+isImportant: boolean",
            "+createCorbaMessages: AbstractList<CorbaMessageContainer>");

    Class.MANVChildJoined("MANVChildJoined")
            ("#source: iZigBit")
            ("+MANVChildLost(raw: String)",
            "+isImportant: boolean",
            "+createCorbaMessages: AbstractList<CorbaMessageContainer>");

    leftToRight.top(45)(MANVResult, MANVDataReceived);
    topToBottom(45)(MANVChildJoined, MANVChildLost, MANVDataReceived);
    leftToRight.top(45)(MANVCommand, MANVEvent);
    drawObjects(MANVResult, MANVCommand);
    link(inheritance)(pathStepY(MANVCommand.n, MANVPrioritized.s, 20));
    drawObjects(MANVDataReceived, MANVChildLost, MANVChildJoined);
    link(inheritance)(pathStepY(MANVDataReceived.n, MANVEvent.s, 20));
    link(inheritance)(pathStepX(MANVChildLost.w, MANVEvent.e, -30));
    link(inheritance)(pathStepX(MANVChildJoined.w, MANVEvent.e, -30));


    Class.MANVStatusMessage("MANVStatusMessage")
            ("#pulse: short",
            "#breathing: short",
            "-alertStatus: HashMap<Integer, Integer>")
            ("+MANVStatusMessage(raw: String)",
            "+parseRaw(raw: String)",
            "+createCorbaMessages: AbstractList<CorbaMessageContainer>",
            "+toString: String");

    topToBottom(110)(MANVDataReceived, MANVStatusMessage);
    drawObject(MANVStatusMessage);
    link(inheritance)(pathStepY(MANVStatusMessage.n, MANVDataReceived.s, 20));

    Class MANVGsn("MANVGsn")
            ("-myRaw: String")
            ("+MANVGsn(raw: String)",
            "+isCcomposite: boolean",
            "+getData: String",
            "+addSubResult(result: MANVResult)");

    Class MANVChildrenList("MANVChildrenList")
            ()
            ("+MANVChildrenList(raw: String)",
            "+isComposite: boolean",
            "+getChildList(commandQueue: BlockingQueue<MANVCommand>",
            "+addSubResult(result: MANVResult)");

    leftToRight.top(45)(MANVChildrenList, MANVGsn, MANVStatusMessage);
    drawObjects(MANVGsn, MANVChildrenList);
    link(inheritance)(pathStepY(MANVGsn.n, MANVResult.s, 20));
    link(inheritance)(pathStepY(MANVChildrenList.n, MANVResult.s, 20));

endfig;
end





















