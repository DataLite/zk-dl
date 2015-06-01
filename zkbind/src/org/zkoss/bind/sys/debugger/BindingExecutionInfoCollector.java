/* BindingExecutionInfoCollector.java

	Purpose:
		
	Description:
		
	History:
		2013/1/7 Created by Dennis Chen

Copyright (C) 2013 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.bind.sys.debugger;


/**
 * the collector to collect runtime binding execution information
 * 
 * @author dennis
 * @since 6.5.2
 */
public interface BindingExecutionInfoCollector {

	/**
	 * push the execution stack
	 */
	void pushStack(String name);

	/**
	 * pop the execution stack
	 */
	String popStack();
	
	/**
	 * add an execution information
	 */
	void addInfo(ExecutionInfo info);
}
