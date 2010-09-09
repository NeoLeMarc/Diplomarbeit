package CORBA_Server;


/**
 * Generated from IDL interface "Incoming".
 *
 * @author JacORB IDL compiler V 2.3.1, 27-May-2009
 * @version generated at 21.02.2010 18:56:44
 */

public interface IncomingOperations
{
	/* constants */
	/* operations  */
	void notify_data(Common.CORBA_DataMessage data);
	void notify_event(Common.CORBA_EventMessage event);
}
