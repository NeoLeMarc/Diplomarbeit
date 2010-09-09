package Common;


/**
 * Generated from IDL struct "CORBA_EventMessage".
 *
 * @author JacORB IDL compiler V 2.3.1, 27-May-2009
 * @version generated at 06.05.2010 14:17:36
 */

public final class CORBA_EventMessageHelper
{
	private static org.omg.CORBA.TypeCode _type = null;
	public static org.omg.CORBA.TypeCode type ()
	{
		if (_type == null)
		{
			_type = org.omg.CORBA.ORB.init().create_struct_tc(Common.CORBA_EventMessageHelper.id(),"CORBA_EventMessage",new org.omg.CORBA.StructMember[]{new org.omg.CORBA.StructMember("nodeID", Common.NodeID_tHelper.type(), null),new org.omg.CORBA.StructMember("groupID", Common.GroupID_tHelper.type(), null),new org.omg.CORBA.StructMember("eventType", Common.CORBA_EventTypeHelper.type(), null),new org.omg.CORBA.StructMember("connectorTimestamp", Common.Timestamp_tHelper.type(), null),new org.omg.CORBA.StructMember("serverTimestamp", Common.Timestamp_tHelper.type(), null)});
		}
		return _type;
	}

	public static void insert (final org.omg.CORBA.Any any, final Common.CORBA_EventMessage s)
	{
		any.type(type());
		write( any.create_output_stream(),s);
	}

	public static Common.CORBA_EventMessage extract (final org.omg.CORBA.Any any)
	{
		return read(any.create_input_stream());
	}

	public static String id()
	{
		return "IDL:Common/CORBA_EventMessage:1.0";
	}
	public static Common.CORBA_EventMessage read (final org.omg.CORBA.portable.InputStream in)
	{
		Common.CORBA_EventMessage result = new Common.CORBA_EventMessage();
		result.nodeID=in.read_wstring();
		result.groupID=in.read_short();
		result.eventType=in.read_long();
		result.connectorTimestamp=in.read_longlong();
		result.serverTimestamp=in.read_longlong();
		return result;
	}
	public static void write (final org.omg.CORBA.portable.OutputStream out, final Common.CORBA_EventMessage s)
	{
		out.write_wstring(s.nodeID);
		out.write_short(s.groupID);
		out.write_long(s.eventType);
		out.write_longlong(s.connectorTimestamp);
		out.write_longlong(s.serverTimestamp);
	}
}
