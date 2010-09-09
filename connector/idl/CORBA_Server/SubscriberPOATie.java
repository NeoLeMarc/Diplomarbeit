package CORBA_Server;

import org.omg.PortableServer.POA;

/**
 * Generated from IDL interface "Subscriber".
 *
 * @author JacORB IDL compiler V 2.3.1, 27-May-2009
 * @version generated at 20.03.2010 14:29:05
 */

public class SubscriberPOATie
	extends SubscriberPOA
{
	private SubscriberOperations _delegate;

	private POA _poa;
	public SubscriberPOATie(SubscriberOperations delegate)
	{
		_delegate = delegate;
	}
	public SubscriberPOATie(SubscriberOperations delegate, POA poa)
	{
		_delegate = delegate;
		_poa = poa;
	}
	public CORBA_Server.Subscriber _this()
	{
		return CORBA_Server.SubscriberHelper.narrow(_this_object());
	}
	public CORBA_Server.Subscriber _this(org.omg.CORBA.ORB orb)
	{
		return CORBA_Server.SubscriberHelper.narrow(_this_object(orb));
	}
	public SubscriberOperations _delegate()
	{
		return _delegate;
	}
	public void _delegate(SubscriberOperations delegate)
	{
		_delegate = delegate;
	}
	public POA _default_POA()
	{
		if (_poa != null)
		{
			return _poa;
		}
		return super._default_POA();
	}
	public void notify_data(Common.CORBA_DataMessage data)
	{
_delegate.notify_data(data);
	}

	public void notify_event(Common.CORBA_EventMessage event)
	{
_delegate.notify_event(event);
	}

}
