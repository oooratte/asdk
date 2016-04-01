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
package net.as_development.asdk.sdt;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.as_development.asdk.sdt.desc.AuthenticationDescriptor;
import net.as_development.asdk.sdt.desc.NodeDescriptor;
import net.as_development.asdk.sdt.impl.TaskDeployFrameworkCore;
import net.as_development.asdk.sdt.impl.TaskStart;
import net.as_development.asdk.sdt.impl.TaskStop;
import net.as_development.asdk.ssh.SSHIdentity;
import net.as_development.asdk.ssh.SSHServer;

//=============================================================================
/** define a node within a SDT setup.
 * 
 *  A node is the representation of a VM or a real machine.
 *  A node can be configured by setting an instance of {link NodeDescriptor} to it.
 *  A node can be filled with a list of tasks (instances of {link TaskBase}).
 *  Every task within this node implement a deployment step.
 */
public class Node
{
    //-------------------------------------------------------------------------
	public Node ()
	    throws Exception
	{}

	//-------------------------------------------------------------------------
	/** called from the outside framework to make the global SDT instance available
	 *  to this node.
	 *  
	 *  Has not to be called from outside.
	 * 
	 *	@param	aSDT [IN]
	 *			the global SDT instance bound to this node.
	 */
	protected void bind (final SDT aSDT)
		throws Exception
	{
		m_rSDT = new WeakReference< SDT > (aSDT);
	}
	
	//-------------------------------------------------------------------------
	/** configure this node.
	 * 
	 *  YOU have to define a suitable NodeDescriptor for this node
	 *  and set it here. It's provided to all added tasks at deployment time
	 *  so they can access those configuration values.
	 * 
	 *  @param	aDesc [IN]
	 *  		the new node configuration
	 */
	public void configure (final NodeDescriptor aDesc)
	    throws Exception
	{
		m_aDesc = aDesc;
	}

	//-------------------------------------------------------------------------
	public String getId ()
		throws Exception
	{
		if (m_aDesc != null)
			return m_aDesc.sId;
		else
			return "";
	}
	
	//-------------------------------------------------------------------------
	public void setId (final String sId)
		throws Exception
	{
		if (m_aDesc != null)
			m_aDesc.sId = sId;
	}

	//-------------------------------------------------------------------------
	/** add a new deployment task to this node.
	 * 
	 *  Each task added here will implement one deployment step.
	 *  The order of tasks is important and will be guaranteed.
	 *  Double registration of tasks is forbidden and will be filtered out !
	 * 
	 *  see {link Taskbase}
	 * 
	 *	@param	lNewTasks [IN]
	 *			the list of new deployment tasks.
	 */
	public void addTasks (final TaskBase... lNewTasks)
		throws Exception
	{
		for (final TaskBase aNewTask : lNewTasks)
		{
			final Class< ? > aTaskClass = aNewTask.getClass();

			if (TaskStart.class.isAssignableFrom(aTaskClass))
				impl_addStartTasks  (aNewTask);
			else
			if (TaskStop.class.isAssignableFrom(aTaskClass))
				impl_addStopTasks   (aNewTask);
			else
				impl_addDeployTasks (aNewTask);
		}
	}

	//-------------------------------------------------------------------------
	/** starts the deployment.
	 * 
	 *  It's called internally. Dont call it from outside.
	 *  All exceptions has to be thrown out (do not catch them).
	 *  Errors will be handled outside.
	 *  
	 *  First failed task will fail the whole deployment of this node ...
	 *  but might not tsop the deployment of other nodes.
	 * 
	 *  @throws Exception
	 */
	public void deploy ()
		throws Exception
	{
		final SSHServer aSSH = mem_SSH ();
		aSSH.connect ();
		
		try
		{
			final List< TaskBase > lTasks = mem_DeployTasks ();
			for (final TaskBase aTask : lTasks)
				aTask.execute(this);
		}
		finally
		{
			aSSH.disconnect();
		}
	}

	//-------------------------------------------------------------------------
	public void start ()
	    throws Exception
	{
		final Map< Integer, List< TaskStart >> aRegistry = mem_StartTasks ();
		
		for (int nLevel=0; nLevel<10; ++nLevel)
		{
			final List< TaskStart > lTasks4Level = aRegistry.get(nLevel);
			if (lTasks4Level == null)
				continue;

			for (final TaskStart aTask : lTasks4Level)
			{
				aTask.execute(this);
			}
		}
	}
	
	//-------------------------------------------------------------------------
	public void stop ()
	    throws Exception
	{
		final Map< Integer, List< TaskStop >> aRegistry = mem_StopTasks ();
		
		for (int nLevel=0; nLevel<10; ++nLevel)
		{
			final List< TaskStop > lTasks4Level = aRegistry.get(nLevel);
			if (lTasks4Level == null)
				continue;

			for (final TaskStop aTask : lTasks4Level)
			{
				aTask.execute(this);
			}
		}
	}

	//-------------------------------------------------------------------------
	/** provides read access to the global SDT environment.
	 * 
	 *  @return a reference to the SDT instance owning this node.
	 */
	public SDT accessSDT ()
	    throws Exception
	{
		return m_rSDT.get();
	}

	//-------------------------------------------------------------------------
	/** @return a reference to the configuration descriptor of this node.
	 * 
	 *  As it's a reference only - don't change it's values. Read them only ...
	 */
	public NodeDescriptor accessDescriptor ()
	    throws Exception
	{
		return m_aDesc;
	}

	//-------------------------------------------------------------------------
	/** provides access to the underlying SSH connection.
	 * 
	 *  It's the low level API of SDT.
	 *  Needed if the SDT framework do not provide a functionality ...
	 *  you want to have.
	 *  
	 *  Note: the SSH connection is already open.
	 *  Use it - but do not open/close it !
	 * 
	 *  @return	the low level SSH access.
	 */
	public SSHServer accessSSH ()
	    throws Exception
	{
		return mem_SSH ();
	}

	//-------------------------------------------------------------------------
	private void impl_addDeployTasks (final TaskBase... lNewTasks)
		throws Exception
	{
		final List< TaskBase > lTasks = mem_DeployTasks ();
		
		for (final TaskBase aNewTask : lNewTasks)
		{
			if ( ! lTasks.contains(aNewTask))
				lTasks.add(aNewTask);
		}
	}
	
	//-------------------------------------------------------------------------
	private void impl_addStartTasks (final TaskBase... lNewTasks)
		throws Exception
	{
		final Map< Integer, List< TaskStart >> aRegistry = mem_StartTasks ();
		
		for (final TaskBase aNewTask : lNewTasks)
		{
			final TaskStart         aStartTask   = (TaskStart) aNewTask;
			final int               nRunLevel    = aStartTask.getRunLevel();
				  List< TaskStart > lTasks4Level = aRegistry.get(nRunLevel);

			if (lTasks4Level == null)
			{
				lTasks4Level = new ArrayList< TaskStart > ();
				aRegistry.put(nRunLevel, lTasks4Level);
			}
			
			if ( ! lTasks4Level.contains(aStartTask))
			{
				lTasks4Level.add(aStartTask);
			}
		}
	}

	//-------------------------------------------------------------------------
	private void impl_addStopTasks (final TaskBase... lNewTasks)
		throws Exception
	{
		final Map< Integer, List< TaskStop >> aRegistry = mem_StopTasks ();
		
		for (final TaskBase aNewTask : lNewTasks)
		{
			final TaskStop         aStopTask    = (TaskStop) aNewTask;
			final int              nRunLevel    = aStopTask.getRunLevel();
				  List< TaskStop > lTasks4Level = aRegistry.get(nRunLevel);

			if (lTasks4Level == null)
			{
				lTasks4Level = new ArrayList< TaskStop > ();
				aRegistry.put(nRunLevel, lTasks4Level);
			}
			
			if ( ! lTasks4Level.contains(aStopTask))
			{
				lTasks4Level.add(aStopTask);
			}
		}
	}

	//-------------------------------------------------------------------------
	/** add all default tasks of SDT to every node.
	 * 
	 *  E.g. one default task is the deployment of the remote SDT framework
	 *  (a bunch of script files). It's the core framework and can't be omitted.
	 */
	private void impl_addDefaultDeployTasks (final List< TaskBase > lTasks)
	    throws Exception
	{
		lTasks.add(new TaskDeployFrameworkCore());
	}

	//-------------------------------------------------------------------------
	private List< TaskBase > mem_DeployTasks ()
	    throws Exception
	{
		if (m_lDeployTasks == null)
		{
			m_lDeployTasks = new ArrayList< TaskBase > ();
			impl_addDefaultDeployTasks (m_lDeployTasks);
		}
		return m_lDeployTasks;
	}

	//-------------------------------------------------------------------------
	private Map< Integer, List< TaskStart >> mem_StartTasks ()
	    throws Exception
	{
		if (m_lStartTasks == null)
			m_lStartTasks = new HashMap< Integer, List< TaskStart >> ();
		return m_lStartTasks;
	}

	//-------------------------------------------------------------------------
	private Map< Integer, List< TaskStop >> mem_StopTasks ()
	    throws Exception
	{
		if (m_lStopTasks == null)
			m_lStopTasks = new HashMap< Integer, List< TaskStop >> ();
		return m_lStopTasks;
	}

	//-------------------------------------------------------------------------
	private SSHServer mem_SSH ()
	    throws Exception
	{
    	if (m_aSSH == null)
    	{
    		final SSHServer   aSSH      = new SSHServer ();
    		final SSHIdentity aIdentity = AuthenticationDescriptor.toSSHIdentity(m_aDesc.aAuth);
    		
    		aSSH.setHost    (m_aDesc.sIpDns   );
    		aSSH.setPort    (m_aDesc.nSSHPort);
    		aSSH.setIdentity(aIdentity        );
    		
    		m_aSSH = aSSH;
    	}
    	return m_aSSH;
	}

	//-------------------------------------------------------------------------
	private WeakReference< SDT > m_rSDT = null;
	
	//-------------------------------------------------------------------------
	private NodeDescriptor m_aDesc = null;

	//-------------------------------------------------------------------------
	private List< TaskBase > m_lDeployTasks = null;

	//-------------------------------------------------------------------------
	private Map< Integer, List< TaskStart > > m_lStartTasks = null;

	//-------------------------------------------------------------------------
	private Map< Integer, List< TaskStop > > m_lStopTasks = null;

	//-------------------------------------------------------------------------
	private SSHServer m_aSSH = null;
}
