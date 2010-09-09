package CORBA_Server;


/**
 * Generated from IDL interface "Incoming".
 *
 * @author JacORB IDL compiler V 2.3.1, 27-May-2009
 * @version generated at 21.02.2010 18:56:44
 */

public abstract class IncomingPOA
	extends org.omg.PortableServer.Servant
	implements org.omg.CORBA.portable.InvokeHandler, CORBA_Server.IncomingOperations
{
	static private final java.util.Hashtable m_opsHash = new java.util.Hashtable();
	static
	{
		m_opsHash.put ( "notify_data", new java.lang.Integer(0));
		m_opsHash.put ( "notify_event", new java.lang.Integer(1));
	}
	private String[] ids = {"IDL:CORBA_Server/Incoming:1.0"};
	public CORBA_Server.Incoming _this()
	{
		return CORBA_Server.IncomingHelper.narrow(_this_object());
	}
	public CORBA_Server.Incoming _this(org.omg.CORBA.ORB orb)
	{
		return CORBA_Server.IncomingHelper.narrow(_this_object(orb));
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
			case 0: // notify_data
			{
				Common.CORBA_DataMessage _arg0=Common.CORBA_DataMessageHelper.read(_input);
				_out = handler.createReply();
				notify_data(_arg0);
				break;
			}
			case 1: // notify_event
			{
				Common.CORBA_EventMessage _arg0=Common.CORBA_EventMessageHelper.read(_input);
				_out = handler.createReply();
				notify_event(_arg0);
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
