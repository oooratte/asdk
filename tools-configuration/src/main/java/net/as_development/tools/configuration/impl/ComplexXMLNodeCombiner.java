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
package net.as_development.tools.configuration.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.configuration.tree.NodeCombiner;
import org.apache.commons.configuration.tree.ViewNode;
import org.apache.commons.lang3.StringUtils;

//=============================================================================
public class ComplexXMLNodeCombiner extends NodeCombiner
{
	//-------------------------------------------------------------------------
	public static final String ATTRIBUTE_MERGE_OPERATION = "merge-operation";
	public static final String MERGE_OPERATION_REMOVE    = "remove"  ;
	public static final String MERGE_OPERATION_EXPLICIT  = "explicit";
	
	//-------------------------------------------------------------------------
	public ComplexXMLNodeCombiner ()
		throws Exception
	{}
	
	//-------------------------------------------------------------------------
	@Override
	public synchronized ConfigurationNode combine(final ConfigurationNode aNodeUpstream  ,
									              final ConfigurationNode aNodeDownstream)
	{
		try
		{
			return impl_merge(aNodeUpstream, aNodeDownstream);
		}
		catch (final Exception ex)
		{
			throw new RuntimeException (ex);
		}
	}
	
	//-------------------------------------------------------------------------
	private ConfigurationNode impl_merge(final ConfigurationNode aNodeUpstream  ,
			 					         final ConfigurationNode aNodeDownstream)
		throws Exception
	{
		final ViewNode aMergedNode = createViewNode();
	    aMergedNode.setName (aNodeUpstream.getName ());
	    impl_mergeAttributes(aMergedNode, aNodeUpstream, aNodeDownstream);

	    final String sNodeHash = impl_calculateNodeHash(aNodeUpstream);
		if (impl_isNodeRemoved(sNodeHash))
			return aMergedNode;
		
		if (impl_hasNodeAttribute (aNodeUpstream, ATTRIBUTE_MERGE_OPERATION, MERGE_OPERATION_REMOVE))
		{
			impl_markNodeAsRemoved(sNodeHash);
			return aMergedNode;
		}
		
		boolean bAddRemaining = true;
		if (impl_hasNodeAttribute (aNodeUpstream, ATTRIBUTE_MERGE_OPERATION, MERGE_OPERATION_EXPLICIT))
		{
			// in explicit mode children of down-stream node has to be ignored
			// ... excepting children which also exists on the up-stream node
			// to make it short : remaining children of down-stream node has to be ignored ;-)
			
			bAddRemaining = false;
		}

		aMergedNode.setValue(aNodeUpstream.getValue());
	
	    final List< ConfigurationNode > lChildrenOfDownstream = new LinkedList< ConfigurationNode >(aNodeDownstream.getChildren());
	    for (final ConfigurationNode aChildUpstream : aNodeUpstream.getChildren())
	    {
	        final ConfigurationNode aChildDownstream = impl_findMatchingChild(aNodeUpstream, aNodeDownstream, aChildUpstream, lChildrenOfDownstream);
	        if (aChildDownstream != null)
	        {
	        	final ConfigurationNode aMergedChild = impl_merge(aChildUpstream, aChildDownstream);
	        	if (aMergedChild != null)
	        		aMergedNode.addChild(aMergedChild);
	            lChildrenOfDownstream.remove(aChildDownstream);
	        }
	        else
	        {
	            aMergedNode.addChild(aChildUpstream);
	        }
	    }
	
	    if (bAddRemaining)
	    {
		    for (final ConfigurationNode aRemainingChild : lChildrenOfDownstream)
		        aMergedNode.addChild(aRemainingChild);
	    }

	    return aMergedNode;
	}

	//-------------------------------------------------------------------------
    private void impl_mergeAttributes(final ViewNode          aMergedNode    ,
    								  final ConfigurationNode aNodeUpstream  ,
    								  final ConfigurationNode aNodeDownstream)
    	throws Exception
    {
        aMergedNode.appendAttributes(aNodeUpstream);

        for (final ConfigurationNode aAttribute : aNodeDownstream.getAttributes())
        {
            if (aNodeUpstream.getAttributeCount(aAttribute.getName()) == 0)
                aMergedNode.addAttribute(aAttribute);
        }
    }

	//-------------------------------------------------------------------------
    private ConfigurationNode impl_findMatchingChild(final ConfigurationNode         aNodeUpstream      ,
            										 final ConfigurationNode         aNodeDownstream    ,
            										 final ConfigurationNode         aChildUpstream     ,
            										 final List< ConfigurationNode > lChildrenDownstream)
		throws Exception
    {
        final List< ConfigurationNode >     lAttributes       = aChildUpstream.getAttributes();
        final List< ConfigurationNode >     lNodes            = new ArrayList< ConfigurationNode >();
        final List< ConfigurationNode >     lMatchingChildren = aNodeDownstream.getChildren(aChildUpstream.getName());
        final Iterator< ConfigurationNode > rMatchingChildren = lMatchingChildren.iterator();

        while (rMatchingChildren.hasNext())
        {
                  ConfigurationNode             aMatchingChild           = rMatchingChildren.next();
            final Iterator< ConfigurationNode > rMatchingChildAttributes = lAttributes.iterator();
            while (rMatchingChildAttributes.hasNext())
            {
                final ConfigurationNode         aMatchingChildAttribute  = rMatchingChildAttributes.next();
                final List< ConfigurationNode > aMatchingChildAttributes = aMatchingChild.getAttributes(aMatchingChildAttribute.getName());
                
                if (
                	(   aMatchingChildAttributes.size() == 1                                                  ) &&
                	( ! aMatchingChildAttribute .getValue().equals(aMatchingChildAttributes.get(0).getValue()))
                   )
                {
                    aMatchingChild = null;
                    break;
                }
            }
            
            if (aMatchingChild != null)
                lNodes.add(aMatchingChild);
        }

        if (lNodes.size() == 1)
            return lNodes.get(0);

        if (
        	(   lNodes.size() > 1         ) &&
        	( ! isListNode(aChildUpstream))
           )
        {
            final Iterator< ConfigurationNode > rNodes = lNodes.iterator();
            while (rNodes.hasNext())
                lChildrenDownstream.remove(rNodes.next());
        }

        return null;
    }

	//-------------------------------------------------------------------------
    private boolean impl_isNodeRemoved (final String sNodeHash)
    	throws Exception
    {
    	final List< String > lRemovedNodes = mem_RemovedNodes ();
    	final boolean        bIsRemoved    = lRemovedNodes.contains(sNodeHash);
    	return bIsRemoved;
    }
    
	//-------------------------------------------------------------------------
    private void impl_markNodeAsRemoved (final String sNodeHash)
    	throws Exception
    {
    	final List< String > lRemovedNodes = mem_RemovedNodes ();
    	if ( ! lRemovedNodes.contains(sNodeHash))
    		lRemovedNodes.add (sNodeHash);
    }
    
	//-------------------------------------------------------------------------
	private String impl_calculateNodeHash (final ConfigurationNode aNode)
	    throws Exception
	{
		final StringBuffer sDesc = new StringBuffer (256);
		sDesc.append ("name="                                  );
		sDesc.append (StringUtils.trimToEmpty(aNode.getName ()));
		sDesc.append (":"                                      );
		
		final List< String > lAttributes = impl_getAttributeNamesAscending (aNode);
		for (final String sAttribute : lAttributes)
		{
			String sValue = impl_getAttribute (aNode, sAttribute);
 			       sValue = StringUtils.trimToEmpty(sValue);
			
 			if (StringUtils.equalsIgnoreCase(sAttribute, ATTRIBUTE_MERGE_OPERATION))
 				continue;
 			       
 			sDesc.append (sAttribute);
			sDesc.append ("="       );
			sDesc.append (sValue    );
			sDesc.append (":"       );
		}
		
		return sDesc.toString ();
	}
	
    //-------------------------------------------------------------------------
	private boolean impl_hasNodeAttribute (final ConfigurationNode aNode     ,
										   final String            sAttribute,
										   final String            sValue    )
    	throws Exception
	{
		final String sAttributeValue = impl_getAttribute (aNode, sAttribute);
		if (StringUtils.equals(sAttributeValue, sValue))
			return true;
		return false;
	}

	//-------------------------------------------------------------------------
	private String impl_getAttribute (final ConfigurationNode aNode     ,
									  final String            sAttribute)
    	throws Exception
	{
		if (aNode == null)
			return null;
		
		if (StringUtils.isEmpty (sAttribute))
			return null;
		
		final List< ConfigurationNode > lAttributes = aNode.getAttributes(sAttribute);

		if (lAttributes == null)
			return null;
		
		if (lAttributes.size() < 1)
			return null;
		
		final ConfigurationNode aAttribute = lAttributes.get(0);
		final Object            aValue     = aAttribute.getValue();

		if (aValue == null)
			return null;
		
		if ( ! (aValue instanceof String))
			return null;
		
		return (String) aValue;
	}

	//-------------------------------------------------------------------------
	private List< String > impl_getAttributeNamesAscending (final ConfigurationNode aNode)
	    throws Exception
	{
		final List< String >            lResult     = new ArrayList< String > ();
		final List< ConfigurationNode > lAttributes = aNode.getAttributes();
		for (final ConfigurationNode aAttribute : lAttributes)
		{
			final String sAttribute = aAttribute.getName();
			if (StringUtils.isEmpty(sAttribute))
				continue;
			lResult.add (sAttribute);
		}
		
		Collections.sort(lResult);
		return lResult;
	}
	
	//-------------------------------------------------------------------------
	private List< String > mem_RemovedNodes ()
	    throws Exception
	{
		if (m_lRemovedNodes == null)
			m_lRemovedNodes = new ArrayList< String > ();
		return m_lRemovedNodes;
	}
	
	//-------------------------------------------------------------------------
	private List< String > m_lRemovedNodes = null;
}
