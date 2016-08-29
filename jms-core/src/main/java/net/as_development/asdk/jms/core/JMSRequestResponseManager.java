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
package net.as_development.asdk.jms.core;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.jms.Message;

import org.apache.commons.lang3.Validate;

//=============================================================================
public class JMSRequestResponseManager
{
	//-------------------------------------------------------------------------
	public JMSRequestResponseManager ()
	{}

	//-------------------------------------------------------------------------
	public static void bindResponseToRequest (final Message aResponse,
										      final Message aRequest )
	    throws Exception
	{
		final String sCorrelationID = aRequest.getJMSCorrelationID();
		Validate.notEmpty(sCorrelationID, "No correlation ID found on request. Can not bind response to it !");
		aResponse.setJMSCorrelationID(sCorrelationID);
	}

	//-------------------------------------------------------------------------
	public /*no synchronize*/ JMSRequestResponsePair registerRequest (final Message aRequest)
	    throws Exception
	{
		final String sCorrelationID = impl_newCorrelationID ();
		aRequest.setJMSCorrelationID(sCorrelationID);
		
		final Map< String, JMSRequestResponsePair > aRegistry      = mem_Registry ();
		final JMSRequestResponsePair                aReqistryEntry = new JMSRequestResponsePair ();

		aReqistryEntry.setRequest(aRequest);
		
		synchronized(aRegistry)
		{
			aRegistry.put(sCorrelationID, aReqistryEntry);
		}
		
		return aReqistryEntry;
	}
	
	//-------------------------------------------------------------------------
	public /*no synchronize*/ JMSRequestResponsePair registerResponseAndNotify (final Message aResponse)
	    throws Exception
	{
		final String sID = aResponse.getJMSCorrelationID();
		Validate.notEmpty(sID, "Miss correlation ID for this response !");

		final Map< String, JMSRequestResponsePair > aRegistry      = mem_Registry ();
			  JMSRequestResponsePair                aRegistryEntry = null;

		synchronized(aRegistry)
		{
			aRegistryEntry = aRegistry.get(sID);
			aRegistry.remove(sID);
		}
		
		if (aRegistryEntry == null)
		{
			// TODO error handling
			throw new RuntimeException ("Miss registry item to bind response to request ... and dont know how to handle these error ,-)");
		}

		aRegistryEntry.notifyResponse (aResponse);
		
		return aRegistryEntry;
	}

	//-------------------------------------------------------------------------
	private String impl_newCorrelationID ()
	    throws Exception
	{
		return UUID.randomUUID().toString();
	}
	
	//-------------------------------------------------------------------------
	private synchronized Map< String, JMSRequestResponsePair > mem_Registry ()
		throws Exception
	{
		if (m_aRegistry == null)
			m_aRegistry = new HashMap< String, JMSRequestResponsePair > ();
		return m_aRegistry;
	}
	
	//-------------------------------------------------------------------------
	private Map< String, JMSRequestResponsePair > m_aRegistry = null;
}