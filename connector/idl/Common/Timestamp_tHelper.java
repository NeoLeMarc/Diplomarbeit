package Common;

/**
 * Generated from IDL alias "Timestamp_t".
 *
 * @author JacORB IDL compiler V 2.3.1, 27-May-2009
 * @version generated at 06.05.2010 14:17:36
 */

public final class Timestamp_tHelper
{
	private static org.omg.CORBA.TypeCode _type = null;

	public static void insert (org.omg.CORBA.Any any, long s)
	{
		any.insert_longlong(s);
	}

	public static long extract (final org.omg.CORBA.Any any)
	{
		return any.extract_longlong();
	}

	public static org.omg.CORBA.TypeCode type ()
	{
		if (_type == null)
		{
			_type = org.omg.CORBA.ORB.init().create_alias_tc(Common.Timestamp_tHelper.id(), "Timestamp_t",org.omg.CORBA.ORB.init().get_primitive_tc(org.omg.CORBA.TCKind.from_int(23)));
		}
		return _type;
	}

	public static String id()
	{
		return "IDL:Common/Timestamp_t:1.0";
	}
	public static long read (final org.omg.CORBA.portable.InputStream _in)
	{
		long _result;
		_result=_in.read_longlong();
		return _result;
	}

	public static void write (final org.omg.CORBA.portable.OutputStream _out, long _s)
	{
		_out.write_longlong(_s);
	}
}
