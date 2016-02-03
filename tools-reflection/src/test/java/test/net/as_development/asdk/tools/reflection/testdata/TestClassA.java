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
package test.net.as_development.asdk.tools.reflection.testdata;


import org.junit.Ignore;

//==============================================================================
/** Internal test class which provide some test fields ...
 *  some of them annotated ... some not.
 */
@Ignore // not an unit test .-)
public class TestClassA
{
	//-------------------------------------------------------------------------
    /// Name of the field A1 (must correspond to real field name used within this test class !)
    public static final String NAME_OF_FIELD_A1 = "m_nFieldA1";

	//-------------------------------------------------------------------------
    /// test field not bound to any annotation !
    @SuppressWarnings("unused")
	private int m_nFieldA1;

	//-------------------------------------------------------------------------
    // used by unit test ObjectManipulationTest
    public void setFieldA1 (int nValue)
    {
    	m_nFieldA1 = nValue;
    }
    
	//-------------------------------------------------------------------------
    // used by unit test ObjectManipulationTest
    public int getFieldA1 ()
    {
    	return m_nFieldA1;
    }
    
	//-------------------------------------------------------------------------
    /// Name of the field A2 (must correspond to real field name used within this test class !)
    public static final String NAME_OF_FIELD_A2 = "m_nFieldA2";

	//-------------------------------------------------------------------------
    /// test field bound to our test annotation ... so it must be found
    @SuppressWarnings("unused")
    @TestFieldAnnotation
    private int m_nFieldA2;
}