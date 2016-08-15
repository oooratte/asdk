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
package com.openexchange.office.rt2.persistence.impl;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import com.openexchange.office.rt2.persistence.ISimplePersistence;
import com.openexchange.office.tools.CollectionUtils;
import com.openexchange.office.tools.logging2.ELogLevel;
import com.openexchange.office.tools.logging2.v2.LogConst;
import com.openexchange.office.tools.logging2.v2.LogContext;
import com.openexchange.office.tools.logging2.v2.Logger;
import com.openexchange.office.tools.logging2.v2.Slf4JLogger;

//=============================================================================
public class DiscPersistence implements ISimplePersistence
{
	//-------------------------------------------------------------------------
	private static final Logger LOG = Slf4JLogger.create(DiscPersistence.class);

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
		m_sDataPath = aConfig.get(CFG_DATA_PATH);
	}

	//-------------------------------------------------------------------------
	@Override
	public synchronized void clear ()
		throws Exception
	{
		FileUtils.deleteQuietly(mem_KeyFile  ());
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

		LOG.forLevel   (ELogLevel.E_TRACE)
		   .setVar     (LogConst.THIS, ObjectUtils.identityToString(this))
		   .setVar     ("persistent keys", lKeys)
		   .withMessage("list keys ...")
		   .log        ();
		
		return lKeys;
	}

	//-------------------------------------------------------------------------
	@Override
	public synchronized < T > void set(final String sKey  ,
									   final T      aValue)
		throws Exception
	{
		LOG.forLevel   (ELogLevel.E_TRACE)
		   .setVar     (LogConst.THIS, ObjectUtils.identityToString(this))
		   .withMessage("set '"+sKey+"' = '"+aValue+"'")
		   .log        ();

		final List< String > lAllKeys = listKeys ();
		final File           aFile    = impl_getFileForKey (sKey);
		      boolean        bNewKey  = ( ! lAllKeys.contains(sKey));

		if (aValue == null)
		{
			LOG .forLevel   (ELogLevel.E_TRACE)
			    .withMessage("... remove key '"+sKey+"' and persistent file '"+aFile+"'")
			    .log        ();
			FileUtils.deleteQuietly(aFile);
			bNewKey = false;
		}
		else
		{
			LOG .forLevel   (ELogLevel.E_TRACE)
			    .withMessage("... persist key '"+sKey+"' in file '"+aFile+"'")
		    	.log        ();
			final byte[] aDataBlob = SerializationUtils.serialize((Serializable)aValue);
			FileUtils.writeByteArrayToFile(aFile, aDataBlob);
		}

		if (bNewKey)
		{
			lAllKeys.add(sKey);
			final File aKeyFile = mem_KeyFile ();
			FileUtils.writeLines(aKeyFile, ENCODING, lAllKeys, false);
		}
	}

	//-------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	@Override
	public synchronized < T > T get(final String sKey)
		throws Exception
	{
		LogContext aLog = LOG.forLevel(ELogLevel.E_TRACE)
							 .setVar  (LogConst.THIS, ObjectUtils.identityToString(this));
		
		final File aDataFile = impl_getFileForKey (sKey);
		if (! aDataFile.isFile())
		{
			aLog.withMessage("get '"+sKey+"' = 'null' (persistent file do not exist)")
			    .log        ();
			return null;
		}
		
		final byte[] aDataBlob = FileUtils.readFileToByteArray(aDataFile);
		final T      aValue    = (T) SerializationUtils.deserialize(aDataBlob);
		aLog.setVar     (LogConst.FILE, aDataFile)
		    .withMessage("get '"+sKey+"' = '"+aValue+"' (from persistent file)")
		    .log        ();
		
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
			File aDataDir = null;
			if (StringUtils.isEmpty(m_sDataPath))
			{
				final File aTempDir = FileUtils.getTempDirectory();
				aDataDir = new File (aTempDir, "disc-persistence");
			}
			else
			{
				aDataDir = new File (m_sDataPath);
			}
			
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
	private File m_aDataDir = null;
	
	//-------------------------------------------------------------------------
	private File m_aKeyFile = null;
}
