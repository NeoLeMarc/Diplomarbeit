package CORBA_Server;


/**
 * Generated from IDL interface "Subscriber".
 *
 * @author JacORB IDL compiler V 2.3.1, 27-May-2009
 * @version generated at 20.03.2010 14:29:05
 */

public interface SubscriberOperations
{
	/* constants */
	/* operations  */
	void notify_data(Common.CORBA_DataMessage data);
	void notify_event(Common.CORBA_EventMessage event);
}
