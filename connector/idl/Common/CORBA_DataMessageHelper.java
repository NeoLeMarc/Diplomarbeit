package Common;


/**
 * Generated from IDL struct "CORBA_DataMessage".
 *
 * @author JacORB IDL compiler V 2.3.1, 27-May-2009
 * @version generated at 06.05.2010 14:17:36
 */

public final class CORBA_DataMessageHelper
{
	private static org.omg.CORBA.TypeCode _type = null;
	public static org.omg.CORBA.TypeCode type ()
	{
		if (_type == null)
		{
			_type = org.omg.CORBA.ORB.init().create_struct_tc(Common.CORBA_DataMessageHelper.id(),"CORBA_DataMessage",new org.omg.CORBA.StructMember[]{new org.omg.CORBA.StructMember("nodeID", Common.NodeID_tHelper.type(), null),new org.omg.CORBA.StructMember("groupID", Common.GroupID_tHelper.type(), null),new org.omg.CORBA.StructMember("pulse", Common.Pulse_tHelper.type(), null),new org.omg.CORBA.StructMember("breathing", Common.Breathing_tHelper.type(), null),new org.omg.CORBA.StructMember("connectorTimestamp", Common.Timestamp_tHelper.type(), null),new org.omg.CORBA.StructMember("serverTimestamp", Common.Timestamp_tHelper.type(), null)});
		}
		return _type;
	}

	public static void insert (final org.omg.CORBA.Any any, final Common.CORBA_DataMessage s)
	{
		any.type(type());
		write( any.create_output_stream(),s);
	}

	public static Common.CORBA_DataMessage extract (final org.omg.CORBA.Any any)
	{
		return read(any.create_input_stream());
	}

	public static String id()
	{
		return "IDL:Common/CORBA_DataMessage:1.0";
	}
	public static Common.CORBA_DataMessage read (final org.omg.CORBA.portable.InputStream in)
	{
		Common.CORBA_DataMessage result = new Common.CORBA_DataMessage();
		result.nodeID=in.read_wstring();
		result.groupID=in.read_short();
		result.pulse=in.read_short();
		result.breathing=in.read_short();
		result.connectorTimestamp=in.read_longlong();
		result.serverTimestamp=in.read_longlong();
		return result;
	}
	public static void write (final org.omg.CORBA.portable.OutputStream out, final Common.CORBA_DataMessage s)
	{
		out.write_wstring(s.nodeID);
		out.write_short(s.groupID);
		out.write_short(s.pulse);
		out.write_short(s.breathing);
		out.write_longlong(s.connectorTimestamp);
		out.write_longlong(s.serverTimestamp);
	}
}
