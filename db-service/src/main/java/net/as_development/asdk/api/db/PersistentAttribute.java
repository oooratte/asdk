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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//==============================================================================
/** Such annotation mark any class field as a persistent DB attribute.
 *  Non marked class fields will be ignored.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PersistentAttribute
{
	//-------------------------------------------------------------------------
	/// The API name of those attribute.
	public String name ();
	
	//-------------------------------------------------------------------------
	/// The name of the column within the generated back end table.
	public String column ();

	//-------------------------------------------------------------------------
	/// Define if those attribute is a 'reference' (foreign key) to an ID of another entity (primary key).
	public boolean is_id_reference () default false;
	
	//-------------------------------------------------------------------------
	/// Define if those attribute can have null values or not.
	public boolean can_be_null () default false;
	
	//-------------------------------------------------------------------------
	/// Define if those attribute can be updated or not.
	public boolean allow_updates () default true;
	
	//-------------------------------------------------------------------------
	/// Define if those attribute value must be encoded/decoded before save and after read.
	public boolean scramble () default false;
	
	//-------------------------------------------------------------------------
	/// Define the maximum length for string based attributes. [OPTIONAL]
	public int length () default 0;
}
