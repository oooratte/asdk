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
package net.as_development.asdk.tools.test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;

//==============================================================================
/** Extends the original JUnit Assert class.
 */
public class AssertEx extends Assert
{
	//-------------------------------------------------------------------------
	public static final void assertThrowsException(Class< ? > aExpectedException,
												   Object     aObject			,
												   String     sMethod			,
												   Object...  lArguments		)
		throws Exception
	{
		AssertEx.assertThrowsException("", aExpectedException, aObject, sMethod, lArguments);
	}
	
	//-------------------------------------------------------------------------
	/** Check if the specified method throws the right exception.
	 * 
	 *	@param	sMessage [IN]
	 *			the message printed out on error.
	 *
	 *	@param	aExpectedException [IN]
	 *			the expected exception type.
	 *
	 *	@param  aObject [IN]
	 *			the object where a method must be invoked.
	 *
	 *	@param	sMethod [IN]
	 *			the name of the method to be invoked on the given object.
	 *
	 *	@param	lArguments [IN, OPTIONAL]
	 *			the list of (optional) arguments.
	 *
	 *	@throws Exception if method does not throw any exception
	 *			or thrown exception does not match the expected one.
	 */
	public static final void assertThrowsException(String  	  sMessage			,
												   Class< ? > aExpectedException,
												   Object     aObject			,
												   String     sMethod			,
												   Object...  lArguments		)
	    throws Exception
	{
		Class< ? > aClass   = aObject.getClass();
		Method[]   lMethods = aClass.getDeclaredMethods();
		
		for (Method aMethod : lMethods)
		{
			if ( ! aMethod.getName().equals(sMethod))
				continue;
			
			try
			{
				aMethod.invoke(aObject, lArguments);
				Assert.fail (sMessage+" : no exception was thrown but expected was '"+aExpectedException.getName ()+"'.");
			}
			catch (InvocationTargetException exInvocation)
			{
				Throwable  ex      = exInvocation.getTargetException();
				Class< ? > exClass = ex.getClass();
				if ( ! exClass.equals(aExpectedException))
					Assert.fail (sMessage+" : thrown exception was '"+exClass.getName()+"' but expected one was '"+aExpectedException.getName()+"'.");
			}
		}
	}
	
    //-------------------------------------------------------------------------
	public static < T > void assertNotEquals (T aNotExpected,
	                                          T aActual     )
	    throws Exception
	{
	    assertNotEquals ("", aNotExpected, aActual);
	}
	
    //-------------------------------------------------------------------------
	/** check if both values are not equals.
	 *  Throw an error if they are equals.
	 *
	 *  @param sMessage [IN]
	 *         the error message shown in case both values are equals.
	 *         
	 *  @param aNotExpected [IN]
	 *         the value not expected here.
	 * 
     *  @param aActual [IN]
     *         the actual value to be checked here.
	 */
    public static < T >  void assertNotEquals (String sMessage    ,
                                               T      aNotExpected,
                                               T      aActual     )
        throws Exception
    {
        if (StringUtils.isEmpty (sMessage))
            sMessage = "equals - but was not expected: '"+aNotExpected+"' <> '"+aActual+"'";
        
        if (
            (aNotExpected == null) &&
            (aActual      == null)
           )
            AssertEx.fail (sMessage);
        
        if (
            (aNotExpected != null        ) &&
            (aActual      != null        ) &&
            (aNotExpected.equals(aActual))
           )
            AssertEx.fail (sMessage);
    }
    
    //-------------------------------------------------------------------------
    /// see @link #assertArrayNotEquals(java.lang.String,...);
    public static < T >  void assertArrayNotEquals (T[] aNotExpected,
                                                    T[] aActual     )
        throws Exception
    {
        AssertEx.assertArrayNotEquals("", aNotExpected, aActual);
    }
    
    //-------------------------------------------------------------------------
    /** check if both array are not equals.
     *  Throw an error if they are equals.
     *
     *  @param sMessage [IN]
     *         the error message shown in case both arrays are equals.
     *         
     *  @param aNotExpected [IN]
     *         the array not expected here.
     * 
     *  @param aActual [IN]
     *         the actual array to be checked here.
     */
    public static < T >  void assertArrayNotEquals (String sMessage    ,
                                                    T[]    aNotExpected,
                                                    T[]    aActual     )
        throws Exception
    {
        if (StringUtils.isEmpty (sMessage))
            sMessage = "equals - but was not expected: '"+aNotExpected+"' <> '"+aActual+"'";
        
        if (
            (aNotExpected == null) &&
            (aActual      == null)
           )
            AssertEx.fail (sMessage);
        
        if (
            (aNotExpected        != null          ) &&
            (aActual             != null          ) &&
            (aNotExpected.length == aActual.length)
           )
        {
            int c = aNotExpected.length;
            int i = 0;

            for (i=0; i<c; ++i)
            {
                if ( ! aNotExpected[i].equals(aActual[i]))
                    return;
            }
            
            AssertEx.fail (sMessage);
        }
    }
    
    //-------------------------------------------------------------------------
    /// see @link #assertArrayNotEquals(java.lang.String,...);
    public static void assertArrayNotEquals (byte[] aNotExpected,
                                             byte[] aActual     )
        throws Exception
    {
        AssertEx.assertArrayNotEquals("", aNotExpected, aActual);
    }
    
    //-------------------------------------------------------------------------
    public static void assertArrayNotEquals (String sMessage    ,
                                             byte[] aNotExpected,
                                             byte[] aActual     )
        throws Exception
    {
        if (StringUtils.isEmpty (sMessage))
            sMessage = "equals - but was not expected: '"+aNotExpected+"' <> '"+aActual+"'";
        
        if (
            (aNotExpected == null) &&
            (aActual      == null)
           )
            AssertEx.fail (sMessage);
        
        if (
            (aNotExpected        != null          ) &&
            (aActual             != null          ) &&
            (aNotExpected.length == aActual.length)
           )
        {
            int c = aNotExpected.length;
            int i = 0;

            for (i=0; i<c; ++i)
            {
                if (aNotExpected[i] != (aActual[i]))
                    return;
            }
            
            AssertEx.fail (sMessage);
        }
    }
}
