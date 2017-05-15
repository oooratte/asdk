package net.as_development.asdk.jms.core.simple;

import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.commons.lang3.StringUtils;

import net.as_development.asdk.jms.core.IJMSClient;
import net.as_development.asdk.jms.core.JMSClient;
import net.as_development.asdk.jms.core.JMSEnv;
import net.as_development.asdk.tools.common.UriParser;

//=============================================================================
public class SimpleMessaging
{
	//-------------------------------------------------------------------------
	public SimpleMessaging ()
		throws Exception
	{}

	//-------------------------------------------------------------------------
	public synchronized void setEnv (final JMSEnv aEnv)
	    throws Exception
	{
		m_aEnv = aEnv;
	}

	//-------------------------------------------------------------------------
	public synchronized void close ()
	    throws Exception
	{
		final Session    aSession   = m_aSession;
		               m_aSession   = null;
                       m_iJMSClient = null;

        if (aSession != null)
			aSession.close();
	}

	//-------------------------------------------------------------------------
	public synchronized void subscribe (final String      sDestination,
						   			    final ISubscriber iSubscriber )
		throws Exception
    {
		final Session         aSession     = mem_Session         ();
		final Destination     aDestination = impl_getDestination (sDestination);
		final MessageConsumer aConsumer    = aSession.createConsumer(aDestination);
		
		aConsumer.setMessageListener(new MessageListener ()
		{
			@Override
			public /* no synchronized */ void onMessage(final Message aMessage)
			{
				try
				{
					String sMessage = null;
					if (TextMessage.class.isAssignableFrom(aMessage.getClass()))
					{
						final TextMessage aTextMessage = (TextMessage)aMessage;
						                  sMessage     = aTextMessage.getText();
					}
					else
				    	throw new UnsupportedOperationException ("No support for message type '"+aMessage.getClass ()+"' implemented yet.");
					
					iSubscriber.recieve(sMessage);
				}
				catch (Throwable ex)
				{
					System.err.println(ex.getMessage ());
					ex.printStackTrace(System.err);
				}
			}
		});
    }
	
	//-------------------------------------------------------------------------
	public synchronized void publish (final String sDestination,
						   			  final String sMessage    )
		throws Exception
    {
		final Session         aSession     = mem_Session         ();
		final Destination     aDestination = impl_getDestination (sDestination);
		final MessageProducer aProducer    = aSession.createProducer(aDestination);
		final TextMessage     aMessage     = aSession.createTextMessage();

		aMessage .setText(sMessage);
		aProducer.send   (aMessage);
    }

	//-------------------------------------------------------------------------
	private Destination impl_getDestination (final String sDestination)
		throws Exception
	{
		final UriParser   aURI         = UriParser.parse(sDestination);
		final Session     aSession     = mem_Session   ();
		final String      sScheme      = aURI.getScheme();
		final String      sDestName    = aURI.getHost  ();
			  Destination aDestination = null;

	    if (StringUtils.startsWithIgnoreCase(sDestination, "queue"))
	    	aDestination = aSession.createQueue(sDestName);
	    else
	    if (StringUtils.startsWithIgnoreCase(sDestination, "topic"))
	    	aDestination = aSession.createTopic(sDestName);
	    else
	    	throw new UnsupportedOperationException ("No support for scheme '"+sScheme+"' implemented yet.");

		return aDestination;
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
	private JMSEnv m_aEnv = null;
	
    //-------------------------------------------------------------------------
    private IJMSClient m_iJMSClient = null;

    //-------------------------------------------------------------------------
    private Session m_aSession = null;
}
