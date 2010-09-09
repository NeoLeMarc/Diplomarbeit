package CORBA_Server;

import org.omg.PortableServer.POA;

/**
 * Generated from IDL interface "Control".
 *
 * @author JacORB IDL compiler V 2.3.1, 27-May-2009
 * @version generated at 06.05.2010 14:17:36
 */

public class ControlPOATie
	extends ControlPOA
{
	private ControlOperations _delegate;

	private POA _poa;
	public ControlPOATie(ControlOperations delegate)
	{
		_delegate = delegate;
	}
	public ControlPOATie(ControlOperations delegate, POA poa)
	{
		_delegate = delegate;
		_poa = poa;
	}
	public CORBA_Server.Control _this()
	{
		return CORBA_Server.ControlHelper.narrow(_this_object());
	}
	public CORBA_Server.Control _this(org.omg.CORBA.ORB orb)
	{
		return CORBA_Server.ControlHelper.narrow(_this_object(orb));
	}
	public ControlOperations _delegate()
	{
		return _delegate;
	}
	public void _delegate(ControlOperations delegate)
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
	public void registerConnector()
	{
_delegate.registerConnector();
	}

}
