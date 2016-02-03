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
package test.net.as_development.asdk.service.env;

import java.util.List;
import java.util.Vector;

import org.junit.Ignore;

import net.as_development.asdk.service.env.impl.ServiceRegistryModule;

//=============================================================================
/**
 */
@Ignore
public class ExampleServiceRegistry extends ServiceRegistryModule
{
    //--------------------------------------------------------------------------
    public ExampleServiceRegistry ()
        throws Exception
    {
        markServiceSingleton (IExampleServiceB.class);
    }

    //--------------------------------------------------------------------------
    @Override
    public List< String > listServices() throws Exception
    {
        if (m_lServices == null)
        {
            List< String > lServices = new Vector< String > (10);
            lServices.add (IExampleServiceA.class.getName ());
            lServices.add (IExampleServiceB.class.getName ());
            m_lServices = lServices;
        }
        return m_lServices;
    }

    //--------------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    @Override
    public < T > T mapServiceToImplementation(String sService)
        throws Exception
    {
        if (sService.equals (IExampleServiceA.class.getName ()))
            return (T) new ExampleServiceA ();

        if (sService.equals (IExampleServiceB.class.getName ()))
            return (T) new ExampleServiceB ();

        return null;
    }
    
    //--------------------------------------------------------------------------
    private List< String > m_lServices = null;
}

