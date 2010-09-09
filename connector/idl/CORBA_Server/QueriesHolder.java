package CORBA_Server;

/**
 * Generated from IDL interface "Queries".
 *
 * @author JacORB IDL compiler V 2.3.1, 27-May-2009
 * @version generated at 01.03.2010 03:18:57
 */

public final class QueriesHolder	implements org.omg.CORBA.portable.Streamable{
	 public Queries value;
	public QueriesHolder()
	{
	}
	public QueriesHolder (final Queries initial)
	{
		value = initial;
	}
	public org.omg.CORBA.TypeCode _type()
	{
		return QueriesHelper.type();
	}
	public void _read (final org.omg.CORBA.portable.InputStream in)
	{
		value = QueriesHelper.read (in);
	}
	public void _write (final org.omg.CORBA.portable.OutputStream _out)
	{
		QueriesHelper.write (_out,value);
	}
}
