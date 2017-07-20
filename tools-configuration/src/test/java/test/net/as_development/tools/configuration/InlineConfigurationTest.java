package test.net.as_development.tools.configuration;

import org.junit.Assert;
import org.junit.Test;

import net.as_development.tools.configuration.IInlineConfiguration;
import net.as_development.tools.configuration.impl.InlineConfiguration;

//=============================================================================
public class InlineConfigurationTest
{
	//-------------------------------------------------------------------------
	@Test
	public void test()
		throws Exception
	{
		final IInlineConfiguration iConfig = new InlineConfiguration ();
		Assert.assertNull("test[01] non existing items needs to return null", iConfig.get("non-existing", String.class));

		final IInlineConfiguration iCheck = iConfig.set("key.a", String.class, "value.a");
		Assert.assertEquals("test[02] setter has to return same config instance - but has not", iConfig, iCheck);

		Assert.assertEquals("test[03] set/get dont work in pair - miss value", "value.a", iConfig.get("key.a", String.class));

		Assert.assertEquals("test[4] handling of default value wrong", "my-default", iConfig.get("non-existing", String.class, "my-default"));

		iConfig.set("key.a", String.class, null);
		Assert.assertNull("test[5] setting value to null has to remove item from config", iConfig.get("key.a", String.class));
	}

	//-------------------------------------------------------------------------
	@Test
	public void testTypeConversion()
		throws Exception
	{
		final IInlineConfiguration iConfig = new InlineConfiguration ();

		iConfig.set("key.int", "4711");
		final int nValue = iConfig.get("key.int", int.class);
		Assert.assertEquals("testTypeConversion[01] string-2-int conversion failed", 4711, nValue);
	}
}
