package net.as_development.camel.core;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.camel.CamelContext;
import org.apache.camel.CamelContextAware;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Producer;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.RouteDefinition;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

//=============================================================================
public abstract class CamelRouteGroup extends RouteBuilder
{
    //-------------------------------------------------------------------------
    public CamelRouteGroup ()
    {}

    //-------------------------------------------------------------------------
    public synchronized void setCamelRouteContext (final CamelRouteContext aCamelRouteContext)
    	    throws Exception
    	{
    		m_aCamelRouteContext = new WeakReference< CamelRouteContext > (aCamelRouteContext);
    	}
    
    //-------------------------------------------------------------------------
	public synchronized void setGroupID (final String sID)
		throws Exception
	{
		m_sGroupID = sID;
	}

    //-------------------------------------------------------------------------
	public synchronized String getGroupID ()
		throws Exception
	{
		return m_sGroupID;
	}

	//-------------------------------------------------------------------------
	public synchronized void setJMSConnectionID (final String sID)
		throws Exception
	{
		m_sJMSConnectionID = sID;
	}

	//-------------------------------------------------------------------------
    public synchronized String getJMSConnectionID ()
        throws Exception
    {
    		return m_sJMSConnectionID;
    }

    //-------------------------------------------------------------------------
    public synchronized String getEndpoint4Request ()
    		throws Exception
    {
    		return m_sEPID4Request;
    }

    //-------------------------------------------------------------------------
    public synchronized String getEndpoint4Response ()
        throws Exception
    {
        return m_sEPID4Response;
    }

    //-------------------------------------------------------------------------
    public synchronized boolean exists ()
    		throws Exception
    {
	    	final CamelCoreContext     aCamelCoreContext = mem_CamelCoreContext();
	    	final Collection< String > lRouteIds         = mem_RouteRefs ().keySet();
	
	    	if (lRouteIds.isEmpty())
	    		return false;
	
	    	for (final String sRouteId : lRouteIds)
	    	{
	    		final ProcessorDefinition< ? > aRoute = aCamelCoreContext.getRouteDefinition(sRouteId);
	    		if (aRoute == null)
	    			return false;
	    	}
	
	    	return true;
    }

    //-------------------------------------------------------------------------
    public /* no synchronized */ void create ()
    		throws Exception
    {
	    	if (exists ())
	    		return;
	
	    	final CamelCoreContext aCamelCoreContext = mem_CamelCoreContext ();
	    	aCamelCoreContext.addRoutes(this); // -> calls configure() -> calls impl_create() -> calls ...
    }

//    //-------------------------------------------------------------------------
//    public synchronized void attach ()
//    	throws Exception
//    {
//    	if (m_lRouteDefs != null)
//    		return;
//
//    	impl_attach ();
//    }
//
    //-------------------------------------------------------------------------
    public synchronized void remove ()
    		throws Exception
    {
	    	if ( ! exists ())
	    		return;
	
	    	impl_remove ();
    }

    //-------------------------------------------------------------------------
    /** It's called from camel context to 'render' this route.
     *  We forward that request to our derived class ...
     */
    @Override
	public /* no synchronized */ void configure()
		throws Exception
	{
    		defineRoute ();
	}

    //-------------------------------------------------------------------------
    public /* no synchronized ! */ void send (final Message aRTMessage)
    		throws Exception
    {
	    	final Endpoint aEP4Request  = mem_EP4Request ();
	    	final Endpoint aEP4Response = mem_EP4Response();
	
	    	Validate.isTrue(aEP4Request != null, "Endpoint for sending out requests do not exists.");
	
	    	if (aEP4Response != null)
	    	{
	    		final String sBackURI = aEP4Response.getEndpointUri();
	//    		RT2MessageGetSet.setRecipients(aRTMessage, sBackURI);
	    	}
	
	    	final Exchange aCamelExchange = aEP4Request.createExchange();
	    	final Producer aSend          = aEP4Request.createProducer();
	    	
	    	aCamelExchange.setIn(aRTMessage);
	    	
	//    	final Message  aCamelMsg      = aCamelExchange.getIn();
	//    	RT2MessageFactoryCamel.toCamelMessage(aRTMessage, aCamelMsg);
	
	//    	// Async sending bring us 5% performance ...
	//    	// but brings also more threads into the game ...
	//    	// What do you prefer ? ;-)
	//
	//		final AsyncProcessor aAsync = AsyncProcessorConverterHelper.convert(aSend);
	//		final AsyncCallback  aSync  = new AsyncCallback ()
	//		{
	//			@Override
	//			public void done(boolean doneSync)
	//			{
	//				// TODO do we need to wait for the result ?!
	//			}
	//		};
	//		aAsync.process(aCamelExchange, aSync);
	
	    	aSend.process (aCamelExchange);
    }

//
//    //-------------------------------------------------------------------------
//    protected void setJMSReplyTo (final String sReplyTo)
//        throws Exception
//    {
//    	m_sJMSReplyTo = sReplyTo;
//    }
//

    //-------------------------------------------------------------------------
    protected synchronized void defineEP4Request (final String sEPID)
    		throws Exception
    {
    		m_sEPID4Request = sEPID;
    }

    //-------------------------------------------------------------------------
    protected synchronized void defineEP4Response (final String sEPID)
        throws Exception
    {
    		m_sEPID4Response = sEPID;
    }

    //-------------------------------------------------------------------------
    @Override
    public synchronized Endpoint endpoint (final String sEPURI)
    {
	    	try
	    	{
			final Endpoint                                 aEP     = super.endpoint (sEPURI);
	    		Validate.notNull(aEP, "No endpoint for URI '"+sEPURI+"'.");
	
		    	final Map< String, WeakReference< Endpoint > > lEPRefs = mem_EPRefs ();
	    		final WeakReference< Endpoint >                rEP     = new WeakReference< Endpoint > (aEP);
				final String                               sEPID   = UUID.randomUUID().toString();

			lEPRefs.put(sEPID, rEP);
			return aEP;
	    	}
	    	catch (Throwable ex)
	    	{
	    		throw new RuntimeException (ex);
	    	}
    }

    //-------------------------------------------------------------------------
    protected synchronized Endpoint endpoint (final String sEPID ,
    									          final String sEPURI)
        throws Exception
    {
	    	final Map< String, WeakReference< Endpoint > > lEPRefs = mem_EPRefs ();
	    	      WeakReference< Endpoint >                rEP     = lEPRefs.get(sEPID);
	    	      Endpoint                                 aEP     = null;
	
	    	if (rEP != null)
	    		aEP = rEP.get ();
	
	    	if (aEP == null)
	    	{
	    		aEP = super.endpoint (sEPURI);
	    		Validate.notNull(aEP, "No endpoint for URI '"+sEPURI+"'.");
	
	    		rEP = new WeakReference< Endpoint > (aEP);
	    		lEPRefs.put(sEPID, rEP);
	
	    		final CamelRouteContext aCamelRouteContext   = mem_CamelRouteContext ();
	        	final CamelCoreContext  aCamelRoutingContext = aCamelRouteContext.getCamelCoreContext ();
	        	aCamelRoutingContext.registerEndpoint(sEPID, aEP);
	    	}
	
	    	return aEP;
    }

    //-------------------------------------------------------------------------
    protected synchronized Endpoint endpointByName (final String sEPID)
        throws Exception
    {
    		final Map< String, WeakReference< Endpoint > > lEPRefs = mem_EPRefs ();
		      WeakReference< Endpoint >                rEP     = lEPRefs.get(sEPID);
		      Endpoint                                 aEP     = null;

		if (rEP != null)
			aEP = rEP.get ();

		if (aEP == null)
		{
			try
			{
				aEP = mem_CamelCoreContext().getEndpoint(sEPID, Endpoint.class);
			}
			catch (Throwable ex)
			{
				// ignored !
			}
		}

		if (aEP == null)
		{
			try
			{
				final String sRefURI = "ref:" + sEPID;
				aEP = endpoint (sRefURI);
			}
			catch (Throwable ex)
			{
				// ignored !
			}
		}

		return aEP;
    }

    //-------------------------------------------------------------------------
    @SuppressWarnings("unchecked")
	protected synchronized < T > T bean (final String     sBeanID,
    					                     final Class< T > aType  )
        throws Exception
    {
	    	final Object aBean = aType.newInstance ();
	    	bean (sBeanID, aBean);
	    	return (T) aBean;
    }

    //-------------------------------------------------------------------------
    @SuppressWarnings("unchecked")
	protected synchronized < T > T bean (final String sBeanID,
    					                     final Object aBean  )
        throws Exception
    {
	    	final CamelRouteContext                      aCamelRouteContext = mem_CamelRouteContext ();
	    	final CamelCoreContext                       aCamelCoreContext  = aCamelRouteContext.getCamelCoreContext ();
	    	final Map< String, WeakReference< Object > > lBeanRefs          = mem_BeanRefs ();
	    	final WeakReference< Object >                rBean              = new WeakReference< Object > (aBean);
	
		aCamelCoreContext.registerBean(sBeanID, aBean);
	    	lBeanRefs.put(sBeanID, rBean);
	
	    	if (CamelContextAware.class.isAssignableFrom(aBean.getClass()))
	    	{
	    	    final CamelContext      aRealCamelContext  = aCamelCoreContext.accessRealCamelContext();
	    		final CamelContextAware aCamelContextAware = (CamelContextAware) aBean;
	    		aCamelContextAware.setCamelContext(aRealCamelContext);
	    	}
	
	    	return (T) aBean;
    }

    //-------------------------------------------------------------------------
    public synchronized RouteDefinition from (final String sEPURI)
    {
	    	try
	    	{
	    		final RouteDefinition aOrg = super.from (sEPURI);
	    		final RouteDefinition aDef = new CamelInterceptedRouteDefinition (this, aOrg);
	    		return aDef;
	    	}
	    	catch (Throwable ex)
	    	{
	    		throw new RuntimeException (ex);
	    	}
    }

    //-------------------------------------------------------------------------
    public synchronized RouteDefinition from (final Endpoint aEP)
    {
	    	try
	    	{
	    		final RouteDefinition aOrg = super.from (aEP);
	    		final RouteDefinition aDef = new CamelInterceptedRouteDefinition (this, aOrg);
	    		return aDef;
	    	}
	    	catch (Throwable ex)
	    	{
	    		throw new RuntimeException (ex);
	    	}
    }

    //-------------------------------------------------------------------------
    protected abstract void defineRoute ()
        throws Exception;

    //-------------------------------------------------------------------------
    protected /* no synchronized */ String nameRoute (final String... lNameParts)
    		throws Exception
    {
	    	final String sFullName = makeNameAbsolute (lNameParts);
	    	return sFullName;
    }

    //-------------------------------------------------------------------------
    protected /* no synchronized */ String nameEP (final String... lNameParts)
    		throws Exception
    {
	    	final String sFullName = makeNameAbsolute (lNameParts);
	    	return sFullName;
    }

    //-------------------------------------------------------------------------
    protected /* no synchronized */ String nameBean (final String... lNameParts)
    		throws Exception
    {
	    	final String sFullName = makeNameAbsolute  (lNameParts);
	    	return sFullName;
    }

    //-------------------------------------------------------------------------
    protected /* no synchronized */ String nameQueue (final String... lNameParts)
    		throws Exception
    {
	    	final String sFullName = makeNameAbsolute (lNameParts);
	    	return sFullName;
    }

    //-------------------------------------------------------------------------
    protected /* no synchronized */ String nameTopic (final String... lNameParts)
    		throws Exception
    {
	    	final String sFullName = makeNameAbsolute (lNameParts);
	    	return sFullName;
    }

    //-------------------------------------------------------------------------
    protected /* no synchronized */ String defineQueueEPURI (final String sQueueEP)
    		throws Exception
    {
	    	final StringBuilder sURI = new StringBuilder (256);
	    	sURI.append (getJMSConnectionID()           );
	    	sURI.append (":queue:"                      );
	    	sURI.append (sQueueEP                       );
	    	sURI.append ("?"                            );
	    	sURI.append (impl_getQueueSettingsStandard());
	    	return sURI.toString ();
    }

    //-------------------------------------------------------------------------
    protected /* no synchronized */ String defineTopicEPURI (final String sTopicEP)
    		throws Exception
    {
	    	final StringBuilder sURI = new StringBuilder (256);
	    	sURI.append (getJMSConnectionID()           );
	    	sURI.append (":topic:"                      );
	    	sURI.append (sTopicEP                       );
	    	sURI.append ("?"                            );
	    	sURI.append (impl_getTopicSettingsStandard());
	    	return sURI.toString ();
    }

    //-------------------------------------------------------------------------
    protected /* no synchronized */ String defineSimuTopicEPURI (final String sTopicEP)
        throws Exception
    {
        final StringBuilder sURI = new StringBuilder (256);
        sURI.append ("simutopic:"                   );
        sURI.append (sTopicEP                       );
        sURI.append ("?"                            );
        sURI.append (impl_getTopicSettingsStandard());
        return sURI.toString ();
    }

    //-------------------------------------------------------------------------
    protected /* no synchronized */ String defineDirectEPURI (final String sDirectEP)
    		throws Exception
    {
	    	final StringBuilder sURI = new StringBuilder (256);
	    	sURI.append ("direct:");
	    	sURI.append (sDirectEP);
	    	return sURI.toString ();
    }

    //-------------------------------------------------------------------------
    protected /* no synchronized */ String defineBeanEPURI (final String sBeanEP,
    								                            final String sMethod)
    	    throws Exception
    {
	    	final StringBuilder sURI = new StringBuilder (256);
	    	sURI.append ("bean:"   );
	    	sURI.append (sBeanEP   );
	    	sURI.append ("?method=");
	    	sURI.append (sMethod   );
	    	return sURI.toString ();
    }

    //-------------------------------------------------------------------------
    protected /* no synchronized */ String makeNameAbsolute (final String... lNameParts)
    		throws Exception
    {
//    	final String sNamePrefix   = mem_NamePrefix ();
//    	final String sName         = RT2RouteNaming.nameEP(lNameParts        );
//    	final String sAbsoluteName = RT2RouteNaming.nameIt(sNamePrefix, sName);
	    	final String sAbsoluteName = StringUtils.join(lNameParts, "_");
	    	return sAbsoluteName;
    }

    //-------------------------------------------------------------------------
    public synchronized String dump ()
    		throws Exception
    {
	    	final StringBuffer sDump = new StringBuffer (256);
	
	    	sDump.append ("------------------------------------------------\n");
	    	sDump.append ("route-group   : " + m_sGroupID       +         "\n");
	    	sDump.append ("ep-4-requests : " + m_sEPID4Request  +         "\n");
	    	sDump.append ("ep-4-response : " + m_sEPID4Response +         "\n");
	
	    	sDump.append ("----- beans :\n");
	    	final Iterator< Entry< String, WeakReference< Object >>> lBeans = mem_BeanRefs().entrySet().iterator();
	    	while (lBeans.hasNext())
	    	{
	    		final Entry< String, WeakReference< Object >> aBean     = lBeans.next    ();
	    		final String                                  sBeanID   = aBean .getKey  ();
	    		final Object                                  aBeanInst = aBean .getValue() != null ? aBean.getValue().get () : null;
	    		sDump.append ("      bean["+sBeanID+"] = "+aBeanInst+"\n");
	    	}
	
	    	sDump.append ("----- endpoints :\n");
	    	final Iterator< Entry< String, WeakReference< Endpoint >>> lEPs = mem_EPRefs().entrySet().iterator();
	    	while (lEPs.hasNext())
	    	{
	    		final Entry< String, WeakReference< Endpoint >> aEP     = lEPs.next    ();
	    		final String                                    sEPID   = aEP .getKey  ();
	    		final Endpoint                                  aEPInst = aEP .getValue() != null ? aEP.getValue().get () : null;
	    		sDump.append ("      endpoint["+sEPID+"] = "+aEPInst+"\n");
	    	}
	
	    	sDump.append ("----- routes :\n");
	    	final Iterator< Entry< String, WeakReference< ProcessorDefinition< ? > >>> lRoutes = mem_RouteRefs().entrySet().iterator();
	    	while (lRoutes.hasNext())
	    	{
	    		final Entry< String, WeakReference< ProcessorDefinition< ? > >> aRoute     = lRoutes.next    ();
	    		final String                                                    sRouteID   = aRoute .getKey  ();
	    		final ProcessorDefinition< ? >                                  aRouteInst = aRoute .getValue() != null ? aRoute.getValue().get () : null;
	    		sDump.append ("      route["+sRouteID+"] = "+aRouteInst+"\n");
	    	}
	
	    	return sDump.toString ();
    }

    //-------------------------------------------------------------------------
    private /* no synchronized */ void impl_remove ()
    		throws Exception
    {
	    	final CamelCoreContext                                         aCamelCoreContext = mem_CamelRouteContext().getCamelCoreContext();
	    	final Map< String, WeakReference< ProcessorDefinition< ? > > > lRoutes           = mem_RouteRefs        ();
	    	final Map< String, WeakReference< Endpoint > >                 lEPs              = mem_EPRefs           ();
	    	final Map< String, WeakReference< Object > >                   lBeans            = mem_BeanRefs         ();
	
	    	impl_removeRoutesFromContextOrFail    (aCamelCoreContext, lRoutes);
	    	impl_removeEndpointsFromContextOrFail (aCamelCoreContext, lEPs   );
        impl_removeBeansFromContextOrFail     (aCamelCoreContext, lBeans );

        final ProducerTemplate aProducer = m_aProducerTemplate;
        m_aProducerTemplate = null;

        if (aProducer != null)
        		aProducer.stop();
    }

    //-------------------------------------------------------------------------
    private /* no synchronized */ void impl_removeRoutesFromContextOrFail (final CamelCoreContext                                         aContext,
    												                           final Map< String, WeakReference< ProcessorDefinition< ? > > > lRoutes )
    	    throws Exception
    {
	    	if (lRoutes == null)
	    		return;
	
	    	for (final WeakReference< ProcessorDefinition< ? > > aRoute : lRoutes.values ())
	    		impl_removeRouteFromContextOrFail (aContext, aRoute);
	
	    	lRoutes.clear ();
    }

    //-------------------------------------------------------------------------
    private /* no synchronized */ void impl_removeRouteFromContextOrFail (final CamelCoreContext                         aContext ,
    												                          final WeakReference< ProcessorDefinition< ? > > aRouteRef)
    	    throws Exception
    {
	    	if (aRouteRef == null)
	    		return;
	
	    	final ProcessorDefinition< ? > aRoute = aRouteRef.get ();
	    	if (aRoute == null)
	    		return;
	
	    	final String sRouteId = aRoute.getId();
	
	    	                    aContext.stopRoute  (sRouteId);
	    	final boolean bOK = aContext.removeRoute(sRouteId);
	
	    	if ( ! bOK)
	    	{
	    		// TODO tbd
	    	}
	
	    	aRouteRef.clear();
    }

    //-------------------------------------------------------------------------
    private /* no synchronized */ void impl_removeEndpointsFromContextOrFail (final CamelCoreContext                        aContext,
    												                              final Map< String, WeakReference< Endpoint > > lEPs    )
    	    throws Exception
    {
	    	if (lEPs == null)
	    		return;
	
	    	for (final WeakReference< Endpoint > aEP : lEPs.values ())
	    		impl_removeEndpointFromContextOrFail (aContext, aEP);
	
	    	lEPs.clear ();
    }

    //-------------------------------------------------------------------------
    private /* no synchronized */ void impl_removeEndpointFromContextOrFail (final CamelCoreContext         aContext,
    												                             final WeakReference< Endpoint > aEPRef  )
        	throws Exception
    {
	    	if (aEPRef == null)
	    		return;
	
	    	final Endpoint aEP = aEPRef.get ();
	    	if (aEP == null)
	    		return;
	
        aContext.removeEndpoint(aEP);
	
	    	aEPRef.clear();
    }

    //-------------------------------------------------------------------------
    private /* no synchronized */ void impl_removeBeansFromContextOrFail (final CamelCoreContext                      aContext,
    											                              final Map< String, WeakReference< Object > > lBeans  )
        	throws Exception
    {
	    	if (lBeans == null)
	    		return;
	
	    	final Iterator< Entry< String, WeakReference< Object > > > rBeans = lBeans.entrySet().iterator();
	    	while (rBeans.hasNext())
	    	{
	    		final Entry< String, WeakReference< Object > > rBean = rBeans.next    ();
	    		final String                                   sId   = rBean .getKey  ();
	    		final WeakReference< Object >                  aBean = rBean .getValue();
	
	    		impl_removeBeanFromContextOrFail (aContext, sId, aBean);
	    	}
    }

    //-------------------------------------------------------------------------
    private /* no synchronized */ void impl_removeBeanFromContextOrFail (final CamelCoreContext        aContext,
    											                             final String                  sBeanID ,
    											                             final WeakReference< Object > aBeanRef)
        	throws Exception
    {
	    	if (aBeanRef == null)
	    		return;
	
	    	final Object aBean = aBeanRef.get ();
	    	if (aBean == null)
	    		return;
	
	    	aContext.removeBean(sBeanID);
    }

    //-------------------------------------------------------------------------
    private synchronized Endpoint impl_getEndpoint (final String sEPID)
	    	throws Exception
    {
        if (StringUtils.isEmpty (sEPID))
            return null;
        
	    	final Map< String, WeakReference< Endpoint > > lEPRefs = mem_EPRefs ();
	    	final WeakReference< Endpoint >                rEP     = lEPRefs.get(sEPID);
	    	      Endpoint                                 aEP     = null;
	
	    	if (rEP != null)
	    		aEP = rEP.get ();
	
	    	return aEP;
    }

    //-------------------------------------------------------------------------
    private synchronized String impl_getQueueSettingsStandard ()
	    	throws Exception
    {
	    	if (m_sQueueSettingsStd == null)
	    	{
	    		final StringBuffer sSettings = new StringBuffer (256);
	
	    		// see http://camel.apache.org/jms.html
	
	    		sSettings.append ("concurrentConsumers=1"    ); //   mandatory ... ensure ONE consumer handle our messages at the same time (will guarantee order of messages)
	    		sSettings.append ("&maxConcurrentConsumers=1");
	    		sSettings.append ("&maxMessagesPerTask=-1"   ); //   unlimited messages for one thread within our thread pool
	    		sSettings.append ("&asyncConsumer=false"     ); // ! important ... otherwise order of messages wont be guaranteed
	    		sSettings.append ("&preserveMessageQos=true" ); //   optional  ... does nothing until somewhere define right JMS header (as e.g. JMSPriority)
	
	    		m_sQueueSettingsStd = sSettings.toString ();
	    	}
	
	    	return m_sQueueSettingsStd;
    }

    //-------------------------------------------------------------------------
    private synchronized String impl_getTopicSettingsStandard ()
	    	throws Exception
    {
	    	if (m_sTopicSettingsStd == null)
	    	{
	    		final StringBuffer sSettings = new StringBuffer (256);
	
	    		// see http://camel.apache.org/jms.html
	    		
	    		// ATTENTION: Be careful : count of concurrentConsumer will influence how many consumer will be created for same topic within same route ...
	    		// So it duplicate each message for each consumer ... and you will get a firework of messages (which is not expected). Let the count 1 !
	    		
            sSettings.append ("concurrentConsumers=1"    ); // initial pool size
	    		sSettings.append ("&maxConcurrentConsumers=1"); // max pool size
	    		sSettings.append ("&maxMessagesPerTask=-1"   ); // unlimited messages for one thread within our thread pool
	    		sSettings.append ("&asyncConsumer=true"      ); // sped up broadcast by push messages asynchronous
            sSettings.append ("&disableReplyTo=true"     );
	    		
	    		m_sTopicSettingsStd = sSettings.toString ();
	    	}
	
	    	return m_sTopicSettingsStd;
    }

    //-------------------------------------------------------------------------
  	protected synchronized CamelRouteContext mem_CamelRouteContext ()
  		throws Exception
  	{
  		Validate.notNull(m_aCamelRouteContext, "Route not bound to routing context ?!");
  		final CamelRouteContext aContext = m_aCamelRouteContext.get();
  		Validate.notNull(aContext, "Routing context already closed ?!");
  		return aContext;
  	}
  	
  	//-------------------------------------------------------------------------
    private synchronized CamelCoreContext mem_CamelCoreContext ()
	    	throws Exception
    {
	    	final CamelRouteContext aCamelRouteContext = mem_CamelRouteContext ();
	    	final CamelCoreContext  aCamelCoreContext  = aCamelRouteContext.getCamelCoreContext();
	    	return aCamelCoreContext;
    }

    //-------------------------------------------------------------------------
    protected synchronized Map< String, WeakReference< ProcessorDefinition< ? > > > mem_RouteRefs ()
        throws Exception
    {
	    	if (m_lRouteDefs == null)
	    	{
	    		final Map< String, WeakReference< ProcessorDefinition< ? > > > lRouteDefs = new ConcurrentHashMap< String, WeakReference< ProcessorDefinition< ? > > >();
	    		m_lRouteDefs = lRouteDefs;
	    	}
	    	return m_lRouteDefs;
    }

    //-------------------------------------------------------------------------
    private synchronized Map< String, WeakReference< Endpoint > > mem_EPRefs ()
        throws Exception
    {
	    	if (m_lEPRefs == null)
	    	{
	    		final Map< String, WeakReference< Endpoint > > lEPRefs = new ConcurrentHashMap< String, WeakReference< Endpoint > >();
	    		m_lEPRefs = lEPRefs;
	    	}
	    	return m_lEPRefs;
    }

    //-------------------------------------------------------------------------
    private synchronized Map< String, WeakReference< Object > > mem_BeanRefs ()
        throws Exception
    {
	    	if (m_lBeanRefs == null)
	    		m_lBeanRefs = new ConcurrentHashMap< String, WeakReference< Object > > ();
	    	return m_lBeanRefs;
    }

	//-------------------------------------------------------------------------
    private synchronized Endpoint mem_EP4Request ()
        throws Exception
    {
	    	if (m_aEP4Request == null)
	    		m_aEP4Request = impl_getEndpoint (m_sEPID4Request);
	    	return m_aEP4Request;
    }

	//-------------------------------------------------------------------------
    private synchronized Endpoint mem_EP4Response ()
        throws Exception
    {
	    	if (m_aEP4Response == null)
	    		m_aEP4Response = impl_getEndpoint (m_sEPID4Response);
	    	return m_aEP4Response;
    }

    //-------------------------------------------------------------------------
    private WeakReference< CamelRouteContext > m_aCamelRouteContext = null;
    
    //-------------------------------------------------------------------------
	private String m_sGroupID = null;

	//-------------------------------------------------------------------------
	private String m_sNamePrefix = null;

	//-------------------------------------------------------------------------
	private String m_sJMSConnectionID = null;

    //-------------------------------------------------------------------------
    private Map< String, WeakReference< ProcessorDefinition< ? > > > m_lRouteDefs = null;

    //-------------------------------------------------------------------------
    private Map< String, WeakReference< Endpoint > > m_lEPRefs = null;

    //-------------------------------------------------------------------------
    private Map< String, WeakReference< Object > > m_lBeanRefs = null;

    //-------------------------------------------------------------------------
    private String m_sEPID4Request = null;

    //-------------------------------------------------------------------------
    private Endpoint m_aEP4Request = null;

    //-------------------------------------------------------------------------
    private String m_sEPID4Response = null;

    //-------------------------------------------------------------------------
    private Endpoint m_aEP4Response = null;

    //-------------------------------------------------------------------------
    private ProducerTemplate m_aProducerTemplate = null;

    //-------------------------------------------------------------------------
    private String m_sJMSReplyTo = null;

    //-------------------------------------------------------------------------
    private String[] m_lRecipients = null;

    //-------------------------------------------------------------------------
    private String m_sQueueSettingsStd = null;

    //-------------------------------------------------------------------------
    private String m_sTopicSettingsStd = null;
}