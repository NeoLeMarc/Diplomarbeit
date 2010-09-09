package CORBA_Server;


/**
 * Generated from IDL interface "Commands".
 *
 * @author JacORB IDL compiler V 2.3.1, 27-May-2009
 * @version generated at 06.05.2010 12:06:05
 */

public interface CommandsOperations
{
	/* constants */
	/* operations  */
	void mute(Common.CORBA_Node node);
	void enableAlert(Common.CORBA_Node node);
	void disableAlert(Common.CORBA_Node node);
	void toggleAlert(Common.CORBA_Node node);
}
