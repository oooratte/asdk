package com.ibeo.di;

import java.lang.ref.WeakReference;
import java.net.URL;

import org.apache.commons.lang.Validate;

import net.as_development.tools.configuration.ConfigurationFactory;
import net.as_development.tools.configuration.IReadOnlyConfiguration;

//=============================================================================
/** A global configuration provided by the DI context to the whole context.
 *  Will exists always - but can be empty if no data are available.
 */
public class DIConfig
{
	//-------------------------------------------------------------------------
	/** used internally - so protected ;-)
	 *
	 *  @internal
	 */
	protected DIConfig ()
		throws Exception
	{}

	//-------------------------------------------------------------------------
	/**	bind this config to the global DI context.
	 *
	 *  The configuration itself needs e.g. access to the DIEnv object
	 *  to know where it's configuration data has to be loaded from ;-)
	 *
	 * 	It's hold weak internally to not hold the context alive ...
	 *
	 *	@internal
	 *
	 *	@param	aContext [IN]
	 *			the DI context where this configuration is bound to.
	 */
	protected synchronized void bind (final DIContext aContext)
		throws Exception
	{
		Validate.notNull(aContext, "Invalid argument 'context'.");
		m_rContext = new WeakReference< DIContext > (aContext);
	}

	//-------------------------------------------------------------------------
	/** get the configuration value defined by key converted to the return type
	 *  caller defined on left side (if key exists and conversion is possible).
	 * 
	 *	@param	sKey [IN]
	 *			the configuration key.
	 *
	 *	@param	aType [IN]
	 *			the value type for conversion.
	 *
	 *	@return	the (might) existing and (hopefully) converted value.
	 */
	public synchronized < T > T getValue (final String     sKey ,
										  final Class< T > aType)
		throws Exception
	{
		final IReadOnlyConfiguration iConfig = mem_Config ();
		final T                     aValue  = (T) iConfig.get (sKey, aType);
		return aValue;
	}

	//-------------------------------------------------------------------------
	/** does the same then {@link getValue(String, Class)} ...
	 *  but return default value in case original value don't exists.
	 * 
	 *	@param	sKey [IN]
	 *			the configuration key.
	 *
	 *	@param	aType [IN]
	 *			the value type for conversion.
	 *
	 *	@param	aDefault [IN]
	 *			the default value in case original value don't exists.

	 *	@return	the (might) existing or the given default value.
	 */
	public synchronized < T > T getValue (final String     sKey    ,
										  final Class< T > aType   ,
										  final T          aDefault)
		throws Exception
	{
		final IReadOnlyConfiguration iConfig = mem_Config ();
		final T                     aValue  = (T) iConfig.get (sKey, aType, aDefault);
		return aValue;
	}

	//-------------------------------------------------------------------------
	private synchronized IReadOnlyConfiguration mem_Config ()
	    throws Exception
	{
		if (m_iConfig == null)
		{
			final DIEnv  aEnv       = m_rContext.get ().env();
			final String sConfigURL = aEnv.get(DIEnv.ENV_CONFIG);
			final URL    aConfigURL = new URL (sConfigURL);
						m_iConfig   = ConfigurationFactory.createReadOnlyConfiguration(aConfigURL);
		}
		return m_iConfig;
	}

	//-------------------------------------------------------------------------
	private WeakReference< DIContext > m_rContext = null; 
	
	//-------------------------------------------------------------------------
	private IReadOnlyConfiguration m_iConfig = null;
}
