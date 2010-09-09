package CORBA_Server;

/**
 * Generated from IDL interface "Incoming".
 *
 * @author JacORB IDL compiler V 2.3.1, 27-May-2009
 * @version generated at 21.02.2010 18:56:44
 */

public final class IncomingHolder	implements org.omg.CORBA.portable.Streamable{
	 public Incoming value;
	public IncomingHolder()
	{
	}
	public IncomingHolder (final Incoming initial)
	{
		value = initial;
	}
	public org.omg.CORBA.TypeCode _type()
	{
		return IncomingHelper.type();
	}
	public void _read (final org.omg.CORBA.portable.InputStream in)
	{
		value = IncomingHelper.read (in);
	}
	public void _write (final org.omg.CORBA.portable.OutputStream _out)
	{
		IncomingHelper.write (_out,value);
	}
}
