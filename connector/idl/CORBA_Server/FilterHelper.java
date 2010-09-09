package CORBA_Server;

/**
 * Generated from IDL valuetype "Filter".
 *
 * @author JacORB IDL compiler V 2.3.1, 27-May-2009
 * @version generated at 20.03.2010 14:29:05
 */

public abstract class FilterHelper
{
	private static org.omg.CORBA.TypeCode type = null;
	public static void insert (org.omg.CORBA.Any a, CORBA_Server.Filter v)
	{
		a.insert_Value (v, v._type());
	}
	public static CORBA_Server.Filter extract (org.omg.CORBA.Any a)
	{
		return (CORBA_Server.Filter)a.extract_Value();
	}
	public static org.omg.CORBA.TypeCode type()
	{
		if (type == null)
			type = org.omg.CORBA.ORB.init().create_value_tc ("IDL:CORBA_Server/Filter:1.0", "Filter", (short)0, null, new org.omg.CORBA.ValueMember[] {new org.omg.CORBA.ValueMember ("", "IDL:CORBA_Server/Subscriber:1.0", "Filter", "1.0", org.omg.CORBA.ORB.init().create_interface_tc("IDL:CORBA_Server/Subscriber:1.0", "Subscriber"), null, (short)0), new org.omg.CORBA.ValueMember ("", "IDL:*primitive*:1.0", "Filter", "1.0", org.omg.CORBA.ORB.init().get_primitive_tc(org.omg.CORBA.TCKind.from_int(5)), null, (short)0)});
		return type;
	}
	public static String id()
	{
		return "IDL:CORBA_Server/Filter:1.0";
	}
	public static CORBA_Server.Filter read (org.omg.CORBA.portable.InputStream is)
	{
		return (CORBA_Server.Filter)((org.omg.CORBA_2_3.portable.InputStream)is).read_value ("IDL:CORBA_Server/Filter:1.0");
	}
	public static void write (org.omg.CORBA.portable.OutputStream os, CORBA_Server.Filter val)
	{
((org.omg.CORBA_2_3.portable.OutputStream)os).write_value (val, "IDL:CORBA_Server/Filter:1.0");
	}
	public static Filter init( org.omg.CORBA.ORB orb, CORBA_Server.Subscriber s, int registered_events )
	{
		FilterValueFactory f = ( FilterValueFactory )((org.omg.CORBA_2_3.ORB)orb).lookup_value_factory(id());
		if (f == null)
			throw new org.omg.CORBA.MARSHAL( 1, org.omg.CORBA.CompletionStatus.COMPLETED_NO );
		return f.init( s, registered_events );
	}
}
