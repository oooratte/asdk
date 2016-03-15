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
package test.net.as_development.asdk.tools.common;

import static org.junit.Assert.*;

import org.junit.Test;

import junit.framework.Assert;
import net.as_development.asdk.tools.common.HexUtils;

public class HexUtilsTest
{
	@Test
	public void test()
		throws Exception
	{
		Assert.assertEquals("",      4097, HexUtils.hexToDecimal(new byte[] {      0x00, (byte)0x00, (byte)0x10, (byte)0x01}));
		Assert.assertEquals("",      4113, HexUtils.hexToDecimal(new byte[] {      0x00, (byte)0x00, (byte)0x10, (byte)0x11}));
		Assert.assertEquals("", 299563472, HexUtils.hexToDecimal(new byte[] {      0x11, (byte)0xDA, (byte)0xF9, (byte)0xD0}));
		Assert.assertEquals("",      2014, HexUtils.hexToDecimal(new byte[] {(byte)0x07, (byte)0xDE}));
//		System.out.println(
//				HexUtils.hexToDecimal(new byte[] {0x07, (byte)0xDE})
//				);
		
	  //final byte[] lBytes = new byte[] {0x6B, 0x07, 0x02, 0x64, 0x01, 0x09, (byte)0xFF, (byte)0xDA, (byte)0xF9, (byte)0xE3, 0x00};
//		final byte[] lBytes = new byte[] {(byte)0xff, 0x5A, 0x6B, 0x07, 0x02, 0x00, 0x01, 0x08, (byte)0xFF, (byte)0xDA, (byte)0xF9, (byte)0xE3, 0x00};
//		System.out.println(
//				HexUtils.formatByteArrayHex(
//						HexUtils.calculateCRC(2, lBytes.length-2, lBytes)));
		
//		final int nHex = 0x11DAF9D0;
//        System.out.println("los ...");
//        final byte[] lBytes = HexUtils.convertHex2ByteArray(nHex);
//        System.out.println(HexUtils.formatByteArrayHex(lBytes));
//        System.out.println("ok."); 
		
//		System.out.println(
//			HexUtils.formatByteHaufenHex(
//					HexUtils.convertStringToByteHaufen("\\245Zk\\7\\1\\0\\0\\t\\377\\332\\371\\340\\0.")));
//		System.out.println(
//				HexUtils.formatByteHaufenHex(
//						HexUtils.convertStringToByteHaufen("\245Zk\7\1\0\0\10\377\332\371\340\0-")));
	}
}
