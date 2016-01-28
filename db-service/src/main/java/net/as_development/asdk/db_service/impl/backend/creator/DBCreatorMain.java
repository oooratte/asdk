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
package net.as_development.asdk.db_service.impl.backend.creator;

import java.util.List;

//==============================================================================
/**
 */
public class DBCreatorMain
{
    //-------------------------------------------------------------------------
    public static final int RETURN_OK = 0;
    
    //-------------------------------------------------------------------------
    public static final int RETURN_HELP = -1;
    
    //-------------------------------------------------------------------------
    public static final int RETURN_ERROR = -2;
    
    //-------------------------------------------------------------------------
    public static int main (String[] lArguments)
    {
        try
        {
            DBCreatorCommandLine aCmdLine = new DBCreatorCommandLine ();
            aCmdLine.parse(lArguments);
            
            if (aCmdLine.needsHelp())
            {
                aCmdLine.printHelp();
                return DBCreatorMain.RETURN_HELP;
            }

            DBCreator            aDBCreator        = new DBCreator ();
            String               sUser             = aCmdLine.getUser();
            String               sPassword         = aCmdLine.getPassword();
            List< String >       lPersistenceUnits = aCmdLine.getPersistenceUnitList();
            List< String >       lEntities         = aCmdLine.getEntities();
            DBCreator.EOperation eOperation        = aCmdLine.getOperation();
            
            aDBCreator.setAdminCredentials(sUser, sPassword );
            aDBCreator.setPersistenceUnits(lPersistenceUnits);
            aDBCreator.setEntities        (lEntities        );
            
            aDBCreator.doOperation(eOperation);
            
            return DBCreatorMain.RETURN_OK;
        }
        catch (Throwable ex)
        {
            System.err.println (ex.getMessage());
            
            ex.printStackTrace();
            
            return DBCreatorMain.RETURN_ERROR;
        }
    }
}
