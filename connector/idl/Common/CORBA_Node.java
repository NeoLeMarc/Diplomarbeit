package Common;

/**
 * Generated from IDL struct "CORBA_Node".
 *
 * @author JacORB IDL compiler V 2.3.1, 27-May-2009
 * @version generated at 06.05.2010 14:17:36
 */

public final class CORBA_Node
	implements org.omg.CORBA.portable.IDLEntity
{
	public CORBA_Node(){}
	public java.lang.String node_id;
	public short group_id;
	public CORBA_Node(java.lang.String node_id, short group_id)
	{
		this.node_id = node_id;
		this.group_id = group_id;
	}
}
