package com.ibeo.di;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

//=============================================================================
/** The DIEnv knows the basic configuration of the DIContext.
 *  As e.g. the list of modules, it's main configuration entries etcpp.
 */
public class DIEnv
{
	//-------------------------------------------------------------------------
	public static final String ENV_CONFIG  = "config" ;
	public static final String ENV_MODULES = "modules";

	//-------------------------------------------------------------------------
	public static final String LIST_SEPARATOR = ",";
	
	//-------------------------------------------------------------------------
	/** internally used by the DIContext class - not thought to be used public ;-)
	 *	@internal
	 */
	protected DIEnv ()
		throws Exception
	{}

	//-------------------------------------------------------------------------
	/** provides read access to an env key as string value.
	 * 
	 *	@internal
	 * 
	 *	@param	sKey [IN]
	 *			the key where the value has to be returned.
	 *
	 *	@return the value for this key
	 *			Will never be null ! But can be empty !
	 */
	protected synchronized String get (final String sKey)
	    throws Exception
	{
		String sValue = mem_Envs ().get(sKey);
		       sValue = StringUtils.trimToEmpty(sValue);
		return sValue;
	}

	//-------------------------------------------------------------------------
	/** provides read access to an env key returned as string array
	 * 
	 *	@internal
	 * 
	 *	@param	sKey [IN]
	 *			the key where the value has to be returned.
	 *
	 *	@return the value for this key
	 *			Will never be null ! But can be empty !
	 */
	protected synchronized String[] getList (final String sKey)
	    throws Exception
	{
		final String   sValue = get (sKey);
		      String[] aList  = StringUtils.splitByWholeSeparatorPreserveAllTokens(sValue, LIST_SEPARATOR);

		if (aList == null)
			aList = new String[0];

		return aList;
	}

	//-------------------------------------------------------------------------
	/** define an env key/value pair (the generic way)
	 * 
	 *	@param	sKey [IN]
	 *			the new env key.
	 *
	 *	@param	sValue [IN]
	 *			the new value for it.
	 *
	 *	@return	THIS - so further environment definitions can be done.
	 */
	public synchronized DIEnv define (final String sKey  ,
									  final String sValue)
	    throws Exception
	{
		mem_Envs ().put(sKey, sValue);
		return this;
	}

	//-------------------------------------------------------------------------
	/** 'boot' this env by loading properties from the given resource.
	 * 
	 *	@param	sURL [IN]
	 *			the resource URL where the properties can be read from.
	 *
	 *	@return	THIS - so further environment definitions can be done.
	 */
	public synchronized DIEnv boot (final String sURL)
	    throws Exception
	{
		InputStream aStream = null;
		try
		{
			final URL        aURL    = new URL (sURL);
			                 aStream = aURL.openStream();
			final Properties lProps  = new Properties ();

			lProps.load(aStream);

			final Map< String, String > lEnvs = mem_Envs ();
			for (final String sKey : lProps.stringPropertyNames())  
				lEnvs.put(sKey, lProps.getProperty(sKey));
		}
		finally
		{
			IOUtils.closeQuietly(aStream);
		}
		
		return this;
	}

	//-------------------------------------------------------------------------
	/** define the configuration descriptor used later for loading/providing
	 *  configuration data to the DI context.
	 * 
	 *	@param	sURL [IN]
	 *			the resource URL where the configuration descriptor can be read from.
	 *
	 *	@return	THIS - so further environment definitions can be done.
	 */
	public synchronized DIEnv config (final String sURL)
		throws Exception
	{
		return define (ENV_CONFIG, sURL);
	}
	
	//-------------------------------------------------------------------------
	/** define the list of Guice modules (module class names !)
	 *  to be loaded into the DI context.
	 * 
	 *	@param	lModules [IN]
	 *			the list of modules.
	 *
	 *	@return	THIS - so further environment definitions can be done.
	 */
	public synchronized DIEnv modules (final String... lModules)
	    throws Exception
	{
		final String sModules = StringUtils.join(lModules, LIST_SEPARATOR);
		return define (ENV_MODULES, sModules);
	}
	
	//-------------------------------------------------------------------------
	private synchronized Map< String, String > mem_Envs ()
	    throws Exception
	{
		if (m_lEnvs == null)
			m_lEnvs = new HashMap< String, String > ();
		return m_lEnvs;
	}

	//-------------------------------------------------------------------------
	private Map< String, String > m_lEnvs = null;
}
