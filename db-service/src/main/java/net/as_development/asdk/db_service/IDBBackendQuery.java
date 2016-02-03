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

import net.as_development.asdk.db_service.impl.QueryPartValue;

//=============================================================================
/** wraps the IDBQuery to the back end implementation ...
 *  Here we can provide some methods which are usefully for the back end only
 *  without the need to publish more details on the 'normal API'. 
 */
public interface IDBBackendQuery
{
    //-------------------------------------------------------------------------
	/** @return the internal id of this query.
	 * 
	 *  Those id will be guaranteed as unique. The back end implementation
	 *  can use it to e.g. implement caching of 'compiled' queries .-)
	 */
	public String getId ()
		throws Exception;
	
    //-------------------------------------------------------------------------
	/** provides read access to the different parts of this query.
	 * 
	 *  Order within that list is important. On creating the query the 'user'
	 *  decided in which order those parts has to be added ... and now we have
	 *  respect that too. Otherwise e.g. logical operator like 'and'/'or'
	 *  wont work as expected.
	 *  
	 *  So please start with the first part at position 0 .-)
	 */
	public QueryPartValue[] getQueryParts ()
		throws Exception;
}
