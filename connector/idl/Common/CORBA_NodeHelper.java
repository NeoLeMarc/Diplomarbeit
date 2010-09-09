package Common;


/**
 * Generated from IDL struct "CORBA_Node".
 *
 * @author JacORB IDL compiler V 2.3.1, 27-May-2009
 * @version generated at 06.05.2010 14:17:36
 */

public final class CORBA_NodeHelper
{
	private static org.omg.CORBA.TypeCode _type = null;
	public static org.omg.CORBA.TypeCode type ()
	{
		if (_type == null)
		{
			_type = org.omg.CORBA.ORB.init().create_struct_tc(Common.CORBA_NodeHelper.id(),"CORBA_Node",new org.omg.CORBA.StructMember[]{new org.omg.CORBA.StructMember("node_id", Common.NodeID_tHelper.type(), null),new org.omg.CORBA.StructMember("group_id", Common.GroupID_tHelper.type(), null)});
		}
		return _type;
	}

	public static void insert (final org.omg.CORBA.Any any, final Common.CORBA_Node s)
	{
		any.type(type());
		write( any.create_output_stream(),s);
	}

	public static Common.CORBA_Node extract (final org.omg.CORBA.Any any)
	{
		return read(any.create_input_stream());
	}

	public static String id()
	{
		return "IDL:Common/CORBA_Node:1.0";
	}
	public static Common.CORBA_Node read (final org.omg.CORBA.portable.InputStream in)
	{
		Common.CORBA_Node result = new Common.CORBA_Node();
		result.node_id=in.read_wstring();
		result.group_id=in.read_short();
		return result;
	}
	public static void write (final org.omg.CORBA.portable.OutputStream out, final Common.CORBA_Node s)
	{
		out.write_wstring(s.node_id);
		out.write_short(s.group_id);
	}
}
