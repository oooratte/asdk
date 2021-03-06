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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import net.as_development.asdk.persistence.ISimplePersistence;
import net.as_development.asdk.persistence.ISimplePersistenceAtomic;
import net.as_development.asdk.persistence.ISimplePersistenceImpl;
import net.as_development.asdk.persistence.ISimplePersistenceLock;
import net.as_development.asdk.persistence.ISimplePersistenceTransacted;
import net.as_development.asdk.persistence.SimplePersistenceConfig;
import net.as_development.asdk.tools.common.CollectionUtils;
import net.as_development.asdk.tools.common.NumberUtils;
import net.as_development.asdk.tools.common.type.TypeConverter;

//=============================================================================
public class SimplePersistenceImpl implements ISimplePersistenceTransacted
											, ISimplePersistenceAtomic
											, ISimplePersistenceLock
{
	//-------------------------------------------------------------------------
	public SimplePersistenceImpl ()
		throws Exception
	{}

	//-------------------------------------------------------------------------
	public SimplePersistenceImpl (final ISimplePersistenceImpl aImpl)
		throws Exception
	{
		m_iPersistenceLayer = aImpl;
	}
	
	//-------------------------------------------------------------------------
	@Override
	public void configure(final String... lConfig)
		throws Exception
	{
		final Map< String, String > aConfig = CollectionUtils.flat2MappedArguments(lConfig);

		// optional
		if (m_iPersistenceLayer == null)
		{
			final String     sImpl      = aConfig.get(SimplePersistenceConfig.CFG_PERSISTENCE_IMPL);
			final Class< ? > aImplClass = Class.forName(sImpl);
			m_iPersistenceLayer = (ISimplePersistenceImpl) aImplClass.newInstance();
		}

		// optional
		final String sScope   = aConfig.get(SimplePersistenceConfig.CFG_PERSISTENCE_SCOPE);
		             m_sScope = StringUtils.defaultString(sScope, "");

		// optional
 		final String sAutoCommit = aConfig.get(SimplePersistenceConfig.CFG_PERSISTENCE_AUTO_COMMIT);
 		if ( ! StringUtils.isEmpty(sAutoCommit))
 			m_bAutoCommit = Boolean.parseBoolean(sAutoCommit);
	}

	//-------------------------------------------------------------------------
	@Override
	public synchronized List< String > listKeys ()
		throws Exception
	{
		final List< String >        lList        = new Vector< String > ();
		final Map< String, Object > lChanges     = mem_Changes ();
		final Iterator< String >    rChangedKeys = lChanges.keySet().iterator();

		while (rChangedKeys.hasNext())
		{
			final String sFullKey = rChangedKeys.next();
			if ( ! impl_isKeyInCurrentScope (sFullKey))
				continue;
			
			final String sRelKey = impl_makeKeyRelative (sFullKey);
			lList.add (sRelKey);
		}
		
		final Iterator< String > rPersistentKeys = m_iPersistenceLayer.listKeys().iterator();
		while (rPersistentKeys.hasNext())
		{
			final String sFullKey = rPersistentKeys.next();
			if ( ! impl_isKeyInCurrentScope (sFullKey))
				continue;
			
			final String sRelKey = impl_makeKeyRelative (sFullKey);
			lList.add (sRelKey);
		}
		
		return lList;
	}
	
	//-------------------------------------------------------------------------
	@Override
	public synchronized < T > void set (final String sKey  ,
			               			    final T      aValue)
	    throws Exception
	{
		begin ();
		
		final Map< String, Object > lChanges = mem_Changes ();
		final String                sFullKey = impl_makeKeyAbsolute (sKey);
		lChanges.put (sFullKey, aValue);
		
		if (m_bAutoCommit)
			commit ();
	}

	//-------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	@Override
	public synchronized < T > T get (final String sKey)
	    throws Exception
	{
		      T                     aValue   = null;
		final String                sFullKey = impl_makeKeyAbsolute (sKey);
		final Map< String, Object > lChanges = mem_Changes ();

		if (lChanges.containsKey(sFullKey))
			aValue = (T) lChanges.get (sFullKey);
		else
			aValue = (T) m_iPersistenceLayer.get(sFullKey);

		return aValue;
	}

	//-------------------------------------------------------------------------
	@Override
	public synchronized void begin ()
		throws Exception
	{
	}

	//-------------------------------------------------------------------------
	@Override
	public synchronized void commit ()
		throws Exception
	{
		final Map< String, Object > lChanges = mem_Changes ();
		if ( ! lChanges.isEmpty())
			m_iPersistenceLayer.set(lChanges);
		lChanges.clear();
	}

	//-------------------------------------------------------------------------
	@Override
	public synchronized void rollback ()
		throws Exception
	{
		mem_Changes ().clear ();
	}

	//-------------------------------------------------------------------------
	@Override
	public synchronized ISimplePersistence getSubset (final String sSubSet)
		throws Exception
	{
		Validate.isTrue(mem_Changes().isEmpty(), "Creating sub set not allowed if changes exists.");
		
		final SimplePersistenceImpl aSubset = new SimplePersistenceImpl ();
		aSubset.m_iPersistenceLayer = m_iPersistenceLayer.getSubSet(sSubSet);
		aSubset.m_sScope            = m_sScope;
		aSubset.m_sSubSet           = KeyHelper.nameKey(m_sSubSet, sSubSet);
	
		return aSubset;
	}

	//-------------------------------------------------------------------------
	@Override
	public synchronized void clear ()
		throws Exception
	{
		mem_Changes ().clear();
		
		// NOTE : SubSets share same persistence layer ... see getSubSet().
		// Dont call clear() on such shared layer !
		// Remove all keys bound to the current subset explicit !

		if (StringUtils.isEmpty(m_sSubSet))
		{
			if (m_iPersistenceLayer != null)
				m_iPersistenceLayer.clear();
		}
		else
		{
			final List< String > lSubSetKeys = listKeys ();
			for (final String sSubSetKey : lSubSetKeys)
				set (sSubSetKey, null);
		}
	}

	//-------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	@Override
	public synchronized < T > boolean setIf (final String sKey          ,
							                 final T      aExpectedValue,
							                 final T      aNewValue     )
		throws Exception
	{
		// a) interface directly supported by impl layer -> call it
		if (ISimplePersistenceAtomic.class.isAssignableFrom(m_iPersistenceLayer.getClass()))
		{
			final String                   sAbsoluteKey = impl_makeKeyAbsolute(sKey);
			final ISimplePersistenceAtomic iAtomic      = (ISimplePersistenceAtomic) m_iPersistenceLayer;
			final boolean                  bOK          = iAtomic.setIf(sAbsoluteKey, aExpectedValue, aNewValue);
			return bOK;
		}

		// b) not supported by impl layer -> "simulate" it on top
		final T aCurrentValue = (T) get (sKey);
		if ( ! TypeConverter.equalsWithDefault(aCurrentValue, aExpectedValue))
			return false;

		set (sKey, aNewValue);
		return true;
	}

	//-------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	@Override
	public synchronized < T > T setAndGet(final String sKey  ,
										  final T      aValue)
		throws Exception
	{
		// a) interface directly supported by impl layer -> call it
		if (ISimplePersistenceAtomic.class.isAssignableFrom(m_iPersistenceLayer.getClass()))
			{
			final String                   sAbsoluteKey = impl_makeKeyAbsolute(sKey);
			final ISimplePersistenceAtomic iAtomic      = (ISimplePersistenceAtomic) m_iPersistenceLayer;
			final T                        aOldValue    = (T) iAtomic.setAndGet(sAbsoluteKey, aValue);
			return aOldValue;
		}

		// b) not supported by impl layer -> "simulate" it on top
		final T aCurrentValue = (T) get (sKey);
		set (sKey, aValue);
		return aCurrentValue;
	}

	//-------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public synchronized < T extends Number > T inc (final String sKey      ,
			   										final T      nIncrement)
		throws Exception
	{
		// a) interface directly supported by impl layer -> call it
		if (ISimplePersistenceAtomic.class.isAssignableFrom(m_iPersistenceLayer.getClass()))
		{
			final String                   sAbsoluteKey = impl_makeKeyAbsolute(sKey);
			final ISimplePersistenceAtomic iAtomic      = (ISimplePersistenceAtomic) m_iPersistenceLayer;
			final T                        aOldValue    = (T) iAtomic.inc(sAbsoluteKey, nIncrement);
			return aOldValue;
		}

		// b) not supported by impl layer -> "simulate" it on top
		Number aCurrentValue = (T) get (sKey);
			   aCurrentValue = NumberUtils.defaultNumber(aCurrentValue, nIncrement.getClass());
		Number aNewValue     = NumberUtils.increment    (aCurrentValue, nIncrement           );

		set (sKey, aNewValue);
		
		return (T) aCurrentValue;
	}

	//-------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public synchronized < T extends Number > T dec (final String sKey      ,
			   										final T      nDecrement)
		throws Exception
	{
		// a) interface directly supported by impl layer -> call it
		if (ISimplePersistenceAtomic.class.isAssignableFrom(m_iPersistenceLayer.getClass()))
		{
			final String                   sAbsoluteKey = impl_makeKeyAbsolute(sKey);
			final ISimplePersistenceAtomic iAtomic      = (ISimplePersistenceAtomic) m_iPersistenceLayer;
			final T                        aOldValue    = (T) iAtomic.dec(sAbsoluteKey, nDecrement);
			return aOldValue;
		}

		// b) not supported by impl layer -> "simulate" it on top
		Number aCurrentValue = (T) get (sKey);
		       aCurrentValue = NumberUtils.defaultNumber(aCurrentValue, nDecrement.getClass());
	    Number aNewValue     = NumberUtils.decrement    (aCurrentValue, nDecrement           );

        set (sKey, aNewValue);
	
        return (T) aCurrentValue;
	}

	//-------------------------------------------------------------------------
	@Override
	public synchronized ILock lock(final String sId)
		throws Exception
	{
		Validate.isTrue( ! StringUtils.isEmpty(sId), "Invalid argument 'id'.");

		// a) interface directly supported by impl layer -> call it
		if (ISimplePersistenceLock.class.isAssignableFrom(m_iPersistenceLayer.getClass()))
		{
			final ISimplePersistenceLock       iLockable = (ISimplePersistenceLock) m_iPersistenceLayer;
			final ISimplePersistenceLock.ILock iLock     = iLockable.lock(sId);
			return iLock;
		}

		final ILock iLock = impl_tryLock (sId, Integer.MAX_VALUE, TimeUnit.DAYS); // max-int in days : that should be infinite enough ;-)
		return iLock;
	}

	//-------------------------------------------------------------------------
	@Override
	public synchronized ILock tryLock(final String   sId      ,
									  final int      nTimeOut ,
									  final TimeUnit aTimeUnit)
		throws Exception
    {
		Validate.isTrue( ! StringUtils.isEmpty(sId), "Invalid argument 'id'."                   );
		Validate.isTrue(   nTimeOut > 0            , "Invalid argument 'timeout'. Needs to > 0.");

		// a) interface directly supported by impl layer -> call it
		if (ISimplePersistenceLock.class.isAssignableFrom(m_iPersistenceLayer.getClass()))
		{
			final ISimplePersistenceLock       iLockable = (ISimplePersistenceLock) m_iPersistenceLayer;
			final ISimplePersistenceLock.ILock iLock     = iLockable.tryLock(sId, nTimeOut, aTimeUnit);
			return iLock;
		}

		final ILock iLock = impl_tryLock (sId, nTimeOut, aTimeUnit);
		return iLock;
	}

	//-------------------------------------------------------------------------
	@Override
	public synchronized boolean unlock(final ILock iLock)
		throws Exception
	{
		Validate.isTrue(iLock != null, "Invalid argument 'lock'.");
		
		// a) interface directly supported by impl layer -> call it
		if (ISimplePersistenceLock.class.isAssignableFrom(m_iPersistenceLayer.getClass()))
		{
			final ISimplePersistenceLock iLockable = (ISimplePersistenceLock) m_iPersistenceLayer;
			final boolean                bOk       = iLockable.unlock(iLock);
			return bOk;
		}
		
		if ( ! Lock.class.isAssignableFrom(iLock.getClass ()))
			throw new IllegalMonitorStateException ("This is not a lock owned by this persistence instance. (wrong type)");

		final Lock   aLock    = (Lock) iLock;
		final String sSecrect = mem_LockSecret ();

		if ( ! StringUtils.equals(sSecrect, aLock.sSecret))
			throw new IllegalMonitorStateException ("This is not a lock owned by this persistence instance. (wrong secret)");
		
		final boolean bLockUnset = impl_unlock (aLock.sId);
		return bLockUnset;
	}

	//-------------------------------------------------------------------------
	private /* no synchronized*/ ILock impl_tryLock (final String   sId      ,
			  										 final int      nTimeOut ,
			  										 final TimeUnit aTimeUnit)
	    throws Exception
	{
		      long nNow         = System.currentTimeMillis();
		final long nTimeOutInMS = aTimeUnit.toMillis(nTimeOut);
		final long nEnd         = nNow + nTimeOutInMS;

		while (true)
		{
			final ILock iLock = impl_tryLock (sId);
			if (iLock != null)
				return iLock;

			Thread.sleep(25);
			
			nNow = System.currentTimeMillis();
			if (nNow > nEnd)
				return null;
		}
	}
	
	//-------------------------------------------------------------------------
	private ILock impl_tryLock (final String sId)
	    throws Exception
	{
		final String  sLockKey = "lock-"+sId;
		final boolean bLockSet = setIf (sLockKey, Boolean.FALSE, Boolean.TRUE);
		
		if ( ! bLockSet)
			return null;

		final Lock iLock = new Lock ();
		iLock.sId     = sId;
		iLock.sSecret = mem_LockSecret ();

		return iLock;
	}
	
	//-------------------------------------------------------------------------
	private boolean impl_unlock (final String sId)
	    throws Exception
	{
		final String  sLockKey   = "lock-"+sId;
		final boolean bLockUnset = setIf (sLockKey, Boolean.TRUE, Boolean.FALSE);
		return bLockUnset;
	}

	//-------------------------------------------------------------------------
	public synchronized String dump ()
		throws Exception
	{
		final StringBuffer sDump       = new StringBuffer (256);
		final StringBuffer sOutOfScope = new StringBuffer (256);
		
		sDump.append (super.toString ());
		sDump.append ("\n"             );

		// a) dump 'real persistent' key-value pairs first
		
		sDump.append ("commited values :\n");
		
		final Iterator< String > rKeys = m_iPersistenceLayer.listKeys().iterator();
		while (rKeys.hasNext())
		{
			final String sKey = rKeys.next();
			if ( ! impl_isKeyInCurrentScope (sKey))
			{
				sOutOfScope.append("['"+sKey+"']\n");
				continue;
			}

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

			if ( ! impl_isKeyInCurrentScope (sKey))
			{
				sOutOfScope.append("['"+sKey+"']\n");
				continue;
			}

			sDump.append ("['"+sKey+"'] = '"+aValue+"'\n");
		}
		
		sDump.append("out of scope :\n");
		sDump.append(sOutOfScope       );
		
		return sDump.toString ();
	}
	
	//-------------------------------------------------------------------------
	private String impl_makeKeyAbsolute (final String sRelKey)
		throws Exception
	{
		final String sFullKey = KeyHelper.makeKeyAbsolute(m_sScope, m_sSubSet, sRelKey);
		return sFullKey;
	}

	//-------------------------------------------------------------------------
	private String impl_makeKeyRelative (final String sAbsKey)
		throws Exception
	{
		final String sRelKey = KeyHelper.makeKeyRelative(m_sScope, m_sSubSet, sAbsKey);
		return sRelKey;
	}

	//-------------------------------------------------------------------------
	private boolean impl_isKeyInCurrentScope (final String sKey)
		throws Exception
	{
		final boolean bIs = KeyHelper.isAbsoluteKeyInScopeSubset(m_sScope, m_sSubSet, sKey);
		return bIs;
	}
	
	//-------------------------------------------------------------------------
	private class Lock implements ISimplePersistenceLock.ILock
	{
		protected String sId     = null;
		protected String sSecret = null;
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
	private synchronized String mem_LockSecret ()
	    throws Exception
	{
		if (m_sLockSecret == null)
			m_sLockSecret = UUID.randomUUID().toString();
		return m_sLockSecret;
	}

	//-------------------------------------------------------------------------
	private ISimplePersistenceImpl m_iPersistenceLayer = null;

	//-------------------------------------------------------------------------
	private String m_sScope = "";
	
	//-------------------------------------------------------------------------
	private String m_sSubSet = null;

	//-------------------------------------------------------------------------
	private boolean m_bAutoCommit = true;

	//-------------------------------------------------------------------------
	private Map< String, Object > m_lChanges = null;

	//-------------------------------------------------------------------------
	private String m_sLockSecret = null;
}
