package Common;

/**
 * Generated from IDL struct "CORBA_DataMessage".
 *
 * @author JacORB IDL compiler V 2.3.1, 27-May-2009
 * @version generated at 06.05.2010 14:17:36
 */

public final class CORBA_DataMessageHolder
	implements org.omg.CORBA.portable.Streamable
{
	public Common.CORBA_DataMessage value;

	public CORBA_DataMessageHolder ()
	{
	}
	public CORBA_DataMessageHolder(final Common.CORBA_DataMessage initial)
	{
		value = initial;
	}
	public org.omg.CORBA.TypeCode _type ()
	{
		return Common.CORBA_DataMessageHelper.type ();
	}
	public void _read(final org.omg.CORBA.portable.InputStream _in)
	{
		value = Common.CORBA_DataMessageHelper.read(_in);
	}
	public void _write(final org.omg.CORBA.portable.OutputStream _out)
	{
		Common.CORBA_DataMessageHelper.write(_out, value);
	}
}
