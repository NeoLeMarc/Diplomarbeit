package CORBA_Server.QueriesPackage;

/**
 * Generated from IDL alias "EventSeq".
 *
 * @author JacORB IDL compiler V 2.3.1, 27-May-2009
 * @version generated at 01.03.2010 03:18:57
 */

public final class EventSeqHelper
{
	private static org.omg.CORBA.TypeCode _type = null;

	public static void insert (org.omg.CORBA.Any any, Common.CORBA_EventMessage[] s)
	{
		any.type (type ());
		write (any.create_output_stream (), s);
	}

	public static Common.CORBA_EventMessage[] extract (final org.omg.CORBA.Any any)
	{
		return read (any.create_input_stream ());
	}

	public static org.omg.CORBA.TypeCode type ()
	{
		if (_type == null)
		{
			_type = org.omg.CORBA.ORB.init().create_alias_tc(CORBA_Server.QueriesPackage.EventSeqHelper.id(), "EventSeq",org.omg.CORBA.ORB.init().create_sequence_tc(0, Common.CORBA_EventMessageHelper.type()));
		}
		return _type;
	}

	public static String id()
	{
		return "IDL:CORBA_Server/Queries/EventSeq:1.0";
	}
	public static Common.CORBA_EventMessage[] read (final org.omg.CORBA.portable.InputStream _in)
	{
		Common.CORBA_EventMessage[] _result;
		int _l_result2 = _in.read_long();
		try
		{
			 int x = _in.available();
			 if ( x > 0 && _l_result2 > x )
				{
					throw new org.omg.CORBA.MARSHAL("Sequence length too large. Only " + x + " available and trying to assign " + _l_result2);
				}
		}
		catch (java.io.IOException e)
		{
		}
		_result = new Common.CORBA_EventMessage[_l_result2];
		for (int i=0;i<_result.length;i++)
		{
			_result[i]=Common.CORBA_EventMessageHelper.read(_in);
		}

		return _result;
	}

	public static void write (final org.omg.CORBA.portable.OutputStream _out, Common.CORBA_EventMessage[] _s)
	{
		
		_out.write_long(_s.length);
		for (int i=0; i<_s.length;i++)
		{
			Common.CORBA_EventMessageHelper.write(_out,_s[i]);
		}

	}
}
