package test.net.as_development.asdk.tools.common.type;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import net.as_development.asdk.tools.common.type.TypeConverter;

//=============================================================================
public class TypeConverterTest
{
	//-------------------------------------------------------------------------
	@Test
	public void testString2Array()
		throws Exception
	{
		Assert.assertEquals("testString2Array [01] unexpected", "[1, 2, 3]", Arrays.toString(TypeConverter.string2Array("[1|2|3]")));
	}
}
