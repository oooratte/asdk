package net.as_development.asdk.distributed_cache.impl.channel;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.apache.commons.lang3.StringUtils;

import net.as_development.asdk.distributed_cache.DistributedCacheConfig;
import net.as_development.asdk.distributed_cache.impl.Message;
import net.as_development.asdk.tools.common.pattern.observation.ObservableBase;

//=============================================================================
public class MulticastChannel extends    ObservableBase< Message >
							  implements IChannel
{
	//-------------------------------------------------------------------------
	public MulticastChannel ()
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
		if (m_aSocket != null)
			return;
		
		final String                 sME      = m_sSenderId;
		final DistributedCacheConfig aConfig  = m_aConfig;
		final String                 sAddress = aConfig.getAddress();
		final int                    nPort    = aConfig.getPort   ();
		final InetAddress            aAddress = InetAddress.getByName(sAddress);
		final MulticastSocket        aSocket  = new MulticastSocket(nPort);

		aSocket.setTimeToLive       (1              );
		aSocket.setReuseAddress     (true           );
		aSocket.setReceiveBufferSize(Message.BUFSIZE);
		aSocket.setSoTimeout        (15000          );
		aSocket.setLoopbackMode     (false          ); // do not want to read own data packets ;-)
		aSocket.joinGroup           (aAddress       );
		
		System.out.println ("["+sME+"] connected to '"+sAddress+":"+nPort+"'");
		m_aSocket  = aSocket ;
		m_aAddress = aAddress;
		m_nPort    = nPort   ;
		
		impl_listen ();
	}
	
	//-------------------------------------------------------------------------
	@Override
	public synchronized void disconnect ()
	    throws Exception
	{
		if (m_aSocket == null)
			return;
		
		final String            sME      = m_sSenderId;
		final MulticastSocket   aSocket  = m_aSocket;
		final String            sAddress = m_aAddress.getHostAddress();
		final int               nPort    = m_nPort;
		                      m_aSocket  = null;
		                      m_aAddress = null;
		                      m_nPort    = null;
		
		if (aSocket != null)
			aSocket.close();

		System.out.println ("["+sME+"] disconnected from '"+sAddress+":"+nPort+"'");
	}
	
	//-------------------------------------------------------------------------
	@Override
	public synchronized void send (final Message aMsg)
		throws Exception
	{
		final String sME = m_sSenderId;
		if (m_aSocket == null)
			throw new RuntimeException ("["+sME+"] Not connected.");

		aMsg.setSender(sME);

		final String         sMsg  = Message.serialize(aMsg);
		final DatagramPacket aData = new DatagramPacket(sMsg.getBytes(),sMsg.length(), m_aAddress, m_nPort);
		System.out.println("... ["+sME+"] send out : '"+sMsg+"'");
		m_aSocket.send(aData);
	}
	
	//-------------------------------------------------------------------------
	private synchronized void impl_listen ()
		throws Exception
	{
		if (m_aInQueue != null)
			return;

		final MulticastSocket aSocket  = m_aSocket;
		final String          sME      = m_sSenderId;
		final Thread          aInQueue = new Thread ()
		{
			@Override
			public void run ()
			{
				try
				{
					while (true)
					{
						final byte[]         lBuf  = new byte[Message.BUFSIZE];
						final DatagramPacket aData = new DatagramPacket (lBuf, lBuf.length);
						
						impl_receive (aSocket, aData);

				        final int     nBytes = aData.getLength ();
				        final byte[]  lBytes = aData.getData   ();
				        final String  sMsg   = new String(lBytes, 0, nBytes);
				        final Message aMsg   = Message.deserialize(sMsg);

				        if (StringUtils.equals(aMsg.getSender(), sME))
			        		continue;
				        
				        fire (aMsg);
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
		
		aInQueue.start();
		m_aInQueue = aInQueue;
	}
	
	//-------------------------------------------------------------------------
	private void impl_receive (final MulticastSocket aSocket,
							   final DatagramPacket  aData  )
	    throws Exception
	{
		while (true)
		{
			try
			{
				aSocket.receive(aData);
				return;
			}
			catch (final SocketTimeoutException exTimeout)
			{}
		}
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
	private MulticastSocket m_aSocket = null;

	//-------------------------------------------------------------------------
	private Thread m_aInQueue = null;
}
