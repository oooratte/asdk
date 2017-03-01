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
package net.as_development.tools.configuration;

import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.DefaultConfigurationBuilder;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.as_development.tools.configuration.impl.ComplexConfiguration;
import net.as_development.tools.configuration.impl.SimpleConfiguration;

//=============================================================================
public class ConfigBase
{
	//-------------------------------------------------------------------------
	public ConfigBase ()
	    throws Exception
	{}

	//-------------------------------------------------------------------------
	protected void defineConfig (final String sConfigPath   ,
							     final String sConfigPackage)
	    throws Exception
	{
		m_sConfigPath    = sConfigPath   ;
		m_sConfigPackage = sConfigPackage;
	}
	
	//-------------------------------------------------------------------------
	protected < T > T get (final String     sKey ,
					       final Class< T > aType)
		throws Exception
	{
		return (T) mem_Config ().get(sKey, aType);
	}

	//-------------------------------------------------------------------------
	/** @return the value for the queried key if it exists - the given default otherwise.
	 *  
	 *  @param	sKey [IN]
	 *  		the configuration key where we want to get it's value.
	 */
	protected < T > T get (final String     sKey    ,
						   final Class< T > aType   ,
						   final T          aDefault)
		throws Exception
	{
		return (T) mem_Config ().get(sKey, aType, aDefault);
	}

	//-------------------------------------------------------------------------
	/** @return return all key-value pairs which are child of the defined key.
	 *
	 * 	This is useful for lists in e.g. XML configuration files
	 *  where numbered childs exists below a root-key element.
	 *  
	 *  Further you can filter those lists if you define a key-type.
	 *  
	 *  @param	sKey [IN]
	 *  		the root key where we have to search for children.
	 *  
	 * 	@param	sType [IN]
	 * 			the key type for all childs to be selected from a list of
	 * 			ALL childs.
	 */
	protected Set< Map< String, String > > gets (final String sKey ,
            							         final String sType)
        throws Exception
	{
		return mem_Config ().gets(sKey, sType);
	}

	//-------------------------------------------------------------------------
	private synchronized IComplexConfiguration mem_Config ()
		throws Exception
	{
		if (m_iConfig == null)
		{
			Validate.notEmpty(m_sConfigPath   , "Miss 'configPath'. Did you called defineConfig() ?"   );
			Validate.notEmpty(m_sConfigPackage, "Miss 'configPackage'. Did you called defineConfig() ?");

			m_iConfig = ConfigurationFactory.getComplexConfiguration(m_sConfigPath, m_sConfigPackage);
		}
		return m_iConfig;
	}

	//-------------------------------------------------------------------------
	private String m_sConfigPath = null;
	
	//-------------------------------------------------------------------------
	private String m_sConfigPackage = null;

	//-------------------------------------------------------------------------
	private IComplexConfiguration m_iConfig = null;
}
