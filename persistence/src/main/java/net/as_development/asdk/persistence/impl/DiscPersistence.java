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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import net.as_development.asdk.persistence.ISimplePersistence;
import net.as_development.asdk.persistence.ISimplePersistenceTransacted;
import net.as_development.asdk.tools.common.CollectionUtils;

//=============================================================================
public class DiscPersistence implements ISimplePersistenceTransacted
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

		m_sScope = aConfig.get(ISimplePersistence.CFG_PERSISTENCE_SCOPE);
		
		if (aConfig.containsKey(CFG_DATA_PATH))
			m_sDataPath = aConfig.get(CFG_DATA_PATH);
		
		Validate.notEmpty (m_sScope, "Miss config item '"+ISimplePersistence.CFG_PERSISTENCE_SCOPE+"'.");
	}

	//-------------------------------------------------------------------------
	@Override
	public synchronized void clear ()
		throws Exception
	{
		mem_Changes ().clear();
		FileUtils.deleteQuietly(mem_KeyFile ());
		FileUtils.deleteQuietly(mem_DataPath ());
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
	@Override
	public synchronized < T extends Serializable > void set(final String sKey  ,
											   				final T      aValue)
		throws Exception
	{
		mem_Changes ().put(sKey, aValue);
	}

	//-------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	@Override
	public synchronized < T extends Serializable > T get(final String sKey)
		throws Exception
	{
		T aValue = (T) mem_Changes ().get(sKey);
		if (aValue != null)
			return aValue;
		
		final File aDataFile = impl_getFileForKey (sKey);
		if (! aDataFile.isFile())
			return null;
		
		final byte[] aDataBlob = FileUtils.readFileToByteArray(aDataFile);
		aValue = (T) SerializationUtils.deserialize(aDataBlob);
		
		return aValue;
	}

	//-------------------------------------------------------------------------
	@Override
	public synchronized void begin()
		throws Exception
	{
	}

	//-------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	@Override
	public synchronized void commit()
		throws Exception
	{
		final File                                      aKeyFile        = mem_KeyFile ();
		final Map< String, Serializable >               lChanges        = mem_Changes ();
		final Iterator< Entry< String, Serializable > > rChanges        = lChanges.entrySet().iterator();
		final List< String >                            lPersistentKeys = listKeys    ();
		final List< String >                            lChangedKeys    = new ArrayList< String > (lChanges.keySet());
		final List< String > 							lAllKeys        = ListUtils.sum(lPersistentKeys, lChangedKeys);
		
		while (rChanges.hasNext())
		{
			final Entry< String, Serializable > aChange = rChanges.next    ();
			final String                        sKey    = aChange .getKey  ();
			final Serializable                  aValue  = aChange .getValue();
			final File                          aFile   = impl_getFileForKey (sKey);
		
			if (aValue == null)
			{
				lAllKeys.remove(sKey);
				FileUtils.deleteQuietly(aFile);
			}
			else
			{
				final byte[] aDataBlob = SerializationUtils.serialize(aValue);
				FileUtils.writeByteArrayToFile(aFile, aDataBlob);
			}
		}

		FileUtils.writeLines(aKeyFile, ENCODING, lAllKeys, false);
		
		m_lChanges = null;
	}

	//-------------------------------------------------------------------------
	@Override
	public synchronized void rollback()
		throws Exception
	{
		m_lChanges = null;
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
	private Map< String, Serializable > mem_Changes ()
		throws Exception
	{
		if (m_lChanges == null)
			m_lChanges = new HashMap< String, Serializable > ();
		return m_lChanges;
	}

	//-------------------------------------------------------------------------
	private String m_sDataPath = null;
	
	//-------------------------------------------------------------------------
	private String m_sScope = null;

	//-------------------------------------------------------------------------
	private File m_aDataDir = null;
	
	//-------------------------------------------------------------------------
	private File m_aKeyFile = null;

	//-------------------------------------------------------------------------
	private Map< String, Serializable > m_lChanges = null;
}
