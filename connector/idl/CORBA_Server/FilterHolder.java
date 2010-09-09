package CORBA_Server;

/**
 * Generated from IDL valuetype "Filter".
 *
 * @author JacORB IDL compiler V 2.3.1, 27-May-2009
 * @version generated at 20.03.2010 14:29:05
 */

public final class FilterHolder
	implements org.omg.CORBA.portable.Streamable
{
	public CORBA_Server.Filter value;
	public FilterHolder () {}
	public FilterHolder (final CORBA_Server.Filter initial)
	{
		value = initial;
	}
	public void _read (final org.omg.CORBA.portable.InputStream is)
	{
		value = CORBA_Server.FilterHelper.read (is);
	}
	public void _write (final org.omg.CORBA.portable.OutputStream os)
	{
		CORBA_Server.FilterHelper.write (os, value);
	}
	public org.omg.CORBA.TypeCode _type ()
	{
		return value._type ();
	}
}
