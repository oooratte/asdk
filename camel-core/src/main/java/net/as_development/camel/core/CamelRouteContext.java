package net.as_development.camel.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.camel.Endpoint;

//=============================================================================
public class CamelRouteContext
{
    //------------------------------------------------------------------------- 
	public CamelRouteContext ()
	{}

    //------------------------------------------------------------------------- 
	public synchronized void setCamelCoreContext (final CamelCoreContext aContext)
		throws Exception
	{
		m_aCamelCoreContext = aContext;
	}

	//-------------------------------------------------------------------------
	public /* no synchronized */< T extends CamelRouteGroup > void registerRoute (final T aRoute)
	    throws Exception
	{
		final String                         sId     = aRoute.getGroupID ();
		final Map< String, CamelRouteGroup > lRoutes = mem_Routes ();
		final boolean                        bIsNew  = ( ! lRoutes.containsKey(sId));
		
		if ( ! bIsNew)
			return;

		lRoutes.put(sId, aRoute);

		aRoute.setCamelRouteContext(this);
		aRoute.create              (    );
	}
	
	//-------------------------------------------------------------------------
	protected /* no synchronized */ String mapEPId2EPUri (final String sEPID)
	    throws Exception
	{
	    final Endpoint aEP    = m_aCamelCoreContext.getEndpoint(sEPID, Endpoint.class);
	    final String   sEPURI = aEP.getEndpointUri();
	    return sEPURI;
	}
	
	//-------------------------------------------------------------------------
	protected /* no synchronized */ CamelCoreContext getCamelCoreContext ()
	    throws Exception
	{
		return m_aCamelCoreContext;
	}

	//-------------------------------------------------------------------------
	protected synchronized Map< String, CamelRouteGroup > mem_Routes ()
		throws Exception
	{
		if (m_lRoutes == null)
			m_lRoutes = new ConcurrentHashMap< String, CamelRouteGroup > ();
		return m_lRoutes;
	}

	//-------------------------------------------------------------------------
	private CamelCoreContext m_aCamelCoreContext = null;

	//-------------------------------------------------------------------------
	private Map< String, CamelRouteGroup > m_lRoutes = null;
}
