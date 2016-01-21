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
package net.as_development.api.di;


//==============================================================================
/** Try to wrap a context.
 *  We know what we expect from a context. Most of the time we want to access
 *  or create services. How this can be done within a 'real context' as e.g.
 *  a spring container ... will be hidden from the code using our context ;-)
*/
public interface IDIContext
{
	//--------------------------------------------------------------------------
	public < T > T accessService (final Class< ? > aService)
		throws Exception;
	
	//--------------------------------------------------------------------------
	public < T > T accessService (final String sService)
		throws Exception;
}
