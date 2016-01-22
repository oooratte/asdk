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
package net.as_development.asdk.service.env;

import net.as_development.asdk.api.service.env.IServiceEnv;
import net.as_development.asdk.service.env.impl.ServiceEnvImpl;

//=============================================================================
/** Provides access to the global (because singleton) service manager instance.
*/
public class ServiceEnv
{
  //--------------------------------------------------------------------------
  /** @return	the singleton instance of those IServiceEnv.
   */
  public static synchronized IServiceEnv get ()
      throws Exception
  {
      if (m_gInstance == null)
  		m_gInstance = new ServiceEnvImpl ();
      return m_gInstance;
  }

  //--------------------------------------------------------------------------
  private static IServiceEnv m_gInstance = null;
}