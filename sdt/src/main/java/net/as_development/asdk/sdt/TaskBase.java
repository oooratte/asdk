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

import org.apache.commons.lang3.StringUtils;

//=============================================================================
/** This is the base class for all deployment tasks used within SDT.
 * 
 *  A task is a Java object ...
 *  - bound to a {link Node}
 *  - able to access the configuration of such node
 *  - implementing one part of the whole deployment
 *  
 *  The combination of simple tasks can make a complex deployment at one node.
 *  
 *  There are already some default tasks implemented.
 *  As e.g. TaskPatchFramework or TaskScriptlet
 */
public abstract class TaskBase
{
    //-------------------------------------------------------------------------
	public TaskBase ()
	    throws Exception
	{}

	//-------------------------------------------------------------------------
	/** override this method and implement your deployment operation.
	 * 
	 *  The task is bound to the node passed as parameter to this method.
	 *  He provides some environment data and access to the node itself (SSH).
	 * 
	 *  Note: Don't catch any error inside. Let errors/exceptions pass.
	 *  It's up to the framework to know and handle it.
	 * 
	 *  @param	aNode [IN]
	 *  		the node where this task should run it's deployment.
	 *  
	 *  @throws Exception you want (if it indicates a real error - of course)
	 */
	public abstract void execute (final Node aNode)
		throws Exception;

	//-------------------------------------------------------------------------
	protected void log (final Node   aNode   ,
						final String sMessage)
		throws Exception
	{
		final StringBuffer sLog = new StringBuffer (256);
		if (aNode != null)
		{
			sLog.append("["          );
			sLog.append(aNode.getId());
			sLog.append("] : "       );
		}
		
		if ( ! StringUtils.isEmpty(sMessage))
			sLog.append(sMessage);
		
		System.out.println(sLog.toString ());
	}
}
