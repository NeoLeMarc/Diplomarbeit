package CORBA_Server;


/**
 * Generated from IDL interface "Commands".
 *
 * @author JacORB IDL compiler V 2.3.1, 27-May-2009
 * @version generated at 06.05.2010 12:06:05
 */

public final class CommandsHelper
{
	public static void insert (final org.omg.CORBA.Any any, final CORBA_Server.Commands s)
	{
			any.insert_Object(s);
	}
	public static CORBA_Server.Commands extract(final org.omg.CORBA.Any any)
	{
		return narrow(any.extract_Object()) ;
	}
	public static org.omg.CORBA.TypeCode type()
	{
		return org.omg.CORBA.ORB.init().create_interface_tc("IDL:CORBA_Server/Commands:1.0", "Commands");
	}
	public static String id()
	{
		return "IDL:CORBA_Server/Commands:1.0";
	}
	public static Commands read(final org.omg.CORBA.portable.InputStream in)
	{
		return narrow(in.read_Object(CORBA_Server._CommandsStub.class));
	}
	public static void write(final org.omg.CORBA.portable.OutputStream _out, final CORBA_Server.Commands s)
	{
		_out.write_Object(s);
	}
	public static CORBA_Server.Commands narrow(final org.omg.CORBA.Object obj)
	{
		if (obj == null)
		{
			return null;
		}
		else if (obj instanceof CORBA_Server.Commands)
		{
			return (CORBA_Server.Commands)obj;
		}
		else if (obj._is_a("IDL:CORBA_Server/Commands:1.0"))
		{
			CORBA_Server._CommandsStub stub;
			stub = new CORBA_Server._CommandsStub();
			stub._set_delegate(((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate());
			return stub;
		}
		else
		{
			throw new org.omg.CORBA.BAD_PARAM("Narrow failed");
		}
	}
	public static CORBA_Server.Commands unchecked_narrow(final org.omg.CORBA.Object obj)
	{
		if (obj == null)
		{
			return null;
		}
		else if (obj instanceof CORBA_Server.Commands)
		{
			return (CORBA_Server.Commands)obj;
		}
		else
		{
			CORBA_Server._CommandsStub stub;
			stub = new CORBA_Server._CommandsStub();
			stub._set_delegate(((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate());
			return stub;
		}
	}
}
