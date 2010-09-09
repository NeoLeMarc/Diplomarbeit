package CORBA_Server;

/**
 * Generated from IDL interface "Publisher".
 *
 * @author JacORB IDL compiler V 2.3.1, 27-May-2009
 * @version generated at 20.03.2010 14:29:05
 */

public final class PublisherHolder	implements org.omg.CORBA.portable.Streamable{
	 public Publisher value;
	public PublisherHolder()
	{
	}
	public PublisherHolder (final Publisher initial)
	{
		value = initial;
	}
	public org.omg.CORBA.TypeCode _type()
	{
		return PublisherHelper.type();
	}
	public void _read (final org.omg.CORBA.portable.InputStream in)
	{
		value = PublisherHelper.read (in);
	}
	public void _write (final org.omg.CORBA.portable.OutputStream _out)
	{
		PublisherHelper.write (_out,value);
	}
}
