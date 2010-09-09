package CORBA_Server;

import org.omg.PortableServer.POA;

/**
 * Generated from IDL interface "Queries".
 *
 * @author JacORB IDL compiler V 2.3.1, 27-May-2009
 * @version generated at 01.03.2010 03:18:57
 */

public class QueriesPOATie
	extends QueriesPOA
{
	private QueriesOperations _delegate;

	private POA _poa;
	public QueriesPOATie(QueriesOperations delegate)
	{
		_delegate = delegate;
	}
	public QueriesPOATie(QueriesOperations delegate, POA poa)
	{
		_delegate = delegate;
		_poa = poa;
	}
	public CORBA_Server.Queries _this()
	{
		return CORBA_Server.QueriesHelper.narrow(_this_object());
	}
	public CORBA_Server.Queries _this(org.omg.CORBA.ORB orb)
	{
		return CORBA_Server.QueriesHelper.narrow(_this_object(orb));
	}
	public QueriesOperations _delegate()
	{
		return _delegate;
	}
	public void _delegate(QueriesOperations delegate)
	{
		_delegate = delegate;
	}
	public POA _default_POA()
	{
		if (_poa != null)
		{
			return _poa;
		}
		return super._default_POA();
	}
	public Common.CORBA_EventMessage[] getEventsFromGroup(long starting_from, short group_id)
	{
		return _delegate.getEventsFromGroup(starting_from,group_id);
	}

	public Common.CORBA_Node[] getAllNodes()
	{
		return _delegate.getAllNodes();
	}

	public Common.CORBA_EventMessage[] getEventsFromNode(long starting_from, Common.CORBA_Node node)
	{
		return _delegate.getEventsFromNode(starting_from,node);
	}

	public Common.CORBA_Node[] getAllNodesFromGroup(short group_id)
	{
		return _delegate.getAllNodesFromGroup(group_id);
	}

	public Common.CORBA_DataMessage[] getDataFromNode(long starting_from, Common.CORBA_Node node)
	{
		return _delegate.getDataFromNode(starting_from,node);
	}

	public Common.CORBA_EventMessage[] getEvents(long starting_from)
	{
		return _delegate.getEvents(starting_from);
	}

	public Common.CORBA_DataMessage[] getData(long starting_from)
	{
		return _delegate.getData(starting_from);
	}

	public Common.CORBA_DataMessage[] getDataFromGroup(long starting_from, short group_id)
	{
		return _delegate.getDataFromGroup(starting_from,group_id);
	}

}
