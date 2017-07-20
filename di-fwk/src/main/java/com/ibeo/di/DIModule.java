package com.ibeo.di;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

//=============================================================================
/** bring all DI context objects itself to the DI environment
 *  so any code can rely on that and inject e.g. context and config.
 *
 *	@internal
 */
public class DIModule extends AbstractModule
{
	//-------------------------------------------------------------------------
	@Override 
	protected void configure()
	{
		try
		{
			// context and configuration are (and has to be) singletons

			final DIContext aContext = DIContext.get    ();
			final DIConfig  aConfig  = aContext .config ();
			
			bind(DIContext.class).toInstance(aContext);
			bind(DIConfig .class).toInstance(aConfig );
		}
		catch (final Throwable ex)
		{
			throw new RuntimeException (ex);
		}
	}
}
