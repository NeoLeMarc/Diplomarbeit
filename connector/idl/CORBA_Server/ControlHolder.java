package CORBA_Server;

/**
 * Generated from IDL interface "Control".
 *
 * @author JacORB IDL compiler V 2.3.1, 27-May-2009
 * @version generated at 06.05.2010 14:17:36
 */

public final class ControlHolder	implements org.omg.CORBA.portable.Streamable{
	 public Control value;
	public ControlHolder()
	{
	}
	public ControlHolder (final Control initial)
	{
		value = initial;
	}
	public org.omg.CORBA.TypeCode _type()
	{
		return ControlHelper.type();
	}
	public void _read (final org.omg.CORBA.portable.InputStream in)
	{
		value = ControlHelper.read (in);
	}
	public void _write (final org.omg.CORBA.portable.OutputStream _out)
	{
		ControlHelper.write (_out,value);
	}
}
