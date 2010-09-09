package Common;

/**
 * Generated from IDL struct "CORBA_EventMessage".
 *
 * @author JacORB IDL compiler V 2.3.1, 27-May-2009
 * @version generated at 06.05.2010 14:17:36
 */

public final class CORBA_EventMessageHolder
	implements org.omg.CORBA.portable.Streamable
{
	public Common.CORBA_EventMessage value;

	public CORBA_EventMessageHolder ()
	{
	}
	public CORBA_EventMessageHolder(final Common.CORBA_EventMessage initial)
	{
		value = initial;
	}
	public org.omg.CORBA.TypeCode _type ()
	{
		return Common.CORBA_EventMessageHelper.type ();
	}
	public void _read(final org.omg.CORBA.portable.InputStream _in)
	{
		value = Common.CORBA_EventMessageHelper.read(_in);
	}
	public void _write(final org.omg.CORBA.portable.OutputStream _out)
	{
		Common.CORBA_EventMessageHelper.write(_out, value);
	}
}
