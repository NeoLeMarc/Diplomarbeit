package CORBA_Server;

import org.omg.PortableServer.POA;

/**
 * Generated from IDL interface "Incoming".
 *
 * @author JacORB IDL compiler V 2.3.1, 27-May-2009
 * @version generated at 21.02.2010 18:56:44
 */

public class IncomingPOATie
	extends IncomingPOA
{
	private IncomingOperations _delegate;

	private POA _poa;
	public IncomingPOATie(IncomingOperations delegate)
	{
		_delegate = delegate;
	}
	public IncomingPOATie(IncomingOperations delegate, POA poa)
	{
		_delegate = delegate;
		_poa = poa;
	}
	public CORBA_Server.Incoming _this()
	{
		return CORBA_Server.IncomingHelper.narrow(_this_object());
	}
	public CORBA_Server.Incoming _this(org.omg.CORBA.ORB orb)
	{
		return CORBA_Server.IncomingHelper.narrow(_this_object(orb));
	}
	public IncomingOperations _delegate()
	{
		return _delegate;
	}
	public void _delegate(IncomingOperations delegate)
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
