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

import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;

//=============================================================================
/** Base class providing all you need to work with JMS as client in ...
 *
 *  ... WORKER MODE
 *  
 *  Use it if you wish to register a worker process (instance) to a queue.
 *  Those worker listen for new incoming messages ...
 *  handle them ...
 *  and send response messages back to another queue.
 *  
 *  All those base on request/response pairs and corresponding queues.
 *  Loops, worker threads, request-response correlation is done by these
 *  helper.
 */
public class JMSWorkerBase
{
    //-------------------------------------------------------------------------
	public static final String QUEUE_TYPE = "worker";
	
    //-------------------------------------------------------------------------
	public static final String QUEUE_NAME_4_REQUESTS = "request";

	//-------------------------------------------------------------------------
	public JMSWorkerBase ()
		throws Exception
	{}

	//-------------------------------------------------------------------------
	public synchronized void startWorkerMode ()
	    throws Exception
	{
		if (m_aWorkerThread != null)
			return;
		
		impl_registerInQueueContext ();
		
		m_aWorkerThread = new Thread (new Runnable ()
		{
			@Override
			public void run()
			{
				impl_doWorkerLoop ();
			}
		});
		m_aWorkerThread.start ();
	}
	
	//-------------------------------------------------------------------------
	public /* NO SYNCHRONIZED HERE ! */ void stopWorkerMode ()
	    throws Exception
	{
		//System.out.println ("worker: stop worker ...");
		
		// tricky :-)
		// We reset our thread member first ...
		// but then those instance might die ...
		// Holding one last reference within this method scope ...
		// will ensure those instance will live till the end of this method .-)

		@SuppressWarnings("unused")
		Thread aWorkerThreadHold = null;

		synchronized (this)
		{
			if (m_aWorkerThread == null)
				return;
			
			aWorkerThreadHold = m_aWorkerThread;
			m_aWorkerThread   = null;
		}
		
		synchronized (m_aShutdownSync)
		{
			m_aShutdownSync.setValue(true);
			m_aShutdownSync.wait (); // TODO timeout ?
		}

		if (m_aWorkerConsumer != null)
			m_aWorkerConsumer.close();
		
		if (m_aWorkerProducer != null)
			m_aWorkerProducer.close();
		
		if (m_aSession != null)
			m_aSession.close();

		//System.out.println ("worker: worker stopped.");
	}

	//-------------------------------------------------------------------------
	protected synchronized void setQueueNamespace (final String sNamespace)
	    throws Exception
	{
		m_sQueueNamespace = sNamespace;
	}

	//-------------------------------------------------------------------------
	protected synchronized void setWorkerID (final String sID)
	    throws Exception
	{
		m_sWorkerID = sID;
	}

	//-------------------------------------------------------------------------
	protected /*no synchronize*/ Message newMessage ()
	    throws Exception
	{
		final Session aSession = mem_Session ();
		final Message aMessage = aSession.createTextMessage();
		return aMessage;
	}

	//-------------------------------------------------------------------------
	protected Message handleRequest (final Session aSession,
									 final Message aRequest)
        throws Exception
	{
		throw new UnsupportedOperationException ("Has to be implemented by derived class ... or has not to be used .-)");
	}
	
	//-------------------------------------------------------------------------
	private void impl_doWorkerLoop ()
	{
		try
		{
			//System.out.println ("worker loop : start ...");
			
			final Session         aSession = mem_Session ();
			final MessageProducer aWrite   = mem_WorkerProducer ();
			final MessageConsumer aRead    = mem_WorkerConsumer ();
			
			while (true)
			{
				synchronized (m_aShutdownSync)
				{
					if (m_aShutdownSync.booleanValue() == true)
					{
						m_aShutdownSync.notifyAll();
						break;
					}
				}
				
				//System.out.println ("worker loop : wait for request ...");
				final Message aRequest = aRead.receive(100);
				if (aRequest == null)
					continue;
				
				impl_dumpMessage ("REQUEST : ", aRequest);

				//System.out.println ("worker loop : handle request ...");
				final Message aResponse = handleRequest(aSession, aRequest);

				JMSRequestResponseManager.bindResponseToRequest(aResponse, aRequest);

				impl_dumpMessage ("RESPONSE : ", aResponse);

				//System.out.println ("worker loop : send response ...");
				final Destination aReplyToQueue = aRequest.getJMSReplyTo();
				if (aReplyToQueue != null)
					aWrite.send(aReplyToQueue, aResponse);
				else
					aWrite.send(aResponse); // TODO will fail ... because producer isn't bound to any queue by default ... use error queue instead ?

				//System.out.println ("worker loop : ack request ...");
				aRequest.acknowledge();
			}
		}
		catch (Throwable ex)
		{
			ex.printStackTrace();
			// TODO error handling ?!
		}
	}
	
	//-------------------------------------------------------------------------
	private void impl_dumpMessage (final String  sDesc   ,
								   final Message aMessage)
     	throws Exception
	{
		final StringBuffer sDump = new StringBuffer ();
		
		sDump.append (sDesc);
		sDump.append ("\n" );
		
		final Enumeration< ? > lProps = aMessage.getPropertyNames();
		while (lProps.hasMoreElements())
		{
			final String sProp  = (String) lProps.nextElement();
			final String sValue = aMessage.getStringProperty(sProp);
			
			sDump.append ("'"+sProp+"' = '"+sValue+"'\n");
		}
		
		//System.out.println (sDump.toString ());
	}
	
	//-------------------------------------------------------------------------
	private void impl_registerInQueueContext ()
	    throws Exception
	{
	    if (
	    	( ! StringUtils.isEmpty(m_sQueueNamespace)) &&
	    	( ! StringUtils.isEmpty(m_sWorkerID      ))
	       )
	    {
	    	impl_registerInQueueContext4NamespaceMode ();
	    }
	    else
	    if (
		    (StringUtils.isEmpty(m_sQueueNamespace)) ||
		    (StringUtils.isEmpty(m_sWorkerID      ))
		   )
	    {
	    	impl_registerInQueueContext4TempMode ();
	    }
	    else
	    	throw new UnsupportedOperationException ("No namspace nor any worker ID ... which queue name mode do you want to use ?");

		//System.out.println ("worker : my consumer ID   = '"+m_sMyConsumerID  +"'");
	    //System.out.println ("worker : my request queue = '"+m_sMyRequestQueue+"'");
	}
	
	//-------------------------------------------------------------------------
	private void impl_registerInQueueContext4NamespaceMode ()
	    throws Exception
	{
		final JMSQueueManager aManager      = JMSQueueManager.create(m_sQueueNamespace);
		final String          sMe           = m_sWorkerID;
		final String          sRequestQueue = JMSQueueManager.buildQueueName(sMe, QUEUE_NAME_4_REQUESTS);

		aManager.bindConsumer2Queue(sMe          , sRequestQueue);
		aManager.bindQueue2Type    (sRequestQueue, QUEUE_TYPE   );

		m_sMyConsumerID   = sMe          ;
		m_sMyRequestQueue = sRequestQueue;
		m_aQueueManager   = aManager     ;
	}

	//-------------------------------------------------------------------------
	private void impl_registerInQueueContext4TempMode ()
	    throws Exception
	{
		 final JMSQueueManager aManager   = JMSQueueManager.create(null);
		 final Session         aSession   = mem_Session ();
		 final Queue           aTempQueue = (Queue) aSession.createTemporaryQueue();
		 final String          sTempQueue = aTempQueue.getQueueName();
		 final String          sMe        = UUID.randomUUID().toString();
		 
		 aManager.bindConsumer2Queue(sMe       , sTempQueue);
		 aManager.bindQueue2Type    (sTempQueue, QUEUE_TYPE);

		 m_sWorkerID       = sMe;
		 m_sMyConsumerID   = sMe;
		 m_sMyRequestQueue = sTempQueue;
		 m_aQueueManager   = aManager;
	}

	//-------------------------------------------------------------------------
    private synchronized MessageProducer mem_WorkerProducer ()
    	throws Exception
    {
    	if (m_aWorkerProducer == null)
    	{
    		// note : our producer is not bound to any specific queue ...
    		// we use reply-to field of incoming message to know what's
    		// the right destination for our response .-)
    		
    		final Session aSession          = mem_Session ();
    		              m_aWorkerProducer = aSession.createProducer(null);
    	}
    	return m_aWorkerProducer;
    }

    //-------------------------------------------------------------------------
    private synchronized MessageConsumer mem_WorkerConsumer ()
    	throws Exception
    {
    	if (m_aWorkerConsumer == null)
    	{
    		final Session     aSession          = mem_Session ();
    		final Destination aRequestQueue     = mem_RequestQueue ();
    						  m_aWorkerConsumer = aSession.createConsumer(aRequestQueue);
    	}
    	return m_aWorkerConsumer;
    }

    //-------------------------------------------------------------------------
	private synchronized Destination mem_RequestQueue ()
	    throws Exception
	{
		if (m_aRequestQueue == null)
		{
			// note : impl_registerInQueueContext () has to be called before this method .-)
			// we need m_sMyRequestQueue well defined here .-)
			
			final Session     aSession              = mem_Session ();
			final String      sRequestQueueAbsolute = m_aQueueManager.getQueueNameAbsolute(m_sMyRequestQueue);
			final Destination aRequestQueue         = aSession.createQueue(sRequestQueueAbsolute);
			
			m_aRequestQueue = aRequestQueue;
		}
		return m_aRequestQueue;
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
    		m_iJMSClient = JMSClient.newClient();
    	return m_iJMSClient;
    }

    //-------------------------------------------------------------------------
    private IJMSClient m_iJMSClient = null;

    //-------------------------------------------------------------------------
    private Session m_aSession = null;

    //-------------------------------------------------------------------------
    private Destination m_aRequestQueue = null;
    
    //-------------------------------------------------------------------------
    private MessageProducer m_aWorkerProducer = null;

    //-------------------------------------------------------------------------
    private MessageConsumer m_aWorkerConsumer = null;

    //-------------------------------------------------------------------------
    private Thread m_aWorkerThread = null;

    //-------------------------------------------------------------------------
    private JMSQueueManager m_aQueueManager = null;

    //-------------------------------------------------------------------------
    private String m_sWorkerID = null;
    
    //-------------------------------------------------------------------------
    private String m_sMyConsumerID = null;
    
    //-------------------------------------------------------------------------
    private String m_sQueueNamespace = null;

    //-------------------------------------------------------------------------
    private String m_sMyRequestQueue = null;

    //-------------------------------------------------------------------------
	private MutableBoolean m_aShutdownSync = new MutableBoolean (false);
}
