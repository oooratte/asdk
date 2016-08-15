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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import com.hazelcast.core.MapStore;

//=============================================================================
public class RecoverableHZMapLoader< TKey, TValue > implements MapStore< TKey, TValue >
{
	//-------------------------------------------------------------------------
	public RecoverableHZMapLoader()
    	throws Exception
	{}

	//-------------------------------------------------------------------------
	public synchronized void setMapId (final String sMapId)
	    throws Exception
	{
		m_sMapId = sMapId;
	}
	
	//-------------------------------------------------------------------------
	public synchronized void setPersistenceDir (final String sDir)
	    throws Exception
	{
		m_sPersistenceDir = sDir;
	}

	//-------------------------------------------------------------------------
	public synchronized void clear ()
		throws Exception
	{
		mem_Map ().clear ();
		
		final File aMapFile = mem_MapFile ();
		aMapFile.delete ();
		Validate.isTrue( ! mem_MapFile().exists(), "Could not clean up map file '"+aMapFile+"'.");
	}
	
	//-------------------------------------------------------------------------
	@Override
    public synchronized void delete(final TKey aKey)
    {
        try
        {
        	mem_Map ().remove(aKey);
        }
        catch (Throwable ex)
        {
            throw new RuntimeException(ex);
        }
    }

	//-------------------------------------------------------------------------
	@Override
    public synchronized void store(final TKey   aKey  ,
    							   final TValue aValue)
    {
        try
        {
        	final Map< TKey, TValue > aMap = mem_Map ();
        	aMap.put(aKey, aValue);
        	impl_writeMap (aMap);
        }
        catch (Throwable ex)
        {
            throw new RuntimeException(ex);
        }
    }

	//-------------------------------------------------------------------------
	@Override
    public synchronized void storeAll(final Map< TKey, TValue > aChange)
    {
        try
        {
        	final Map< TKey, TValue > aMap = mem_Map ();
        	aMap.putAll (aChange);
        	impl_writeMap (aMap);
        }
        catch (Throwable ex)
        {
            throw new RuntimeException(ex);
        }
    }

	//-------------------------------------------------------------------------
	@Override
    public synchronized void deleteAll(final Collection< TKey > lKeys)
    {
        for (final TKey aKey : lKeys)
        	delete(aKey);
    }

	//-------------------------------------------------------------------------
	@Override
    public synchronized TValue load(final TKey aKey)
    {
        try
        {
        	return (TValue) mem_Map ().get(aKey);
        }
        catch (Throwable ex)
        {
        	throw new RuntimeException(ex);
        }
    }

	//-------------------------------------------------------------------------
	@Override
    public synchronized Map< TKey, TValue > loadAll(final Collection< TKey > lKeys)
    {
        try
        {
        	return mem_Map ();
        }
        catch (Throwable ex)
        {
        	throw new RuntimeException(ex);
        }
    }

	//-------------------------------------------------------------------------
	@Override
    public synchronized Set< TKey > loadAllKeys()
    {
    	try
    	{
    		return mem_Map ().keySet();
    	}
    	catch (final Throwable ex)
    	{
        	throw new RuntimeException(ex);
    	}
    }
    
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
				aMap.putAll((Map< TKey, TValue >) SerializationUtils.deserialize(aBlob));
		}

		return aMap;
	}
	
	//-------------------------------------------------------------------------
	private void impl_writeMap (final Map< TKey, TValue > aMap)
		throws Exception
	{
		final File   aFile = mem_MapFile ();
		final byte[] aBlob = SerializationUtils.serialize((Serializable)aMap);
		FileUtils.writeByteArrayToFile(aFile, aBlob, false);
	}

	//-------------------------------------------------------------------------
	private synchronized File impl_accessDataDir ()
	    throws Exception
	{
		if (m_aDataDir == null)
		{
			File aDataDir = null;
			
			if (StringUtils.isEmpty(m_sPersistenceDir))
			{
				final File aTempDir = FileUtils.getTempDirectory();
						   aDataDir = new File (aTempDir, RecoverableHZMapLoader.class.getName()+"/maps");
			}
			else
			{
				aDataDir = new File (m_sPersistenceDir);
			}

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

			final File aDir  = impl_accessDataDir ();
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
			m_aMap = impl_readMap ();
		return m_aMap;
	}

	//-------------------------------------------------------------------------
	private String m_sMapId = null;

	//-------------------------------------------------------------------------
	private String m_sPersistenceDir = null;
	
	//-------------------------------------------------------------------------
	private static File m_aDataDir = null;
	
	//-------------------------------------------------------------------------
	private File m_aMapFile = null;

	//-------------------------------------------------------------------------
	private Map< TKey, TValue > m_aMap = null;
}