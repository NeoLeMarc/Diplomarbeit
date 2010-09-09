package Common;

/**
 * Generated from IDL struct "CORBA_DataMessage".
 *
 * @author JacORB IDL compiler V 2.3.1, 27-May-2009
 * @version generated at 06.05.2010 14:17:36
 */

public final class CORBA_DataMessage
	implements org.omg.CORBA.portable.IDLEntity
{
	public CORBA_DataMessage(){}
	public java.lang.String nodeID;
	public short groupID;
	public short pulse;
	public short breathing;
	public long connectorTimestamp;
	public long serverTimestamp;
	public CORBA_DataMessage(java.lang.String nodeID, short groupID, short pulse, short breathing, long connectorTimestamp, long serverTimestamp)
	{
		this.nodeID = nodeID;
		this.groupID = groupID;
		this.pulse = pulse;
		this.breathing = breathing;
		this.connectorTimestamp = connectorTimestamp;
		this.serverTimestamp = serverTimestamp;
	}
}
