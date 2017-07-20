package com.ibeo.di;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
//import com.squarespace.jersey2.guice.JerseyGuiceUtils;

//=============================================================================
/** central DI context object.
 *  It's a singleton. Any object inside DI context can {@link @Inject} it
 *  or call static getter function direct.
 */
public class DIContext
{
	//-------------------------------------------------------------------------
	/** force using of singleton getter !
	 */
	private DIContext ()
		throws Exception
	{}

	//-------------------------------------------------------------------------
	/** provides access to the singleton instance of this DIContext.
	 *
	 *	@return the singleton DI context object.
	 */
	public static synchronized DIContext get ()
		throws Exception
	{
		if (m_gSingleton == null)
			m_gSingleton = new DIContext ();
		return m_gSingleton;
	}

	//-------------------------------------------------------------------------
	/** provides access to the global DI context environment.
	 *
	 *  @see {@link DIEnv}
	 *
	 *	@return the global env of this DI context
	 */
	public synchronized DIEnv env ()
	    throws Exception
	{
		return mem_Env ();
	}
	
	//-------------------------------------------------------------------------
	/** provides access to the global DI context configuration.
	 *
	 *  @see {@link DIConfig}
	 *
	 *	@return the global configuration of this DI context
	 */
	public synchronized DIConfig config ()
	    throws Exception
	{
		return mem_Config ();
	}

	//-------------------------------------------------------------------------
	/** start the DI context
	 * 
	 *  There is no auto-start-any-bean-within-the-context magic !
	 *  This means more an 'initialization' and 'preparation' phase
	 *  to be done.
	 *  
	 *  Can be called often -
	 *  but run's one times only -
	 *  excepting you called stop in between.
	 */
	public synchronized void start ()
	    throws Exception
	{
		if (m_aMainInjector != null)
			return;
		
		final DIEnv          aEnv            = mem_Env ();
		final String[]       lModuleNames    = aEnv.getList(DIEnv.ENV_MODULES);
		final List< Module > lModules        = impl_loadModuleInsts (lModuleNames);

		lModules.add(new DIModule ());
		m_aMainInjector = Guice.createInjector(Stage.DEVELOPMENT, lModules);
	}

	//-------------------------------------------------------------------------
	public synchronized void stop ()
	    throws Exception
	{
		m_aMainInjector = null;
	}
	
	//-------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public synchronized < T > T getInstance (final Class< ? > aContract)
		throws Exception
	{
		final T aInst = (T) m_aMainInjector.getInstance(aContract);
		return aInst;
	}
	
	//-------------------------------------------------------------------------
	private /* no synchronized */ List< Module > impl_loadModuleInsts (final String... lModuleNames)
		throws Exception
	{
		final List< Module > lModules = new ArrayList< Module > ();
		
		for (final String sModule : lModuleNames)
		{
			final String     sModuleClass = sModule;
			final Class< ? > aModuleClass = Class.forName(sModuleClass);
			final Module     aModule      = (Module) aModuleClass.newInstance();
			lModules.add(aModule);
		}
		
		return lModules;
	}

	//-------------------------------------------------------------------------
	private synchronized DIEnv mem_Env()
	    throws Exception
	{
		if (m_aEnv == null)
			m_aEnv = new DIEnv ();
		return m_aEnv;
	}

	//-------------------------------------------------------------------------
	private synchronized DIConfig mem_Config ()
		throws Exception
	{
		if (m_aConfig == null)
		{
			final DIConfig aConfig = new DIConfig ();
			aConfig.bind(this);
			m_aConfig = aConfig;
		}
		return m_aConfig;
	}

	//-------------------------------------------------------------------------
	private static DIContext m_gSingleton = null;
	
	//-------------------------------------------------------------------------
	private DIEnv m_aEnv = null;

	//-------------------------------------------------------------------------
	private DIConfig m_aConfig = null;

	//-------------------------------------------------------------------------
	private Injector m_aMainInjector = null;
}
