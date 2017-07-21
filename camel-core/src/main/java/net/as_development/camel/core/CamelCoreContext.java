package net.as_development.camel.core;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.camel.CamelContext;
import org.apache.camel.Component;
import org.apache.camel.Endpoint;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.ThreadPoolRejectedPolicy;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.spi.EndpointRegistry;
import org.apache.camel.spi.ExecutorServiceManager;
import org.apache.camel.spi.ShutdownStrategy;
import org.apache.camel.spi.ThreadPoolProfile;
import org.apache.commons.lang3.Validate;

//=============================================================================
public class CamelCoreContext
{
    //-------------------------------------------------------------------------
    public CamelCoreContext ()
        throws Exception
    {}
    
    //-------------------------------------------------------------------------
    public /* no synchronized */ CamelCoreContext create (final String sId)
        throws Exception
    {
		final CamelCoreContext aContext = new CamelCoreContext ();
		aContext.setId (sId);
		return aContext;
    }
    
    //-------------------------------------------------------------------------
	public synchronized void setId (final String sId)
		throws Exception
	{
		final DefaultCamelContext aContext = mem_CamelContext ();
		aContext.setName(sId);
	}

    //-------------------------------------------------------------------------
	public synchronized String getId ()
		throws Exception
	{
		final DefaultCamelContext aContext = mem_CamelContext ();
		final String              sId      = aContext.getName ();
		return sId;
	}

	//-------------------------------------------------------------------------
	public synchronized void start()
	    throws Exception
	{
		if (mem_CamelContext ().isStarted())
			return;
		mem_CamelContext ().start();
	}

	//-------------------------------------------------------------------------
	public synchronized void stop()
	    throws Exception
	{
		if ( ! mem_CamelContext ().isStarted())
			return;
		mem_CamelContext ().stop();
	}

	//-------------------------------------------------------------------------
	public synchronized void registerCamelJmsComponent (final CamelJmsComponent aComponent)
		throws Exception
	{
		final String sId = aComponent.getId();
		Validate.notEmpty (sId, "Camel Jms Component has no valid ID.");

		final JmsComponent aJmsComponent = aComponent.getJmsComponent();
		final CamelContext aCamelContext = mem_CamelContext();
		aCamelContext.addComponent(sId, aJmsComponent);
	}
	
    //-------------------------------------------------------------------------
	public synchronized void registerBean (final String sID  ,
									       final Object aBean)
	    throws Exception
	{
		if (Component.class.isAssignableFrom(aBean.getClass ()))
		{
			final CamelContext aCamelContext = mem_CamelContext();
			aCamelContext.addComponent(sID, (Component)aBean);
		}
		else
		{
			final SimpleRegistry aRegistry = mem_Registry ();
			aRegistry.put(sID, aBean);
		}
	}

	//-------------------------------------------------------------------------
	public synchronized void removeBean (final String sID)
	    throws Exception
	{
		final CamelContext aCamelContext = mem_CamelContext();
		if (aCamelContext.hasComponent(sID) != null)
			aCamelContext.removeComponent(sID);

		final SimpleRegistry aRegistry = mem_Registry ();
		if (aRegistry.containsKey(sID))
			aRegistry.remove(sID);
	}

	//-------------------------------------------------------------------------
//	@SuppressWarnings({ "unchecked", "rawtypes" })
	public synchronized void registerEndpoint (final String   sID,
											   final Endpoint aEP)
	    throws Exception
	{
//		final DefaultCamelContext aCamelContext = mem_CamelContext ();
//		final EndpointRegistry    aRegistry     = aCamelContext.getEndpointRegistry();
//		aRegistry.put(sID, aEP);

		final SimpleRegistry aRegistry = mem_Registry ();
		aRegistry.put(sID, aEP);
	}
	
    //-------------------------------------------------------------------------
    public synchronized < T extends Endpoint > Endpoint getEndpoint (final String     sID  ,
                                                                     final Class< T > aType)
        throws Exception
    {
        return mem_CamelContext ().getEndpoint(sID, aType);
    }

    //-------------------------------------------------------------------------
	public synchronized void removeEndpoint (final Endpoint aEP)
	    throws Exception
	{
	    mem_CamelContext ().removeEndpoint(aEP);
	}

    //-------------------------------------------------------------------------
	public synchronized void addRoutes (final RoutesBuilder aRoutesBuilder)
	    throws Exception
	{
	    mem_CamelContext ().addRoutes(aRoutesBuilder);
	}

	//-------------------------------------------------------------------------
    public synchronized void stopRoute (final String sRouteId)
        throws Exception
    {
        mem_CamelContext ().stopRoute(sRouteId);
    }

    //-------------------------------------------------------------------------
    public synchronized boolean removeRoute(final String sRouteId)
        throws Exception
    {
        return mem_CamelContext ().removeRoute(sRouteId);
    }

    //-------------------------------------------------------------------------
    public synchronized RouteDefinition getRouteDefinition (final String sRouteID)
        throws Exception
    {
        return mem_CamelContext ().getRouteDefinition(sRouteID);
    }
    
    //-------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
    public String dump ()
	    throws Exception
	{
		final StringBuffer   sDump     = new StringBuffer (256);
		final SimpleRegistry aRegistry = mem_Registry ();

		sDump.append("========= simple registry :\n");

		final Iterator< Entry< String, Object > > rRegistry = aRegistry.entrySet().iterator();
		while (rRegistry.hasNext())
		{
			final Entry< String, Object > aEntry = rRegistry.next    ();
			final String                  sKey   = aEntry   .getKey  ();
			final Object                  aValue = aEntry   .getValue();

			sDump.append ("["+sKey+"] = '"+aValue+"'\n");
		}

		sDump.append("========= EP registry :\n");

		final EndpointRegistry< String >            aEPRegistry = mem_CamelContext ().getEndpointRegistry();
		final Iterator< Entry< String, Endpoint > > rEPRegistry = aEPRegistry.entrySet().iterator();
		while (rEPRegistry.hasNext())
		{
			final Entry< String, Endpoint > aEntry = rEPRegistry.next    ();
			final String                    sKey   = aEntry     .getKey  ();
			final Endpoint                  aValue = aEntry     .getValue();

			sDump.append ("["+sKey+"] = '"+aValue+"'\n");
		}

		return sDump.toString ();
	}

    //-------------------------------------------------------------------------
	protected /* no synchronized */ CamelContext accessRealCamelContext ()
		throws Exception
	{
		return mem_CamelContext ();
	}
	
    //-------------------------------------------------------------------------
    private void impl_defineCamelShutdownStrategy (final CamelContext aContext)
        throws Exception
    {
        final ShutdownStrategy aStrategy = aContext.getShutdownStrategy();
        aStrategy.setShutdownNowOnTimeout(true                 );
        aStrategy.setTimeout             (500                  );
        aStrategy.setTimeUnit            (TimeUnit.MILLISECONDS);
    }

    //-------------------------------------------------------------------------
    private void impl_defineCamelDefaultThreadPoolProfile (final CamelContext aContext)
        throws Exception
    {
        final ExecutorServiceManager aMgr        = aContext.getExecutorServiceManager();
        final ThreadPoolProfile      aThreadPool = aMgr.getDefaultThreadPoolProfile();
        aThreadPool.setPoolSize              (1        );
        aThreadPool.setMaxPoolSize           (5        );
        aThreadPool.setAllowCoreThreadTimeOut(false    );
        aThreadPool.setDefaultProfile        (true     );
        aThreadPool.setRejectedPolicy        (ThreadPoolRejectedPolicy.CallerRuns);
    }

    //-------------------------------------------------------------------------
	private synchronized DefaultCamelContext mem_CamelContext ()
	    throws Exception
	{
		if (m_aContext == null)
		{
			final SimpleRegistry      aRegistry = mem_Registry ();
			final DefaultCamelContext aContext  = new DefaultCamelContext (aRegistry);

			impl_defineCamelShutdownStrategy         (aContext);
			impl_defineCamelDefaultThreadPoolProfile (aContext);

			m_aContext = aContext;
		}
		return m_aContext;
	}

	//-------------------------------------------------------------------------
	private synchronized SimpleRegistry mem_Registry ()
	    throws Exception
	{
		if (m_aRegistry == null)
		{
			final SimpleRegistry aRegistry = new SimpleRegistry ();
			m_aRegistry = aRegistry;
		}
		return m_aRegistry;
	}

	//-------------------------------------------------------------------------
	private DefaultCamelContext m_aContext = null;

	//-------------------------------------------------------------------------
	private SimpleRegistry m_aRegistry = null;
}
