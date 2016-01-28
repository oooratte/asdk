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
package net.as_development.asdk.db_service.impl.simpledb;

import net.as_development.asdk.api.db.BetweenQueryRange;
import net.as_development.asdk.api.db.EQueryPartBinding;
import net.as_development.asdk.api.db.EQueryPartOperation;
import net.as_development.asdk.db_service.IDBBackendQuery;
import net.as_development.asdk.db_service.impl.QueryPart;
import net.as_development.asdk.db_service.impl.QueryPartValue;
import net.as_development.asdk.db_service.impl.Row;

import org.apache.commons.lang3.StringUtils;

//==============================================================================
/**
 * @todo document me
 */
public class SdbStatementGenerator
{
    //--------------------------------------------------------------------------
	public static final String DOMAIN_QUOTE = "`";
	
    //--------------------------------------------------------------------------
	public static final String VALUE_QUOTE = "'";
	
    //--------------------------------------------------------------------------
	public SdbStatementGenerator (boolean bScramble)
	{
		m_bScrambleData = bScramble;
	}

    //--------------------------------------------------------------------------
	public String createSelectStatement (Row             aMeta ,
							             IDBBackendQuery iQuery)
		throws Exception
	{
		String       sTable = aMeta.getTable();
        StringBuffer sSql   = new StringBuffer (256);

        sSql.append ("select * from "  );
        sSql.append (quoteTable(sTable));
        sSql.append (" where "         );

        boolean          bFirst = true;
        QueryPartValue[] lParts = iQuery.getQueryParts();
        int              i      = 0;
        int              c      = lParts.length;
        
        for (i=0; i<c; ++i)
        {
            QueryPartValue      aValuePart = lParts[i];
        	QueryPart           aPart      = aValuePart.getPart();
        	EQueryPartBinding   eBinding   = aPart.getLogicBinding();
        	EQueryPartOperation eOperation = aPart.getOperation();
        	String              sColumn    = aPart.getColumn();
        	Object              aValue     = aValuePart.getValue();
        	Class< ? >          aType      = aMeta.getColumnType(sColumn);

        	if (bFirst)
        		bFirst = false;
        	else
        	{
        		if (eBinding == EQueryPartBinding.E_AND)
        			sSql.append (" and");
        		else
        		if (eBinding == EQueryPartBinding.E_OR)
        			sSql.append (" or");
        		else
        			throw new IllegalArgumentException ("Unknown logical binding. Did you changed enum IDBQuery.ELogicBinding and forgot to change this line of code here ?");
        	}

    		sSql.append (" "                 );
    		sSql.append (quoteColumn(sColumn));

        	if (eOperation == EQueryPartOperation.E_MATCH)
        	{
            	String sValue = SdbDatatypeConvert.toSdbValue(aType, aValue, m_bScrambleData);
        		sSql.append (" = "+quoteValue (sValue));
        	}
        	else
        	if (eOperation == EQueryPartOperation.E_LIKE)
        	{
            	String sValue = SdbDatatypeConvert.toSdbValue(aType, aValue, m_bScrambleData);
		           	   sValue = StringUtils.replaceChars(sValue, '*', '%');
		           	   sValue = StringUtils.replaceChars(sValue, '?', '%');
        		sSql.append (" like "+quoteValue (sValue));
        	}
        	else
        	if (eOperation == EQueryPartOperation.E_BETWEEN)
        	{
        		BetweenQueryRange aRange = (BetweenQueryRange) aValue;
            	String            sFrom  = SdbDatatypeConvert.toSdbValue(aType, aRange.MinRange, m_bScrambleData);
            	String            sTo    = SdbDatatypeConvert.toSdbValue(aType, aRange.MaxRange, m_bScrambleData);
        		sSql.append (" between "+quoteValue(sFrom)+" and "+quoteValue(sTo));
        	}
    		else
        	if (eOperation == EQueryPartOperation.E_LESS_THAN)
        	{
            	String sValue = SdbDatatypeConvert.toSdbValue(aType, aValue, m_bScrambleData);
        		sSql.append (" < "+quoteValue (sValue));
        	}
    		else
        	if (eOperation == EQueryPartOperation.E_GREATER_THAN)
        	{
            	String sValue = SdbDatatypeConvert.toSdbValue(aType, aValue, m_bScrambleData);
        		sSql.append (" > "+quoteValue (sValue));
        	}
            else
    			throw new IllegalArgumentException ("Unknown operation. Did you changed enum IDBQuery.EOperation and forgot to change this line of code here ?");
        }

        return sSql.toString ();
	}
    
    //--------------------------------------------------------------------------
    /** @return the table name for the given entity meta information.
     * 
     *  This method is idempotent - you can call it as often as you want.
     *  It checks if there is something real to do.
     * 
     *  @note   returning name will contain quotes. (if enabled)
     *          So you don't have to add quotes outside.
     *          
     *  @param  aMeta [IN]
     *          the set of meta information.
     */
    public static String quoteTable (String sTable)
        throws Exception
    {
    	if (
    		(StringUtils.startsWith(sTable, SdbStatementGenerator.DOMAIN_QUOTE)) &&
    		(StringUtils.endsWith  (sTable, SdbStatementGenerator.DOMAIN_QUOTE))
    	   )
    		return sTable;
    	
        StringBuffer sName  = new StringBuffer (256);
        
        sName.append (SdbStatementGenerator.DOMAIN_QUOTE     );
        sName.append (SdbStatementGenerator.nameTable(sTable));//idempotent to .-)
        sName.append (SdbStatementGenerator.DOMAIN_QUOTE     );
        
        return sName.toString ();
    }
    
    //--------------------------------------------------------------------------
    /** quote the given column.
     * 
     *  Even if current implementation of that method makes nothing real ...
     *  it makes sense to have such function. Might be AWS change it's
     *  quoting scheme ... we are already prepared regarding that and can
     *  implement that functionality at one place .-)
     * 
     *  @param	sColumn [IN]
     *  		the column name to be quoted here.
     *  
     *  @return	the quoted column name.
     */
    public static String quoteColumn (String sColumn)
    	throws Exception
    {
    	return SdbStatementGenerator.nameColumn(sColumn);//idempotent to .-)
    }
    
    //--------------------------------------------------------------------------
    public static String quoteValue (String sValue)
		throws Exception
	{
    	if (
    		(StringUtils.startsWith(sValue, SdbStatementGenerator.VALUE_QUOTE)) &&
    		(StringUtils.endsWith  (sValue, SdbStatementGenerator.VALUE_QUOTE))
    	   )
    		return sValue;
        	
        StringBuffer sName  = new StringBuffer (256);
        
        sName.append (SdbStatementGenerator.VALUE_QUOTE);
        sName.append (sValue								);
        sName.append (SdbStatementGenerator.VALUE_QUOTE);
        
        return sName.toString ();
	}
    
    //--------------------------------------------------------------------------
    /** make sure that table names use our prefix.
     * 
     *  This method is idempotent - you can call it as often as you want.
     *  It checks if there is something real to do.
     * 
     *  @param	sTable [IN]
     *  		the pure table name to be extended here.
     *  
     *  @return the extended table name.
     */
    public static String nameTable (String sTable)
    	throws Exception
    {
    	return sTable;
    	/*
    	String sName = sTable;
    	if ( ! StringUtils.startsWith(sName, EntityMetaInfo.PREFIX_TABLES))
    		sName = EntityMetaInfo.PREFIX_TABLES+sTable;
    	return sName;
    	*/
    }
    
    //--------------------------------------------------------------------------
    /** make sure that column names use our prefix.
     * 
     *  This method is idempotent - you can call it as often as you want.
     *  It checks if there is something real to do.
     *
     *  @param	sColumn [IN]
     *  		the pure column name to be extended here.
     *  
     *  @return the extended column name.
     */
    public static String nameColumn (String sColumn)
        throws Exception
    {
    	return sColumn;
    	/*
    	String sName = sColumn;
    	if ( ! StringUtils.startsWith(sName, EntityMetaInfo.PREFIX_COLUMNS))
    		sName = EntityMetaInfo.PREFIX_COLUMNS+sColumn;
    	return sName;
    	*/
    }

    //--------------------------------------------------------------------------
    private static boolean m_bScrambleData = true;
}
