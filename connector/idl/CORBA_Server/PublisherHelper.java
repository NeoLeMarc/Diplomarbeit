package CORBA_Server;


/**
 * Generated from IDL interface "Publisher".
 *
 * @author JacORB IDL compiler V 2.3.1, 27-May-2009
 * @version generated at 20.03.2010 14:29:05
 */

public final class PublisherHelper
{
	public static void insert (final org.omg.CORBA.Any any, final CORBA_Server.Publisher s)
	{
			any.insert_Object(s);
	}
	public static CORBA_Server.Publisher extract(final org.omg.CORBA.Any any)
	{
		return narrow(any.extract_Object()) ;
	}
	public static org.omg.CORBA.TypeCode type()
	{
		return org.omg.CORBA.ORB.init().create_interface_tc("IDL:CORBA_Server/Publisher:1.0", "Publisher");
	}
	public static String id()
	{
		return "IDL:CORBA_Server/Publisher:1.0";
	}
	public static Publisher read(final org.omg.CORBA.portable.InputStream in)
	{
		return narrow(in.read_Object(CORBA_Server._PublisherStub.class));
	}
	public static void write(final org.omg.CORBA.portable.OutputStream _out, final CORBA_Server.Publisher s)
	{
		_out.write_Object(s);
	}
	public static CORBA_Server.Publisher narrow(final org.omg.CORBA.Object obj)
	{
		if (obj == null)
		{
			return null;
		}
		else if (obj instanceof CORBA_Server.Publisher)
		{
			return (CORBA_Server.Publisher)obj;
		}
		else if (obj._is_a("IDL:CORBA_Server/Publisher:1.0"))
		{
			CORBA_Server._PublisherStub stub;
			stub = new CORBA_Server._PublisherStub();
			stub._set_delegate(((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate());
			return stub;
		}
		else
		{
			throw new org.omg.CORBA.BAD_PARAM("Narrow failed");
		}
	}
	public static CORBA_Server.Publisher unchecked_narrow(final org.omg.CORBA.Object obj)
	{
		if (obj == null)
		{
			return null;
		}
		else if (obj instanceof CORBA_Server.Publisher)
		{
			return (CORBA_Server.Publisher)obj;
		}
		else
		{
			CORBA_Server._PublisherStub stub;
			stub = new CORBA_Server._PublisherStub();
			stub._set_delegate(((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate());
			return stub;
		}
	}
}
