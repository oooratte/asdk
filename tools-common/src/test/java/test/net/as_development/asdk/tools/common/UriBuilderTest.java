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

import org.junit.Assert;
import org.junit.Test;

import net.as_development.asdk.tools.common.UriBuilder;

//=============================================================================
public class UriBuilderTest
{
    //-------------------------------------------------------------------------
	@Test
	public void test()
		throws Exception
	{
		Assert.assertEquals("test[01]",
			"tcp://karl:xxx@localhost:1234/foo/bla?p1=01",
			UriBuilder	.newUri()
						.scheme        ("tcp"      )
						.user          ("karl"     )
						.password      ("xxx"      )
						.host          ("localhost")
						.port          (1234       )
						.path          ("foo"      )
						.path          ("bla"      )
						.queryParameter("p1", "01" )
						.toUri 		   (           )
						.toString      (           ));

		Assert.assertEquals("test[02]",
				"tcp://localhost:1234/foo/bla?p1=01",
				UriBuilder	.newUri()
							.scheme        ("tcp"      )
							.host          ("localhost")
							.port          (1234       )
							.path          ("foo"      )
							.path          ("bla"      )
							.queryParameter("p1", "01" )
							.toUri 		   (           )
							.toString      (           ));

		Assert.assertEquals("test[03]",
				"tcp://127.0.0.1/foo/bla?p1=01",
				UriBuilder	.newUri()
							.scheme        ("tcp"      )
							.host          ("127.0.0.1")
							.path          ("foo"      )
							.path          ("bla"      )
							.queryParameter("p1", "01" )
							.toUri 		   (           )
							.toString      (           ));

		Assert.assertEquals("test[04]",
				"tcp://127.0.0.1/foo/bla",
				UriBuilder	.newUri()
							.scheme        ("tcp"      )
							.host          ("127.0.0.1")
							.path          ("foo"      )
							.path          ("bla"      )
							.toUri 		   (           )
							.toString      (           ));

		Assert.assertEquals("test[05]",
				"tcp://karl:xxx@localhost/foo/bla?p1=01",
				UriBuilder	.newUri()
							.scheme        ("tcp"      )
							.user          ("karl"     )
							.password      ("xxx"      )
							.host          ("localhost")
							.port          (null       )
							.path          ("foo"      )
							.path          ("bla"      )
							.queryParameter("p1", "01" )
							.toUri 		   (           )
							.toString      (           ));

		Assert.assertEquals("test[06]",
				"tcp://localhost/foo/bla?p2=02",
				UriBuilder	.newUri()
							.scheme           ("tcp"             )
							.host             ("localhost"       )
							.path             ("foo"             )
							.path             ("bla"             )
							.queryParameter   ("p2", "02"        )
							.queryParameterOpt(false, "p1", "01" )
							.toUri 		      (                  )
							.toString         (                  ));
	}
}
