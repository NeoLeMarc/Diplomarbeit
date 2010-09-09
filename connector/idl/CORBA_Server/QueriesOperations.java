package CORBA_Server;


/**
 * Generated from IDL interface "Queries".
 *
 * @author JacORB IDL compiler V 2.3.1, 27-May-2009
 * @version generated at 01.03.2010 03:18:57
 */

public interface QueriesOperations
{
	/* constants */
	/* operations  */
	Common.CORBA_Node[] getAllNodes();
	Common.CORBA_Node[] getAllNodesFromGroup(short group_id);
	Common.CORBA_DataMessage[] getData(long starting_from);
	Common.CORBA_DataMessage[] getDataFromGroup(long starting_from, short group_id);
	Common.CORBA_DataMessage[] getDataFromNode(long starting_from, Common.CORBA_Node node);
	Common.CORBA_EventMessage[] getEvents(long starting_from);
	Common.CORBA_EventMessage[] getEventsFromGroup(long starting_from, short group_id);
	Common.CORBA_EventMessage[] getEventsFromNode(long starting_from, Common.CORBA_Node node);
}
