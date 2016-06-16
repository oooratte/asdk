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
package net.as_development.asdk.db_service.impl.simpledb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.as_development.asdk.api.db.IPersistenceUnit;
import net.as_development.asdk.db_service.IDBBackend;
import net.as_development.asdk.db_service.IDBBackendQuery;
import net.as_development.asdk.db_service.impl.EntityMetaInfoProvider;
import net.as_development.asdk.db_service.impl.PersistenceUnitConst;
import net.as_development.asdk.db_service.impl.Row;

import org.apache.commons.lang3.mutable.MutableInt;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.BatchPutAttributesRequest;
import com.amazonaws.services.simpledb.model.CreateDomainRequest;
import com.amazonaws.services.simpledb.model.DeleteAttributesRequest;
import com.amazonaws.services.simpledb.model.DeleteDomainRequest;
import com.amazonaws.services.simpledb.model.GetAttributesRequest;
import com.amazonaws.services.simpledb.model.GetAttributesResult;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.ReplaceableItem;
import com.amazonaws.services.simpledb.model.SelectRequest;
import com.amazonaws.services.simpledb.model.SelectResult;

//==============================================================================
/**
 * TODO document me
 */
public class SimpleDbProvider implements IDBBackend
{
    //--------------------------------------------------------------------------
    private static final int DEFAULT_BLOCKSIZE_BATCH_PUT = 25;

    //--------------------------------------------------------------------------
    //private static final int MAX_ATTRIBUTE_COUNT_PER_SDBITEM = 256;

    //--------------------------------------------------------------------------
    /** create new instance.
     */
    public SimpleDbProvider ()
    {}

    //--------------------------------------------------------------------------
    @Override
    public void setEntityMetaInfoProvider(EntityMetaInfoProvider aProvider)
        throws Exception
    {
        m_aMetaProvider = aProvider;
    }

    //--------------------------------------------------------------------------
    @Override
    public void createDB (Row aMeta)
        throws Exception
    {
    	throw new UnsupportedOperationException ("Not implemented yet.");
    }

    //--------------------------------------------------------------------------
    @Override
    public void createTable(Row aRow)
        throws Exception
    {
    	AmazonSimpleDB      aDB      = mem_DB ();
        String              sDomain  = aRow.getTable();
        CreateDomainRequest aRequest = new CreateDomainRequest();
        aRequest.setDomainName(sDomain);
        aDB.createDomain(aRequest);
    }

    //--------------------------------------------------------------------------
    @Override
    public void removeTable(Row aRow)
        throws Exception
    {
    	AmazonSimpleDB      aDB      = mem_DB ();
        String              sDomain  = aRow.getTable();
        DeleteDomainRequest aRequest = new DeleteDomainRequest(); 
        aRequest.setDomainName(sDomain);
        aDB.deleteDomain(aRequest);
    }
    
    //--------------------------------------------------------------------------
    @Override
    public void insertRows(List< Row > lRows)
        throws Exception
    {
    	AmazonSimpleDB            aDB         = mem_DB ();
    	// We can do that here ... because back end will never be call with an empty or invalid list
    	// Further all rows was sorted before so they point to the same entity type (we say Domain here) .-)
        String                    sDomain     = lRows.get(0).getTable();
        List< ReplaceableItem >   lPutItems   = new ArrayList< ReplaceableItem >();
        int                       nLastCursor = 0;
        MutableInt                nCursor     = new MutableInt (0);
        
        while (SimpleDbProvider.impl_mapNextBlockOfRowsToSdb(lRows, lPutItems, nCursor, SimpleDbProvider.DEFAULT_BLOCKSIZE_BATCH_PUT))
        {
            BatchPutAttributesRequest aRequest  = new BatchPutAttributesRequest ();
            aRequest.setDomainName(sDomain  );
        	aRequest.setItems     (lPutItems);
        	
            aDB.batchPutAttributes(aRequest);
            
            for (int i=nLastCursor; i<nCursor.intValue(); ++i)
            {
            	Row aRow = lRows.get(i);
            	aRow.getPersistentStateHandler().setPersistent();
            }
            
            nLastCursor = nCursor.intValue();
        }
    }

    //--------------------------------------------------------------------------
    @Override
    public void updateRows(List< Row > lRows)
        throws Exception
    {
    	insertRows (lRows); // ???! :-)
    }

    //--------------------------------------------------------------------------
    @Override
    public void deleteRows(List< Row > lRows)
        throws Exception
    {
    	AmazonSimpleDB            aDB       = mem_DB ();
    	// We can do that here ... because back end will never be call with an empty or invalid list
    	// Further all rows was sorted before so they point to the same entity type (we say Domain here) .-)
        String                    sDomain   = lRows.get(0).getTable();
        
        for (Row aRow : lRows)
        {
            String                  sId      = (String) aRow.getIdValue();
        	DeleteAttributesRequest aRequest = new DeleteAttributesRequest ();
            aRequest.setDomainName(sDomain );
            aRequest.setItemName  (sId     );
        	aDB.deleteAttributes  (aRequest);
        	
        	aRow.getPersistentStateHandler().setTransient();
        }
    }

    //--------------------------------------------------------------------------
    @Override
    public void deleteAllRows(Row aRow)
        throws Exception
    {
    	removeTable (aRow);
    	createTable (aRow);
    }

    //--------------------------------------------------------------------------
	@Override
    public void getRowById(Row aRow)
        throws Exception
    {
    	AmazonSimpleDB       aDB      = mem_DB ();
        String               sDomain  = aRow.getTable();
        Object               aId      = aRow.getIdValue();
        Class< ? >           aIdType  = aRow.getIdType();
        String               sId      = SdbDatatypeConvert.toSdbValue(aIdType, aId, false);
        GetAttributesRequest aRequest = new GetAttributesRequest ();
        
        aRequest.setDomainName    (sDomain);
        aRequest.setItemName      (sId    );
        aRequest.setConsistentRead(m_bConsistentRead);
        
        GetAttributesResult aResult     = aDB.getAttributes(aRequest);
        List< Attribute >   lAttributes = aResult.getAttributes();
        
        // non existent entities has to be handled gracefully !
        // no exception please.
        if (lAttributes == null)
        	return;
        
        for (Attribute aAttribute : lAttributes)
        {
        	String     sColumn = aAttribute.getName();
        	String     sValue  = aAttribute.getValue();
            Class< ? > aType   = aRow.getColumnType(sColumn);
            Object     aValue  = SdbDatatypeConvert.fromSdbValue(aType, sValue, m_bScrambleData);
            aRow.setColumnValue(sColumn, aValue);
        }
        
        aRow.getPersistentStateHandler().setPersistent();
    }

    //--------------------------------------------------------------------------
    @Override
    public String getAllRows (Row         aMeta     ,
                              String      sNextToken,
                              List< Row > lResults  )
        throws Exception
    {
        throw new UnsupportedOperationException ("Not implemented yet.");
    }
    
    //--------------------------------------------------------------------------
    @Override
	public String queryRows(Row             aMetaRow  ,
	                        String          sNextToken,
	                        List< Row >     lResults  ,
	                        IDBBackendQuery iQuery    )
    	throws Exception
    {
    	AmazonSimpleDB aDB      = mem_DB ();
        String         sQuery   = impl_getSelectStatement (aMetaRow, iQuery); 
        SelectRequest  aRequest = new SelectRequest ();
        
        aRequest.setSelectExpression(sQuery           );
        aRequest.setNextToken       (sNextToken       );
        aRequest.setConsistentRead  (m_bConsistentRead);
        
        SelectResult aResult = aDB.select(aRequest);
        if (aResult == null) // no result set at all ? break further search by returning END-token
        	return null;

        List< Item > lItems = aResult.getItems();
        if (lItems == null) // empty result set ? break further search by returning END-token
        	return null;
        
        for (Item aItem : lItems)
        {
        	Row aRow = aMetaRow.newRow();
        	impl_mapSdbItemToRow (aItem, aRow);
        	
        	aRow.getPersistentStateHandler().setPersistent();
        	lResults.add(aRow);
        }
        
        String sNextNextToken = aResult.getNextToken();
        return sNextNextToken;
    }

    //--------------------------------------------------------------------------
    private String impl_getSelectStatement (Row             aMetaRow,
    									    IDBBackendQuery iQuery  )
    	throws Exception
    {
    	String sStatement = mem_StatementGenerator ().createSelectStatement(aMetaRow, iQuery);
    	System.out.println ("SimpleDB statement = ["+sStatement+"]");
    	return sStatement;
    }
    
    //--------------------------------------------------------------------------
    private static boolean impl_mapNextBlockOfRowsToSdb (List< Row >             lRows     ,
                                                         List< ReplaceableItem > lSdb      ,
                                                         MutableInt              nOffset   ,
                                                         int                     nBlockSize)
        throws Exception
    {
        if (nBlockSize < 1)
            throw new IllegalArgumentException ("Oh no ... no stupid calls please. Blocksize makes no sense.");

        int nOffsetVal = nOffset.intValue();
        int nLastIndex = lRows.size()-1;

        if (nOffsetVal < 0)
            throw new IllegalArgumentException ("Oh no ... no stupid calls please. Offset is negative.");

        lSdb.clear();

        int nStart = nOffsetVal;
        int nEnd   = nStart + nBlockSize - 1;

        if (nEnd > nLastIndex)
            nEnd = nLastIndex;

        //System.out.println ("\timpl_mapNextBlockOfRowsToSdb from "+nStart+" to "+nEnd+" ...");
        for (int nStep=nStart; nStep<=nEnd; ++nStep)
        {
            Row                          aRow  = lRows.get(nStep);
            ReplaceableItem				 aSdb  = new ReplaceableItem ();              
            Object                       aId   = aRow.getIdValue();
            String                       sId   = SdbDatatypeConvert.toSdbValue(aId.getClass(), aId, false);
            List< ReplaceableAttribute > lAttr = SimpleDbProvider.impl_mapRowToSdbItem(aRow);

            aSdb.setName      (sId  );
            aSdb.setAttributes(lAttr);
            
            lSdb.add(aSdb);
        }

        nOffset.setValue(nEnd+1);
        return (! lSdb.isEmpty());
    }

    //--------------------------------------------------------------------------
	private static List< ReplaceableAttribute > impl_mapRowToSdbItem (Row aRow)
        throws Exception
    {
        List< ReplaceableAttribute > lSdbItem = new ArrayList< ReplaceableAttribute >(10);
        Iterator< String >           pColumns = aRow.listColumns();
        while (pColumns.hasNext())
        {
            String      sColumn = pColumns.next();
            Class< ? >  aType   = aRow.getColumnType(sColumn);
            Object      aValue  = aRow.getColumnValue(sColumn);
            String      sValue  = SdbDatatypeConvert.toSdbValue(aType, aValue, m_bScrambleData);

            ReplaceableAttribute aSdbAttribute = new ReplaceableAttribute (sColumn, sValue, false);
            lSdbItem.add(aSdbAttribute);
        }

        return lSdbItem;
    }

    //--------------------------------------------------------------------------
	private static void impl_mapSdbItemToRow (Item aItem,
										      Row  aRow )
        throws Exception
    {
		aRow.setIdValue(aItem.getName());
		
		List< Attribute > lAttributes = aItem.getAttributes();
		if (lAttributes == null)
			return;
		
		for (Attribute aAttribute : lAttributes)
		{
			String     sColumn = aAttribute.getName ();
			String     sValue  = aAttribute.getValue();
			Class< ? > aType   = aRow.getColumnType(sColumn);
			Object     aValue  = SdbDatatypeConvert.fromSdbValue(aType, sValue, m_bScrambleData);
			aRow.setColumnValue(sColumn, aValue);
		}
    }

    //--------------------------------------------------------------------------
    private AmazonSimpleDB mem_DB ()
    	throws Exception
    {
    	if (m_aDB == null)
    	{
    		IPersistenceUnit      iPU          = m_aMetaProvider.getPersistenceUnit();
    		String                sAccessKey   = iPU.getProperty(PersistenceUnitConst.SIMPLEDB_ACCESSKEY);
    		String                sSecretKey   = iPU.getProperty(PersistenceUnitConst.SIMPLEDB_SECRETKEY);
    		String                sRegion      = iPU.getProperty(PersistenceUnitConst.SIMPLEDB_REGION   );
    		AWSCredentials        aCredentials = new BasicAWSCredentials (sAccessKey, sSecretKey); 
    		AmazonSimpleDB        aDB          = new AmazonSimpleDBClient(aCredentials);
    		aDB.setEndpoint(AwsEndPointDefinitions.getEndPoint(sRegion, AwsEndPointDefinitions.EService.E_DB));
    		m_aDB = aDB;
    	}
    	return m_aDB;
    }
    
    //--------------------------------------------------------------------------
    private SdbStatementGenerator mem_StatementGenerator ()
    	throws Exception
    {
    	if (m_aStatementGenerator == null)
    		m_aStatementGenerator = new SdbStatementGenerator (m_bScrambleData);
    	return m_aStatementGenerator;
    }
    
    //--------------------------------------------------------------------------
    private AmazonSimpleDB m_aDB = null;
    
    //--------------------------------------------------------------------------
    private static boolean m_bScrambleData = true;

    //--------------------------------------------------------------------------
    private boolean m_bConsistentRead = true;
    
    //--------------------------------------------------------------------------
    private SdbStatementGenerator m_aStatementGenerator = null;
    
    //--------------------------------------------------------------------------
    private EntityMetaInfoProvider m_aMetaProvider = null;
}
