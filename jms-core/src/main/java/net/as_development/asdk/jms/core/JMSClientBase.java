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
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;

import org.apache.commons.lang3.StringUtils;

import net.as_development.asdk.jms.core.beans.JMSBeanMapper;
import net.as_development.asdk.jms.core.beans.JMSMessageBean;
import net.as_development.asdk.jms.core.beans.JMSMessageUtils;

//=============================================================================
/** Base class providing all you need to work with JMS as client.
 *
 *  It's a 'pure' client sending requests to one queue
 *  and retrieve response messages within another queue.
 *  
 *  All those base on request/response pairs and corresponding queues.
 *  Loops, worker threads, request-response correlation is done by these
 *  helper.
 *  
 *  There exists two modes:
 *  
 *  Mode A )
 *  
 *  YOU define a queue explicit where all outgoing messages has to be sent to.
 *  WE define a temporary queue where responses comes in.
 *  
 *  To enable that mode You have to call:
 *  <code>
 *  setFullQualifiedOutQueue ();
 *  start();
 *  ...
 *  </code>
 *  
 *  Mode B)
 *
 *  YOU define a queue namespace and a queue type for outgoing messages.
 *  WE search for a suitable queue within the current JMS context and use it.
 *  Further WE define a temporary queue where responses comes in.
 *  
 *  To enable that mode You have to call:
 *  <code>
 *  setQueueNamespace ();
 *  setTargetQueueType ();
 *  start();
 *  ...
 *  </code>
 *  
 *  Independent from the mode you use ...
 *  we use setJMSReplyTo() to define our temporary queue where
 *  we expect to get our answer back.
 */
public class JMSClientBase
{
    //-------------------------------------------------------------------------
	public static final String QUEUE_TYPE = "simple-client";

    //-------------------------------------------------------------------------
	public static final String QUEUE_NAME_4_RESPONSES = "response";

	//-------------------------------------------------------------------------
	public static final long DEFAULT_TIMEOUT_IN_MS = 60000;
	
    //-------------------------------------------------------------------------
	public JMSClientBase ()
		throws Exception
	{}

	//-------------------------------------------------------------------------
	public synchronized void setEnv (final JMSEnv aEnv)
	    throws Exception
	{
		m_aEnv = aEnv;
	}
	
	//-------------------------------------------------------------------------
	/** set an unique ID for this client.
	 *  It's up to you what 'unique' means.
	 *  
	 *  If this method is not called ...
	 *  WE generate such unique ID by our own automatically.
	 *
	 *	@param	sID [IN]
	 *			the unique ID.
	 */
	public synchronized void setClientID (final String sID)
	    throws Exception
	{
		m_sClientID = sID;
	}

	//-------------------------------------------------------------------------
	public synchronized void setQueueNamespace (final String sNamespace)
	    throws Exception
	{
		m_sQueueNamespace = sNamespace;
	}

	//-------------------------------------------------------------------------
	public synchronized void setTargetQueueType (final String sType)
	    throws Exception
	{
		m_sTargetQueueType = sType;
	}

	//-------------------------------------------------------------------------
	public synchronized void setTimeout (final long nTimeout)
	    throws Exception
	{
		m_nTimeout = nTimeout;
	}

	//-------------------------------------------------------------------------
	public synchronized void setMessageTTL (final long nTTL)
	    throws Exception
	{
		m_nMessageTTL = nTTL;
	}

	//-------------------------------------------------------------------------
	public synchronized void setFullQualifiedRequestQueue (final String sQueue)
		throws Exception
	{
		m_sFullQualifiedRequestQueue = sQueue;
	}

	//-------------------------------------------------------------------------
	public synchronized void useTopic ()
	    throws Exception
	{
		m_bUseTopic = true;
	}

	//-------------------------------------------------------------------------
	public /*no synchronize*/ String getResponseQueueName ()
		throws Exception
	{
		final Destination aResponseQueue = mem_ResponseQueue();
		      String      sResponseQueue = "<unknown>";
		if (
			(aResponseQueue !=         null ) &&
			(aResponseQueue instanceof Queue)
		   )
		{
			sResponseQueue = ((Queue)aResponseQueue).getQueueName();
		}
		return sResponseQueue;
	}
	
	//-------------------------------------------------------------------------
	public synchronized void start ()
	    throws Exception
	{
		if (m_bRunning == true)
			return;

		impl_registerInQueueContext ();
		
		impl_listenForResponses ();

//		m_aResponseCollectorThread = new Thread (new Runnable ()
//		{
//			@Override
//			public void run()
//			{
//				impl_collectResponses ();
//			}
//		});
//		m_aResponseCollectorThread.start ();
		
		m_bRunning = true;
	}

	//-------------------------------------------------------------------------
	public synchronized void stop ()
	    throws Exception
	{
		if (m_bRunning == false)
			return;
		
		// tricky :-)
		// We reset our thread member first ...
		// but then those instance might die ...
		// Holding one last reference within this method scope ...
		// will ensure those instance will live till the end of this method .-)

		@SuppressWarnings("unused")
		Thread aCollectorThreadHold = m_aResponseCollectorThread;
		m_aResponseCollectorThread  = null;
		
//		synchronized (m_aShutdownSync)
//		{
//			m_aShutdownSync.setValue(true);
//			m_aShutdownSync.wait (); // TODO timeout ?
//		}
		
		if (m_aClientConsumer != null)
			m_aClientConsumer.close();
		
		if (m_aClientProducer != null)
			m_aClientProducer.close();
		
		if (m_aSession != null)
			m_aSession.close();
		
		m_bRunning = false;
	}

	//-------------------------------------------------------------------------
	public < T extends JMSMessageBean > /*no synchronize*/ void sendAsynchronousNoResponse (final T aRequest)
	    throws Exception
	{
		final MessageProducer aWrite      = mem_ClientProducer ();
		final Message         aJMSRequest = impl_newMessage    ();
		final JMSBeanMapper   aBeanMapper = mem_BeanMapper     ();
		
		aBeanMapper.mapBeanToMessage(aRequest, aJMSRequest);
		aWrite.send(aJMSRequest);
	}

	//-------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public < T extends JMSMessageBean > T /*no synchronize*/ requestSynchronous (final T aRequest)
	    throws Exception
	{
		final Destination               aResponseQueue = mem_ResponseQueue ();
		final MessageProducer           aWrite         = mem_ClientProducer ();
		final JMSRequestResponseManager aRRManager     = mem_RequestResponseManager ();
		final Message                   aJMSRequest    = impl_newMessage ();
		final JMSBeanMapper             aBeanMapper    = mem_BeanMapper ();

		aBeanMapper.mapBeanToMessage(aRequest, aJMSRequest);
		aJMSRequest.setJMSReplyTo(aResponseQueue);
		
		final JMSRequestResponsePair aResponseSync = aRRManager.registerRequest (aJMSRequest);
		aWrite.send(aJMSRequest);

		final boolean bFinished = aResponseSync.await(m_nTimeout, TimeUnit.MILLISECONDS);
		if ( ! bFinished)
			throw new TimeoutException ("No response in time. timeout=["+m_nTimeout+"]");
		
		final Message        aJMSResponse = aResponseSync.getResponse ();
		final JMSMessageBean aResponse    = JMSMessageUtils.emptyBeanFitsToMessage(aJMSResponse);
		aBeanMapper.mapMessageToBean(aJMSResponse, aResponse);
		
		return (T) aResponse;
	}

	//-------------------------------------------------------------------------
	private javax.jms.Message impl_newMessage ()
	    throws Exception
	{
		final Session           aSession    = mem_Session ();
		final javax.jms.Message aJmsMessage = aSession.createTextMessage();
		return aJmsMessage;
	}
	
	//-------------------------------------------------------------------------
	private void impl_listenForResponses ()
		throws Exception
	{
		final MessageConsumer           aRead         = mem_ClientConsumer ();
		final JMSRequestResponseManager aRRManager    = mem_RequestResponseManager ();
		
		aRead.setMessageListener(new MessageListener ()
		{
			@Override
			public void onMessage(final Message aJMSResponse)
			{
				try
				{
					aRRManager.registerResponseAndNotify(aJMSResponse);
					aJMSResponse.acknowledge();
				}
				catch (Throwable ex)
				{
					// TODO fix me
				}
			}
		});
	}
	
//	//-------------------------------------------------------------------------
//	private void impl_collectResponses ()
//	{
//		final LogContext aLog = LOG.inCategory ("JMS" ,"CLIENT");
//		try
//		{
//			final long                      nPollTimeInMS = 1000;
//			final MessageConsumer           aRead         = mem_ClientConsumer ();
//			final JMSRequestResponseManager aRRManager    = mem_RequestResponseManager ();
//			
//			while (true)
//			{
//				synchronized (m_aShutdownSync)
//				{
//					if (m_aShutdownSync.booleanValue() == true)
//					{
//						m_aShutdownSync.notifyAll();
//						break;
//					}
//				}
//
//				aLog.forLevel   (ELogLevel.E_TRACE       )
//				    .withMessage("wait for response ..." )
//				    .setVar     ("timeout", nPollTimeInMS)
//				    .log        ();
//
//				final Message aJMSResponse = aRead.receive(nPollTimeInMS);
//				if (aJMSResponse == null)
//				{
//					aLog.forLevel   (ELogLevel.E_TRACE       )
//					    .withMessage("no response in time ...")
//					    .log        ();
//					continue;
//				}
//
//				aLog.forLevel   (ELogLevel.E_TRACE)
//				    .withMessage("got rsponse ...")
//				    .log        ();
//				aRRManager.registerResponseAndNotify(aJMSResponse);
//				aJMSResponse.acknowledge();
//			}
//		}
//		catch (final Throwable ex)
//		{
//			// TODO better error handling ?!
//			// This code runs within a worker-thread ...
//			// throwing away those exception is not an option ...
//			// find right target to forward error ...
//			
//			aLog.forLevel (ELogLevel.E_ERROR)
//			    .withError(ex)
//			    .log      ();
//		}
//	}

	//-------------------------------------------------------------------------
	private synchronized void impl_registerInQueueContext ()
	    throws Exception
	{
	    if (
	    	( ! StringUtils.isEmpty(m_sQueueNamespace)) &&
	    	( ! StringUtils.isEmpty(m_sClientID      ))
	       )
	    {
	    	impl_registerInQueueContext4NamespaceMode ();
	    }
	    else
	    if (
		    (StringUtils.isEmpty(m_sQueueNamespace)) ||
		    (StringUtils.isEmpty(m_sClientID      ))
		   )
	    {
	    	impl_registerInQueueContext4TempMode ();
	    }
	    else
	    	throw new UnsupportedOperationException ("No namspace nor any worker ID ... which queue name mode do you want to use ?");
	}
	
	//-------------------------------------------------------------------------
	private synchronized void impl_registerInQueueContext4NamespaceMode ()
	    throws Exception
	{
		final JMSQueueManager aManager       = JMSQueueManager.create(m_sQueueNamespace);
		final String          sMe            = m_sClientID;
		final String          sResponseQueue = JMSQueueManager.buildQueueName(sMe, QUEUE_NAME_4_RESPONSES);

		aManager.bindConsumer2Queue(sMe           , sResponseQueue);
		aManager.bindQueue2Type    (sResponseQueue, QUEUE_TYPE   );

		m_sMyConsumerID    = sMe           ;
		m_sMyResponseQueue = sResponseQueue;
		m_aQueueManager    = aManager      ;
	}

	//-------------------------------------------------------------------------
	private synchronized void impl_registerInQueueContext4TempMode ()
	    throws Exception
	{
		 final JMSQueueManager aManager   = JMSQueueManager.create(null);
		 final Session         aSession   = mem_Session ();
		 final Queue           aTempQueue = (Queue) aSession.createTemporaryQueue();
		 final String          sTempQueue = aTempQueue.getQueueName();
		 final String          sMe        = UUID.randomUUID().toString();
		 
		 aManager.bindConsumer2Queue(sMe       , sTempQueue);
		 aManager.bindQueue2Type    (sTempQueue, QUEUE_TYPE);

		 m_sClientID        = sMe;
		 m_sMyConsumerID    = sMe;
		 m_sMyResponseQueue = sTempQueue;
		 m_aQueueManager    = aManager;
	}

	//-------------------------------------------------------------------------
	public static String toString (final Message aMessage)
     	throws Exception
	{
		final StringBuffer sString = new StringBuffer ();
		
		final Enumeration< ? > lProps = aMessage.getPropertyNames();
		while (lProps.hasMoreElements())
		{
			final String sProp  = (String) lProps.nextElement();
			final String sValue = aMessage.getStringProperty(sProp);
			
			sString.append (sProp+"="+sValue+"\n");
		}

		sString.append ("JMSMessageID="    +aMessage.getJMSMessageID    ()+"\n");
		sString.append ("JMSCorrelationID="+aMessage.getJMSCorrelationID()+"\n");
		sString.append ("JMSType="         +aMessage.getJMSType         ()+"\n");
		sString.append ("JMSDeliveryMode=" +aMessage.getJMSDeliveryMode ()+"\n");
		sString.append ("JMSTimestamp="    +aMessage.getJMSTimestamp    ()+"\n");
		sString.append ("JMSExpiration="   +aMessage.getJMSExpiration   ()+"\n");
		sString.append ("JMSDestination="  +aMessage.getJMSDestination  ()+"\n");
		sString.append ("JMSReplyTo="      +aMessage.getJMSReplyTo      ()+"\n");
		
		return sString.toString ();
	}
	
	//-------------------------------------------------------------------------
    private synchronized MessageProducer mem_ClientProducer ()
    	throws Exception
    {
    	if (m_aClientProducer == null)
    	{
    		final Session         aSession          = mem_Session ();
    		final Destination     aRequestQueue     = mem_RequestQueue ();
    		final MessageProducer aProducer         = aSession.createProducer(aRequestQueue);

    		if (m_nMessageTTL != null)
    			aProducer.setTimeToLive(m_nMessageTTL);
    		
    		m_aClientProducer = aProducer;
    	}
    	return m_aClientProducer;
    }
    
    //-------------------------------------------------------------------------
    private synchronized MessageConsumer mem_ClientConsumer ()
    	throws Exception
    {
    	if (m_aClientConsumer == null)
    	{
    		final Session     aSession          = mem_Session ();
    		final Destination aResponseQueue    = mem_ResponseQueue ();
    		                  m_aClientConsumer = aSession.createConsumer(aResponseQueue);
    	}
    	return m_aClientConsumer;
    }

    //-------------------------------------------------------------------------
	private synchronized Destination mem_RequestQueue ()
	    throws Exception
	{
		if (m_aRequestQueue == null)
		{
			String sRequestQueueAbsolute = null;
			
			if ( ! StringUtils.isEmpty(m_sFullQualifiedRequestQueue))
			{
				sRequestQueueAbsolute = m_sFullQualifiedRequestQueue;
			}
			else
			{
				final String sRequestQueue         = m_aQueueManager.getBalancedQueue4Type(m_sTargetQueueType);
				             sRequestQueueAbsolute = m_aQueueManager.getQueueNameAbsolute (sRequestQueue     );
			}
			
			final Session aSession = mem_Session ();
			if (m_bUseTopic)
				m_aRequestQueue = aSession.createTopic(sRequestQueueAbsolute);
			else
                m_aRequestQueue = aSession.createQueue(sRequestQueueAbsolute);
		}
		return m_aRequestQueue;
	}
	
    //-------------------------------------------------------------------------
	private synchronized Destination mem_ResponseQueue ()
	    throws Exception
	{
		if (m_aResponseQueue == null)
		{
			// note : impl_registerInQueueContext () has to be called before this method ...
			// we need m_sMyResponseQueue well defined here .-)

			final Session     aSession               = mem_Session ();
			final String      sResponseQueueAbsolute = m_aQueueManager.getQueueNameAbsolute(m_sMyResponseQueue);
			final Destination aResponseQueue         = aSession.createQueue(sResponseQueueAbsolute);
			
		    m_aResponseQueue = aResponseQueue;
		}
		return m_aResponseQueue;
	}

    //-------------------------------------------------------------------------
    private synchronized Session mem_Session ()
    	throws Exception
    {
    	if (m_aSession == null)
    	{
    		final IJMSClient iClient  = mem_JMSClient ();
    		final Session    aSession = iClient.newSession();
    		m_aSession = aSession;
    	}
    	return m_aSession;
    }
    
    //-------------------------------------------------------------------------
    private synchronized IJMSClient mem_JMSClient ()
        throws Exception
    {
    	if (m_iJMSClient == null)
    	{
    		final IJMSClient iClient = JMSClient.newClient();
    		iClient.setEnv(m_aEnv);
    		m_iJMSClient = iClient;
    	}
    	return m_iJMSClient;
    }

    //-------------------------------------------------------------------------
	private synchronized JMSRequestResponseManager mem_RequestResponseManager ()
	    throws Exception
	{
		if (m_aRequestResponseManager == null)
			m_aRequestResponseManager = new JMSRequestResponseManager ();
		return m_aRequestResponseManager;
	}
	
	//-------------------------------------------------------------------------
    private synchronized JMSBeanMapper mem_BeanMapper ()
        throws Exception
    {
    	if (m_aBeanMapper == null)
    		m_aBeanMapper = JMSBeanMapper.get();
    	return m_aBeanMapper;
    }
   
    //-------------------------------------------------------------------------
    private boolean m_bRunning = false;
    
    //-------------------------------------------------------------------------
	private String m_sClientID = null;
	
    //-------------------------------------------------------------------------
	private JMSEnv m_aEnv = null;
	
    //-------------------------------------------------------------------------
    private IJMSClient m_iJMSClient = null;

    //-------------------------------------------------------------------------
    private Session m_aSession = null;

    //-------------------------------------------------------------------------
    private long m_nTimeout = DEFAULT_TIMEOUT_IN_MS;
    
    //-------------------------------------------------------------------------
    private Destination m_aRequestQueue = null;
    
    //-------------------------------------------------------------------------
    private Destination m_aResponseQueue = null;

    //-------------------------------------------------------------------------
    private MessageProducer m_aClientProducer = null;

    //-------------------------------------------------------------------------
    private MessageConsumer m_aClientConsumer = null;

    //-------------------------------------------------------------------------
    private Thread m_aResponseCollectorThread = null;

	//-------------------------------------------------------------------------
	private JMSRequestResponseManager m_aRequestResponseManager = null;

    //-------------------------------------------------------------------------
    private JMSQueueManager m_aQueueManager = null;

    //-------------------------------------------------------------------------
    private String m_sFullQualifiedRequestQueue = null;
    
    //-------------------------------------------------------------------------
    private String m_sQueueNamespace = null;

    //-------------------------------------------------------------------------
    private String m_sMyConsumerID = null;
    
    //-------------------------------------------------------------------------
    private String m_sTargetQueueType = null;

    //-------------------------------------------------------------------------
    private String m_sMyResponseQueue = null;

//    //-------------------------------------------------------------------------
//    private MutableBoolean m_aShutdownSync = new MutableBoolean (false);
    
	//-------------------------------------------------------------------------
    private JMSBeanMapper m_aBeanMapper = null;

    //-------------------------------------------------------------------------
    private boolean m_bUseTopic = false;

    //-------------------------------------------------------------------------
    private Long m_nMessageTTL = null;
}
