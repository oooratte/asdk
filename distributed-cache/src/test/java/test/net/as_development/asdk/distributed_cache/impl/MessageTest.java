package test.net.as_development.asdk.distributed_cache.impl;

import static org.junit.Assert.*;

import org.junit.Test;

import net.as_development.asdk.distributed_cache.impl.Message;

//=============================================================================
public class MessageTest
{
	//-------------------------------------------------------------------------
	@Test
	public void test()
		throws Exception
	{
		final Message aMsg01 = new Message ();
		aMsg01.setSender("me");
		aMsg01.setAction("list-keys");
		final String sEnc = Message.serialize(aMsg01);
		System.out.println(sEnc);
		final Message aMsg02 = Message.deserialize(sEnc);
		System.out.println(aMsg02);
	}
}
