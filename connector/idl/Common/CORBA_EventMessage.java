package Common;

/**
 * Generated from IDL struct "CORBA_EventMessage".
 *
 * @author JacORB IDL compiler V 2.3.1, 27-May-2009
 * @version generated at 06.05.2010 14:17:36
 */

public final class CORBA_EventMessage
	implements org.omg.CORBA.portable.IDLEntity
{
	public CORBA_EventMessage(){}
	public java.lang.String nodeID;
	public short groupID;
	public int eventType;
	public long connectorTimestamp;
	public long serverTimestamp;
	public CORBA_EventMessage(java.lang.String nodeID, short groupID, int eventType, long connectorTimestamp, long serverTimestamp)
	{
		this.nodeID = nodeID;
		this.groupID = groupID;
		this.eventType = eventType;
		this.connectorTimestamp = connectorTimestamp;
		this.serverTimestamp = serverTimestamp;
	}
}
