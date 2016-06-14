package net.as_development.asdk.tools.common.type;

//=============================================================================
public interface Convertible
{
	//-------------------------------------------------------------------------
	public String convertToString ()
		throws Exception;

	//-------------------------------------------------------------------------
	public void convertFromString (final String sValue)
		throws Exception;
}
