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
package net.as_development.asdk.tools.exec.impl;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jezhumble.javasysmon.JavaSysMon;
import com.jezhumble.javasysmon.OsProcess;
import com.jezhumble.javasysmon.ProcessInfo;
import com.jezhumble.javasysmon.ProcessVisitor;

//==============================================================================
/** visit the child process tree of a given process and return a list of all PIDs
 */
public class ProcessChildVisitor implements ProcessVisitor
{
	//--------------------------------------------------------------------------
	private static final Logger LOG = LoggerFactory.getLogger(ProcessChildVisitor.class);
	
	//--------------------------------------------------------------------------
	/// see using of it and you will (hopefully) understand ;-)
	private static final boolean DO_NOT_KILL_PROCESS = false;
	
	//--------------------------------------------------------------------------
    public ProcessChildVisitor ()
    	throws Exception
    {}
    
	//--------------------------------------------------------------------------
    /** set the pid of the root process where  we start search for child processes.
     * 
     *  @param	nPid [IN]
     *  		the pid of the root process.
     */
    public void setRootPid (final int nPid)
    	throws Exception
    {
    	m_nRootPid = nPid;
	}

    //--------------------------------------------------------------------------
    /** ATTENTION : NEVER EVER RETURN TRUE WITHIN THAT METHOD !
     *  Read the documentation of {@link ProcessVisitor.visit} carefully.
     *  Return TRUE means : kill the process you are visiting ...
     *  Do you want that here real ?
     */
    @Override
    public boolean visit(final OsProcess aProcess   ,
    					 final int       nChildLevel)
    {
    	try
    	{
	    	final ProcessInfo aInfo = aProcess.processInfo ();
	    	final int         nPid  = aInfo   .getPid      ();
	
	    	impl_traceProcessInfo (aInfo);
	    	
	    	// ignore root of process tree (we are interested on childs only)
	    	if (nPid != m_nRootPid)
	    		mem_Pids ().add(nPid);
    	}
    	catch (Throwable ex)
    	{
    		// this is by intention !
    	}

    	return DO_NOT_KILL_PROCESS;
    }

    //--------------------------------------------------------------------------
    /** @return the list of child pids we found so far.
     *  Will be never null ... but can be empty.
     */
    public Set< Integer > getPids ()
    	throws Exception
    {
        return mem_Pids ();
    }

    //--------------------------------------------------------------------------
    private void impl_traceProcessInfo (final ProcessInfo aInfo)
    	throws Exception
    {
    	if ( ! LOG.isTraceEnabled())
    		return;
    	
    	final StringBuffer sDump = new StringBuffer (256);

    	sDump.append ("##### DUMP : ------------------------------------------------------\n");
    	sDump.append ("##### DUMP : this   pid  : "+new JavaSysMon ().currentPid()+"\n");
    	sDump.append ("##### DUMP : root   pid  : "+m_nRootPid+"\n");
    	sDump.append ("##### DUMP : proc   name : "+aInfo.getName      ()+"\n");
    	sDump.append ("##### DUMP : proc   cmd  : "+aInfo.getCommand   ()+"\n");
    	sDump.append ("##### DUMP : proc   pid  : "+aInfo.getPid       ()+"\n");
    	sDump.append ("##### DUMP : parent pid  : "+aInfo.getParentPid ()+"\n");
    	
    	LOG.trace(sDump.toString ());
    }
    
    //--------------------------------------------------------------------------
    private Set< Integer > mem_Pids ()
    	throws Exception
    {
    	if (m_lPids == null)
    		m_lPids = new HashSet< Integer >();
    	return m_lPids;
    }

    //--------------------------------------------------------------------------
    /// pid of the root process where we start our search for child processes
    private int m_nRootPid = GlobalPidProcessHandler.INVALID_PID;
    
    //--------------------------------------------------------------------------
    /// the set of child pids we found
    private Set< Integer > m_lPids = new HashSet< Integer >();
}