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
    protected String impl_createSqlCreateSchema4Entity (final Row aMeta)
        throws Exception
    {
    	String sSql = super.impl_createSqlCreateSchema4Entity(aMeta);
    	       sSql = StringUtils.replace(sSql, "create schema ", "create schema if not exists ");
    	return sSql;
    }
    
    //--------------------------------------------------------------------------
    protected String impl_createSqlCreateTable4Entity (final Row aMeta)
        throws Exception
    {
    	String sSql = super.impl_createSqlCreateTable4Entity(aMeta);
	       	   sSql = StringUtils.replace(sSql, "create table ", "create table if not exists ");
	    return sSql;
    }

    //--------------------------------------------------------------------------
    protected String impl_createSqlRemoveTable4Entity (final Row aMeta)
        throws Exception
    {
    	String sSql = super.impl_createSqlRemoveTable4Entity(aMeta);
    	       sSql = StringUtils.replace(sSql, "drop table ", "drop table if exists ");
    	return sSql;
    }
}
