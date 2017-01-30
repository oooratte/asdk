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
package net.as_development.asdk.persistence;

import java.util.List;
import java.util.Map;

//=============================================================================
public interface ISimplePersistenceImpl
{
	//-------------------------------------------------------------------------
	/** configure this instance
	 *  Is called at first - before other methods are used at this instance.
	 *  Is called one times only.
	 *  
	 *	@param	lConfig [IN]
	 *			a "flatten config" (where string list contains tuple of <key, value> pairs)
	 */
	public void configure (final String... lConfig)
		throws Exception;
	
	//-------------------------------------------------------------------------
	/** @return a impl for the define sub set of current key scope.
	 * 
	 *  It's up to the implementation to decide if same instance can be reused ...
	 *  needs to be wrapped ... or if a complete new instance has to be used here.
	 *  
	 *  NOTE : SubSet information has not to be used to encode/decode keys !
	 *  Thats done within a wrapper implementation already !
	 *  
	 *  @param	sSubSet [IN]
	 *  		the new sub set.
	 */
	public ISimplePersistenceImpl getSubSet (final String sSubSet)
		throws Exception;

	//-------------------------------------------------------------------------
	/** @return a list of all keys supported by this instance.
	 * 
	 *  NOTE: All keys within that list needs to be absolute !
	 *  It's allowed (not mandatory) to filter them by using sub set information ...
	 *  but it's not allowed to make keys relative to the sub set information !
	 */
	public List< String > listKeys ()
		throws Exception;

	//-------------------------------------------------------------------------
	/** set new values for a some keys within current scope.
	 * 
	 *  Setting a key to "null" means : remove it.
	 * 
	 *  NOTE: All keys within that list are absolute.
	 *  If anything goes right no key outside sub set scope will reach this method ;-)
	 *  
	 *	@param	lChanges [IN]
	 *			the list of changes to be applied.
	 */
	public void set (final Map< String, Object > lChanges)
	    throws Exception;

	//-------------------------------------------------------------------------
	/** @return the current value of the requested key.
	 * 
	 *  NOTE: Key is absolute.
	 *  If anything goes right no key outside sub set scope will reach this method ;-)

	 *	@param	sKey [IN]
	 *			the key where the value needs to be returned.
	 */
	public Object get (final String sKey)
	    throws Exception;

	//-------------------------------------------------------------------------
	/** clear all information within the current sub set scope.
	 */
	public void clear ()
		throws Exception;
}
