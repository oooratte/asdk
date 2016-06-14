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
package test.net.as_development.asdk.distributed_cache;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.junit.Test;

import net.as_development.asdk.distributed_cache.DistributedCache;
import net.as_development.asdk.distributed_cache.impl.ERunMode;

//=============================================================================
public class DistributedCacheLastTest
{
	//-------------------------------------------------------------------------
	@Test
	public void test()
		throws Exception
	{
		final int CACHE_SIZE = 1000000;
		
		final DistributedCache aServer = impl_newCache (ERunMode.E_SERVER);
		aServer.connect();

		System.out.println("fill cache with "+CACHE_SIZE+" items ...");
		impl_fillCache (aServer, CACHE_SIZE);
		System.out.println("ok.");
		
		long start = System.nanoTime(); // System.currentTimeMillis();
		
		System.out.println("list key items ...");
		final List< String > lKeys = aServer.listSubSet("msg.*");
		System.out.println("ok.");
		
		System.out.println("keys : "+lKeys.size());

		System.out.println("list messages ...");
		final List< String > lMsgs = impl_listMessageIdsInCacheV01a (lKeys);
		System.out.println("ok.");

		System.out.println("msgs : "+lMsgs.size());
		
		final StringBuffer   sReport  = new StringBuffer (256);
		      int            nNotSent = 0;
		      int            nLost    = 0;
		final List< String > lCheck   = new ArrayList< String > ();

		sReport.append ("<html><head></head><body>");
		sReport.append ("<table>");

		for (final String sMsgId : lMsgs)
		{
//			if (lCheck.contains(sMsgId))
//				throw new Error ("Duplicate : "+sMsgId);
//			lCheck.add(sMsgId);
			
			final String sClientOut = aServer.get("msg."+sMsgId+".ws-client-out");
			final String sServerIn  = aServer.get("msg."+sMsgId+".ws-server-in" );
			
			if (StringUtils.isEmpty(sClientOut))
			{
				sReport.append ("\n<tr><td>msg ["                     );
				sReport.append (sMsgId                                );
				sReport.append ("]</td><td>was not sent out</td></tr>");
				nNotSent++;
			}
			else
			if (StringUtils.isEmpty(sServerIn))
			{
				sReport.append ("\n<tr><td>msg ["                             );
				sReport.append (sMsgId                                        );
				sReport.append ("]</td><td>didnt reached the server</td></tr>");
				nLost++;
			}
			else
			{
				final long nClientOut  = Long.parseLong(sClientOut);
				final long nServerIn   = Long.parseLong(sServerIn );
				final long nTim2Server = nServerIn - nClientOut;
				
				sReport.append ("\n<tr><td>msg ["                             );
				sReport.append (sMsgId                                        );
				sReport.append ("]</td><td>"                                  );
				sReport.append (DurationFormatUtils.formatDuration(nTim2Server, "HH:mm:ss-SSS"));
				sReport.append ("</td></tr>"                                  );
			}
		}

		sReport.append ("</table>");
		
		sReport.append ("</br></br>"      );
		sReport.append ("<div>"           );
		sReport.append ("<h3>Results</h3>");
		sReport.append ("messages : "+lMsgs.size()+"</br>");
		sReport.append ("not sent : "+nNotSent+"</br>");
		sReport.append ("lost     : "+nLost   +"</br>");
		sReport.append ("</div>"          );

		sReport.append ("</body></html>"  );
		
		long end  = System.nanoTime(); //System.currentTimeMillis();
		long time = end - start;
		
		System.out.println("time : "+time+" ns / "+ (time/1000000)+" ms / " + (time/1000000000)+" s");
		
		final String sReportContent = sReport.toString ();
//		System.out.println(sReportContent);
		final int nBytes = sReportContent.length();
		final int nKB    = nBytes / 1024;
		final int nMB    = nKB    / 1024;
		System.out.println("report size : "+nBytes+" byte / "+nKB+" kb / "+nMB+" MB");
		
		aServer.disconnect();
	}

	//-------------------------------------------------------------------------
	private void impl_fillCache (final DistributedCache aCache,
								 final int              nCount)
		throws Exception
	{
		int i=0;
		for (i=0; i<nCount; ++i)
		{
			final String sUID = UUID.randomUUID().toString();
			aCache.set("msg."+sUID+".ws-client-out", Long.toString(System.currentTimeMillis()));
			aCache.set("msg."+sUID+".ws-server-in" , Long.toString(System.currentTimeMillis()));
		}
	}
	
	//-------------------------------------------------------------------------
	private List< String > impl_listMessageIdsInCacheV01 (final List< String > lCacheKeys)
	    throws Exception
	{
		final List< String > lMsgIds = new ArrayList< String > ();
		for (final String sKey : lCacheKeys)
		{
			final String sMsgId = sKey.substring(4, 40);
			
			if ( ! lMsgIds.contains(sMsgId))
				lMsgIds.add(sMsgId);
		}
		return lMsgIds;
	}	
	
	//-------------------------------------------------------------------------
	private List< String > impl_listMessageIdsInCacheV01a (final List< String > lCacheKeys)
	    throws Exception
	{
		final int            nMax    = lCacheKeys.size();
		final List< String > lMsgIds = new ArrayList< String > (nMax);
		final Set < String > lTemp   = new HashSet  < String > (nMax);
		for (final String sKey : lCacheKeys)
		{
			final String sMsgId = sKey.substring(4, 40);
			lTemp.add(sMsgId);
		}
		lMsgIds.addAll(lTemp);
		return lMsgIds;
	}	

	//-------------------------------------------------------------------------
	private List< String > impl_listMessageIdsInCacheV01b (final List< String > lCacheKeys)
	    throws Exception
	{
		final int            nMax    = lCacheKeys.size();
		final List< String > lMsgIds = new ArrayList< String > (nMax);
		for (final String sKey : lCacheKeys)
		{
			final String sMsgId = sKey.substring(4, 40);
			
			if ( ! lMsgIds.contains(sMsgId))
				lMsgIds.add(sMsgId);
		}
		return lMsgIds;
	}	
	
	//-------------------------------------------------------------------------
	private List< String > impl_listMessageIdsInCacheV02 (final List< String > lCacheKeys)
	    throws Exception
	{
		final List< String > lMsgIds = new ArrayList< String > ();
		for (String sKey : lCacheKeys)
		{
			sKey = StringUtils.removeStart    (sKey, "msg.");
			sKey = StringUtils.substringBefore(sKey, "."   );
			final String sMsgId = sKey;
			
			if ( ! lMsgIds.contains(sMsgId))
				lMsgIds.add(sMsgId);
		}
		return lMsgIds;
	}	

	//-------------------------------------------------------------------------
	private DistributedCache impl_newCache (final ERunMode eRunMode)
		throws Exception
	{
		final DistributedCache aCache = new DistributedCache ();
		aCache.configure().setRunMode(eRunMode);
		aCache.connect  ();
		return aCache;
	}
}
