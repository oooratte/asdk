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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.mutable.MutableInt;

import net.as_development.asdk.tools.common.CollectionUtils;

//=============================================================================
/** Must be used to 'manage' a set of queues for a defined work flow.
 *  E.g. those you can manage a combination of request/response queues;
 *  handle stickiness for queues; define error or timeout queues etcpp.
 *  
 *  If you provide queue performance data to this manager ...
 *  it can be used as load balancer mapping new or sticky requests
 *  to queues.
 * 
 *  @note	These manager handle queue names only !
 *  		It's not (directly) bound to queues ...
 */
public class JMSQueueManager
{
    //-------------------------------------------------------------------------
	/** made private to force using of our static factory method !
	 * 
	 * @see {@link create}
	 */
	private JMSQueueManager ()
		throws Exception
	{}

    //-------------------------------------------------------------------------
	/** create new manager instances ...
	 *  for the given queue set.
	 *  
	 *  A queue set define is nothing more then a 'name space' for queues.
	 *  All queues handled by this manager are relative to that name space.
	 * 
	 *  @param	sQueueSetNamespace [IN]
	 *  		the name space to be used by the manager returned.
	 *          Can be null or empty to use the 'global' name space.
	 * 
	 *  @return a manager for the given name space.
	 */
	public static synchronized JMSQueueManager create (final String sQueueSetNamespace)
		throws Exception
	{
		final Map< String, JMSQueueManager > aManagerRegistry = mem_Registry ();
		      JMSQueueManager                aManager         = null;
		
		if (aManagerRegistry.containsKey(sQueueSetNamespace))
			aManager = aManagerRegistry.get(sQueueSetNamespace);
		
		if (aManager == null)
		{
			aManager = new JMSQueueManager ();
			aManager.m_sQueueSetNamespace = sQueueSetNamespace;
			aManagerRegistry.put(sQueueSetNamespace, aManager);
		}
		
		return aManager;
	}

	//-------------------------------------------------------------------------
	public /*no synchronize*/ String newConsumerIDForType (final String sType)
	    throws Exception
	{
		final String sID         = UUID.randomUUID().toString();
		final String sConsumerID = sType + "-" + sID;
		return sConsumerID;
	}
	
	//-------------------------------------------------------------------------
	public synchronized String getQueueNameAbsolute (final String sQueue)
	    throws Exception
	{
		String sName = null;

		if (StringUtils.isEmpty(m_sQueueSetNamespace))
			sName = sQueue;
		else
			sName = buildQueueName (m_sQueueSetNamespace, sQueue);

		return sName;
	}

	//-------------------------------------------------------------------------
	public static String buildQueueName (final String...lParts)
	    throws Exception
	{
		final StringBuffer sName         = new StringBuffer (256);
		      boolean      bAddSeparator = false;

		for (String sPart : lParts)
		{
			if (bAddSeparator)
				sName.append ("-");
			else
				bAddSeparator = true;
			
			sName.append (sPart);
		}
		return sName.toString ();
	}

	//-------------------------------------------------------------------------
	public synchronized void bindQueue2Type (final String  sQueue,
										     final String  sType )
	    throws Exception
	{
		final Map< String, List< String > > aMap            = mem_QueueTypeMap ();
 		      List< String >                aQueueList4Type = aMap.get (sType);

 		if (aQueueList4Type == null)
 		{
 			aQueueList4Type = new ArrayList< String > ();
 			aMap.put(sType, aQueueList4Type);
 		}
 		
 		if ( ! aQueueList4Type.contains(sQueue))
 			aQueueList4Type.add (sQueue);
	}
	
	//-------------------------------------------------------------------------
	public synchronized List< String > getQueues4Type (final String sType)
	    throws Exception
	{
		final Map< String, List< String > > aMap            = mem_QueueTypeMap ();
	          List< String >                aQueueList4Type = aMap.get (sType);
		
	    // return empty list instead of null ...
        // can be handled by outside code better :-)
		if (aQueueList4Type == null)
			aQueueList4Type = new ArrayList< String > ();
		
		return aQueueList4Type;
	}
	
	//-------------------------------------------------------------------------
	public synchronized String getBalancedQueue4Type (final String sType)
	    throws Exception
	{
		Validate.notEmpty(sType, "Invalid argument 'type'.");
		
		final Map< String, List< String > > aMap            = mem_QueueTypeMap ();
	          List< String >                aQueueList4Type = aMap   .get (sType);
		
	    // a) no queues for type ... no balanced queue for return

		if (CollectionUtils.isEmpty(aQueueList4Type))
			return null;

		// b) one queue only .. no load balancing needed - nor possible .-)

		if (aQueueList4Type.size() == 1)
			return aQueueList4Type.get(0);
		
		// c) multiple queues available ...
		//    use round robin counter to select next queue.

		final Map< String, MutableInt > aRRBMap     = mem_RoundRobinBalancerMap4Types ();
		      MutableInt				aRRBPointer = aRRBMap.get (sType);
		  
		if (aRRBPointer == null)
		{
			aRRBPointer = new MutableInt (0);
			aRRBMap.put(sType, aRRBPointer);
		}

		final int    nMaxIndex = aQueueList4Type.size()-1;
		final int    nIndex    = aRRBPointer.intValue();
		final String sQueue    = aQueueList4Type.get(nIndex);
		
		if (nIndex < nMaxIndex)
			aRRBPointer.increment();
		else
			aRRBPointer.setValue(0);
		
		return sQueue;
	}

	//-------------------------------------------------------------------------
	public synchronized void bindConsumer2Queue (final String sConsumerID,
											     final String sQueue     )
	    throws Exception
	{
		//throw new UnsupportedOperationException ("not implemented yet");
	}

	//-------------------------------------------------------------------------
	public synchronized void dump ()
	    throws Exception
	{
		final StringBuffer sDump = new StringBuffer (256);
		
		sDump.append ("QUEUE MANAGER DUMP :\n");
		
		sDump.append ("\tTYPE-MAP :\n");
		
		final Map< String, List< String > >                aTypeMap = mem_QueueTypeMap ();
		final Iterator< Entry < String, List< String > > > pIt      = aTypeMap.entrySet().iterator();
		while (pIt.hasNext())
		{
			final Entry < String, List< String > > aEntry  = pIt.next();
			final String                           sType   = aEntry.getKey();
			final List< String >                   lQueues = aEntry.getValue();
			
			sDump.append ("\ttype='"+sType+"' => queues={"+lQueues+"}\n");
		}
		
		sDump.append ("\n");
		
		System.out.println (sDump.toString ());
	}
	
	//-------------------------------------------------------------------------
	private synchronized static Map< String, JMSQueueManager > mem_Registry ()
	    throws Exception
	{
		if (m_aRegistry == null)
			m_aRegistry = new HashMap< String, JMSQueueManager > ();
		return m_aRegistry;
	}
	
    //-------------------------------------------------------------------------
	private synchronized Map< String, List< String > > mem_QueueTypeMap ()
	    throws Exception
	{
		if (m_aQueueTypeMap == null)
			m_aQueueTypeMap = new HashMap< String, List< String > > ();
		return m_aQueueTypeMap;
	}
	
	//-------------------------------------------------------------------------
	private synchronized Map< String, MutableInt > mem_RoundRobinBalancerMap4Types ()
	    throws Exception
	{
		if (m_aRoundRobinBalancerMap4Types == null)
			m_aRoundRobinBalancerMap4Types = new HashMap< String, MutableInt > ();
		return m_aRoundRobinBalancerMap4Types;
	}
	
    //-------------------------------------------------------------------------
	private static Map< String, JMSQueueManager > m_aRegistry = null;
	
    //-------------------------------------------------------------------------
	private String m_sQueueSetNamespace = null;
	
    //-------------------------------------------------------------------------
	private Map< String, List< String > > m_aQueueTypeMap = null;

	//-------------------------------------------------------------------------
	private Map< String, MutableInt > m_aRoundRobinBalancerMap4Types = null;
}
