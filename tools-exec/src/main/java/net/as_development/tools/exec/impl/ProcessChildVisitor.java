/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package net.as_development.tools.exec.impl;

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