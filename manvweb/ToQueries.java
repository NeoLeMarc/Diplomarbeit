/**
 * 
 */
import java.sql.Timestamp;
import CORBA_Server.Queries;
import CORBA_Server.QueriesOperations;
import Common.CORBA_DataMessage;
import Common.CORBA_EventMessage;
import Common.CORBA_Node;
import Common.CORBA_StatusMessage;

/**
 * @author Jan
 *
 */
class Logger {
    public void debug(String input){
        System.err.println(input);
    }

    public static Logger getLogger(String name){
        return new Logger();
    }
}

public class ToQueries implements QueriesOperations {
	private static Logger logger = Logger.getLogger("ToQueries");
	private Queries queries;
	public ToQueries(Queries queries) {
		this.queries = queries;
	}

	/* (non-Javadoc)
	 * @see CORBA_Server.QueriesOperations#getAllNodes()
	 */
	@Override
	public CORBA_Node[] getAllNodes() {
		logger.debug("getAllNodes()");
		return this.queries.getAllNodes();
	}
	
	@Override
	public CORBA_StatusMessage[] getAllNodesStatus() {
		logger.debug("getAllNodesStatus()");
		return this.queries.getAllNodesStatus();
	}

	/* (non-Javadoc)
	 * @see CORBA_Server.QueriesOperations#getAllNodesFromGroup(short)
	 */
	@Override
	public CORBA_Node[] getAllNodesFromGroup(short groupId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see CORBA_Server.QueriesOperations#getData(long)
	 */
	@Override
	public CORBA_DataMessage[] getData(long startingFrom) {
		logger.debug("getData(" + new Timestamp(startingFrom) + ")");
		return this.queries.getData(startingFrom);
	}

	/* (non-Javadoc)
	 * @see CORBA_Server.QueriesOperations#getDataFromGroup(long, short)
	 */
	@Override
	public CORBA_DataMessage[] getDataFromGroup(long startingFrom, short groupId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see CORBA_Server.QueriesOperations#getDataFromNode(long, Common.CORBA_Node)
	 */
	@Override
	public CORBA_DataMessage[] getDataFromNode(long startingFrom, CORBA_Node node) {
		logger.debug("getDataFromNode(" + new Timestamp(startingFrom) + 
						", nodeId=" + node.node_id + ", groupId=" + node.group_id + ")" );
		return this.queries.getDataFromNode(startingFrom, node);
	}

	/* (non-Javadoc)
	 * @see CORBA_Server.QueriesOperations#getEvents(long)
	 */
	@Override
	public CORBA_EventMessage[] getEvents(long startingFrom) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see CORBA_Server.QueriesOperations#getEventsFromGroup(long, short)
	 */
	@Override
	public CORBA_EventMessage[] getEventsFromGroup(long startingFrom, short groupId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see CORBA_Server.QueriesOperations#getEventsFromNode(long, Common.CORBA_Node)
	 */
	@Override
	public CORBA_EventMessage[] getEventsFromNode(long startingFrom, CORBA_Node node) {
		logger.debug("getEventsFromNode(" + new Timestamp(startingFrom) + 
				", nodeId=" + node.node_id + ", groupId=" + node.group_id + ")" );
		return this.queries.getEventsFromNode(startingFrom, node);
	}

	@Override
	public CORBA_StatusMessage getStatus(CORBA_Node node) {
		logger.debug("getStatus(nodeId=" + node.node_id + ", groupId=" + node.group_id + ")" );
		return this.queries.getStatus(node);
	}
}
