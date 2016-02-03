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
