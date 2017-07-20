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
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.DefaultConfigurationBuilder;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import net.as_development.tools.configuration.impl.ComplexConfiguration;
import net.as_development.tools.configuration.impl.InlineConfiguration;

//=============================================================================
public class ConfigurationFactory
{
	//-------------------------------------------------------------------------
	public static final String ENCODING = "utf-8";
	
	//-------------------------------------------------------------------------
	/// we provide a static API only ... ctor not needed real .-)
	private ConfigurationFactory ()
	    throws Exception
	{}

	//-------------------------------------------------------------------------
	public static synchronized IInlineConfiguration createInlineConfiguration ()
		throws Exception
	{
		final IInlineConfiguration iConfig = new InlineConfiguration ();
		return iConfig;
	}

	//-------------------------------------------------------------------------
	public static synchronized IReadOnlyConfiguration createReadOnlyConfiguration (final URL aURL)
		throws Exception
	{
		final DefaultConfigurationBuilder aLoader = new DefaultConfigurationBuilder ();
		
		aLoader.setListDelimiter             ((char) 0);
		aLoader.setDelimiterParsingDisabled  ( true   );
		aLoader.setAttributeSplittingDisabled( true   );
		aLoader.setEncoding                  (ENCODING);
		aLoader.setURL                       (aURL    );
		aLoader.clearErrorListeners          (        );
		
		final Configuration        aConfig4Read = aLoader.getConfiguration();
		final ComplexConfiguration aConfig      = new ComplexConfiguration ();

		aConfig.bindStore4Reading(aConfig4Read);
		
		return aConfig;
	}

	//-------------------------------------------------------------------------
	@Deprecated
	public static synchronized IComplexConfiguration getComplexConfiguration (final String sConfigPath   ,
																			  final String sConfigPackage)
		throws Exception
	{
		final URL                         aDescriptor     = impl_getConfigDescriptor     (sConfigPath, sConfigPackage);
   		final File                        aNormDescriptor = impl_getNormalizedDescriptor (aDescriptor, sConfigPath   , sConfigPackage);
		final DefaultConfigurationBuilder aLoader         = new DefaultConfigurationBuilder ();
		
		aLoader.setListDelimiter             ((char) 0                       );
		aLoader.setDelimiterParsingDisabled  ( true                          );
		aLoader.setAttributeSplittingDisabled( true                          );
		aLoader.setEncoding                  (ENCODING                       );
		aLoader.setURL                       (aNormDescriptor.toURI().toURL());
		aLoader.clearErrorListeners          (                               );
		
		final Configuration        aConfig4Read = aLoader.getConfiguration();
		final ComplexConfiguration aConfig      = new ComplexConfiguration ();

		aConfig.bindStore4Reading(aConfig4Read);
		
		return aConfig;
	}

	//-------------------------------------------------------------------------
	private static File impl_getNormalizedDescriptor (final URL    aDescURL      ,    
													        String sConfigPath   ,
													  final String sConfigPackage)
	    throws Exception
	{
	    String sDesc = IOUtils.toString(aDescURL, ENCODING);
	           sDesc = StringUtils.replace(sDesc, "file:", "");
	    
	    final Iterator< Entry< Object, Object > > lProps = System.getProperties ().entrySet().iterator();
	    while (lProps.hasNext())
	    {
	    	final Entry< Object, Object > aProp  = lProps.next();
	    	
	    	final Object aKey = aProp.getKey ();
	    	if ( ! (aKey instanceof String))
	    		continue;

	    	final Object aValue = aProp.getValue ();
	    	if ( ! (aValue instanceof String))
	    		continue;
	    	
	    	final String sKey   = (String)aKey;
	    	final String sValue = (String)aValue;
	    	
	        sDesc = StringUtils.replace(sDesc, "${"+sKey+"}", sValue);
	    }

	    // strip protocol as classpath: or file:
	    sConfigPath = StringUtils.substringAfterLast(sConfigPath, ":");
	    
	    final String sUUID     = UUID.randomUUID().toString();
		final File   aBaseDir  = new File (FileUtils.getTempDirectory(), "cfg-desc"                     );
		final File   aWorkDir  = new File (aBaseDir                    , sConfigPath                    );
		final File   aDescFile = new File (aWorkDir                    , sConfigPackage+"-"+sUUID+".xml");

		aBaseDir .deleteOnExit();
		aDescFile.deleteOnExit();
		
		aWorkDir.mkdirs();
		Validate.isTrue(aWorkDir.exists(), "Could not create temp work dir for generating configuration descriptor files.");

		FileUtils.deleteQuietly(aDescFile);
		Validate.isTrue( ! aDescFile.exists(), "Descriptor file '"+aDescFile+"' could not be removed and renewed.");
		
		FileUtils.write(aDescFile, sDesc, ENCODING, false);
		
		return aDescFile;
	}
	
	//-------------------------------------------------------------------------
	private static URL impl_getConfigDescriptor (final String sConfigPath   ,
			  									 final String sConfigPackage)
	    throws Exception
	{
		//impl_dumpEnv ();
		
		String sDescriptor = StringUtils.join(new String[]{sConfigPath, sConfigPackage, "descriptor.xml"}, "/");
		URL    aDescriptor = null;

		sDescriptor = StringUtils.replace(sDescriptor, "//", "/");
		
		if (StringUtils.startsWithIgnoreCase(sDescriptor, "classpath:"))
		{
			aDescriptor = new URL (sDescriptor);
		}
		else
		if (StringUtils.startsWithIgnoreCase(sDescriptor, "file:"))
		{
			final String sRealDescriptor = StringUtils.removeStartIgnoreCase(sDescriptor, "file:");
			final File   aDescriptorFile = new File (sRealDescriptor);
			             aDescriptor     = aDescriptorFile.toURI().toURL();
		}
		else
			throw new UnsupportedOperationException ("No support for config protocol '"+sDescriptor+"' yet.");
		
		return aDescriptor;
	}
	
//	//-------------------------------------------------------------------------
//	private static void impl_dumpEnv ()
//	    throws Exception
//	{
//		if ( ! LOG.isDebugEnabled())
//			return;
//
//		final Map< String, String > lEnv = System.getenv();
//		if (lEnv == null)
//			return;
//		
//		final Iterator< Entry< String, String >> rEnv = lEnv.entrySet().iterator();
//		while (rEnv.hasNext())
//		{
//			final Entry< String, String > aEnvVar = rEnv   .next    ();
//			final String                  sVar    = aEnvVar.getKey  ();
//			final String                  sVal    = aEnvVar.getValue();
//			
//			LOG.debug("::CONFIGURATION - env var : '"+sVar+"' = '"+sVal+"'");
//		}
//	}
}
