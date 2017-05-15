package net.as_development.asdk.jms.core.simple;

//=============================================================================
public interface ISubscriber
{
	//-------------------------------------------------------------------------
	public void recieve (final String sMessage)
		throws Exception;
}
