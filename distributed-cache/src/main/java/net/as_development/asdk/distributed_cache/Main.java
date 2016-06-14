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
package net.as_development.asdk.distributed_cache;

import org.apache.commons.lang3.StringUtils;

import net.as_development.asdk.distributed_cache.impl.ERunMode;

public class Main
{
	public static void main (final String[] lArgs)
	{
		try
		{
			String sServerAddress = null;
			long   nTimeout       = 10000;
			String sMode          = "client";

			if (lArgs.length >= 3)
			{
				nTimeout = Long.parseLong(lArgs[2]);
				System.out.println("... define TIMEOUT : "+nTimeout);
			}
			if (lArgs.length >= 2)
			{
				sServerAddress = lArgs[1];
				System.out.println("... define SERVER-ADDRESS : "+sServerAddress);
			}
			if (lArgs.length >= 1)
			{
				sMode = lArgs[0];
				System.out.println("... define MODE : "+sMode);
			}
			
			if (StringUtils.equalsIgnoreCase(sMode, "client"))
				impl_doClient (sServerAddress);
			else
			if (StringUtils.equalsIgnoreCase(sMode, "server"))
				impl_doServer (sServerAddress, nTimeout);
			else
				throw new UnsupportedOperationException ("No support for mode '"+sMode+"'.");
		}
		catch (Throwable ex)
		{
			System.err.println(ex.getMessage ());
			ex.printStackTrace(System.err      );
			System.exit(-1);
		}
		System.out.println("FINI");
		System.exit(0);
	}
	
	private static void impl_doServer (final String sAddress,
									   final long   nTimeout)
	    throws Exception
	{
		final DistributedCache aCache = new DistributedCache ();
		aCache.configure().setRunMode(ERunMode.E_SERVER);
		if ( ! StringUtils.isEmpty(sAddress))
			aCache.configure().setAddress(sAddress);
		aCache.connect  ();
		System.out.println("... go sleep for "+nTimeout+" ms");
		Thread.sleep (nTimeout);
	}

	private static void impl_doClient (final String sAddress)
	    throws Exception
	{
		final DistributedCache aCache = new DistributedCache ();
		aCache.configure().setRunMode(ERunMode.E_CLIENT);
		if ( ! StringUtils.isEmpty(sAddress))
			aCache.configure().setAddress(sAddress);
		aCache.connect  ();
		for (int i=0; i<10; ++i)
		{
			aCache.set("i", Integer.toString(i));
			Thread.sleep(1000);
		}
	}
}
