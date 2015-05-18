/**
 * Copyright 2012 Andreas Schlüns - as-development.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.as_development.tools.test;

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
	/// @see {@link #assertThrowsException(java.lang.String, ...) }
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
	 *	@throws	an exception if method does not throw any exception
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
	/// @see @link #assertNotEquals(java.lang.String,...)
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
	 *  @param sMessgae [IN]
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
    /// @see @link #assertArrayNotEquals(java.lang.String,...);
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
    /// @see @link #assertArrayNotEquals(java.lang.String,...);
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
