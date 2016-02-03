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
package net.as_development.asdk.tools.exec;

import java.io.ByteArrayOutputStream;

import org.apache.commons.lang3.StringUtils;

import com.jezhumble.javasysmon.JavaSysMon;
import com.jezhumble.javasysmon.OsProcess;
import com.jezhumble.javasysmon.ProcessInfo;

import net.as_development.asdk.tools.exec.impl.GlobalPidProcessHandler;

//==============================================================================
public class ProcessUtils
{
	//--------------------------------------------------------------------------
	public static final String PINFO_RSS = "RSS";
	public static final String PINFO_VSZ = "VSZ";
	
	//--------------------------------------------------------------------------
	/// utility class do not need a ctor .. static functions only ;-)
	private ProcessUtils ()
	{}

	//--------------------------------------------------------------------------
	public static int getPidOfThis ()
		throws Exception
	{
    	final JavaSysMon aMonitor = new JavaSysMon();
    	      Integer    nPid     = aMonitor.currentPid();
    	      
    	if (nPid < 1)
    		nPid = null;

    	return nPid;
	}
	
	//--------------------------------------------------------------------------
	public static Long getProcessResidentSetSize (final int nPid)
	    throws Exception
	{
		String sRSS = impl_getSingleProcessInfoJSMON (nPid, PINFO_RSS);

		if (StringUtils.isEmpty(sRSS))
			sRSS = impl_getSingleProcessInfoPS (nPid, PINFO_RSS);

		if (StringUtils.isEmpty(sRSS))
			return null;
		
		final long nRSS = Long.parseLong(sRSS);
		return nRSS;
	}

	//--------------------------------------------------------------------------
	public static Long getProcessVirtualSetSize (final int nPid)
	    throws Exception
	{
		String sVSZ = impl_getSingleProcessInfoJSMON (nPid, PINFO_VSZ);

		if (StringUtils.isEmpty(sVSZ))
			sVSZ = impl_getSingleProcessInfoPS (nPid, PINFO_VSZ);

		if (StringUtils.isEmpty(sVSZ))
			return null;
		
		final long nVSZ = Long.parseLong(sVSZ);
		return nVSZ;
	}

	//--------------------------------------------------------------------------
	private static String impl_getSingleProcessInfoJSMON (final int    nPid ,
												          final String sInfo)
	    throws Exception
	{
    	if (nPid == GlobalPidProcessHandler.INVALID_PID)
    		return null;
    	
    	final JavaSysMon aMonitor       = new JavaSysMon();
    	final OsProcess  aParentProcess = aMonitor.processTree();
    	final OsProcess  aPidProcess    = aParentProcess.find(nPid);
    	
    	if (aPidProcess == null)
    		return null;
    	
    	final ProcessInfo aInfo  = aPidProcess.processInfo();
    	      String      sValue = null;

    	if (StringUtils.equalsIgnoreCase (sInfo, PINFO_RSS))
    	{
    		final long nValue = aInfo.getResidentBytes();
    		if (nValue > 0)
    			sValue = Long.toString(nValue);
    	}
    	else
    	if (StringUtils.equalsIgnoreCase (sInfo, PINFO_VSZ))
    	{
    		final long nValue = aInfo.getTotalBytes();
    		if (nValue > 0)
    			sValue = Long.toString(nValue);
    	}
    	else
    		throw new RuntimeException ("No support for '"+sInfo+"' implemented yet.");

    	return sValue;
	}
	
	//--------------------------------------------------------------------------
	private static String impl_getSingleProcessInfoPS (final int    nPid ,
													   final String sInfo)
	    throws Exception
	{
		final String     sPid = Integer.toString(nPid);
		final Executable aPS  = new Executable ();

		aPS.setExecutable("ps"      );
		aPS.addArgument  ("-p", sPid);
		
    	if (StringUtils.equalsIgnoreCase (sInfo, PINFO_RSS))
    		aPS.addArgument  ("-o", "rss");
    	else
    	if (StringUtils.equalsIgnoreCase (sInfo, PINFO_VSZ))
    		aPS.addArgument  ("-o", "vsz");
    	else
    		throw new RuntimeException ("No support for '"+sInfo+"' implemented yet.");
	
		final ByteArrayOutputStream aStdOut = new ByteArrayOutputStream ();
		final ByteArrayOutputStream aStdErr = new ByteArrayOutputStream ();
		aPS.setForward4StdOut(aStdOut);
		aPS.setForward4StdErr(aStdErr);

		aPS.run();
		
		if (aPS.getLastResult() != 0)
			return null;

		// background thread for reading stdout/stderr needs some time ...
		// it's asynchronous ...
		// TODO find better solution (if possible)
		synchronized(aPS)
		{
			aPS.wait(50);
		}
		
		                       aStdOut.flush   ();
		final String sResult = aStdOut.toString();
		      String sValue  = sResult;
		             sValue  = StringUtils.substringAfter (sValue, "\n");
		             sValue  = StringUtils.substringBefore(sValue, "\n");
		             sValue  = StringUtils.trimToNull     (sValue);
		
		if (StringUtils.isEmpty(sValue))
			return null;
		             
		long nKB    = Long.parseLong(sValue);
		long nB     = nKB * 1024;
		     sValue = Long.toString(nB);
		
		return sValue;
	}
}
