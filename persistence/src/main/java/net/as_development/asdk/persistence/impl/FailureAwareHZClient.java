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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicReference;
import com.hazelcast.core.IMap;
import com.hazelcast.core.Partition;
import com.hazelcast.transaction.TransactionContext;
import com.hazelcast.transaction.TransactionOptions;

//=============================================================================
public class FailureAwareHZClient
{
	//-------------------------------------------------------------------------
	public static final String SCOPE_KEY_SEPARATOR = "_";
	
	//-------------------------------------------------------------------------
	private FailureAwareHZClient ()
	    throws Exception
	{}

	//-------------------------------------------------------------------------
	public synchronized static FailureAwareHZClient create (final HazelcastInstance aCore)
	    throws Exception
	{
		final FailureAwareHZClient aInst = new FailureAwareHZClient ();
		aInst.m_aCore = aCore;
		return aInst;
	}
	
	//-------------------------------------------------------------------------
	public synchronized IMap< Object, Object > getMap(final String sScope)
		throws Exception
	{
		try
		{
			return m_aCore.getMap(sScope);
		}
		catch (final Throwable ex)
		{
			m_bHasErrors = true;
			throw new RuntimeException (ex);
		}
	}
	
	//-------------------------------------------------------------------------
	public synchronized IAtomicReference< Object > getObject(final String sScope,
										                     final String sKey  )
		throws Exception
	{
		try
		{
			final String                     sFullKey = StringUtils.join(new String[] {sScope, sKey}, SCOPE_KEY_SEPARATOR);
			final IAtomicReference< Object > iRef     = m_aCore.getAtomicReference(sFullKey);
			Validate.isTrue (iRef != null, "Could not retrieve access to ref value '"+sFullKey+"'.");
			return iRef;
		}
		catch (final Throwable ex)
		{
			m_bHasErrors = true;
			throw new RuntimeException (ex);
		}
	}

	//-------------------------------------------------------------------------
	public synchronized List< String > listKeysInScope (final String sScope)
	    throws Exception
	{
		try
		{
			final List< String >                  lKeys    = new ArrayList< String > ();
			final Collection< DistributedObject > lObjects = m_aCore.getDistributedObjects();

			for (final DistributedObject aObj : lObjects)
			{
				String sName = aObj.getName();
				if ( ! StringUtils.startsWith(sName, sScope))
					continue;
				
				sName = StringUtils.substringAfter(sName, sScope             );
				sName = StringUtils.substringAfter(sName, SCOPE_KEY_SEPARATOR);
				lKeys.add (sName);
			}
			
			return lKeys;
		}
		catch (final Throwable ex)
		{
			m_bHasErrors = true;
			throw new RuntimeException (ex);
		}
	}
	
	//-------------------------------------------------------------------------
	public synchronized TransactionContext newTransactionContext(final TransactionOptions aOpt)
		throws Exception
	{
		try
		{
			return m_aCore.newTransactionContext(aOpt);
		}
		catch (final Throwable ex)
		{
			m_bHasErrors = true;
			throw new RuntimeException (ex);
		}
	}

	//-------------------------------------------------------------------------
	public synchronized boolean hasErrors ()
	    throws Exception
	{
		return m_bHasErrors;
	}
	
	//-------------------------------------------------------------------------
	private HazelcastInstance m_aCore = null;

	//-------------------------------------------------------------------------
	private boolean m_bHasErrors = false;
}