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

import net.as_development.asdk.api.service.env.IServiceEnv;
import net.as_development.asdk.api.service.env.IServiceRegistry;
import net.as_development.asdk.service.env.ServiceEnv;
import net.as_development.asdk.service.env.impl.ServiceEnvImpl;

import org.junit.Assert;
import org.junit.Test;

//=============================================================================
public class ServiceEnvTest
{
    //-------------------------------------------------------------------------
    public ServiceEnvTest()
    {}

    //-------------------------------------------------------------------------
    @Test
    public void testServiceEnvItselfIsSingleton ()
        throws Exception
    {
        IServiceEnv iSmgr1 = ServiceEnv.get();
        IServiceEnv iSmgr2 = ServiceEnv.get();

        Assert.assertNotNull("testServiceEnvItselfIsSingleton [01] global service manager instance has not to be null.", iSmgr1);
        Assert.assertSame   ("testServiceEnvItselfIsSingleton [02] expect to get same instance second time too."       , iSmgr1, iSmgr2);
    }

    //-------------------------------------------------------------------------
    @Test
    public void testSingletons ()
        throws Exception
    {
        IServiceEnv  iSmgr     = new ServiceEnvImpl  ();
        IServiceRegistry iRegistry = iSmgr.getServiceRegistry();
        iRegistry.registerModule(new ExampleServiceRegistry ());
        
        IExampleServiceB aB1 = iSmgr.getService(IExampleServiceB.class);
        IExampleServiceB aB2 = iSmgr.getService(IExampleServiceB.class);

        Assert.assertSame ("testSingletons [01] expect to get same instance second time too.", aB1, aB2);
    }

    //-------------------------------------------------------------------------
    @Test
    public void testInjection ()
        throws Exception
    {
        IServiceEnv  iSmgr     = new ServiceEnvImpl  ();
        IServiceRegistry iRegistry = iSmgr.getServiceRegistry();
        iRegistry.registerModule(new ExampleServiceRegistry ());

        IExampleServiceA iServiceA = iSmgr.getService(IExampleServiceA.class);
        Assert.assertNotNull("testInjection [01] check injected member 'serviceB' of 'serviceA'", iServiceA.getB());
    }
}
