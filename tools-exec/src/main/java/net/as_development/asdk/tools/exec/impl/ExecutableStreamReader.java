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

import java.io.InputStream;
import java.io.PrintStream;
import java.lang.ref.WeakReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

//==============================================================================
public class ExecutableStreamReader implements Runnable
{
    //--------------------------------------------------------------------------
    private static final int BUFFSIZE = 4096;
    
    //--------------------------------------------------------------------------
    public ExecutableStreamReader ()
    {
    }
    
    //--------------------------------------------------------------------------
    public synchronized void bind2Input (final InputStream aIn)
        throws Exception
    {
        if (m_aIn != null)
            m_aIn.close();
        m_aIn = aIn;
    }
    
    //--------------------------------------------------------------------------
    public synchronized void bind2Output (final PrintStream aOut)
    	throws Exception
    {
    	m_aOut = aOut;
    }
    
    //--------------------------------------------------------------------------
    public synchronized void bind2Watch (final ExecutableWatch aWatch)
    	throws Exception
    {
    	m_rWatch = new WeakReference< ExecutableWatch >(aWatch);
    }

    //--------------------------------------------------------------------------
    @Override
    public void run ()
    {
        InputStream aIn  = null;
        try
        {
            PrintStream aOut = null;
            synchronized(this)
            {
                aIn  = m_aIn ;
                aOut = m_aOut;
            }
            
            byte[] lBuffer = new byte[ExecutableStreamReader.BUFFSIZE];
            int    nRead   = 0;
            while ((nRead = aIn.read(lBuffer)) > 0)
            {
                String sOut = new String (lBuffer, 0, nRead);

                if (aOut != null)
                {
                	aOut.print(sOut);
                	aOut.flush();
                }
                
                final ExecutableWatch aWatch = m_rWatch.get ();
                if (aWatch != null)
                {
                	aWatch.scanForWatches(sOut);
                	
                	if (aWatch.allWatchPointsDetected())
                		m_rWatch.clear();
                }
            }
        }
        catch (Throwable ex)
        {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, ex.getMessage(), ex);
        }
        finally
        {
            IOUtils.closeQuietly(aIn);
        }
    }
    
    //--------------------------------------------------------------------------
    private InputStream m_aIn = null;
    
    //--------------------------------------------------------------------------
    private PrintStream m_aOut = null;

    //--------------------------------------------------------------------------
    private WeakReference< ExecutableWatch > m_rWatch = new WeakReference< ExecutableWatch >(null);
}