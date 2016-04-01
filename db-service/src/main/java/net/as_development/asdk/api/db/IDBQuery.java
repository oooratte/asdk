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
package net.as_development.asdk.api.db;

//=============================================================================
/** Queries can be used to search for data base entities which match to a set of
 *  specified criteria.
 *  
 *  We do not provide a special language as e.g. SQL it does.
 *  We provide a more 'object oriented' approach.
 *  
 *  One example:
 *		You search for an entity where the attribute 'a' is equals to '1'
 *		and where another attribute 'b' match the wildcard value 'any_val%'.
 *  
 *  In normal SQL you would write these:
 *  	"select * from my_table where a = 1 and b like 'any_val%'"
 *  
 *  Here you can write the same as:
 *  <code>
 *  	IDBQuery iQuery = ...;
 *  	iQuery.setQueryPart (0, EQueryPartBinding.AND, EQueryPartOperation.MATCH, "a", 1         );
 *  	iQuery.setQueryPart (1, EQueryPartBinding.AND, EQueryPartOperation.LIKE , "b", "any_val*");
 *  </code>
 *  
 *  First line add an operation 'a = 1' to the internal query. The boolean operation AND will be ignored ...
 *  because there is no further leading operation on the left side .-)
 *  Second line add an operation 'and b like any_val%' to the internal query.
 *  Because it's not the leading operation the boolean operator AND will be used real here.
 *  Further the wildcard-asterix '*' will be translated to a value which is supported
 *  by the internal DB back end. 
 *  
 *  First parameter of setQueryPart () will specify an index of that query part within
 *  the internal created query (string). So you can mix these two lines of code in any order ...
 *  if you don't change the index value itself ... the result will be always the same.   
 */
public interface IDBQuery< TEntity extends IEntity >
{
    //-------------------------------------------------------------------------
	/** @return the internal ID of this query.
	 * 
	 *  Those ID was define by YOU ...
	 *  because you have to create queries by calling IDB.prepareQuery(sID) :-)
	 */
	public String getId ()
		throws Exception;
	
    //-------------------------------------------------------------------------
	/** define one part of the query ...
	 *  or set a new value there.
	 * 
	 *  @param	nPosition [IN]
	 *  		the position of that part within the query.
	 *  		Must start at 0.
	 * 
	 * 	@param	eBinding [IN]
	 * 			define the boolean operator (and/or) against the part
	 * 			placed at nPosition-1.
	 * 			Will be ignored if nPosition==0.
	 * 
	 *  @param	eOperation [IN]
	 *  		define the operation used by this part (==, like, between ...)
	 *  
	 *  @param	sAttribute [IN]
	 *  		the name of the attribute to be checked here.
	 *  
	 *  @param	aValue [IN]
	 *  		the search value itself.
	 *  
	 *  Note	For the given value there exists some conditions ...
	 *  		a) if eOperation=like it must be from type String.
	 *  		b) if eOperation=between it must be from type BetweenQueryRange.
	 */
	public void setQueryPart (int                 nPosition ,
							  EQueryPartBinding   eBinding  ,
							  EQueryPartOperation eOperation,
							  String              sAttribute,
							  Object              aValue    )
		throws Exception;
}
