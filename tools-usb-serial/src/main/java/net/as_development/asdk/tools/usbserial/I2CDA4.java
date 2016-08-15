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
package net.as_development.asdk.tools.usbserial;

import org.apache.commons.lang3.Validate;

public class I2CDA4 extends I2CUSBSerial
{
	//-------------------------------------------------------------------------
	public static final int DEVICE_ADDR = I2CUSBSerial.DEVICE_MAX520;
	
	//-------------------------------------------------------------------------
	public I2CDA4 ()
		throws Exception
	{}

	//-------------------------------------------------------------------------
	public boolean dimm (final int nChannel,
						 final int nPercent)
	    throws Exception
	{
		Validate.isTrue(nChannel >= 0 && nChannel <=   3, "Invalid argument 'channel', Out of range [0..3]."  );
		Validate.isTrue(nPercent >= 0 && nPercent <= 100, "Invalid argument 'percent'. Out of range [0..100].");

		final int     nDimmValue = impl_calcDim4Percent (nPercent);
		final boolean bResult    = writeByteRequest(DEVICE_ADDR, nChannel, nDimmValue);
		return bResult;
	}

	//-------------------------------------------------------------------------
	public void dimUp (final int  nChannel,
			           final long nSleep  )
	    throws Exception
	{
		System.out.println("start dim up ...");
		for (int nPercent=0; nPercent<100; ++nPercent)
		{
			System.out.println("... dim to ["+nPercent+"]");
			dimm(nChannel, nPercent);
			synchronized (this)
			{
				this.wait(nSleep);
			}
		}
		System.out.println("ok.");
	}
	
	//-------------------------------------------------------------------------
	public void dimDown (final int  nChannel,
			             final long nSleep  )
	    throws Exception
	{
		System.out.println("start dim down ...");
		for (int nPercent=100; nPercent>0; --nPercent)
		{
			System.out.println("... dim to ["+nPercent+"]");
			dimm(nChannel, nPercent);
			synchronized (this)
			{
				this.wait(nSleep);
			}
		}
		System.out.println("ok.");
	}

	//-------------------------------------------------------------------------
	private int impl_calcDim4Percent (final int nPercent)
	    throws Exception
	{
		final int nMax         = 255;
		final int nVal4Percent = Math.round((nPercent*nMax)/100);
		return nVal4Percent;
	}
}
