package CORBA_Server.QueriesPackage;

/**
 * Generated from IDL alias "DataSeq".
 *
 * @author JacORB IDL compiler V 2.3.1, 27-May-2009
 * @version generated at 01.03.2010 03:18:57
 */

public final class DataSeqHolder
	implements org.omg.CORBA.portable.Streamable
{
	public Common.CORBA_DataMessage[] value;

	public DataSeqHolder ()
	{
	}
	public DataSeqHolder (final Common.CORBA_DataMessage[] initial)
	{
		value = initial;
	}
	public org.omg.CORBA.TypeCode _type ()
	{
		return DataSeqHelper.type ();
	}
	public void _read (final org.omg.CORBA.portable.InputStream in)
	{
		value = DataSeqHelper.read (in);
	}
	public void _write (final org.omg.CORBA.portable.OutputStream out)
	{
		DataSeqHelper.write (out,value);
	}
}
