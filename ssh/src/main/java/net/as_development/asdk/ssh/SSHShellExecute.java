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
package net.as_development.asdk.ssh;

import com.jcraft.jsch.ChannelExec;

/**
 * @todo document me
 */
public class SSHShellExecute
{
    //--------------------------------------------------------------------------
    private static final int POLL_TIME_4_EXECUTE = 1000;

    //--------------------------------------------------------------------------
    /** create new instance.
     */
    public SSHShellExecute ()
        throws Exception
    {}

    //--------------------------------------------------------------------------
    protected void bind (final SSHServer aServer)
        throws Exception
    {
        m_aServer = aServer;
    }
    
    //--------------------------------------------------------------------------
    public int execute (final String sCommand)
        throws Exception
    {
    	System.out.println("exec '"+sCommand+"' ...");
    	
    	final SSHConnection      aConnection  = m_aServer.accessConnection();
    	final SSHStdOutErrSinkV2 aStdOutErr   = m_aServer.accessStdOutErr ();
    	final SSHIdentity        aIdentity    = m_aServer.accessIdentity  ();
    	final boolean            bNeedsSudo   = aIdentity.needsSudo();
        final ChannelExec        aShell       = (ChannelExec) aConnection.openChannel("exec");

        String sRealCmd = sCommand;
        if (bNeedsSudo)
        	sRealCmd = "sudo -i -H sh -c '"+sCommand+"'";
        	
        aStdOutErr.bind       (aShell  );
        aShell    .setPty     (true    );
        aShell    .setCommand (sRealCmd);

        aShell.connect ();
        
        while( ! aShell.isClosed())
        {
        	try
        	{
        		Thread.sleep(SSHShellExecute.POLL_TIME_4_EXECUTE);
        	}
        	catch(Exception ex)
        	{}
        }
        
        int nResult = aShell.getExitStatus();
        aShell.disconnect();
        aStdOutErr.unbind();

        m_aServer.doAutoFailIfNeeded(nResult);
        
        return nResult;
    }

    //--------------------------------------------------------------------------
    private SSHServer m_aServer = null;
}
