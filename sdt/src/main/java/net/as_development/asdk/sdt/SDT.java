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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang3.StringUtils;

import net.as_development.asdk.sdt.impl.TaskDeployFrameworkCore;

//=============================================================================
/** MAIN entry point of the SDT framework.
 *  Create an instance ... add some nodes to it and call deploy().
 *  So easy it can be.
 */
public class SDT
{
	//-------------------------------------------------------------------------
	public static final EDeploymentStrategy DEFAULT_DEPLOYMENT_STRATEGY = EDeploymentStrategy.E_SEQUENTIAL;
	
    //-------------------------------------------------------------------------
	public SDT ()
	    throws Exception
	{}

	//-------------------------------------------------------------------------
	/** define the strategy how all nodes will be deployed - parallel or sequential.
	 * 
	 *  Will have no effect if it's called after deploy() - or course.
	 * 
	 *	@param	eStrategy [IN]
	 *			the new strategy.
	 */
	public void setDeploymentStrategy (final EDeploymentStrategy eStrategy)
		throws Exception
	{
		m_eDeploymentStrategy = eStrategy;
	}
	
	//-------------------------------------------------------------------------
	public void setDebug (final boolean bDebug)
		throws Exception
	{
		m_bDebug = bDebug;
	}

	//-------------------------------------------------------------------------
	public boolean isDebug ()
	    throws Exception
	{
		return m_bDebug;
	}
	
	//-------------------------------------------------------------------------
	/** add new node to the STD deployment chain.
	 * 
	 *  The order of nodes doesnt matter. At least it's possible they will
	 *  be deployed in parallel. But we filter out double registrations of
	 *  the same node ... 
	 * 
	 *  @param	aNode [IN]
	 *  		a new node for the deployment.
	 */
	public void addNode (final Node aNode)
		throws Exception
	{
		final Map< String, Node > lNodes  = mem_Nodes ();
		      String              sNodeId = aNode.getId();
		      
		if (StringUtils.isEmpty(sNodeId))
		{
			sNodeId = UUID.randomUUID().toString();
			aNode.setId(sNodeId);
		}
		
		aNode .bind(this);
		lNodes.put (sNodeId, aNode);
	}

	//-------------------------------------------------------------------------
	/** easy adding of more then one node at the same time
	 * 
	 *  @param	lNodes [IN]
	 *  		the list of nodes.
	 */
	public void addNodes (final Node... lNodes)
		throws Exception
	{
		for (final Node aNode : lNodes)
			addNode (aNode);
	}

	//-------------------------------------------------------------------------
	/** deploy all registered nodes.
	 *  Use the configured deployment strategy.
	 *  
	 *  @see (link setDeploymentStrategy)
	 */
	public void deploy ()
		throws Exception
	{
		if (m_eDeploymentStrategy == EDeploymentStrategy.E_SEQUENTIAL)
			impl_deploySequential ();
		else
		if (m_eDeploymentStrategy == EDeploymentStrategy.E_PARALLEL)
			impl_deployParallel ();
		else
			throw new UnsupportedOperationException ("No support for '"+m_eDeploymentStrategy+"' implemented yet.");
	}
	
	//-------------------------------------------------------------------------
	/** deploy all nodes defined by given list of IDs.
	 * 
	 *  If a node with given ID is not found an error is thrown.
	 *
	 *  @param  lNodeIds [IN]
	 *  		a list of node IDs of nodes which has to be deployed
	 */
	public void deploy (final String... lNodeIds)
		throws Exception
	{
		final List< Node > lNodes = impl_listNodesById (lNodeIds);
		
		if (m_eDeploymentStrategy == EDeploymentStrategy.E_SEQUENTIAL)
			impl_deploySequential (lNodes);
		else
		if (m_eDeploymentStrategy == EDeploymentStrategy.E_PARALLEL)
			impl_deployParallel (lNodes);
		else
			throw new UnsupportedOperationException ("No support for '"+m_eDeploymentStrategy+"' implemented yet.");
	}

	//-------------------------------------------------------------------------
	/** start all configured nodes
	 * 
	 * TODO order nodes in run level
	 */
	public void startAllNodes ()
		throws Exception
	{
		impl_startNodes(impl_listAllNodes ());
	}

	//-------------------------------------------------------------------------
	/** start all defined nodes
	 */
	public void startNodes (final String... lNodeIds)
		throws Exception
	{
		impl_startNodes(impl_listNodesById (lNodeIds));
	}

	//-------------------------------------------------------------------------
	/** stop all configured nodes
	 * 
	 * TODO order nodes in run level
	 */
	public void stopAllNodes ()
		throws Exception
	{
		impl_stopNodes(impl_listAllNodes ());
	}

	//-------------------------------------------------------------------------
	/** stop all defined nodes
	 */
	public void stopNodes (final String... lNodeIds)
		throws Exception
	{
		impl_stopNodes(impl_listNodesById (lNodeIds));
	}

	//-------------------------------------------------------------------------
	/** run task direct on node(s) - immediately
	 * 
	 *  @param	lNodeIds [IN]
	 *  		the list of the nodes (id's) where the given task has to be executed.
	 *  
	 *  @param	aTask [IN]
	 *  		the task to be executed at those nodes
	 */
	public void runTaskOnNodes (final TaskBase  aTask   ,
							    final String... lNodeIds)
		throws Exception
	{
		final List< Node > lNodes = impl_listNodesById (lNodeIds);
		for (final Node aNode : lNodes)
			aTask.execute(aNode);
	}

	//-------------------------------------------------------------------------
	public void forceReDeployOfSDTOnNodes (final String... lNodeIds)
	    throws Exception
	{
		final List< Node > lNodes    = impl_listNodesById (lNodeIds);
		final TaskBase     aReDeploy = TaskDeployFrameworkCore.createForceUpdate ();

		for (final Node aNode : lNodes)
			aReDeploy.execute(aNode);
	}
	
	//-------------------------------------------------------------------------
	public static String defineSDTResource (final String... lParts)
	    throws Exception
	{
		final StringBuffer sRes          = new StringBuffer (256);
		      boolean      bAddSeparator = false;

		for (final String sPart : lParts)
		{
			if (bAddSeparator == true)
				sRes.append("/");
			else
				bAddSeparator = true;
			sRes.append(sPart);
		}
		
		return sRes.toString ();
	}
	
	//-------------------------------------------------------------------------
	private void impl_deploySequential ()
	    throws Exception
	{
		impl_deploySequential(impl_listAllNodes ());
	}
	
	//-------------------------------------------------------------------------
	private void impl_deploySequential (final List< Node > lNodes)
	    throws Exception
	{
		for (final Node aNode : lNodes)
		{
			final Throwable aError = impl_deployNode (aNode);
			if (aError != null)
				throw new Exception(aError);
		}
	}

	//-------------------------------------------------------------------------
	private void impl_deployParallel ()
	    throws Exception
	{
		impl_deployParallel (impl_listAllNodes ());	
	}

	//-------------------------------------------------------------------------
	private void impl_deployParallel (final List< Node > lNodes)
	    throws Exception
	{
		final ExecutorService               aMassDeploy = Executors.newFixedThreadPool(lNodes.size());
		final List< Callable< Throwable > > lDeploys    = new ArrayList< Callable< Throwable > > ();
		
		for (final Node aNode : lNodes)
		{
			final Callable< Throwable > aDeploy = impl_makeNodeCallable (aNode);
			lDeploys.add(aDeploy);
		}
		
		final List< Future< Throwable > > lResults = aMassDeploy.invokeAll(lDeploys);
	          int                         nErrors  = 0;

		for (final Future< Throwable > aResult : lResults)
		{
			final Throwable aError = aResult.get();
			if (aError == null)
				continue;

			nErrors++;
			System.err.println    (aError.getMessage());
			aError.printStackTrace(System.err);
		}
		
		if (nErrors > 0)
			throw new Exception ("Setup had ["+nErrors+"] errors.");
	}
	
	//-------------------------------------------------------------------------
	private void impl_startNodes (final List< Node > lNodes)
		throws Exception
	{
		for (final Node aNode : lNodes)
			aNode.start();
	}

	//-------------------------------------------------------------------------
	private void impl_stopNodes (final List< Node > lNodes)
		throws Exception
	{
		for (final Node aNode : lNodes)
			aNode.stop();
	}

    //--------------------------------------------------------------------------
	private Callable< Throwable > impl_makeNodeCallable (final Node aNode)
	    throws Exception
	{
		final Callable< Throwable > aCallable = new Callable< Throwable > ()
		{
			@Override
			public Throwable call()
				throws Exception
			{
				return impl_deployNode (aNode);
			}
		};
		return aCallable;
	}

	//--------------------------------------------------------------------------
	private Throwable impl_deployNode (final Node aNode)
	    throws Exception
	{
		try
		{
			aNode.deploy();
		}
		catch (Throwable ex)
		{
			return ex;
		}
		return null;
	}
	
    //--------------------------------------------------------------------------
	private List< Node > impl_listAllNodes ()
	    throws Exception
	{
		final List< Node > lNodes = new ArrayList< Node > ();
		lNodes.addAll(mem_Nodes().values());
		return lNodes;
	}

    //--------------------------------------------------------------------------
	private List< Node > impl_listNodesById (final String... lNodeIds)
	    throws Exception
	{
		final Map< String, Node > lAllNodes = mem_Nodes ();
		final List< Node >        lNodes    = new ArrayList< Node > ();
		
		for (final String sNodeId : lNodeIds)
		{
			final Node aNode = lAllNodes.get(sNodeId);
			if (aNode == null)
				throw new Error ("No node found for ID '"+sNodeId+"'.");
			lNodes.add(aNode);
		}
		
		return lNodes;
	}

    //--------------------------------------------------------------------------
	private Node impl_getNodeById (final String sNodeId)
	    throws Exception
	{
		final Map< String, Node > lAllNodes = mem_Nodes ();
		final Node                aNode     = lAllNodes.get(sNodeId);

		if (aNode == null)
			throw new Error ("No node found for ID '"+sNodeId+"'.");
		
		return aNode;
	}

	//--------------------------------------------------------------------------
	private Map< String, Node > mem_Nodes ()
	    throws Exception
	{
		if (m_lNodes == null)
			m_lNodes = new HashMap< String, Node > ();
		return m_lNodes;
	}

	//-------------------------------------------------------------------------
	private EDeploymentStrategy m_eDeploymentStrategy = DEFAULT_DEPLOYMENT_STRATEGY;

    //--------------------------------------------------------------------------
	private boolean m_bDebug = false;
	
    //--------------------------------------------------------------------------
	private Map< String, Node > m_lNodes = null;
}
