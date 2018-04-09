/* DefaultBinder.java

	Purpose:
		
	Description:
		
	History:
		2011/12/21 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind;

import java.util.Map;

import org.zkoss.bind.impl.BinderImpl;
import org.zkoss.bind.impl.BinderUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.EventQueues;

/**
 * ZK default Binder, use this class if you want to new a binder and control components though {@link Binder} API. <br/>
 * After new a instance, you have to call {@link #init(Component, Object)} first, then call 
 * {@link #addCommandBinding(Component, String, String, java.util.Map)}, {@link #addPropertyLoadBindings(Component, String, String, String[], String[], java.util.Map, String, java.util.Map)}
 * ...etc to assign the binding.<br/>
 * After all the add binding done, you have to call {@link #loadComponent(Component, boolean)} to trigger first loading of the binding. <br/> 
 * @author dennischen
 * @since 6.0.0
 */
public class DefaultBinder extends BinderImpl {
	private static final long serialVersionUID = 1463169907348730644L;
	/**
	 * new a binder with default event queue name and scope
	 */
	public DefaultBinder() {
		this(null,null);
	}
	
	/**
	 * new a binder with event queue name and scope
	 * @param qname event queue name
	 * @param qscope event queue scope, see {@link EventQueues}
	 */
	public DefaultBinder(String qname, String qscope) {
		super(qname, qscope);
	}
	
	
	public void init(Component comp, Object vm,Map<String, Object> initArgs){
		super.init(comp, vm, initArgs);
		//mark this component was handled by binder after init
		BinderUtil.markHandling(comp, this);
	}
}
