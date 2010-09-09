package CORBA_Server;

import org.omg.PortableServer.POA;

/**
 * Generated from IDL interface "Publisher".
 *
 * @author JacORB IDL compiler V 2.3.1, 27-May-2009
 * @version generated at 20.03.2010 14:29:05
 */

public class PublisherPOATie
	extends PublisherPOA
{
	private PublisherOperations _delegate;

	private POA _poa;
	public PublisherPOATie(PublisherOperations delegate)
	{
		_delegate = delegate;
	}
	public PublisherPOATie(PublisherOperations delegate, POA poa)
	{
		_delegate = delegate;
		_poa = poa;
	}
	public CORBA_Server.Publisher _this()
	{
		return CORBA_Server.PublisherHelper.narrow(_this_object());
	}
	public CORBA_Server.Publisher _this(org.omg.CORBA.ORB orb)
	{
		return CORBA_Server.PublisherHelper.narrow(_this_object(orb));
	}
	public PublisherOperations _delegate()
	{
		return _delegate;
	}
	public void _delegate(PublisherOperations delegate)
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
	public void setFilter_Global(CORBA_Server.Filter f)
	{
_delegate.setFilter_Global(f);
	}

	public void setFilter_Group(CORBA_Server.Filter f, short group_id)
	{
_delegate.setFilter_Group(f,group_id);
	}

	public void unregister(CORBA_Server.Subscriber sub)
	{
_delegate.unregister(sub);
	}

	public void setDefaultFilter_Global(CORBA_Server.Filter defaultFilter)
	{
_delegate.setDefaultFilter_Global(defaultFilter);
	}

	public void register(CORBA_Server.Filter defaultFilter)
	{
_delegate.register(defaultFilter);
	}

	public void setDefaultFilter_Group(CORBA_Server.Filter defaultFilter, short group_id)
	{
_delegate.setDefaultFilter_Group(defaultFilter,group_id);
	}

	public void setFilter_Node(CORBA_Server.Filter f, Common.CORBA_Node node)
	{
_delegate.setFilter_Node(f,node);
	}

}
