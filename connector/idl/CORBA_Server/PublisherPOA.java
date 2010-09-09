package CORBA_Server;


/**
 * Generated from IDL interface "Publisher".
 *
 * @author JacORB IDL compiler V 2.3.1, 27-May-2009
 * @version generated at 20.03.2010 14:29:05
 */

public abstract class PublisherPOA
	extends org.omg.PortableServer.Servant
	implements org.omg.CORBA.portable.InvokeHandler, CORBA_Server.PublisherOperations
{
	static private final java.util.Hashtable m_opsHash = new java.util.Hashtable();
	static
	{
		m_opsHash.put ( "setFilter_Global", new java.lang.Integer(0));
		m_opsHash.put ( "setFilter_Group", new java.lang.Integer(1));
		m_opsHash.put ( "unregister", new java.lang.Integer(2));
		m_opsHash.put ( "setDefaultFilter_Global", new java.lang.Integer(3));
		m_opsHash.put ( "register", new java.lang.Integer(4));
		m_opsHash.put ( "setDefaultFilter_Group", new java.lang.Integer(5));
		m_opsHash.put ( "setFilter_Node", new java.lang.Integer(6));
	}
	private String[] ids = {"IDL:CORBA_Server/Publisher:1.0"};
	public CORBA_Server.Publisher _this()
	{
		return CORBA_Server.PublisherHelper.narrow(_this_object());
	}
	public CORBA_Server.Publisher _this(org.omg.CORBA.ORB orb)
	{
		return CORBA_Server.PublisherHelper.narrow(_this_object(orb));
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
			case 0: // setFilter_Global
			{
				CORBA_Server.Filter _arg0=(CORBA_Server.Filter)((org.omg.CORBA_2_3.portable.InputStream)_input).read_value ("IDL:CORBA_Server/Filter:1.0");
				_out = handler.createReply();
				setFilter_Global(_arg0);
				break;
			}
			case 1: // setFilter_Group
			{
				CORBA_Server.Filter _arg0=(CORBA_Server.Filter)((org.omg.CORBA_2_3.portable.InputStream)_input).read_value ("IDL:CORBA_Server/Filter:1.0");
				short _arg1=_input.read_short();
				_out = handler.createReply();
				setFilter_Group(_arg0,_arg1);
				break;
			}
			case 2: // unregister
			{
				CORBA_Server.Subscriber _arg0=CORBA_Server.SubscriberHelper.read(_input);
				_out = handler.createReply();
				unregister(_arg0);
				break;
			}
			case 3: // setDefaultFilter_Global
			{
				CORBA_Server.Filter _arg0=(CORBA_Server.Filter)((org.omg.CORBA_2_3.portable.InputStream)_input).read_value ("IDL:CORBA_Server/Filter:1.0");
				_out = handler.createReply();
				setDefaultFilter_Global(_arg0);
				break;
			}
			case 4: // register
			{
				CORBA_Server.Filter _arg0=(CORBA_Server.Filter)((org.omg.CORBA_2_3.portable.InputStream)_input).read_value ("IDL:CORBA_Server/Filter:1.0");
				_out = handler.createReply();
				register(_arg0);
				break;
			}
			case 5: // setDefaultFilter_Group
			{
				CORBA_Server.Filter _arg0=(CORBA_Server.Filter)((org.omg.CORBA_2_3.portable.InputStream)_input).read_value ("IDL:CORBA_Server/Filter:1.0");
				short _arg1=_input.read_short();
				_out = handler.createReply();
				setDefaultFilter_Group(_arg0,_arg1);
				break;
			}
			case 6: // setFilter_Node
			{
				CORBA_Server.Filter _arg0=(CORBA_Server.Filter)((org.omg.CORBA_2_3.portable.InputStream)_input).read_value ("IDL:CORBA_Server/Filter:1.0");
				Common.CORBA_Node _arg1=Common.CORBA_NodeHelper.read(_input);
				_out = handler.createReply();
				setFilter_Node(_arg0,_arg1);
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
