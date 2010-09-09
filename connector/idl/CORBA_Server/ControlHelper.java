package CORBA_Server;


/**
 * Generated from IDL interface "Control".
 *
 * @author JacORB IDL compiler V 2.3.1, 27-May-2009
 * @version generated at 06.05.2010 14:17:36
 */

public final class ControlHelper
{
	public static void insert (final org.omg.CORBA.Any any, final CORBA_Server.Control s)
	{
			any.insert_Object(s);
	}
	public static CORBA_Server.Control extract(final org.omg.CORBA.Any any)
	{
		return narrow(any.extract_Object()) ;
	}
	public static org.omg.CORBA.TypeCode type()
	{
		return org.omg.CORBA.ORB.init().create_interface_tc("IDL:CORBA_Server/Control:1.0", "Control");
	}
	public static String id()
	{
		return "IDL:CORBA_Server/Control:1.0";
	}
	public static Control read(final org.omg.CORBA.portable.InputStream in)
	{
		return narrow(in.read_Object(CORBA_Server._ControlStub.class));
	}
	public static void write(final org.omg.CORBA.portable.OutputStream _out, final CORBA_Server.Control s)
	{
		_out.write_Object(s);
	}
	public static CORBA_Server.Control narrow(final org.omg.CORBA.Object obj)
	{
		if (obj == null)
		{
			return null;
		}
		else if (obj instanceof CORBA_Server.Control)
		{
			return (CORBA_Server.Control)obj;
		}
		else if (obj._is_a("IDL:CORBA_Server/Control:1.0"))
		{
			CORBA_Server._ControlStub stub;
			stub = new CORBA_Server._ControlStub();
			stub._set_delegate(((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate());
			return stub;
		}
		else
		{
			throw new org.omg.CORBA.BAD_PARAM("Narrow failed");
		}
	}
	public static CORBA_Server.Control unchecked_narrow(final org.omg.CORBA.Object obj)
	{
		if (obj == null)
		{
			return null;
		}
		else if (obj instanceof CORBA_Server.Control)
		{
			return (CORBA_Server.Control)obj;
		}
		else
		{
			CORBA_Server._ControlStub stub;
			stub = new CORBA_Server._ControlStub();
			stub._set_delegate(((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate());
			return stub;
		}
	}
}
