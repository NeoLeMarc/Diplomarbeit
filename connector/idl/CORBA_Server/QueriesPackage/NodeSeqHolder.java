package CORBA_Server.QueriesPackage;

/**
 * Generated from IDL alias "NodeSeq".
 *
 * @author JacORB IDL compiler V 2.3.1, 27-May-2009
 * @version generated at 01.03.2010 03:18:57
 */

public final class NodeSeqHolder
	implements org.omg.CORBA.portable.Streamable
{
	public Common.CORBA_Node[] value;

	public NodeSeqHolder ()
	{
	}
	public NodeSeqHolder (final Common.CORBA_Node[] initial)
	{
		value = initial;
	}
	public org.omg.CORBA.TypeCode _type ()
	{
		return NodeSeqHelper.type ();
	}
	public void _read (final org.omg.CORBA.portable.InputStream in)
	{
		value = NodeSeqHelper.read (in);
	}
	public void _write (final org.omg.CORBA.portable.OutputStream out)
	{
		NodeSeqHelper.write (out,value);
	}
}
