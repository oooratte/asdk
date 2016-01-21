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
package test.net.as_development.service_manager;

import net.as_development.api.service.env.IDependencyInjection;

import org.junit.Ignore;

//=============================================================================
/**
 */
@Ignore
public class ExampleServiceA implements IExampleServiceA
									  , IDependencyInjection
{
    //--------------------------------------------------------------------------
	private static final Class< ? >[] INJECTIONS = new Class< ? >[]
	{
		IExampleServiceB.class
	};
	
    //--------------------------------------------------------------------------
	public ExampleServiceA ()
	{}
	
    //--------------------------------------------------------------------------
	@Override
	public IExampleServiceB getB ()
	{
		return m_iB;
	}

    //--------------------------------------------------------------------------
	@Override
	public Class< ? >[] getRequiredInjections()
		throws Exception
	{
		return ExampleServiceA.INJECTIONS;
	}

    //--------------------------------------------------------------------------
	@Override
	public void inject(Class< ? > aService       ,
					   Object     aImplementation)
	throws Exception
	{
		if (aService.equals(IExampleServiceB.class))
			m_iB = (IExampleServiceB) aImplementation;
	}
	
    //--------------------------------------------------------------------------
	private IExampleServiceB m_iB = null;
}
