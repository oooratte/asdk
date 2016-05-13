package net.as_development.asdk.distributed_cache;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import net.as_development.asdk.distributed_cache.impl.Message;
import net.as_development.asdk.distributed_cache.impl.Set;

//=============================================================================
public class DistributedCache
{
	//-------------------------------------------------------------------------
	public DistributedCache ()
	    throws Exception
	{}

	//-------------------------------------------------------------------------
	public synchronized DistributedCacheConfig configure ()
		throws Exception
	{
		return mem_Config ();
	}

	//-------------------------------------------------------------------------
	public synchronized void connect ()
	    throws Exception
	{
		if (m_aSocket != null)
			return;
		
		final String                 sME      = mem_MyId   ();
		final DistributedCacheConfig aConfig  = mem_Config ();
		final String                 sAddress = aConfig.getMulticastAddress();
		final int                    nPort    = aConfig.getMulticastPort   ();
		final InetAddress            aAddress = InetAddress.getByName(sAddress); 
		final MulticastSocket        aSocket  = new MulticastSocket(nPort);

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
	public synchronized void disconnect ()
	    throws Exception
	{
		if (m_aSocket == null)
			return;
		
		final String            sME      = mem_MyId ();
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
	public synchronized void disconnectQuietly ()
	{
		try
		{
			disconnect ();
		}
		catch (Throwable ex)
		{}
	}

	//-------------------------------------------------------------------------
	public synchronized void set (final String sKey  ,
								  final String sValue)
	    throws Exception
	{
		final Map< String, String > aCache    = mem_Cache ();
		final String                sOldValue = aCache.get(sKey);
		final boolean               bChanged  = ! StringUtils.equals(sValue, sOldValue);
		
		if ( ! bChanged)
			return;

		aCache.put(sKey, sValue);
		
		final Message aSet = Set.newSet(sKey, sValue);
		impl_send (aSet);
	}

	//-------------------------------------------------------------------------
	public synchronized String get (final String sKey)
	    throws Exception
	{
		final Map< String, String > aCache = mem_Cache ();
		final String                sValue = aCache.get(sKey);
		return sValue;
	}

	//-------------------------------------------------------------------------
	public synchronized Map< String, String > get (final List< String > lKeys)
	    throws Exception
	{
		final Map< String, String > aCache  = mem_Cache ();
		final Map< String, String > aResult = new HashMap< String, String > ();
		
		for (final String sKey : lKeys)
		{
			final String sValue = aCache.get(sKey);
			aResult.put(sKey, sValue);
		}
		
		return aResult;
	}

	//-------------------------------------------------------------------------
	public synchronized List< String > listAll ()
	    throws Exception
	{
		final Map< String, String > aCache = mem_Cache ();
		final List< String >        lAll   = new ArrayList< String > ();
		lAll.addAll(aCache.keySet());
		return lAll;
	}

	//-------------------------------------------------------------------------
	public /* no synchronized */ List< String > listSubSet (final String sRegEx)
	    throws Exception
	{
		final List< String > lAll    = listAll ();
		final List< String > lSubSet = new ArrayList< String > ();
		
		for (final String sKey : lAll)
		{
			if (sKey.matches(sRegEx))
				lSubSet.add(sKey);
		}
		
		return lSubSet;
	}

	//-------------------------------------------------------------------------
	private void impl_send (final Message aMsg)
		throws Exception
	{
		final String sME = mem_MyId ();
		if (m_aSocket == null)
			throw new RuntimeException ("["+sME+"] Not connected.");

		aMsg.setSender(sME);

		final String         sMsg  = Message.serialize(aMsg);
		final DatagramPacket aData = new DatagramPacket(sMsg.getBytes(),sMsg.length(), m_aAddress, m_nPort);
		m_aSocket.send(aData);
	}
	
	//-------------------------------------------------------------------------
	private synchronized void impl_listen ()
		throws Exception
	{
		if (m_aInQueue != null)
			return;

		final MulticastSocket aSocket  = m_aSocket;
		final String          sME      = mem_MyId (); // needs to be retrieved here ... not inside thread !
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
						aSocket.receive(aData);

				        final int     nBytes = aData.getLength ();
				        final byte[]  lBytes = aData.getData   ();
				        final String  sMsg   = new String(lBytes, 0, nBytes);
				        final Message aMsg   = Message.deserialize(sMsg);

				        if (StringUtils.equals(sME, aMsg.getSender()))
				        	continue;

				        final String sAction = aMsg.getAction();
				        if (StringUtils.equals(sAction, Message.ACTION_SET))
				        	impl_doSet (aMsg);
				        else
				        	throw new UnsupportedOperationException ("No support for '"+sAction+"' implemented yet.");
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
	private synchronized void impl_doSet (final Message aMsg)
	    throws Exception
	{
		final String                sME    = mem_MyId  ();
		final Map< String, String > aCache = mem_Cache ();
		final Set                   aSet   = Set.fromMessage(aMsg);
		final String                sKey   = aSet.getKey  ();
		final String                sValue = aSet.getValue();

		System.out.println("["+sME+"] set : '"+sKey+"' = '"+sValue+"'");
		aCache.put(sKey, sValue);
	}
	
	//-------------------------------------------------------------------------
	private synchronized DistributedCacheConfig mem_Config ()
	    throws Exception
	{
		if (m_aConfig == null)
			m_aConfig = new DistributedCacheConfig ();
		return m_aConfig;
	}

	//-------------------------------------------------------------------------
	private synchronized String mem_MyId ()
	    throws Exception
	{
		if (m_sMyId == null)
			m_sMyId = UUID.randomUUID().toString();
		return m_sMyId;
	}

	//-------------------------------------------------------------------------
	private synchronized Map< String, String > mem_Cache ()
	    throws Exception
	{
		if (m_aCache == null)
			m_aCache = new HashMap< String, String > ();
		return m_aCache;
	}

	//-------------------------------------------------------------------------
	private String m_sMyId = null;
	
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

	//-------------------------------------------------------------------------
	private Map< String, String > m_aCache = null;
}
