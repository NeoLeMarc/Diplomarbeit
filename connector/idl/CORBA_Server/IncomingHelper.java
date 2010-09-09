package CORBA_Server;


/**
 * Generated from IDL interface "Incoming".
 *
 * @author JacORB IDL compiler V 2.3.1, 27-May-2009
 * @version generated at 21.02.2010 18:56:44
 */

public final class IncomingHelper
{
	public static void insert (final org.omg.CORBA.Any any, final CORBA_Server.Incoming s)
	{
			any.insert_Object(s);
	}
	public static CORBA_Server.Incoming extract(final org.omg.CORBA.Any any)
	{
		return narrow(any.extract_Object()) ;
	}
	public static org.omg.CORBA.TypeCode type()
	{
		return org.omg.CORBA.ORB.init().create_interface_tc("IDL:CORBA_Server/Incoming:1.0", "Incoming");
	}
	public static String id()
	{
		return "IDL:CORBA_Server/Incoming:1.0";
	}
	public static Incoming read(final org.omg.CORBA.portable.InputStream in)
	{
		return narrow(in.read_Object(CORBA_Server._IncomingStub.class));
	}
	public static void write(final org.omg.CORBA.portable.OutputStream _out, final CORBA_Server.Incoming s)
	{
		_out.write_Object(s);
	}
	public static CORBA_Server.Incoming narrow(final org.omg.CORBA.Object obj)
	{
		if (obj == null)
		{
			return null;
		}
		else if (obj instanceof CORBA_Server.Incoming)
		{
			return (CORBA_Server.Incoming)obj;
		}
		else if (obj._is_a("IDL:CORBA_Server/Incoming:1.0"))
		{
			CORBA_Server._IncomingStub stub;
			stub = new CORBA_Server._IncomingStub();
			stub._set_delegate(((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate());
			return stub;
		}
		else
		{
			throw new org.omg.CORBA.BAD_PARAM("Narrow failed");
		}
	}
	public static CORBA_Server.Incoming unchecked_narrow(final org.omg.CORBA.Object obj)
	{
		if (obj == null)
		{
			return null;
		}
		else if (obj instanceof CORBA_Server.Incoming)
		{
			return (CORBA_Server.Incoming)obj;
		}
		else
		{
			CORBA_Server._IncomingStub stub;
			stub = new CORBA_Server._IncomingStub();
			stub._set_delegate(((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate());
			return stub;
		}
	}
}
