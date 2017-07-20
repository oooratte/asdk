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
package net.as_development.tools.configuration;

//=============================================================================
public interface IConfigurationSet
{
	//-------------------------------------------------------------------------
	/** set the new value for the given key.
	 * 
	 *  Setting value to null will remove the key from config.
	 *  
	 *  @param	sKey [IN]
	 *  			the configuration key where we want to set the new value.
	 *
	 *	@param	aType [IN]
	 *			the type of the value (needed later on get for might needed type conversion)
	 *
	 *  @param	aValue [IN]
	 *  			the new value.
	 *  
	 *  @return	the configuration object itself to make chained set calls possible.
	 */
	public < T, R extends IConfigurationSet > R set (final String     sKey  ,
													final Class< T > aType ,
													final T          aValue)
		throws Exception;

	//-------------------------------------------------------------------------
	/** does the same then {@link set(String.class, Class.class, T)}
	 *  but retrieves value type directly from given value.
	 */
	public < T, R extends IConfigurationSet > R set (final String sKey  ,
													 final T     aValue)
	    throws Exception;
}
