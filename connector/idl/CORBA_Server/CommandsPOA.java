package CORBA_Server;


/**
 * Generated from IDL interface "Commands".
 *
 * @author JacORB IDL compiler V 2.3.1, 27-May-2009
 * @version generated at 06.05.2010 12:06:05
 */

public abstract class CommandsPOA
	extends org.omg.PortableServer.Servant
	implements org.omg.CORBA.portable.InvokeHandler, CORBA_Server.CommandsOperations
{
	static private final java.util.Hashtable m_opsHash = new java.util.Hashtable();
	static
	{
		m_opsHash.put ( "enableAlert", new java.lang.Integer(0));
		m_opsHash.put ( "mute", new java.lang.Integer(1));
		m_opsHash.put ( "disableAlert", new java.lang.Integer(2));
		m_opsHash.put ( "toggleAlert", new java.lang.Integer(3));
	}
	private String[] ids = {"IDL:CORBA_Server/Commands:1.0"};
	public CORBA_Server.Commands _this()
	{
		return CORBA_Server.CommandsHelper.narrow(_this_object());
	}
	public CORBA_Server.Commands _this(org.omg.CORBA.ORB orb)
	{
		return CORBA_Server.CommandsHelper.narrow(_this_object(orb));
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
			case 0: // enableAlert
			{
				Common.CORBA_Node _arg0=Common.CORBA_NodeHelper.read(_input);
				_out = handler.createReply();
				enableAlert(_arg0);
				break;
			}
			case 1: // mute
			{
				Common.CORBA_Node _arg0=Common.CORBA_NodeHelper.read(_input);
				_out = handler.createReply();
				mute(_arg0);
				break;
			}
			case 2: // disableAlert
			{
				Common.CORBA_Node _arg0=Common.CORBA_NodeHelper.read(_input);
				_out = handler.createReply();
				disableAlert(_arg0);
				break;
			}
			case 3: // toggleAlert
			{
				Common.CORBA_Node _arg0=Common.CORBA_NodeHelper.read(_input);
				_out = handler.createReply();
				toggleAlert(_arg0);
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
