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
