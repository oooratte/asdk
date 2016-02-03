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
package net.as_development.asdk.db_service;

import net.as_development.asdk.api.db.IDB;
import net.as_development.asdk.api.db.IDBPool;
import net.as_development.asdk.service.env.ServiceEnv;

//=============================================================================
/** A base class for all DB related implementations.
 *  It handle creating the right DB instance and provide them to the derived
 *  class. Further it supports a set of helper functions as e.g. naming queries
 *  unique.
 */
public abstract class DBItemBase
{
	//--------------------------------------------------------------------------
	public DBItemBase ()
	{}

	//--------------------------------------------------------------------------
	/** @return a persistence unit ID suitable for the given class.
	 * 
	 *  @param	aImplClass [IN]
	 *  		the class using this DB base class.
	 */
	public static String namePersistenceUnit (Class< ? > aImplClass)
	{
		return aImplClass.getName ();
	}
	
	//--------------------------------------------------------------------------
	/** @return an unique (but stable/fix) query name.
	 * 
	 *  Because such name is used for caching also it's important to have
	 *  and unique name first but a stable name too.
	 *  
	 *  We use a combination of class and 'internal query name' you provide.
	 *  Of course you must make sure your internal query name is unique within
	 *  the area of your implementation class .-)
	 *  
	 *  @param	aImplClass [IN]
	 *  		the class using DB queries.
	 *  
	 *  @param	sQuery [IN]
	 *  		your internal query name.
	 */
	public static String nameQuery (Class< ? > aImplClass,
									String     sQuery    )
	{
		String       sClass   = aImplClass.getName();
		StringBuffer sQueryId = new StringBuffer (256);
		sQueryId.append (sClass);
		sQueryId.append (":"   );
		sQueryId.append (sQuery);
		return sQueryId.toString ();
	}
	
	//--------------------------------------------------------------------------
	/** @return the name of the persistence unit bound to that DB instance.
	 *  Must be overwritten by the derived class and is called from this
	 *  base class on creating the internal DB instance. 
	 */
	protected abstract String getPersistenceUnitName ()
		throws Exception;
	
	//--------------------------------------------------------------------------
	/** @return the internal DB instance to be used here.
	 */
	protected IDB mem_DB ()
		throws Exception
	{
		return mem_DBPool ().getDbForPersistenceUnit(getPersistenceUnitName ());
	}
	
	//--------------------------------------------------------------------------
	private IDBPool mem_DBPool ()
		throws Exception
	{
		if (m_iDBPool == null)
			m_iDBPool = ServiceEnv.get ().getService(IDBPool.class);
		return m_iDBPool;
	}
	
	//--------------------------------------------------------------------------
	private IDBPool m_iDBPool = null; 
}
