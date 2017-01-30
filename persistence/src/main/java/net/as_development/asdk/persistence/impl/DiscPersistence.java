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
package net.as_development.asdk.persistence.impl;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import net.as_development.asdk.persistence.ISimplePersistenceImpl;
import net.as_development.asdk.persistence.SimplePersistenceConfig;
import net.as_development.asdk.tools.common.CollectionUtils;
import net.as_development.asdk.tools.reflection.SerializationUtils;

//=============================================================================
public class DiscPersistence implements ISimplePersistenceImpl
{
	//-------------------------------------------------------------------------
	public static final String ENCODING = "utf-8";

	//-------------------------------------------------------------------------
	public static final String CFG_DATA_PATH = "data.path";

	//-------------------------------------------------------------------------
	public DiscPersistence ()
		throws Exception
	{}

	//-------------------------------------------------------------------------
	@Override
	public synchronized void configure (final String... lConfig)
		throws Exception
	{
		final Map< String, String > aConfig = CollectionUtils.flat2MappedArguments(lConfig);

		m_sScope = aConfig.get(SimplePersistenceConfig.CFG_PERSISTENCE_SCOPE);
		
		if (aConfig.containsKey(CFG_DATA_PATH))
			m_sDataPath = aConfig.get(CFG_DATA_PATH);
		
		Validate.notEmpty (m_sScope, "Miss config item '"+SimplePersistenceConfig.CFG_PERSISTENCE_SCOPE+"'.");
	}

	//-------------------------------------------------------------------------
	@Override
	public synchronized ISimplePersistenceImpl getSubSet (final String sSubSet)
		throws Exception
	{
		final DiscPersistence aSubSet = new DiscPersistence ();
		aSubSet.m_aKeyFile  = m_aKeyFile ;
		aSubSet.m_aDataDir  = m_aDataDir ;
		aSubSet.m_sDataPath = m_sDataPath;
		aSubSet.m_sScope    = m_sScope   ;
		aSubSet.m_sSubSet   = KeyHelper.nameKey(m_sSubSet, sSubSet);
		return aSubSet;
	}

	//-------------------------------------------------------------------------
	@Override
	public synchronized void clear ()
		throws Exception
	{
		// a) no sub set ? -> remove root of all data
		if (StringUtils.isEmpty(m_sSubSet))
		{
			FileUtils.deleteQuietly(mem_KeyFile  ());
			FileUtils.deleteQuietly(mem_DataPath ());
		}
		
		// b) sub set ? -> remove subset relevant data only
		//    It's implemented by our wrapper SimplePersistenceImpl already ...
		//    so it's not called under normal circumstances.
		else
		{
			throw new UnsupportedOperationException ("Implemented by wrapper ?!");
		}
	}

	//-------------------------------------------------------------------------
	@Override
	public synchronized List< String > listKeys()
		throws Exception
	{
		final File           aKeyFile = mem_KeyFile ();
		final List< String > lKeys    = new ArrayList< String >();
		
		if ( ! aKeyFile.isFile ())
			return lKeys;

		lKeys.addAll(FileUtils.readLines(aKeyFile, ENCODING));

		return lKeys;
	}

	//-------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	@Override
	public synchronized void set(final Map< String, Object > lChanges)
		throws Exception
	{
		final File           aKeyFile        = mem_KeyFile ();
		final List< String > lPersistentKeys = listKeys    ();
		final List< String > lChangedKeys    = new ArrayList< String >(lChanges.keySet());
		final List< String > lAllKeys        = ListUtils.sum(lPersistentKeys, lChangedKeys);

		for (final String sChangedKey : lChangedKeys)
		{
			final String sKey   = sChangedKey;
			final Object aValue = lChanges.get(sKey);
			final File   aFile  = impl_getFileForKey (sKey);
		
			if (aValue == null)
			{
				lAllKeys.remove(sKey);
				FileUtils.deleteQuietly(aFile);
			}
			else
			{
				final String sDataBlob = SerializationUtils.mapObject2String((Serializable)aValue);
				FileUtils.writeStringToFile(aFile, sDataBlob);
			}
		}

		FileUtils.writeLines(aKeyFile, ENCODING, lAllKeys, false);
	}

	//-------------------------------------------------------------------------
	@Override
	public synchronized Object get(final String sKey)
		throws Exception
	{
		final File aDataFile = impl_getFileForKey (sKey);
		if (! aDataFile.isFile())
			return null;
		
		final String sDataBlob = FileUtils.readFileToString (aDataFile);
		final Object aValue    = SerializationUtils.mapString2Object(sDataBlob);
		
		return aValue;
	}

	//-------------------------------------------------------------------------
	private File impl_getFileForKey (final String sKey)
	    throws Exception
	{
		final File   aDir  = mem_DataPath ();
		final String sFile = impl_normalizeDataFileName (sKey);
		final File   aFile = new File (aDir, sFile);
		return aFile;
	}
	
	//-------------------------------------------------------------------------
	private String impl_normalizeDataFileName (final String sName)
	    throws Exception
	{
		String sNormalized = StringUtils.replace (sName, "-", "_");
		       sNormalized = StringUtils.replace (sName, ":", "_");
		       sNormalized = StringUtils.replace (sName, ";", "_");
		       sNormalized = StringUtils.replace (sName, ".", "_");
		       sNormalized = StringUtils.replace (sName, ",", "_");
		return sNormalized;
	}
	
	//-------------------------------------------------------------------------
	private File mem_DataPath ()
	    throws Exception
	{
		if (m_aDataDir == null)
		{
			Validate.notEmpty(m_sScope, "Miss attribute 'scope'. Not defined from outside.");
			
			File aRootDir = null;
			if (StringUtils.isEmpty(m_sDataPath))
			{
				final File aTempDir = FileUtils.getTempDirectory();
			               aRootDir = new File (aTempDir, "disc-persistence");
			}
			else
			{
				aRootDir = new File (m_sDataPath);
			}
			
			
			final File aDataDir = new File (aRootDir, m_sScope);
			aDataDir.mkdirs();
			Validate.isTrue(aDataDir.isDirectory(), "Could not create data directory at '"+aDataDir.getAbsolutePath ()+"'.");
			
			m_aDataDir = aDataDir;
		}
		return m_aDataDir;
	}

	//-------------------------------------------------------------------------
	private File mem_KeyFile ()
	    throws Exception
	{
		if (m_aKeyFile == null)
		{
			final File aDir  = mem_DataPath ();
			final File aFile = new File (aDir, ".keys");
			
			FileUtils.touch(aFile);

			m_aKeyFile = aFile;
		}
		return m_aKeyFile;
	}

	//-------------------------------------------------------------------------
	private String m_sDataPath = null;
	
	//-------------------------------------------------------------------------
	private String m_sScope = null;

	//-------------------------------------------------------------------------
	private String m_sSubSet = null;

	//-------------------------------------------------------------------------
	private File m_aDataDir = null;
	
	//-------------------------------------------------------------------------
	private File m_aKeyFile = null;
}
