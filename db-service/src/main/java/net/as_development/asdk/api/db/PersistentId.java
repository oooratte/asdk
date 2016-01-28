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
/** Such annotation mark any field as "primary key".
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PersistentId
{
	//-------------------------------------------------------------------------
	/** Defines a set of strategies how Id's must be implemented/handled
	 *  automatic by the underlying DB layer.
	 */
	public enum EStrategy
	{
		/// use UUIDs as string please
		E_UUID
	};
	
	//-------------------------------------------------------------------------
	/** The API name of those id field.
	 */
	public String name ();
	
	//-------------------------------------------------------------------------
	/** The name of the column within the generated back end table.
	 */
	public String column ();
	
	//-------------------------------------------------------------------------
	/** The bound Id generation strategy.
	 */
	public EStrategy strategy ();
}
