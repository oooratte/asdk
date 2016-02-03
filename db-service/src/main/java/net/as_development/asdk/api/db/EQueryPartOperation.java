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

//==============================================================================
/** Define the operation bound to an IDBQueryPart.
 */
public enum EQueryPartOperation
{
    //--------------------------------------------------------------------------
	/** define a 'match' operation (equals, ==)
	 *  Can be used for all types (int, bool, string, date, time etcpp).
	 */
	E_MATCH,
	
    //--------------------------------------------------------------------------
	/** define a 'like' operation which supports wildcards like * or ?
	 *  Can be used for string types only.
	 *  Supported wildcards are the following ones:
	 *  '*' if you wish to match to [0..n] chars of any kind at that place 
	 *  '?' if you wish to match to [1] char of any kind at that place 
	 */
	E_LIKE,
	
    //--------------------------------------------------------------------------
	/** define a 'between' operation where we search a value inside the specified range.
	 *  Value of the corresponding IDBQueryPart instance must be from type QueryRange.
	 */
	E_BETWEEN,
	
    //--------------------------------------------------------------------------
	/** can be used to find all entities less than a specified reference value.
	 *  @note means NOT lees-or-equals ! 
	 */
	E_LESS_THAN,
	
    //--------------------------------------------------------------------------
	/** can be used to find all entities greater than a specified reference value.
	 *  @note means NOT greater-or-equals ! 
	 */
	E_GREATER_THAN;
}