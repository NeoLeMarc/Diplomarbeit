package CORBA_Server;


/**
 * Generated from IDL interface "Queries".
 *
 * @author JacORB IDL compiler V 2.3.1, 27-May-2009
 * @version generated at 01.03.2010 03:18:57
 */

public abstract class QueriesPOA
	extends org.omg.PortableServer.Servant
	implements org.omg.CORBA.portable.InvokeHandler, CORBA_Server.QueriesOperations
{
	static private final java.util.Hashtable m_opsHash = new java.util.Hashtable();
	static
	{
		m_opsHash.put ( "getEventsFromGroup", new java.lang.Integer(0));
		m_opsHash.put ( "getAllNodes", new java.lang.Integer(1));
		m_opsHash.put ( "getEventsFromNode", new java.lang.Integer(2));
		m_opsHash.put ( "getAllNodesFromGroup", new java.lang.Integer(3));
		m_opsHash.put ( "getDataFromNode", new java.lang.Integer(4));
		m_opsHash.put ( "getEvents", new java.lang.Integer(5));
		m_opsHash.put ( "getData", new java.lang.Integer(6));
		m_opsHash.put ( "getDataFromGroup", new java.lang.Integer(7));
	}
	private String[] ids = {"IDL:CORBA_Server/Queries:1.0"};
	public CORBA_Server.Queries _this()
	{
		return CORBA_Server.QueriesHelper.narrow(_this_object());
	}
	public CORBA_Server.Queries _this(org.omg.CORBA.ORB orb)
	{
		return CORBA_Server.QueriesHelper.narrow(_this_object(orb));
	}
	public org.omg.CORBA.portable.OutputStream _invoke(String method, org.omg.CORBA.portable.InputStream _input, org.omg.CORBA.portable.ResponseHandler handler)
		throws org.omg.CORBA.SystemException
	{
		org.omg.CORBA.portable.OutputStream _out = null;
		// do something
		// quick lookup of operation
		java.lang.Integer opsIndex = (java.lang.Integer)m_opsHash.get ( method );
		if ( null == opsIndex )
			throw new org.omg.CORBA.BAD_OPERATION(method + " not found");
		switch ( opsIndex.intValue() )
		{
			case 0: // getEventsFromGroup
			{
				long _arg0=_input.read_longlong();
				short _arg1=_input.read_short();
				_out = handler.createReply();
				CORBA_Server.QueriesPackage.EventSeqHelper.write(_out,getEventsFromGroup(_arg0,_arg1));
				break;
			}
			case 1: // getAllNodes
			{
				_out = handler.createReply();
				CORBA_Server.QueriesPackage.NodeSeqHelper.write(_out,getAllNodes());
				break;
			}
			case 2: // getEventsFromNode
			{
				long _arg0=_input.read_longlong();
				Common.CORBA_Node _arg1=Common.CORBA_NodeHelper.read(_input);
				_out = handler.createReply();
				CORBA_Server.QueriesPackage.EventSeqHelper.write(_out,getEventsFromNode(_arg0,_arg1));
				break;
			}
			case 3: // getAllNodesFromGroup
			{
				short _arg0=_input.read_short();
				_out = handler.createReply();
				CORBA_Server.QueriesPackage.NodeSeqHelper.write(_out,getAllNodesFromGroup(_arg0));
				break;
			}
			case 4: // getDataFromNode
			{
				long _arg0=_input.read_longlong();
				Common.CORBA_Node _arg1=Common.CORBA_NodeHelper.read(_input);
				_out = handler.createReply();
				CORBA_Server.QueriesPackage.DataSeqHelper.write(_out,getDataFromNode(_arg0,_arg1));
				break;
			}
			case 5: // getEvents
			{
				long _arg0=_input.read_longlong();
				_out = handler.createReply();
				CORBA_Server.QueriesPackage.EventSeqHelper.write(_out,getEvents(_arg0));
				break;
			}
			case 6: // getData
			{
				long _arg0=_input.read_longlong();
				_out = handler.createReply();
				CORBA_Server.QueriesPackage.DataSeqHelper.write(_out,getData(_arg0));
				break;
			}
			case 7: // getDataFromGroup
			{
				long _arg0=_input.read_longlong();
				short _arg1=_input.read_short();
				_out = handler.createReply();
				CORBA_Server.QueriesPackage.DataSeqHelper.write(_out,getDataFromGroup(_arg0,_arg1));
				break;
			}
		}
		return _out;
	}

	public String[] _all_interfaces(org.omg.PortableServer.POA poa, byte[] obj_id)
	{
		return ids;
	}
}
