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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.mutable.Mutable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jezhumble.javasysmon.JavaSysMon;
import com.jezhumble.javasysmon.OsProcess;

//==============================================================================
public class GlobalPidProcessHandler
{
	//--------------------------------------------------------------------------
	private static final Logger LOG = LoggerFactory.getLogger(GlobalPidProcessHandler.class);

	//--------------------------------------------------------------------------
	public static final int INVALID_PID = -1;

	//--------------------------------------------------------------------------
	/// force using of factory method to ensure singleton instance !
	private GlobalPidProcessHandler ()
	{}

	//--------------------------------------------------------------------------
	public static synchronized GlobalPidProcessHandler get ()
		throws Exception
	{
		if (m_gSingleton == null)
			m_gSingleton = new GlobalPidProcessHandler ();
		return m_gSingleton;
	}

	//--------------------------------------------------------------------------
    public synchronized void startChildProcess (final ProcessBuilder     aBuilder,
    								            final Mutable< Process > aProcess,
    								            final Mutable< Integer > nPid    )
    	throws Exception
    {
        final List< Integer > lChildPidsBefore = impl_getChildPidsOfThisProcess ();
        final Process         aNewProcess      = aBuilder.start();
        final List< Integer > lChildPidsAfter  = impl_getChildPidsOfThisProcess ();
        final int             nNewPid          = impl_getPidOfNewStartedProcess (lChildPidsBefore, lChildPidsAfter);
        final List< Integer > lAllChildPids    = mem_AllChildPids ();

        lAllChildPids.clear ();
        lAllChildPids.addAll(lChildPidsAfter);
        
    	aProcess.setValue (aNewProcess);
    	nPid    .setValue (nNewPid    );
    }

    //--------------------------------------------------------------------------
    public synchronized void killChildProcess (final int nPid)
    	throws Exception
    {
    	Validate.isTrue(nPid != INVALID_PID, "invalid argument 'pid'");
    	
        JavaSysMon aMonitor = new JavaSysMon();
        aMonitor.killProcess(nPid);
        impl_forgetChildPid (nPid);
    }
	
    //--------------------------------------------------------------------------
    public synchronized void killAllChilds ()
    	throws Exception
    {
    	final List< Integer > lChilds = new ArrayList< Integer > (mem_AllChildPids ());
    	for (final Integer nPid : lChilds)
    		killChildProcess (nPid);
    }

    //--------------------------------------------------------------------------
    public synchronized boolean isChildProcessAlive (final int nPid)
    	throws Exception
    {
    	if (nPid == GlobalPidProcessHandler.INVALID_PID)
    		return false;
    	
    	final JavaSysMon aMonitor  = new JavaSysMon();
    	final OsProcess  aTreeRoot = aMonitor.processTree();
    	final OsProcess  aProcess  = aTreeRoot.find(nPid);
    	
    	if (aProcess == null)
    	{
            impl_forgetChildPid (nPid);
    		return false;
    	}
    	return true;
    }

	//--------------------------------------------------------------------------
	public synchronized List< Integer > getAllChildPids ()
	     throws Exception
	{
		return mem_AllChildPids ();
	}

	//--------------------------------------------------------------------------
    private List< Integer > impl_getChildPidsOfThisProcess ()
    	throws Exception
    {
	    final JavaSysMon          aMonitor = new JavaSysMon();
	    final ProcessChildVisitor aVisitor = new ProcessChildVisitor();
	    final int                 nThisPid = aMonitor.currentPid();

	    aVisitor.setRootPid      (nThisPid);
	    aMonitor.visitProcessTree(nThisPid, aVisitor);
	    
	    final List< Integer > lChildPids = new ArrayList< Integer >(aVisitor.getPids());
	    return lChildPids;
    }

    //--------------------------------------------------------------------------
    private int impl_getPidOfNewStartedProcess (final List< Integer > lPidsBefore,
			   									final List< Integer > lPidsAfter)
    	throws Exception
    {
    	final List< Integer > aDiff = impl_diffChildPids (lPidsBefore, lPidsAfter);

    	if (aDiff.size() < 1)
    	{
    		LOG.trace ("no pid in diff ...");
    		return INVALID_PID;
    	}
    	else
    	if (aDiff.size() > 1)
    	{
    		LOG.trace ("to many pids in diff ["+aDiff+"] ...");
    		return INVALID_PID;
    	}
    	
    	final int nPid = aDiff.get(0);
    	if (nPid < 1)
    	{
    		LOG.trace ("invalid pid '"+nPid+"' in diff ...");
    		return INVALID_PID;
    	}

    	return nPid;
    }
    
    //--------------------------------------------------------------------------
	private List< Integer > impl_diffChildPids (final List< Integer > lPidsBefore,
    										    final List< Integer > lPidsAfter )
    	throws Exception
    {
    	
    	final List< Integer > lDiff = new ArrayList< Integer > (lPidsAfter);
    	lDiff.removeAll(lPidsBefore);

		LOG.trace ("diff (before) : "+lPidsBefore);
		LOG.trace ("diff (after ) : "+lPidsAfter);
		LOG.trace ("diff (result) : "+lDiff  );

    	return lDiff;
    }
	
	//--------------------------------------------------------------------------
    private void impl_forgetChildPid (final int nPid)
    	throws Exception
    {
        final List< Integer > lAllChildPids = mem_AllChildPids ();
        final Integer         nPidItem      = new Integer (nPid); // if you use nPid directly you call container.remove(index) instead of container.remove(item) ! .-)

        if (lAllChildPids.contains(nPidItem))
        	lAllChildPids.remove(nPidItem);
    }
    
	//--------------------------------------------------------------------------
	private List< Integer > mem_AllChildPids ()
	     throws Exception
	{
		if (m_lAllChildPids == null)
			m_lAllChildPids = new ArrayList< Integer > ();
		return m_lAllChildPids;
	}

	//--------------------------------------------------------------------------
	private static GlobalPidProcessHandler m_gSingleton = null;

	//--------------------------------------------------------------------------
	private List< Integer > m_lAllChildPids = null;
}
