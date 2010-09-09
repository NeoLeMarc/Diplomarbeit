package CORBA_Server.QueriesPackage;

/**
 * Generated from IDL alias "EventSeq".
 *
 * @author JacORB IDL compiler V 2.3.1, 27-May-2009
 * @version generated at 01.03.2010 03:18:57
 */

public final class EventSeqHolder
	implements org.omg.CORBA.portable.Streamable
{
	public Common.CORBA_EventMessage[] value;

	public EventSeqHolder ()
	{
	}
	public EventSeqHolder (final Common.CORBA_EventMessage[] initial)
	{
		value = initial;
	}
	public org.omg.CORBA.TypeCode _type ()
	{
		return EventSeqHelper.type ();
	}
	public void _read (final org.omg.CORBA.portable.InputStream in)
	{
		value = EventSeqHelper.read (in);
	}
	public void _write (final org.omg.CORBA.portable.OutputStream out)
	{
		EventSeqHelper.write (out,value);
	}
}
