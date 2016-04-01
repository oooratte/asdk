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
     *  @param	eStatement [IN]
     *  		describe the statement in general.
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
