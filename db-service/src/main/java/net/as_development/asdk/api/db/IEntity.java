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

import java.io.Serializable;

//==============================================================================
/** Handle all kinds of DB entities.
 */
public interface IEntity extends Serializable
							   , Cloneable
{
    //--------------------------------------------------------------------------
	/** enable the expire feature for this entity and set the expire time in seconds from now.
	 *
	 *  @param	nExpire [IN]
	 *  		the time for expire in seconds.
	 */
	public void setExpireInSeconds (long nExpire)
		throws Exception;

    //--------------------------------------------------------------------------
	/** enable the expire feature for this entity and set the expire time in minutes from now.
	 *
	 *  @param	nExpire [IN]
	 *  		the time for expire in minutes.
	 */
	public void setExpireInMinutes (long nExpire)
		throws Exception;
}
