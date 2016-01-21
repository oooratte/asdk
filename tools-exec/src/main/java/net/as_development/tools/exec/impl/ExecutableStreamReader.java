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