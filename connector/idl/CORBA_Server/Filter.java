package CORBA_Server;

/**
 * Generated from IDL valuetype "Filter".
 *
 * @author JacORB IDL compiler V 2.3.1, 27-May-2009
 * @version generated at 20.03.2010 14:29:05
 */

public abstract class Filter
	implements org.omg.CORBA.portable.StreamableValue
{
	private String[] _truncatable_ids = {"IDL:CORBA_Server/Filter:1.0"};
	protected CORBA_Server.Subscriber sub;
	protected int registered_events;
	public abstract void notify_data(Common.CORBA_DataMessage data);

	public abstract void notify_event(Common.CORBA_EventMessage event);

	public abstract CORBA_Server.Subscriber getSubscriber();

	public abstract int getRegistered_events();

	public abstract boolean alarm_pulse();

	public abstract void alarm_pulse(boolean arg);

	public abstract boolean alarm_breathing();

	public abstract void alarm_breathing(boolean arg);

	public abstract boolean low_battery();

	public abstract void low_battery(boolean arg);

	public abstract boolean join();

	public abstract void join(boolean arg);

	public abstract boolean left();

	public abstract void left(boolean arg);

	public abstract boolean lost();

	public abstract void lost(boolean arg);

	public abstract boolean data();

	public abstract void data(boolean arg);

	public void _write (org.omg.CORBA.portable.OutputStream os)
	{
		CORBA_Server.SubscriberHelper.write(os,sub);
		os.write_ulong(registered_events);
	}

	public void _read (final org.omg.CORBA.portable.InputStream os)
	{
		sub=CORBA_Server.SubscriberHelper.read(os);
		registered_events=os.read_ulong();
	}

	public String[] _truncatable_ids()
	{
		return _truncatable_ids;
	}
	public org.omg.CORBA.TypeCode _type()
	{
		return CORBA_Server.FilterHelper.type();
	}
}
