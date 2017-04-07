/**
 * This is free and unencumbered software released into the public domain.
 *
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 *
 * In jurisdictions that recognize copyright laws, the author or authors
 * of this software dedicate any and all copyright interest in the
 * software to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and
 * successors. We intend this dedication to be an overt act of
 * relinquishment in perpetuity of all present and future rights to this
 * software under copyright law.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information, please refer to <http://unlicense.org/>
 */
package net.as_development.asdk.monitoring.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.time.DateFormatUtils;

import net.as_development.asdk.tools.common.CollectionUtils;
import net.as_development.asdk.tools.common.NumberUtils;
import net.as_development.asdk.tools.common.type.IStringConvertible;
import net.as_development.asdk.tools.common.type.StringConvertibleUtils;

//==============================================================================
/**
 */
public class MonitorRecord implements Serializable
									, IStringConvertible
{
	//--------------------------------------------------------------------------
	private static final long serialVersionUID = 1L;

	//--------------------------------------------------------------------------
	public MonitorRecord ()
		throws Exception
	{
		m_nTimestamp = System.currentTimeMillis();
	}
	
	//--------------------------------------------------------------------------
	public static MonitorRecord create (final String    sScope   ,
										final String    sRecordID,
										final String    sMessage ,
										final Object... lData    )
		throws Exception
	{
		final MonitorRecord aRecord = new MonitorRecord ();
		aRecord.m_sScope   = sScope   ;
		aRecord.m_sID      = sRecordID;
		aRecord.m_sMessage = sMessage ;
		aRecord.m_lData    = CollectionUtils.flat2MappedArguments (lData);
		return aRecord;
	}
	
	//--------------------------------------------------------------------------
	public static MonitorRecord create (String sSerializedString)
		throws Exception
	{
		final MonitorRecord aRecord = new MonitorRecord ();
		aRecord.convertFromString(sSerializedString);
		return aRecord;
	}

	//--------------------------------------------------------------------------
	public synchronized void setScope (final String sScope)
		throws Exception
    {
		m_sScope = sScope;
    }

	//--------------------------------------------------------------------------
	public synchronized void setID (final String sID)
		throws Exception
    {
		m_sID = sID;
    }

	//--------------------------------------------------------------------------
	public synchronized void setMessage (final String sMessage)
		throws Exception
    {
		m_sMessage = sMessage;
    }

	//--------------------------------------------------------------------------
	public synchronized void setData (final Object... lData)
		throws Exception
    {
		m_lData = CollectionUtils.flat2MappedArguments (lData);
    }

	//--------------------------------------------------------------------------
	public synchronized long getTimestamp ()
		throws Exception
    {
		return m_nTimestamp;
    }
	
	//--------------------------------------------------------------------------
	public synchronized String getScope ()
		throws Exception
    {
		return StringUtils.defaultString(m_sScope);
    }

	//--------------------------------------------------------------------------
	public synchronized String getID ()
		throws Exception
    {
		return StringUtils.defaultString(m_sID);
    }

	//--------------------------------------------------------------------------
	public synchronized String getMessage ()
		throws Exception
    {
		return StringUtils.defaultString(m_sMessage);
    }

	//--------------------------------------------------------------------------
	public synchronized Map< Object, Object > getData ()
		throws Exception
    {
		return mem_Data ();
    }

	//--------------------------------------------------------------------------
	@Override
	public synchronized String toString ()
	{
		final StringBuffer sString = new StringBuffer (256);
		
		sString.append(super.toString());
		sString.append(" : timestamp=[");
		sString.append(DateFormatUtils.format(m_nTimestamp, "HH:mm:ss.SSS"));
		sString.append("] : scope=["   );
		sString.append(m_sScope        );
		sString.append("] : id=["      );
		sString.append(m_sID           );
		sString.append("] : message=[" );
		sString.append(m_sMessage      );
		sString.append("] : data=["    );
		
		try
		{
			String sData = CollectionUtils.formatAsProperties(mem_Data ()); 
			       sData = StringUtils.replace  (sData, "\n", ", ");
			       sData = StringUtils.removeEnd(sData,       ", ");
			sString.append(sData);
		}
		catch (Throwable exIgnore)
		{
			sString.append("... ERROR ! " );
		}

		sString.append("]");
		
		return sString.toString ();
	}
	
    //--------------------------------------------------------------------------
	@Override
	public synchronized boolean equals (final Object aValue)
	{
		if (aValue == null)
			return false;

		if ( ! MonitorRecord.class.isAssignableFrom(aValue.getClass()))
			return false;
		
		final MonitorRecord aCheck = (MonitorRecord)aValue;

		try
		{
			if ( ! NumberUtils.equals(getTimestamp (), aCheck.getTimestamp()))
				return false;
			if ( ! StringUtils.equals(getScope     (), aCheck.getScope    ()))
				return false;
			if ( ! StringUtils.equals(getID        (), aCheck.getID       ()))
				return false;
			if ( ! StringUtils.equals(getMessage   (), aCheck.getMessage  ()))
				return false;
		}
		catch (Throwable ex)
		{
			return false;
		}

		return false;
	}

	//--------------------------------------------------------------------------
	@Override
	public synchronized int hashCode()
	{
		return super.hashCode();
	}

	//--------------------------------------------------------------------------
	@Override
	public synchronized String convertToString()
		throws Exception
	{
		final String sString = StringConvertibleUtils.convertToString (serialVersionUID, getTimestamp(), getScope(), getID(), getMessage(), getData());
		return sString;
	}

	//--------------------------------------------------------------------------
	@Override
	public synchronized void convertFromString(final String sValue)
		throws Exception
	{
		final List< Object > lValues = StringConvertibleUtils.convertFromString(sValue);
		Validate.isTrue(lValues.size() == 6, "Malformed serialization string. Unexpected count of values. found=["+lValues.size()+"] expected=[6]");
		
		final long nVersionCheck = (long) lValues.get(0);
		if (nVersionCheck != serialVersionUID)
			throw new Exception ("Mismatch in serial version ID : found ["+nVersionCheck+"] but expected ["+serialVersionUID+"]");

		m_nTimestamp = (long                 )lValues.get(1);
		m_sScope     = (String               )lValues.get(2);
		m_sID        = (String               )lValues.get(3);
		m_sMessage   = (String               )lValues.get(4);
		m_lData      = (Map< Object, Object >)lValues.get(5);
	}

    //--------------------------------------------------------------------------
	private synchronized void writeObject(final ObjectOutputStream aOut)
		  throws IOException
	{
		try
		{
			aOut.writeLong  (serialVersionUID);
			aOut.writeLong  (getTimestamp  ());
			aOut.writeUTF   (getScope      ());
			aOut.writeUTF   (getID         ());
			aOut.writeUTF   (getMessage    ());
			aOut.writeObject(getData       ());
		}
		catch (Throwable ex)
		{
			throw new IOException (ex);
		}
	}

    //--------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	private synchronized void readObject(final ObjectInputStream aIn)
		  throws IOException
		       , ClassNotFoundException
	{
		try
		{
			final long nVersionCheck = aIn.readLong ();
			if (nVersionCheck != serialVersionUID)
				throw new Exception ("Mismatch in serial version ID : found ["+nVersionCheck+"] but expected ["+serialVersionUID+"]");

			m_nTimestamp = aIn.readLong ();
			m_sScope     = aIn.readUTF  ();
			m_sID        = aIn.readUTF  ();
			m_lData      = (Map< Object, Object >)aIn.readObject();
		}
		catch (Throwable ex)
		{
			throw new IOException (ex);
		}
	}

	//--------------------------------------------------------------------------
	private Map< Object, Object > mem_Data ()
	    throws Exception
	{
		if (m_lData == null)
			m_lData = new HashMap< Object, Object > ();
		return m_lData;
	}

	//--------------------------------------------------------------------------
	private Long m_nTimestamp = null;
	
	//--------------------------------------------------------------------------
	private String m_sScope = null;
	
	//--------------------------------------------------------------------------
	private String m_sID = null;

	//--------------------------------------------------------------------------
	private String m_sMessage = null;

	//--------------------------------------------------------------------------
	private Map< Object, Object > m_lData = null;
}
