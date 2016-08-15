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
package test.as_development.asdk.tools.usbserial.mocks;

//==============================================================================
public class SerialMock
{
    //--------------------------------------------------------------------------
	private SerialMock ()
	{}

	//--------------------------------------------------------------------------
	public static SerialMock DEFAULT ()
	    throws Exception
	{
		final SerialMock aMock = new SerialMock ();
		impl_mockInstance  (aMock);
		impl_mockFactory   (aMock);
		impl_mockOpenClose (aMock);
		impl_mockWrite     (aMock);
		impl_mockListening (aMock);
		return aMock;
	}

	//--------------------------------------------------------------------------
	private static void impl_mockInstance (final SerialMock aMockEnv)
	    throws Exception
	{
//		aMockEnv.m_aMock = PowerMockito.mock(Serial.class);
	}
	
	//--------------------------------------------------------------------------
	private static void impl_mockFactory (final SerialMock aMockEnv)
		throws Exception
	{
//		final Serial rMock = aMockEnv.m_aMock;
//
//		PowerMockito.mockStatic(SerialFactory.class);
//		PowerMockito.when      (SerialFactory.class, "createInstance")
//					.thenReturn(rMock);
	}
	
	//--------------------------------------------------------------------------
	private static void impl_mockOpenClose (final SerialMock aMockEnv)
	    throws Exception
	{
//		final Serial rMock = aMockEnv.m_aMock;
//		
//		Mockito.doAnswer(new Answer< Void >()
//		{
//			@Override
//			public Void answer(final InvocationOnMock aCall)
//				throws Throwable
//			{
//				final String  sDevice   = (String ) aCall.getArguments()[0];
//				final Integer nBaudrate = (Integer) aCall.getArguments()[1];
//				System.err.println ("serial.open("+sDevice+", "+nBaudrate+") ...");
//				return null;
//			}
//		}).when(rMock).open(Mockito.anyString(), Mockito.anyInt());
//
//		Mockito.doAnswer(new Answer< Void >()
//		{
//			@Override
//			public Void answer(final InvocationOnMock aCall)
//				throws Throwable
//			{
//				System.err.println ("serial.close() ...");
//				return null;
//			}
//		}).when(rMock).close();
	}
	
	//--------------------------------------------------------------------------
	private static void impl_mockWrite (final SerialMock aMockEnv)
	    throws Exception
	{
//		final Serial rMock = aMockEnv.m_aMock;
//		
//		Mockito.doAnswer(new Answer< Void >()
//		{
//			@Override
//			public Void answer(final InvocationOnMock aCall)
//				throws Throwable
//			{
//				final String sData = (String) aCall.getArguments()[0];
//				System.err.println ("serial.write('"+sData+"') ...");
//				return null;
//			}
//		}).when(rMock).write(Mockito.anyString());
//
//		Mockito.doAnswer(new Answer< Void >()
//		{
//			@Override
//			public Void answer(final InvocationOnMock aCall)
//				throws Throwable
//			{
//				final byte[] lData = (byte[]) aCall.getArguments()[0];
//				final String sData = new String(lData);
//				System.err.println ("serial.write('"+sData+"') ...");
//				
//				if (aMockEnv.m_aListener != null)
//				{
//					System.err.println("serial.dataReceived(...)");
//					aMockEnv.m_aListener.dataReceived(new SerialDataEvent ("source-01", "das sind mal ein paar daten ...."));
//				}
//
//				return null;
//			}
//		}).when(rMock).write(Mockito.any(byte[].class));
	}

	//--------------------------------------------------------------------------
	private static void impl_mockListening (final SerialMock aMockEnv)
		throws Exception
	{
//		final Serial rMock = aMockEnv.m_aMock;
//
//		Mockito.doAnswer(new Answer< Void >()
//		{
//			@Override
//			public Void answer(final InvocationOnMock aCall)
//				throws Throwable
//			{
//				Validate.isTrue(aMockEnv.m_aListener==null, "Mock env do not support registration of more then listener at the same time !");
//				aMockEnv.m_aListener = (SerialDataListener) aCall.getArguments()[0];
//				System.err.println ("serial.addSerialDataListener('"+aMockEnv.m_aListener+"') ...");
//				return null;
//			}
//		}).when(rMock).addListener(Mockito.any(SerialDataListener.class));
//
//		Mockito.doAnswer(new Answer< Void >()
//		{
//			@Override
//			public Void answer(final InvocationOnMock aCall)
//				throws Throwable
//			{
//				aMockEnv.m_aListener = null;
//				System.err.println ("serial.removeSerialDataListener() ...");
//				return null;
//			}
//		}).when(rMock).removeListener(Mockito.any(SerialDataListener.class));
	}

//	//--------------------------------------------------------------------------
//	private Serial m_aMock = null;
//
//	//--------------------------------------------------------------------------
//	private SerialDataListener m_aListener = null;
}
