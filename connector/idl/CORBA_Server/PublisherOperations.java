package CORBA_Server;


/**
 * Generated from IDL interface "Publisher".
 *
 * @author JacORB IDL compiler V 2.3.1, 27-May-2009
 * @version generated at 20.03.2010 14:29:05
 */

public interface PublisherOperations
{
	/* constants */
	/* operations  */
	void register(CORBA_Server.Filter defaultFilter);
	void unregister(CORBA_Server.Subscriber sub);
	void setDefaultFilter_Global(CORBA_Server.Filter defaultFilter);
	void setDefaultFilter_Group(CORBA_Server.Filter defaultFilter, short group_id);
	void setFilter_Global(CORBA_Server.Filter f);
	void setFilter_Group(CORBA_Server.Filter f, short group_id);
	void setFilter_Node(CORBA_Server.Filter f, Common.CORBA_Node node);
}
