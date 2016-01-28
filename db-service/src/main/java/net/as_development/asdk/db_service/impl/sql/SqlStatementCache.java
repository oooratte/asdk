/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package net.as_development.asdk.db_service.impl.sql;

import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Iterator;

import net.as_development.asdk.db_service.IDBBackendQuery;
import net.as_development.asdk.db_service.ISqlGenerator;
import net.as_development.asdk.db_service.impl.Row;

//==============================================================================
/** Implements a (hash based) cache of PreparedStatement objects.
 */
public class SqlStatementCache extends HashMap< String, PreparedStatement >
{
	//--------------------------------------------------------------------------
	private static final long serialVersionUID = -1259132647000327079L;

	//--------------------------------------------------------------------------
    /** create new instance.
     */
    public SqlStatementCache ()
    {}

    //--------------------------------------------------------------------------
    /** generate a new unique cache id for the given set of information.
     * 
     *  @param	sFunc [IN]
     *  		a function name describing the statement in general.
     *  		(e.g. insert, update, delete ...)
     *
     *  @param	aMeta [IN]
     *  		a set of meta information.
     *          (e.g. table name)
     *          
     *  @param	iQuery [IN]
     *  		the query itself where the prepared statement will stand for.
     *  		(e.g. we use the query id here)
     *  
     *  @return	a generated (and hopefully) unique cache id.
     */
    public String buildCacheId (ISqlGenerator.EStatementType eStatement,
                                Row                          aMeta     ,
                                IDBBackendQuery              iQuery    )
        throws Exception
    {
    	
        StringBuffer sCacheId = new StringBuffer (256);
        
        sCacheId.append (aMeta.getTable ());
        sCacheId.append ("_"              );
        sCacheId.append (eStatement.name());
        
        if (iQuery != null)
        {
        	sCacheId.append("_"           );
        	sCacheId.append(iQuery.getId());
        }

        return sCacheId.toString ();
    }
    
    //--------------------------------------------------------------------------
    /** We overload the original clear () method to make sure our cached
     *  PreparedStatement objects will be cleaned up in the right way ...
     *  Such objects should be closed. Otherwise we are running into leaking resources ... ?!
     */
    @Override
    public void clear ()
    {
		Iterator< PreparedStatement > pStatements = values().iterator();
		while (pStatements.hasNext())
		{
			// Ignore any exceptions here.
			// Calling close () is a fine option (to prevent resource leaks)
			// but who says we can't go further even if some of them was not closed ... ?! :-)
	    	try
	    	{
	    		PreparedStatement aStatement = pStatements.next();
	    		aStatement.close();
	    	}
	    	catch(Throwable ex)
	    	{}
		}
		
		super.clear ();
    }
}
