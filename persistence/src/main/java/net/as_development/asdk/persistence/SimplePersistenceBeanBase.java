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
package net.as_development.asdk.persistence;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

//=============================================================================
public class SimplePersistenceBeanBase
{
	//-------------------------------------------------------------------------
	public static final String SEPARATOR = ".";
	
	//-------------------------------------------------------------------------
	public SimplePersistenceBeanBase ()
		throws Exception
	{}

	//-------------------------------------------------------------------------
	public synchronized void defineSubset (final String sSet)
		throws Exception
	{
		m_sSubset = sSet;
	}

	//-------------------------------------------------------------------------
	/** bind this bean to the persistence layer where it can read/write values from/to
	 * 
	 *  @param	iLayer [IN]
	 *  		the persistence layer.
	 */
	public synchronized void bindToPeristenceLayer (final ISimplePersistenceTransacted iLayer)
		throws Exception
	{
		m_iPersistenceLayer = iLayer;
	}
	
	//-------------------------------------------------------------------------
	public synchronized List< String > list ()
		throws Exception
	{
		final List< String >        lList    = new Vector< String > ();
		final Map< String, Object > lChanges = mem_Changes ();

		final Iterator< String > rChangedKeys = lChanges.keySet().iterator();
		while (rChangedKeys.hasNext())
		{
			final String sFullKey = rChangedKeys.next();
			if ( ! impl_isKeyInCurrentSubset (sFullKey))
				continue;
			
			final String sRelKey = impl_makeKeyRelative (sFullKey);
			lList.add (sRelKey);
		}
		
		final Iterator< String > rPersistentKeys = m_iPersistenceLayer.listKeys().iterator();
		while (rPersistentKeys.hasNext())
		{
			final String sFullKey = rPersistentKeys.next();
			if ( ! impl_isKeyInCurrentSubset (sFullKey))
				continue;
			
			final String sRelKey = impl_makeKeyRelative (sFullKey);
			lList.add (sRelKey);
		}
		
		return lList;
	}
	
	//-------------------------------------------------------------------------
	public synchronized < T extends Serializable > void set (final String sKey  ,
			               					                 final T      aValue)
	    throws Exception
	{
		final Map< String, Object > lChanges = mem_Changes ();
		final String                sFullKey = impl_makeKeyAbsolute (sKey);
		lChanges.put (sFullKey, aValue);
	}

	//-------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public synchronized < T extends Serializable > T get (final String sKey)
	    throws Exception
	{
		      T      aValue   = null;
		final String sFullKey = impl_makeKeyAbsolute (sKey);
		
		final Map< String, Object > lChanges = mem_Changes ();
		if (lChanges.containsKey(sFullKey))
			aValue = (T) lChanges.get (sFullKey);
		else
			aValue = (T) m_iPersistenceLayer.get(sFullKey);

		return aValue;
	}

	//-------------------------------------------------------------------------
	public synchronized void flush ()
		throws Exception
	{
		final Map< String, Object > lChanges = mem_Changes ();
		if (lChanges.isEmpty())
			return;
		
		try
		{
			m_iPersistenceLayer.begin();
			
			final Iterator< Entry< String, Object > > rChanges = lChanges.entrySet().iterator();
			while (rChanges.hasNext())
			{
				final Entry< String, Object > rChange  = rChanges.next     ();
				final String                  sKey     = rChange .getKey   ();
				final Object                  aValue   = rChange .getValue ();

				m_iPersistenceLayer.set(sKey, (Serializable)aValue);
			}
			
			m_iPersistenceLayer.commit();
		}
		catch (final Throwable ex)
		{
			m_iPersistenceLayer.rollback();
			throw new IOException (ex);
		}
		
		m_lChanges = null;
	}

	//-------------------------------------------------------------------------
	public synchronized void rollback ()
		throws Exception
	{
		final Map< String, Object > lChanges = mem_Changes ();
		lChanges.clear();
		
		m_iPersistenceLayer.rollback();
	}

	//-------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public synchronized <T extends SimplePersistenceBeanBase> T getSubset (final String     sSubset,
																		   final Class< T > aType  )
		throws Exception
	{
		Validate.isTrue(m_lChanges == null, "Cant create sub set if changes exists.");
		
		final SimplePersistenceBeanBase aSubset = aType.newInstance();
		aSubset.bindToPeristenceLayer( m_iPersistenceLayer);
		aSubset.defineSubset         ( sSubset            );
		aSubset.m_lChanges           = m_lChanges          ;
	
		return (T) aSubset;
	}

	//-------------------------------------------------------------------------
	public synchronized void clear ()
		throws Exception
	{
		mem_Changes ().clear();
		
		if (m_iPersistenceLayer != null)
			m_iPersistenceLayer.clear();
	}

	//-------------------------------------------------------------------------
	public synchronized String dump ()
		throws Exception
	{
		final StringBuffer sDump = new StringBuffer (256);
		
		sDump.append (super.toString ());
		sDump.append ("\n"             );

		// a) dump 'real persistent' key-value pairs first
		
		sDump.append ("commited values :\n");
		
		final Iterator< String > rKeys = m_iPersistenceLayer.listKeys().iterator();
		while (rKeys.hasNext())
		{
			final String sKey   = rKeys.next();
			if ( ! impl_isKeyInCurrentSubset (sKey))
				continue;
			
			final Object aValue = m_iPersistenceLayer .get (sKey);
			sDump.append ("['"+sKey+"'] = '"+aValue+"'\n");
		}

		// b) dump 'non flushed' changes then

		sDump.append ("actual changes :\n");
		
		final Map< String, Object >               lChanges = mem_Changes ();
		final Iterator< Entry< String, Object > > rChanges = lChanges.entrySet().iterator();
		while (rChanges.hasNext())
		{
			final Entry< String, Object > rChange  = rChanges.next     ();
			final String                  sKey     = rChange .getKey   ();
			final Object                  aValue   = rChange .getValue ();

			if ( ! impl_isKeyInCurrentSubset (sKey))
				continue;

			sDump.append ("['"+sKey+"'] = '"+aValue+"'\n");
		}
		
		return sDump.toString ();
	}
	
	//-------------------------------------------------------------------------
	private String impl_makeKeyAbsolute (final String sRelKey)
		throws Exception
	{
		if (StringUtils.isEmpty(m_sSubset))
			return sRelKey;
		
		final StringBuffer sFullKey = new StringBuffer (256);
		sFullKey.append (m_sSubset);
		sFullKey.append (SEPARATOR);
		sFullKey.append (sRelKey  );
		return sFullKey.toString ();
	}

	//-------------------------------------------------------------------------
	private String impl_makeKeyRelative (final String sAbsKey)
		throws Exception
	{
		if (StringUtils.isEmpty(m_sSubset))
			return sAbsKey;
		
		final String sRelKey = StringUtils.substringAfter(sAbsKey, m_sSubset+SEPARATOR);
		return sRelKey;
	}

	//-------------------------------------------------------------------------
	private boolean impl_isKeyInCurrentSubset (final String sKey)
		throws Exception
	{
		final boolean bIs = (
							 (StringUtils.isEmpty   (m_sSubset          )) ||
							 (StringUtils.startsWith(sKey    , m_sSubset))
							);
		return bIs;
	}
	
	//-------------------------------------------------------------------------
	private Map< String, Object > mem_Changes ()
	    throws Exception
	{
		if (m_lChanges == null)
			m_lChanges = new HashMap< String, Object > ();
		return m_lChanges;
	}

	//-------------------------------------------------------------------------
	private ISimplePersistenceTransacted m_iPersistenceLayer = null;

	//-------------------------------------------------------------------------
	private String m_sSubset = null;
	
	//-------------------------------------------------------------------------
	private Map< String, Object > m_lChanges = null;
}
