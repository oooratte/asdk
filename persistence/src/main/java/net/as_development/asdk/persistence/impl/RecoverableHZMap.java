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
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.IMap;

//=============================================================================
public class RecoverableHZMap< TKey extends Serializable, TValue extends Serializable > implements EntryListener< TKey, TValue >
{
	//-------------------------------------------------------------------------
	public static final String ENCODING = "utf-8";
	
	//-------------------------------------------------------------------------
	private RecoverableHZMap ()
		throws Exception
	{}
	
	//-------------------------------------------------------------------------
	public static < TKey extends Serializable, TValue extends Serializable > RecoverableHZMap< TKey, TValue > create (final IMap< TKey, TValue > iMap)
		throws Exception
	{
		RecoverableHZMap< TKey, TValue > aMap = new RecoverableHZMap< TKey, TValue > ();
		aMap.m_sMapId = iMap.getName();

		// do restore BEFORE start listening for changes ;-)
		aMap.restore(iMap);
		
		final boolean bNotifyValue = true;
		iMap.addEntryListener(aMap, bNotifyValue);
		
		return aMap;
	}
	
	//-------------------------------------------------------------------------
	public synchronized void clear ()
		throws Exception
	{
		FileUtils.deleteQuietly(mem_KeyFile ());
		FileUtils.deleteQuietly(mem_DataDir ());
	}

	//-------------------------------------------------------------------------
	public synchronized void restore (final IMap< TKey, TValue > iMap)
		throws Exception
	{
		final List< String > lKeys = impl_listKeys ();
		for (final String sKey : lKeys)
		{
			final TKey   aKey   = impl_string2Key   (sKey);
			final TValue aValue = impl_readValue4Key(sKey);
		
			iMap.put(aKey, aValue);
		}
	}

	//-------------------------------------------------------------------------
	@Override
	public synchronized void entryAdded(final EntryEvent< TKey, TValue > aEvent)
	{
		try
		{
			final TKey   aKey   = aEvent.getKey  ();
			final TValue aValue = aEvent.getValue();
			impl_writeValue4Key     (aKey, aValue);
			impl_updateKeyInKeyList (aKey, false );
		}
		catch (Throwable ex)
		{
			throw new RuntimeException (ex);
		}
	}

	//-------------------------------------------------------------------------
	@Override
	public synchronized void entryRemoved(final EntryEvent< TKey, TValue > aEvent)
	{
		try
		{
			final TKey aKey = aEvent.getKey ();
			impl_writeValue4Key     (aKey, null);
			impl_updateKeyInKeyList (aKey, true);
		}
		catch (Throwable ex)
		{
			throw new RuntimeException (ex);
		}
	}

	//-------------------------------------------------------------------------
	@Override
	public synchronized void entryUpdated(final EntryEvent< TKey, TValue > aEvent)
	{
		try
		{
			final TKey   aKey   = aEvent.getKey  ();
			final TValue aValue = aEvent.getValue();
			impl_writeValue4Key (aKey, aValue);
		}
		catch (Throwable ex)
		{
			throw new RuntimeException (ex);
		}
	}

	//-------------------------------------------------------------------------
	@Override
	public synchronized void entryEvicted(final EntryEvent< TKey, TValue > aEvent)
	{
		try
		{
			final TKey   aKey   = aEvent.getKey  ();
			final TValue aValue = aEvent.getValue();
			impl_writeValue4Key     (aKey, aValue);
			impl_updateKeyInKeyList (aKey, false );
		}
		catch (Throwable ex)
		{
			throw new RuntimeException (ex);
		}
	}
/**
	//-------------------------------------------------------------------------
	@Override
	public synchronized void mapEvicted(final MapEvent aEvent)
	{
		try
		{
			clear ();
		}
		catch (Throwable ex)
		{
			throw new RuntimeException (ex);
		}
	}

	//-------------------------------------------------------------------------
	@Override
	public synchronized void mapCleared(final MapEvent aEvent)
	{
		try
		{
			clear ();
		}
		catch (Throwable ex)
		{
			throw new RuntimeException (ex);
		}
	}
**/
	//-------------------------------------------------------------------------
	private void impl_writeValue4Key (final TKey   aKey  ,
									  final TValue aValue)
		throws Exception
	{
		final String sKey  = impl_key2String    (aKey);
		final File   aFile = impl_getFileForKey (sKey);
		
		if (aValue == null)
		{
			FileUtils.deleteQuietly(aFile);
			return;
		}

		final byte[] aBlob = SerializationUtils.serialize(aValue);
		FileUtils.writeByteArrayToFile(aFile, aBlob);
	}
	
	//-------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	private TValue impl_readValue4Key (final TKey aKey)
		throws Exception
	{
		final String sKey   = impl_key2String    (aKey);
		final File   aFile  = impl_getFileForKey (sKey);
		
		if ( ! aFile.isFile())
			return (TValue) null;

		final byte[] aBlob  = FileUtils.readFileToByteArray(aFile);
		final TValue aValue = (TValue) SerializationUtils.deserialize(aBlob);
		return aValue;
	}

	//-------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	private TValue impl_readValue4Key (final String sKey)
		throws Exception
	{
		final File aFile = impl_getFileForKey (sKey);
		
		if ( ! aFile.isFile())
		{
			return (TValue) null;
		}
		
		final byte[] aBlob  = FileUtils.readFileToByteArray(aFile);
		final TValue aValue = (TValue) SerializationUtils.deserialize(aBlob);
		return aValue;
	}

	//-------------------------------------------------------------------------
	private List< String > impl_listKeys ()
	    throws Exception
	{
		final File           aFile = mem_KeyFile ();
		final List< String > lKeys = FileUtils.readLines(aFile, ENCODING);
		return lKeys;
	}

	//-------------------------------------------------------------------------
	private void impl_updateKeyInKeyList (final TKey    aKey   ,
										  final boolean bRemove)
	    throws Exception
	{
		final File           aFile = mem_KeyFile ();
		final String         sKey  = impl_key2String (aKey);
		final List< String > lKeys = FileUtils.readLines(aFile, ENCODING);
		
		if (   bRemove &&   lKeys.contains(sKey))
			lKeys.remove(sKey);
		else
		if ( ! bRemove && ! lKeys.contains(sKey))
			lKeys.add(sKey);
	
		FileUtils.writeLines(aFile, ENCODING, lKeys, false);
	}
	
	//-------------------------------------------------------------------------
	private String impl_key2String (final TKey aKey)
	    throws Exception
	{
		final byte[] aBlob = SerializationUtils.serialize(aKey);
		final String sKey  = new String (aBlob);
		return sKey;
	}
	
	//-------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	private TKey impl_string2Key (final String sKey)
	    throws Exception
	{
		final byte[] aBlob = sKey.getBytes();
		final TKey   aKey  = (TKey) SerializationUtils.deserialize(aBlob);
		return aKey;
	}

	//-------------------------------------------------------------------------
	private File impl_getFileForKey (final String sKey)
	    throws Exception
	{
		final File   aDir  = mem_DataDir ();
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
	private File mem_DataDir ()
	    throws Exception
	{
		if (m_aDataDir == null)
		{
			Validate.notEmpty(m_sMapId, "Miss map id.");

			final File aTempDir = FileUtils.getTempDirectory();
			final File aRootDir = new File (aTempDir, "persistent/maps");
			final File aDataDir = new File (aRootDir, m_sMapId         );
			
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
			final File aDir  = mem_DataDir ();
			final File aFile = new File (aDir, ".keys");
			
			FileUtils.touch(aFile);

			m_aKeyFile = aFile;
		}
		return m_aKeyFile;
	}

	//-------------------------------------------------------------------------
	private String m_sMapId = null;

	//-------------------------------------------------------------------------
	private File m_aDataDir = null;
	
	//-------------------------------------------------------------------------
	private File m_aKeyFile = null;
}
