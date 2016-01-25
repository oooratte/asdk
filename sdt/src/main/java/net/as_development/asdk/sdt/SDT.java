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
package net.as_development.asdk.sdt;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
		final List< Node > lNodes = mem_Nodes ();
		if (lNodes.contains(aNode))
			return;
		
		aNode .bind(this );
		lNodes.add (aNode);
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
		final List< Node > lNodes = mem_Nodes ();
		
		for (final Node aNode : lNodes)
		{
			final Throwable aError = impl_deployNode (aNode);
			if (aError != null)
			{
				System.err.println    (aError.getMessage());
				aError.printStackTrace(System.err);
			}
		}
	}
	
	//-------------------------------------------------------------------------
	private void impl_deployParallel ()
	    throws Exception
	{
		final List< Node >                  lNodes      = mem_Nodes ();
		final ExecutorService               aMassDeploy = Executors.newFixedThreadPool(lNodes.size());
		final List< Callable< Throwable > > lDeploys    = new ArrayList< Callable< Throwable > > ();
		
		for (final Node aNode : lNodes)
		{
			final Callable< Throwable > aDeploy = impl_makeNodeCallable (aNode);
			lDeploys.add(aDeploy);
		}
		
		final List< Future< Throwable > > lResults = aMassDeploy.invokeAll(lDeploys);
		for (final Future< Throwable > aResult : lResults)
		{
			final Throwable aError = aResult.get();
			if (aError != null)
			{
				System.err.println    (aError.getMessage());
				aError.printStackTrace(System.err);
			}
		}
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
	private List< Node > mem_Nodes ()
	    throws Exception
	{
		if (m_lNodes == null)
			m_lNodes = new ArrayList< Node > ();
		return m_lNodes;
	}

	//-------------------------------------------------------------------------
	private EDeploymentStrategy m_eDeploymentStrategy = DEFAULT_DEPLOYMENT_STRATEGY;

    //--------------------------------------------------------------------------
	private List< Node > m_lNodes = null;
}
