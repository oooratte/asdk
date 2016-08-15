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
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.Validate;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.IMap;

//=============================================================================
public class SimpleRecoverableHZMap< TKey extends Serializable, TValue extends Serializable > implements EntryListener< TKey, TValue >
{
	//-------------------------------------------------------------------------
	public static final String ENCODING = "utf-8";
	
	//-------------------------------------------------------------------------
	private SimpleRecoverableHZMap ()
		throws Exception
	{}
	
	//-------------------------------------------------------------------------
	public static < TKey extends Serializable, TValue extends Serializable > SimpleRecoverableHZMap< TKey, TValue > create (final IMap< TKey, TValue > iMap)
		throws Exception
	{
		SimpleRecoverableHZMap< TKey, TValue > aMap = new SimpleRecoverableHZMap< TKey, TValue > ();
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
		mem_Map ().clear();
		FileUtils.deleteQuietly(mem_MapFile ());
	}

	//-------------------------------------------------------------------------
	public synchronized void restore (final IMap< TKey, TValue > iMap)
		throws Exception
	{
		final Map< TKey, TValue > aMap = impl_readMap ();
		iMap.putAll(aMap);
	}

	//-------------------------------------------------------------------------
	@Override
	public synchronized void entryAdded(final EntryEvent< TKey, TValue > aEvent)
	{
		try
		{
			Map< TKey, TValue > aMap = mem_Map ();
			aMap.put(aEvent.getKey (), aEvent.getValue());
			impl_writeMap (aMap);
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
			Map< TKey, TValue > aMap = mem_Map ();
			aMap.remove(aEvent.getKey ());
			impl_writeMap (aMap);
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
			Map< TKey, TValue > aMap = mem_Map ();
			aMap.put(aEvent.getKey (), aEvent.getValue());
			impl_writeMap (aMap);
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
			Map< TKey, TValue > aMap = mem_Map ();
			aMap.remove(aEvent.getKey ());
			impl_writeMap (aMap);
		}
		catch (Throwable ex)
		{
			throw new RuntimeException (ex);
		}
	}
/*
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
*/
	//-------------------------------------------------------------------------
	private Map< TKey, TValue > impl_readMap ()
		throws Exception
	{
		final Map< TKey, TValue > aMap  = new HashMap< TKey, TValue > ();
		final File                aFile = mem_MapFile ();

		if (aFile.isFile())
		{
			final byte[] aBlob = FileUtils.readFileToByteArray(m_aMapFile);
			if (aBlob != null && aBlob.length > 1)
			{
				aMap.putAll((Map< TKey, TValue >) SerializationUtils.deserialize(aBlob));
			}
		}

		return aMap;
	}
	
	//-------------------------------------------------------------------------
	private void impl_writeMap (final Map< TKey, TValue > aMap)
		throws Exception
	{
		final byte[] aBlob = SerializationUtils.serialize((Serializable)aMap);
		FileUtils.writeByteArrayToFile(mem_MapFile(), aBlob, false);
	}

	//-------------------------------------------------------------------------
	private File mem_DataDir ()
	    throws Exception
	{
		if (m_aDataDir == null)
		{
			final File aTempDir = FileUtils.getTempDirectory();
			final File aDataDir = new File (aTempDir, "persistent/maps");
			
			aDataDir.mkdirs();
			
			Validate.isTrue(aDataDir.isDirectory(), "Could not create data directory at '"+aDataDir.getAbsolutePath ()+"'.");
			
			m_aDataDir = aDataDir;
		}
		return m_aDataDir;
	}

	//-------------------------------------------------------------------------
	private File mem_MapFile ()
	    throws Exception
	{
		if (m_aMapFile == null)
		{
			Validate.notEmpty(m_sMapId, "Miss map id.");

			final File aDir  = mem_DataDir ();
			final File aFile = new File (aDir, m_sMapId+".data");
			
			FileUtils.touch(aFile);

			m_aMapFile = aFile;
		}
		return m_aMapFile;
	}

	//-------------------------------------------------------------------------
	private Map< TKey, TValue > mem_Map ()
	    throws Exception
	{
		if (m_aMap == null)
			m_aMap = new HashMap< TKey, TValue > ();
		return m_aMap;
	}

	//-------------------------------------------------------------------------
	private String m_sMapId = null;

	//-------------------------------------------------------------------------
	private File m_aDataDir = null;
	
	//-------------------------------------------------------------------------
	private File m_aMapFile = null;

	//-------------------------------------------------------------------------
	private Map< TKey, TValue > m_aMap = null;
}
