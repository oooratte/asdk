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
package net.as_development.asdk.db_service.impl;

import java.util.UUID;

import net.as_development.asdk.api.db.PersistentId.EStrategy;

//==============================================================================
/** Provide several functions around DB identities.
 */
public class IdStrategy
{
    //--------------------------------------------------------------------------
	/// max length of UUID based ID strings
	public static final int LENGTH_UUID = 36;
	
    //--------------------------------------------------------------------------
	/** @return the maximum length of string values for the specified
	 * 			id generation strategy.
	 * 
	 *  Such information will be usefully e.g. for the DB back end
	 *  to know which length must be reserved for the ID column.
	 *  
	 *  @param	eStrategy [IN]
	 *  		specify the ID generation strategy.
	 */
	public static int getIdLengthForStrategy (EStrategy eStrategy)
		throws Exception
	{
		if (eStrategy == EStrategy.E_UUID)
			return IdStrategy.LENGTH_UUID;
		
		throw new IllegalArgumentException ("Did you changed enum PersistendId.EStrategy ... but forgot to adapt this method here ?");
	}
	
    //--------------------------------------------------------------------------
	/** @return a new generated ID suitable for the specified strategy.
	 * 
	 *  @param	eStrategy [IN]
	 *  		specify the ID generation strategy.
	 */
	public static String newId (EStrategy eStrategy)
		throws Exception
	{
		if (eStrategy == EStrategy.E_UUID)
			return UUID.randomUUID().toString ();
		
		throw new IllegalArgumentException ("Did you changed enum PersistendId.EStrategy ... but forgot to adapt this method here ?");
	}
}
