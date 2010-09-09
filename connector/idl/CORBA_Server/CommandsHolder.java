package CORBA_Server;

/**
 * Generated from IDL interface "Commands".
 *
 * @author JacORB IDL compiler V 2.3.1, 27-May-2009
 * @version generated at 06.05.2010 12:06:05
 */

public final class CommandsHolder	implements org.omg.CORBA.portable.Streamable{
	 public Commands value;
	public CommandsHolder()
	{
	}
	public CommandsHolder (final Commands initial)
	{
		value = initial;
	}
	public org.omg.CORBA.TypeCode _type()
	{
		return CommandsHelper.type();
	}
	public void _read (final org.omg.CORBA.portable.InputStream in)
	{
		value = CommandsHelper.read (in);
	}
	public void _write (final org.omg.CORBA.portable.OutputStream _out)
	{
		CommandsHelper.write (_out,value);
	}
}
