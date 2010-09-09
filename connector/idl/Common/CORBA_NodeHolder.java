package Common;

/**
 * Generated from IDL struct "CORBA_Node".
 *
 * @author JacORB IDL compiler V 2.3.1, 27-May-2009
 * @version generated at 06.05.2010 14:17:36
 */

public final class CORBA_NodeHolder
	implements org.omg.CORBA.portable.Streamable
{
	public Common.CORBA_Node value;

	public CORBA_NodeHolder ()
	{
	}
	public CORBA_NodeHolder(final Common.CORBA_Node initial)
	{
		value = initial;
	}
	public org.omg.CORBA.TypeCode _type ()
	{
		return Common.CORBA_NodeHelper.type ();
	}
	public void _read(final org.omg.CORBA.portable.InputStream _in)
	{
		value = Common.CORBA_NodeHelper.read(_in);
	}
	public void _write(final org.omg.CORBA.portable.OutputStream _out)
	{
		Common.CORBA_NodeHelper.write(_out, value);
	}
}
