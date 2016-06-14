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
package net.as_development.asdk.distributed_cache.impl.channel;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;

import net.as_development.asdk.distributed_cache.DistributedCacheConfig;
import net.as_development.asdk.distributed_cache.impl.ERunMode;
import net.as_development.asdk.distributed_cache.impl.Message;
import net.as_development.asdk.tools.common.pattern.observation.ObservableBase;

//=============================================================================
public class UnicastChannel extends    ObservableBase< Message >
							  implements IChannel
{
	//-------------------------------------------------------------------------
	public UnicastChannel ()
	{}

	//-------------------------------------------------------------------------
	@Override
	public synchronized void setSenderId (final String sId)
	    throws Exception
	{
		m_sSenderId = sId;
	}
	
	//-------------------------------------------------------------------------
	@Override
	public synchronized void configure (final DistributedCacheConfig aConfig)
		throws Exception
	{
		m_aConfig = aConfig;
	}

	//-------------------------------------------------------------------------
	@Override
	public synchronized void connect ()
	    throws Exception
	{
		if (
			(m_aServerSocket != null) ||
			(m_aClientSocket != null)
		   )
			return;
		
		final String                 sME      = m_sSenderId;
		final DistributedCacheConfig aConfig  = m_aConfig;
		final ERunMode               eRunMode = aConfig.getRunMode();
		final String                 sAddress = aConfig.getAddress();
		final int                    nPort    = aConfig.getPort   ();
		final InetAddress            aAddress = InetAddress.getByName(sAddress);
		
		if (eRunMode == ERunMode.E_SERVER)
		{
			final ServerSocket aServerSocket = new ServerSocket (nPort);
			impl_startServer  (aServerSocket);
			m_aServerSocket =  aServerSocket;
		}
		else
		if (eRunMode == ERunMode.E_CLIENT)
		{
			final Socket aClientSocket = new Socket (sAddress, nPort);
			m_aClientSocket = aClientSocket;
		}
		
//		System.out.println ("["+sME+"] connected to '"+sAddress+":"+nPort+"'");
		m_aAddress = aAddress;
		m_nPort    = nPort   ;
	}

	//-------------------------------------------------------------------------
	@Override
	public synchronized void disconnect ()
	    throws Exception
	{
		if (
			(m_aServerSocket == null) &&
			(m_aClientSocket == null)
		   )
			return;

		final ServerSocket aServerSocket = m_aServerSocket;
		                 m_aServerSocket = null;
		if (aServerSocket != null)
			aServerSocket.close();

		final Socket aClientSocket = m_aClientSocket;
		           m_aClientSocket = null;
		if (aClientSocket != null)
			aClientSocket.close();
	}
	
	//-------------------------------------------------------------------------
	@Override
	public synchronized void send (final Message aMsg)
		throws Exception
	{
		if (m_aServerSocket != null)
			return; // server cant be used for sending data ...
		
		final String sME = m_sSenderId;
		if (m_aClientSocket == null)
			throw new RuntimeException ("["+sME+"] Not connected.");

		aMsg.setSender(sME);
		final String sMsg = Message.serialize(aMsg);

		for (int i=0; i<10; ++i)
		{
			try
			{
				final boolean NO_FORCE = false;
				reconnect (NO_FORCE);
				
				if (m_aClientOut == null)
					m_aClientOut = new DataOutputStream (m_aClientSocket.getOutputStream());
		
				m_aClientOut.writeUTF(sMsg);
				break;
			}
			catch (final Throwable ex)
			{
				final boolean FORCE = true;
				reconnect(FORCE);
			}
		}
	}
	
	//-------------------------------------------------------------------------
	private synchronized void reconnect (final boolean bForced)
	    throws Exception
	{
		final DistributedCacheConfig aConfig  = m_aConfig;
		final ERunMode               eRunMode = aConfig.getRunMode();

		if (eRunMode == ERunMode.E_SERVER)
		{
			if (bForced)
			{
				disconnect();
			}
			else
			if (
				(m_aServerSocket != null    ) &&
				(m_aServerSocket.isClosed ())
			   )
			{
				m_aServerSocket = null;
			}

			// no else
			if (m_aServerSocket == null)
				connect ();
		}
		else
		if (eRunMode == ERunMode.E_CLIENT)
		{
			if (bForced)
			{
				disconnect();
			}
			else
			if (
				(m_aClientSocket != null    ) &&
				(m_aClientSocket.isClosed ())
			   )
			{
				m_aClientSocket = null;
			}
			
			// no else
			if (m_aClientSocket == null)
				connect ();
		}
	}
		
	//-------------------------------------------------------------------------
	private synchronized void impl_startServer (final ServerSocket aSocket)
		throws Exception
	{
		if (m_aServerThread != null)
			return;

		final Thread aAcceptThread = new Thread ()
		{
			@Override
			public void run ()
			{
				try
				{
					while (true)
					{
						final Socket aClient = aSocket.accept();
						impl_handleClient (aClient);
					}
				}
				catch (Throwable ex)
				{
					if (SocketException.class.isAssignableFrom(ex.getClass()))
					{
						if (StringUtils.containsIgnoreCase(ex.getMessage(), "socket closed"))
							return;
					}

					System.err.println(ex.getMessage ());
					ex.printStackTrace(System.err      );
				}
			}
		};
		
		aAcceptThread.start();
		m_aServerThread = aAcceptThread;
	}
	
	//-------------------------------------------------------------------------
	private void impl_handleClient (final Socket aSocket)
	    throws Exception
	{
//		System.out.println("server : new client connected ...");
		
		final String          sME         = m_sSenderId;
		final ExecutorService aThreadPool = mem_ClientThreadPool ();
		final Runnable        aReceiver   = new Runnable ()
		{
			@Override
			public void run ()
			{
				try
				{
					final DataInputStream aIn = new DataInputStream(aSocket.getInputStream());

					while (true)
					{
						final String  sMsg = aIn.readUTF();
						final Message aMsg = Message.deserialize(sMsg);

				        if (StringUtils.equals(aMsg.getSender(), sME))
			        		continue;
				        
				        fire (aMsg);
					}
				}
				catch (EOFException exEOF)
				{
 					// expected ;-)
				}
				catch (Throwable ex)
				{
					System.err.println(ex.getMessage ());
					ex.printStackTrace(System.err      );
				}
			}
		};
		
		aThreadPool.submit(aReceiver);
	}

	//-------------------------------------------------------------------------
	private synchronized ExecutorService mem_ClientThreadPool ()
	    throws Exception
	{
		if (m_aClientThreadPool == null)
			m_aClientThreadPool = Executors.newCachedThreadPool();
		return m_aClientThreadPool;
	}

	//-------------------------------------------------------------------------
	private String m_sSenderId = null;
	
	//-------------------------------------------------------------------------
	private DistributedCacheConfig m_aConfig = null;
	
	//-------------------------------------------------------------------------
	private InetAddress m_aAddress = null;

	//-------------------------------------------------------------------------
	private Integer m_nPort = null;

	//-------------------------------------------------------------------------
	private ServerSocket m_aServerSocket = null;

	//-------------------------------------------------------------------------
	private Socket m_aClientSocket = null;

	//-------------------------------------------------------------------------
	private DataOutputStream m_aClientOut = null;

	//-------------------------------------------------------------------------
	private Thread m_aServerThread = null;

	//-------------------------------------------------------------------------
	private ExecutorService m_aClientThreadPool = null;
}
