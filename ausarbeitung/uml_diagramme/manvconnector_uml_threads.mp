input metauml;
beginfig(1);

    % Hauptklasse
    Class.MANVConnector("MANVConnector")
            ("-commandQueue: BlockingQueue<MANVCommand>",
             "-eventQueue: BlockingQueue<MANVEvent>",
             "-resultQueue: BlockingQueue<MANVCommand>",
             "-corbaSender: CorbaSender",
             "-commandsImpl: CommandsImpl")
            ("+main(args: String[]): void");

    
    drawObjects(MANVConnector);

    Class.CORBA_Node("CORBA_Node")
            ()
            ();

    Class.CommandsImpl("CommandsImpl")
            ()
            ("+disableAlert(node: CORBA_Node)",
            "+enableAlert(node: CORBA_Node)",
            "+toggleAlert(node: CORBA_Node)",
            "+mute(node: CORBA_Node)");

    leftToRight.center(200)(MANVConnector, CommandsImpl);
    topToBottom(45)(CORBA_Node, CommandsImpl);
    drawObjects(CORBA_Node, CommandsImpl);
    link(inheritance)(pathStepY(CommandsImpl.n, CORBA_Node.s, 20));

    Class.BlockingQueueMANVEvent("BlockingQueue<MANVEvent>")
            ()
            ();

    Class.BlockingQueueMANVCommand("BlockingQueue<MANVCommand>")
            ()
            ();

    topToBottom(45)(BlockingQueueMANVEvent, MANVConnector);
    leftToRight.top(60)(BlockingQueueMANVEvent, BlockingQueueMANVCommand);
    drawObjects(BlockingQueueMANVEvent, BlockingQueueMANVCommand);

    % Threads
    Class.Thread("Thread")
            ()
            ("run: void");

%    leftToRight.top(500)(Comparable, Thread);


    Class.SocketReader("SocketReader")
            ("-eventQueue: BlockingQueue<MANVEvent>",
            "-lastResult: MANVResult",
            "-resultSemaphore: Semaphore",
            "-serialIn: BufferReader")
            ("+SocketReader(serialIn: BufferedReader, eventQueue: BlockingQueue<MANVEvent>)",
            "+setLastResult(MANVResult: result)",
            "+getLastResult(MANVResult: result)",
            "run: void");

    Class.SocketWriter("SocketWriter")
            ("-commandQueue: BlockingQueue<MANVCommand>",
            "-resultQueue: BlockingQueue<MANVCommand>",
            "-serialOut: PrintWriter",
            "-reader: SocketReader",
            "-lastResult: MANVResult")
            ("+SocketWriter(serialOut: PrintWriter, commandQueue: <MANVCommand>, reader: SocketReader)",
            "+writeLine(line: String)",
            "+run: void");

    Class.CorbaSender("CorbaSender")
            ("-eventQueue: BlockingQueue<MANVEvent>",
            "-serverIncoming: Incoming")
            ("+CorbaSender(eventQueue: BlockingQueue<MANVEvent>, serverIncoming: Incoming)",
            "+run: void");

    topToBottom(45)(Thread, SocketWriter);
    topToBottom(45)(SocketWriter, BlockingQueueMANVEvent);
    leftToRight.top(45)(SocketReader, SocketWriter);
    leftToRight.top(45)(SocketWriter, CorbaSender);
    drawObjects(Thread, SocketReader, SocketWriter, CorbaSender);
    link(inheritance)(pathStepY(SocketReader.n, Thread.s, 20));
    link(inheritance)(pathStepY(SocketWriter.n, Thread.s, 20));
    link(inheritance)(pathStepY(CorbaSender.n, Thread.s, 20));


    % Corba
    Class.CorbaMessageContainer("CorbaMessageContainer")
            ()
            ("+send(serverIncoming: Incoming)");

    Class.CorbaDataMessageContainer("CorbaDataMessageContainer")
            ("-message: CORBA_DataMessage")
            ("+CorbaDataMessageContainer(message: CORBA_DataMessage)",
            "+send(serverIncoming: Incoming)",
            "+toString: String");

    Class.CorbaEventMessageContainer("CorbaEventMessageContainer")
            ("-message: CORBA_EventMessage")
            ("+send(serverIncoming: Incoming)",
            "+toString: String)");

    topToBottom(100)(SocketReader, CorbaMessageContainer, CorbaDataMessageContainer);
    leftToRight.top(45)(CorbaDataMessageContainer, CorbaEventMessageContainer);
    drawObjects(CorbaMessageContainer, CorbaDataMessageContainer, CorbaEventMessageContainer);
    link(inheritance)(pathStepY(CorbaDataMessageContainer.n, CorbaMessageContainer.s, 20));
    link(inheritance)(pathStepY(CorbaEventMessageContainer.n, CorbaMessageContainer.s, 20));



    % Assoziationen
%    link(association)(BlockingQueueMANVEvent.s -- MANVConnector.n);
%    link(association)(BlockingQueueMANVCommand.s -- MANVConnector.e);
    clink(association)(BlockingQueueMANVEvent, MANVConnector);

%    clink(association)(BlockingQueueMANVCommand, MANVConnector);
    link(association)(pathStepX(BlockingQueueMANVCommand.s, MANVConnector.ne - (0, 20), 20));

    link(association)(pathManhattanX(BlockingQueueMANVEvent.w, SocketReader.s));

    % Beschriftungen
    link(association)(pathStepY(BlockingQueueMANVCommand.n + (20, 0), SocketWriter.e, 100));
    link(association)(pathStepX(BlockingQueueMANVCommand.e, CorbaSender.s, 40));

    link(association)(pathStepX(MANVConnector.e, CommandsImpl.w, 115));
    item(iGuard)("eventQueue")(obj.nw = BlockingQueueMANVEvent.s + (0, -15));
    item(iGuard)("eventQueue")(obj.n  = CorbaSender.s - (80, 50));
    item(iGuard)("eventQueue")(obj.e  = SocketReader.s - (-80, 50));
    item(iGuard)("commandQueue")(obj.w = MANVConnector.ne + (10, -16));
    item(iGuard)("commandsImpl")(obj.w = MANVConnector.ne + (10, -48));
    item(iGuard)("commandQueue")(obj.w = SocketWriter.se + (24, -2));


endfig;
end






















