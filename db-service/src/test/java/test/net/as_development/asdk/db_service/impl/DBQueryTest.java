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
package test.net.as_development.asdk.db_service.impl;

import org.junit.Test;
import org.mockito.Mockito;

import net.as_development.asdk.api.db.EQueryPartBinding;
import net.as_development.asdk.api.db.EQueryPartOperation;
import net.as_development.asdk.db_service.impl.AttributeListMetaInfo;
import net.as_development.asdk.db_service.impl.AttributeMetaInfo;
import net.as_development.asdk.db_service.impl.DBQueryTemplate;
import net.as_development.asdk.db_service.impl.EntityMetaInfo;
import net.as_development.asdk.tools.test.AssertEx;
import test.net.as_development.asdk.db_service.test.entities.TestEntity;

//==============================================================================
public class DBQueryTest
{
    //--------------------------------------------------------------------------
	@Test
	public void testIllegalArguments ()
		throws Exception
	{
	    DBQueryTemplate< TestEntity > aQuery = null;

		String   sValidAttribute   = "ValidAttribute";
		String   sInvalidAttribute = "";
		// there is no invalid match ...
		// even null values or empty strings can be used.
		Object   aValidMinRange    = 0;
		Object   aInvalidMinRange  = null;
		Object   aValidMaxRange    = 100;
		Object   aInvalidMaxRange  = null;
		String   sValidLike        = "some_value*?";
		String   sInvalidLike      = "";

		EntityMetaInfo aEntityMeta = Mockito.mock(EntityMetaInfo.class);

		aQuery = new DBQueryTemplate< TestEntity > ("", aEntityMeta);

		AssertEx.assertThrowsException("testIllegalArguments [01] test invalid attribute at match  ()", IllegalArgumentException.class, aQuery, "match"  , sInvalidAttribute, null);
		AssertEx.assertThrowsException("testIllegalArguments [02] test invalid attribute at like   ()", IllegalArgumentException.class, aQuery, "like"   , sInvalidAttribute, sValidLike);
		AssertEx.assertThrowsException("testIllegalArguments [03] test invalid attribute at between()", IllegalArgumentException.class, aQuery, "between", sInvalidAttribute, aValidMinRange, aValidMaxRange);

		aQuery = new DBQueryTemplate< TestEntity > ("", aEntityMeta);

		AssertEx.assertThrowsException("testIllegalArguments [04] test invalid value at like()", IllegalArgumentException.class, aQuery, "like", sValidAttribute, sInvalidLike);

		aQuery = new DBQueryTemplate< TestEntity > ("", aEntityMeta);

		AssertEx.assertThrowsException("testIllegalArguments [05] test invalid min range at between()", IllegalArgumentException.class, aQuery, "between", sValidAttribute  , aInvalidMinRange, aValidMaxRange  );
		AssertEx.assertThrowsException("testIllegalArguments [06] test invalid max range at between()", IllegalArgumentException.class, aQuery, "between", sValidAttribute  , aValidMinRange  , aInvalidMaxRange);
	}

    //--------------------------------------------------------------------------
	@Test
	public void testAttributeCheckAfterCompile ()
		throws Exception
	{
	    DBQueryTemplate< TestEntity > aQuery = null;

		String sQueryId            = "my_query";
		String sQueryAttribute     = "query_attribute";
		String sUnknownAttribute   = "unknown_attribute";
		String sColumnForAttribute = "column_of_query_attribute";
		Object aMatchValue         = Boolean.TRUE;
		String sLikeValue          = "wildcards?*";
		Object aMinRange           = 0;
		Object aMaxRange           = 100;

		EntityMetaInfo        aEntityMeta        = Mockito.mock(EntityMetaInfo.class       );
		AttributeListMetaInfo aAttributeListMeta = Mockito.mock(AttributeListMetaInfo.class);
		AttributeMetaInfo     aAttributeMeta     = Mockito.mock(AttributeMetaInfo.class    );

		Mockito.when (aEntityMeta.getAttributes()                            ).thenReturn(aAttributeListMeta );
		Mockito.when (aAttributeListMeta.getForAttributeName(sQueryAttribute)).thenReturn(aAttributeMeta     );
		Mockito.when (aAttributeMeta.getColumnName()                         ).thenReturn(sColumnForAttribute);

		aQuery = new DBQueryTemplate< TestEntity > (sQueryId, aEntityMeta);

		// a) first requests to those method must not throw an exception ...
		//    if at least no logical error is within those query .-)
		aQuery.setQueryPart(0, EQueryPartBinding.E_AND, EQueryPartOperation.E_MATCH, sQueryAttribute);

		// b) first request of any further attribute to those methods must not throw any exception
		//    if query is not still compiled !

		// c) but query which contains still one attribute ...
		//    was compiled ...
		//    and we try to set any further attribute ...
		//    must throw any exception.
		aQuery = new DBQueryTemplate< TestEntity > ("", aEntityMeta);
		aQuery.setQueryPart(0, EQueryPartBinding.E_AND, EQueryPartOperation.E_MATCH, sQueryAttribute);
		aQuery.compile();

		AssertEx.assertThrowsException("testAttributeCheckAfterCompile [01] for match ()"  , RuntimeException.class, aQuery, "match"  , sUnknownAttribute, aMatchValue);
		AssertEx.assertThrowsException("testAttributeCheckAfterCompile [02] for like ()"   , RuntimeException.class, aQuery, "like"   , sUnknownAttribute, sLikeValue );
		AssertEx.assertThrowsException("testAttributeCheckAfterCompile [03] for between ()", RuntimeException.class, aQuery, "between", sUnknownAttribute, aMinRange  , aMaxRange);
	}

    //--------------------------------------------------------------------------
	@Test
	public void testIfBackendGetRightInfos ()
		throws Exception
	{
		/*
		String   sAttributeMatch   = "attribute_match";
		String   sAttributeLike    = "attribute_like";
		String   sAttributeBetween = "attribute_between";
		String   sColumnMatch      = "column_match";
		String   sColumnLike       = "column_like";
		String   sColumnBetween    = "column_between";
		Object   aMatchValue       = new Object();
		String   sLikeValue        = "*val?ue";
		Object   aMinRange         = 456;
		Object   aMaxRange         = 789;

		EntityMetaInfo        aEntityMeta           = Mockito.mock(EntityMetaInfo.class       );
		AttributeListMetaInfo aAttributeListMeta    = Mockito.mock(AttributeListMetaInfo.class);
		AttributeMetaInfo     aAttributeMetaMatch   = Mockito.mock(AttributeMetaInfo.class    );
		AttributeMetaInfo     aAttributeMetaLike    = Mockito.mock(AttributeMetaInfo.class    );
		AttributeMetaInfo     aAttributeMetaBetween = Mockito.mock(AttributeMetaInfo.class    );

		Mockito.when (aEntityMeta.getAttributes()                              ).thenReturn(aAttributeListMeta   );
		Mockito.when (aAttributeListMeta.getForAttributeName(sAttributeMatch  )).thenReturn(aAttributeMetaMatch  );
		Mockito.when (aAttributeListMeta.getForAttributeName(sAttributeLike   )).thenReturn(aAttributeMetaLike   );
		Mockito.when (aAttributeListMeta.getForAttributeName(sAttributeBetween)).thenReturn(aAttributeMetaBetween);
		Mockito.when (aAttributeMetaMatch.getColumnName()                      ).thenReturn(sColumnMatch         );
		Mockito.when (aAttributeMetaLike.getColumnName()                       ).thenReturn(sColumnLike          );
		Mockito.when (aAttributeMetaBetween.getColumnName()                    ).thenReturn(sColumnBetween       );

		DBQuery< TestEntity > aQuery = new DBQuery< TestEntity > ("", aEntityMeta);
		aQuery.match   (sAttributeMatch  , aMatchValue);
		aQuery.like    (sAttributeLike   , sLikeValue );
		aQuery.between (sAttributeBetween, aMinRange  , aMaxRange);
		aQuery.compile ();

		List< String > lMatchColumns   = aQuery.listMatchColumns  ();
		List< String > lLikeColumns    = aQuery.listLikeColumns   ();
		List< String > lBetweenColumns = aQuery.listBetweenColumns();

		AssertEx.assertNotNull("testIfBackendGetRightInfos [01] match column list == null ?"   , lMatchColumns  );
		AssertEx.assertNotNull("testIfBackendGetRightInfos [02] like column list == null ?"    , lLikeColumns   );
		AssertEx.assertNotNull("testIfBackendGetRightInfos [03] between column list == null ?" , lBetweenColumns);

		AssertEx.assertEquals ("testIfBackendGetRightInfos [04] match column list size check"  , 1, lMatchColumns.size()  );
		AssertEx.assertEquals ("testIfBackendGetRightInfos [05] like column list size check"   , 1, lLikeColumns.size()   );
		AssertEx.assertEquals ("testIfBackendGetRightInfos [06] between column list size check", 1, lBetweenColumns.size());

		AssertEx.assertEquals ("testIfBackendGetRightInfos [07] match column == right one"     , sColumnMatch  , lMatchColumns.get(0)  );
		AssertEx.assertEquals ("testIfBackendGetRightInfos [08] like column == right one"      , sColumnLike   , lLikeColumns.get(0)   );
		AssertEx.assertEquals ("testIfBackendGetRightInfos [09] between column == right one"   , sColumnBetween, lBetweenColumns.get(0));

		AssertEx.assertEquals ("testIfBackendGetRightInfos [10] match value == right one"      , aMatchValue   , aQuery.getMatchValue(sColumnMatch)  );
		AssertEx.assertEquals ("testIfBackendGetRightInfos [11] like value == right one"       , sLikeValue    , aQuery.getLikeValue (sColumnLike)   );
		AssertEx.assertEquals ("testIfBackendGetRightInfos [12] between min range == right one", aMinRange     , aQuery.getMinRange  (sColumnBetween));
		AssertEx.assertEquals ("testIfBackendGetRightInfos [13] between max range == right one", aMaxRange     , aQuery.getMaxRange  (sColumnBetween));

		AssertEx.assertThrowsException("testIfBackendGetRightInfos [14] exception for wrong match column ?"        , IllegalArgumentException.class, aQuery, "getMatchValue", sColumnLike   );
		AssertEx.assertThrowsException("testIfBackendGetRightInfos [15] exception for wrong like column ?"         , IllegalArgumentException.class, aQuery, "getLikeValue" , sColumnBetween);
		AssertEx.assertThrowsException("testIfBackendGetRightInfos [16] exception for wrong between column (min) ?", IllegalArgumentException.class, aQuery, "getMinRange"  , sColumnMatch  );
		AssertEx.assertThrowsException("testIfBackendGetRightInfos [17] exception for wrong between column (max) ?", IllegalArgumentException.class, aQuery, "getMaxRange"  , sColumnLike   );
		*/
	}
}
