package CORBA_Server;

import org.omg.PortableServer.POA;

/**
 * Generated from IDL interface "Commands".
 *
 * @author JacORB IDL compiler V 2.3.1, 27-May-2009
 * @version generated at 06.05.2010 12:06:05
 */

public class CommandsPOATie
	extends CommandsPOA
{
	private CommandsOperations _delegate;

	private POA _poa;
	public CommandsPOATie(CommandsOperations delegate)
	{
		_delegate = delegate;
	}
	public CommandsPOATie(CommandsOperations delegate, POA poa)
	{
		_delegate = delegate;
		_poa = poa;
	}
	public CORBA_Server.Commands _this()
	{
		return CORBA_Server.CommandsHelper.narrow(_this_object());
	}
	public CORBA_Server.Commands _this(org.omg.CORBA.ORB orb)
	{
		return CORBA_Server.CommandsHelper.narrow(_this_object(orb));
	}
	public CommandsOperations _delegate()
	{
		return _delegate;
	}
	public void _delegate(CommandsOperations delegate)
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
	public void enableAlert(Common.CORBA_Node node)
	{
_delegate.enableAlert(node);
	}

	public void mute(Common.CORBA_Node node)
	{
_delegate.mute(node);
	}

	public void disableAlert(Common.CORBA_Node node)
	{
_delegate.disableAlert(node);
	}

	public void toggleAlert(Common.CORBA_Node node)
	{
_delegate.toggleAlert(node);
	}

}
