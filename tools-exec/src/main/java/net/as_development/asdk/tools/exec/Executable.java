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

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;

import com.jezhumble.javasysmon.JavaSysMon;
import com.jezhumble.javasysmon.OsProcess;
import com.jezhumble.javasysmon.ProcessInfo;

import net.as_development.asdk.tools.exec.impl.ExecutableStreamReader;
import net.as_development.asdk.tools.exec.impl.ExecutableWatch;
import net.as_development.asdk.tools.exec.impl.GlobalPidProcessHandler;

//==============================================================================
/**
 */
public class Executable implements Runnable
{
    //--------------------------------------------------------------------------
    public Executable ()
    {}
    
    //--------------------------------------------------------------------------
    /** define new working path for following process execution.
     *
     *  @param  sWorkingPath
     *          new working path.
     */
    public void setWorkingPath (final String sWorkingPath)
        throws Exception
    {
        // don't check if it exists here ...
        // its done before execution !
        m_sWorkingPath = sWorkingPath;
    }
    
    //--------------------------------------------------------------------------
    /** define new working path for following process execution.
     *
     *  @param  sExecutable
     *          new working path.
     */
    public void setExecutable (final String sExecutable)
        throws Exception
    {
        // don't check if it exists here ...
        // its done before execution !
        m_sExe = sExecutable;
    }
    
    //--------------------------------------------------------------------------
    public void setForward4StdOut (final OutputStream aForward)
        throws Exception
    {
    	m_aStdOutForward = new PrintStream(aForward);
    }

    //--------------------------------------------------------------------------
    public void setForward4StdErr (final OutputStream aForward)
        throws Exception
    {
    	m_aStdErrForward = new PrintStream(aForward);
    }

    //--------------------------------------------------------------------------
    /** clear list of all currently set command line arguments.
     */
    public void clearArguments ()
        throws Exception
    {
        mem_Arguments ().clear();
    }

    //--------------------------------------------------------------------------
    /** add new command line argument.
     * 
     *  @param  sArgument
     *          name of argument (may including "-" or "--" or ...)
     *          
     *  @param  sValue
     *          value for those argument
     *          In case argument does not have any value passing
     *          null or empty value will be allowed.
     */
    public void addArgument(final String sArgument,
                            final String sValue   )
        throws Exception
    {
        if ( ! StringUtils.isEmpty(sArgument))
            mem_Arguments ().add(sArgument);
        if ( ! StringUtils.isEmpty(sValue))
            mem_Arguments ().add(sValue);
    }
    
    //--------------------------------------------------------------------------
    public void addArgument(final String sArgument)
        throws Exception
    {
        if ( ! StringUtils.isEmpty(sArgument))
            mem_Arguments ().add(sArgument);
    }
    
    //--------------------------------------------------------------------------
    public void setEnvronmentVar(final String sVar  ,
    							 final String sValue)
        throws Exception
    {
        Validate.isTrue(StringUtils.isNotEmpty(sVar));
        mem_Environment ().put(sVar, sValue);
    }

    //--------------------------------------------------------------------------
    public void registerWatcher (final IExecutableWatcher iWatcher)
        throws Exception
    {
    	ExecutableWatch aWatch = mem_Watch ();
    	aWatch.addWatcher(iWatcher);
    }

    //--------------------------------------------------------------------------
    public void runAsync(final CountDownLatch aSync)
    	throws Exception
    {
    	synchronized(this)
    	{
            if (m_aProcess != null)
                throw new Exception ("Process still running.");

        	// accept null too !
        	// will be handled within run() method right ...

    		m_aSync = aSync;
    	}

    	new Thread(this).start ();
    }

    //--------------------------------------------------------------------------
    @Override
    public void run()
    {
        try
        {
	        if (isAlive ())
	        	return;
	        	
            final List< String > aCmdLine = impl_getCmdLine ();
            //System.out.println ("CMD LINE : "+aCmdLine);
            ProcessBuilder aBuilder = new ProcessBuilder (aCmdLine);
            
            aBuilder.environment().putAll(mem_Environment ());
            
            if ( ! StringUtils.isEmpty(m_sWorkingPath))
                aBuilder.directory(new File (m_sWorkingPath));
            
            ExecutableStreamReader aStdOut = mem_StdOut ();
            ExecutableStreamReader aStdErr = mem_StdErr ();

        	Mutable< Process > aProcess = new MutableObject< Process > ();
        	Mutable< Integer > nPid     = new MutableObject< Integer > ();

        	GlobalPidProcessHandler aPidHandler = GlobalPidProcessHandler.get();
        	aPidHandler.startChildProcess(aBuilder, aProcess, nPid);
        	
        	m_aProcess    = aProcess.getValue();
        	m_nProcessPid = nPid    .getValue();
        	
            aStdOut.bind2Input (m_aProcess.getInputStream());
            aStdErr.bind2Input (m_aProcess.getErrorStream());

        	aStdOut.bind2Output(mem_StdOutForward ());
            aStdErr.bind2Output(mem_StdErrForward ());

            aStdOut.bind2Watch (mem_Watch());
            aStdErr.bind2Watch (mem_Watch());
            
            new Thread (aStdOut).start();
            new Thread (aStdErr).start();

            int nResult = 0;
            synchronized (m_aProcess)
            {
                nResult = m_aProcess.waitFor();
            }
            
            m_nLastResult = nResult;
        }
        catch (Throwable ex)
        {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, ex.getMessage(), ex);
            m_nLastResult = 1;
        }
        
        if (m_aSync != null)
        	m_aSync.countDown();
    }
    
    //--------------------------------------------------------------------------
    public int getPid ()
    	throws Exception
    {
    	return m_nProcessPid;
    }
    
    //--------------------------------------------------------------------------
    public Long getMemResidentSetSize ()
    	throws Exception
    {
    	final Long nValue = ProcessUtils.getProcessResidentSetSize(m_nProcessPid);
    	return nValue;
    }
    
    //--------------------------------------------------------------------------
    public long getMemVirtualSize ()
    	throws Exception
    {
    	final Long nValue = ProcessUtils.getProcessResidentSetSize(m_nProcessPid);
    	return nValue;
    }

    //--------------------------------------------------------------------------
    public boolean isAlive ()
    	throws Exception
    {
    	if (m_aProcess == null)
    		return false;
    	
    	if (m_nProcessPid == GlobalPidProcessHandler.INVALID_PID)
    		return false;
    	
    	boolean bIsAlive = GlobalPidProcessHandler.get ().isChildProcessAlive(m_nProcessPid);
    	return  bIsAlive;
    }

    //--------------------------------------------------------------------------
    public void kill ()
        throws Exception
    {
    	if (m_nProcessPid != GlobalPidProcessHandler.INVALID_PID)
        	GlobalPidProcessHandler.get ().killChildProcess(m_nProcessPid);
    	else
    	if (m_aProcess != null)
    		m_aProcess.destroy();
    	
    	m_nProcessPid = GlobalPidProcessHandler.INVALID_PID;
    	m_aProcess    = null;
    	m_aStdOut     = null;
    	m_aStdErr     = null;
    }
    
    //--------------------------------------------------------------------------
    public int getLastResult ()
    	throws Exception
    {
    	return m_nLastResult;
    }
    
    //--------------------------------------------------------------------------
    @Override
    public String toString ()
    {
    	final StringBuffer sString = new StringBuffer (256);
    	
    	sString.append (super.toString ());
    	sString.append (" ["             );
    	sString.append (m_sWorkingPath   );
    	sString.append ("] "             );
    	sString.append (m_sExe           );
    	
    	try
    	{
	    	final List< String > lArgs = mem_Arguments ();
	    	for (final String sArg : lArgs)
	    	{
	    		sString.append (" " );
	    		sString.append (sArg);
	    	}
    	}
    	catch (final Throwable ex)
    	{}
    	
    	return sString.toString ();
    }
    
    //--------------------------------------------------------------------------
    private List< String > impl_getCmdLine ()
        throws Exception
    {
        List< String > lArgs    = mem_Arguments ();
        int            c        = lArgs.size();
        List< String > lCmdLine = new ArrayList< String >(c+1);
        
        lCmdLine.add   (m_sExe);
        lCmdLine.addAll(lArgs );
        
        return lCmdLine;
    }

    //--------------------------------------------------------------------------
    private List< String > mem_Arguments ()
        throws Exception
    {
        if (m_lArguments == null)
            m_lArguments = new ArrayList< String >(10);
        return m_lArguments;
    }
    
    //--------------------------------------------------------------------------
    private Map< String, String > mem_Environment () 
        throws Exception
    {
    	if (m_lEnvironment == null)
    		m_lEnvironment = new HashMap< String, String >(10);
    	return m_lEnvironment;
    }
    
    //--------------------------------------------------------------------------
    private ExecutableStreamReader mem_StdOut ()
        throws Exception
    {
        if (m_aStdOut == null)
            m_aStdOut = new ExecutableStreamReader ();
        return m_aStdOut;
    }
    
    //--------------------------------------------------------------------------
    private ExecutableStreamReader mem_StdErr ()
        throws Exception
    {
        if (m_aStdErr == null)
            m_aStdErr = new ExecutableStreamReader ();
        return m_aStdErr;
    }

    //--------------------------------------------------------------------------
    private PrintStream mem_StdOutForward ()
        throws Exception
    {
        if (m_aStdOutForward == null)
        	m_aStdOutForward = System.out;
        return m_aStdOutForward;
    }

    //--------------------------------------------------------------------------
    private PrintStream mem_StdErrForward ()
        throws Exception
    {
        if (m_aStdErrForward == null)
        	m_aStdErrForward = System.err;
        return m_aStdErrForward;
    }

    //--------------------------------------------------------------------------
    private ExecutableWatch mem_Watch ()
        throws Exception
    {
    	if (m_aWatch == null)
    		m_aWatch = new ExecutableWatch ();
    	return m_aWatch;
    }
    
    //--------------------------------------------------------------------------
    private String m_sWorkingPath = null;
    
    //--------------------------------------------------------------------------
    private String m_sExe = null;
    
    //--------------------------------------------------------------------------
    private List< String > m_lArguments = null;
    
    //--------------------------------------------------------------------------
    private Map< String, String > m_lEnvironment = null;

    //--------------------------------------------------------------------------
    private Process m_aProcess = null;
    
    //--------------------------------------------------------------------------
    private int m_nProcessPid = GlobalPidProcessHandler.INVALID_PID;
    
    //--------------------------------------------------------------------------
    private ExecutableStreamReader m_aStdOut = null;
    
    //--------------------------------------------------------------------------
    private ExecutableStreamReader m_aStdErr = null;
    
    //--------------------------------------------------------------------------
    private int m_nLastResult = 0;

    //--------------------------------------------------------------------------
    private CountDownLatch m_aSync = null;

    //--------------------------------------------------------------------------
    private ExecutableWatch m_aWatch = null;

    //--------------------------------------------------------------------------
    private PrintStream m_aStdOutForward = null;

    //--------------------------------------------------------------------------
    private PrintStream m_aStdErrForward = null;
}
