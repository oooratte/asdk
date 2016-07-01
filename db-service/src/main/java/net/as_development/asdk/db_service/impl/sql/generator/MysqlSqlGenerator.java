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
package net.as_development.asdk.db_service.impl.sql.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import net.as_development.asdk.db_service.impl.Row;

//==============================================================================
/** overrules AnsiSqlGenerator and support special features like :
 *  - create schema if not exists
 *  - create table if not exists
 *  - drop table if exists
 */
public class MysqlSqlGenerator extends AnsiSqlGenerator
{
    //--------------------------------------------------------------------------
    public MysqlSqlGenerator ()
    {}

    //--------------------------------------------------------------------------
    protected void impl_createSqlCreateUser (final Map< String, Object > lArgs,
    										 final List< String >        lSqls)
        throws Exception
    {
    	final String       sName            = (String   ) lArgs.get(AnsiSqlGenerator.ARG_CREATE_USER_NAME                 );
    	final String       sPassword        = (String   ) lArgs.get(AnsiSqlGenerator.ARG_CREATE_USER_PASSWORD             );
    	      Boolean      bAdministrative  = (Boolean  ) lArgs.get(AnsiSqlGenerator.ARG_CREATE_USER_ADMINISTRATIVE_RIGHTS);
    	      String[]     lDBSchemas       = (String []) lArgs.get(AnsiSqlGenerator.ARG_CREATE_USER_DB_SCHEMAS           );

    	if (bAdministrative == null)
    		bAdministrative = false;
    	      
    	if (lDBSchemas == null)
    		lDBSchemas = new String[0];
    	
    	final StringBuffer sSql = new StringBuffer (256);
    	
    	sSql.append("create user if not exists ");
    	sSql.append(m_sStringQuote              );
    	sSql.append(sName                       );
    	sSql.append(m_sStringQuote              );
    	sSql.append("@"                         );
    	sSql.append(m_sStringQuote              );
    	sSql.append("localhost"                 );
    	sSql.append(m_sStringQuote              );
    	sSql.append(" identified by "           );
    	sSql.append(m_sStringQuote              );
    	sSql.append(sPassword                   );
    	sSql.append(m_sStringQuote              );
    	
    	lSqls.add(sSql.toString ());
    	sSql.setLength(0);

    	sSql.append("create user if not exists ");
    	sSql.append(m_sStringQuote              );
    	sSql.append(sName                       );
    	sSql.append(m_sStringQuote              );
    	sSql.append("@"                         );
    	sSql.append(m_sStringQuote              );
    	sSql.append("%"                         );
    	sSql.append(m_sStringQuote              );
    	sSql.append(" identified by "           );
    	sSql.append(m_sStringQuote              );
    	sSql.append(sPassword                   );
    	sSql.append(m_sStringQuote              );
    	
    	lSqls.add(sSql.toString ());
    	sSql.setLength(0);
    	
    	for (final String sDBSchema : lDBSchemas)
    	{
    		if (bAdministrative)
    		{
    			sSql.append("grant all on ");
    		}
    		else
    		{
    			sSql.append("grant all on ");
//    			throw new UnsupportedOperationException ("not implemented yet");
    		}
    		
    		sSql.append(sDBSchema     );
    		sSql.append(".* to "      );
	    	sSql.append(m_sStringQuote);
	    	sSql.append(sName         );
	    	sSql.append(m_sStringQuote);
	    	sSql.append("@"           );
	    	sSql.append(m_sStringQuote);
	    	sSql.append("localhost"   );
	    	sSql.append(m_sStringQuote);
    	
	    	lSqls.add(sSql.toString ());
	    	sSql.setLength(0);

    		if (bAdministrative)
    		{
    			sSql.append("grant all on ");
    		}
    		else
    		{
    			sSql.append("grant all on ");
//    			throw new UnsupportedOperationException ("not implemented yet");
    		}

    		sSql.append(sDBSchema     );
    		sSql.append(".* to "      );
	    	sSql.append(m_sStringQuote);
	    	sSql.append(sName         );
	    	sSql.append(m_sStringQuote);
	    	sSql.append("@"           );
	    	sSql.append(m_sStringQuote);
	    	sSql.append("%"           );
	    	sSql.append(m_sStringQuote);
    	
	    	lSqls.add(sSql.toString ());
	    	sSql.setLength(0);
    	}
    }

    //--------------------------------------------------------------------------
    protected void impl_createSqlCreateSchema4Entity (final Row            aMeta,
			 										  final List< String > lSqls)
        throws Exception
    {
    	final List< String > lTempSqls = new ArrayList< String > ();
    	super.impl_createSqlCreateSchema4Entity(aMeta, lTempSqls);
    	
    	for (final String sTempSql : lTempSqls)
    	{
    		final String sSql = StringUtils.replace(sTempSql, "create schema ", "create schema if not exists ");
    		lSqls.add(sSql);
    	}
    }
    
    //--------------------------------------------------------------------------
    protected void impl_createSqlCreateTable4Entity (final Row            aMeta,
			  										 final List< String > lSqls)
        throws Exception
    {
    	final List< String > lTempSqls = new ArrayList< String > ();
    	super.impl_createSqlCreateTable4Entity(aMeta, lTempSqls);
    	
    	for (final String sTempSql : lTempSqls)
    	{
    		final String sSql = StringUtils.replace(sTempSql, "create table ", "create table if not exists ");
    		lSqls.add(sSql);
    	}
    }

    //--------------------------------------------------------------------------
    protected void impl_createSqlRemoveTable4Entity (final Row            aMeta,
			  									     final List< String > lSqls)
        throws Exception
    {
    	final List< String > lTempSqls = new ArrayList< String > ();
    	super.impl_createSqlRemoveTable4Entity(aMeta, lTempSqls);
    	
    	for (final String sTempSql : lTempSqls)
    	{
    		final String sSql = StringUtils.replace(sTempSql, "drop table ", "drop table if not exists ");
    		lSqls.add(sSql);
    	}
    }
}
