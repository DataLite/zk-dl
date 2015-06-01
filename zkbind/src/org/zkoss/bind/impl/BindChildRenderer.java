/* BindChildRenderer.java

	Purpose:
		
	Description:
		
	History:
		2012/1/2 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.impl;

import java.util.LinkedList;
import java.util.List;

import org.zkoss.bind.sys.BinderCtrl;
import org.zkoss.bind.sys.TemplateResolver;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.sys.ShadowElementsCtrl;
import org.zkoss.zk.ui.util.ForEachStatus;
import org.zkoss.zk.ui.util.Template;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Treeitem;

/**
 * to renderer children of component
 * @author dennis
 * @since 6.0.0
 */
public class BindChildRenderer extends AbstractRenderer{
	private static final long serialVersionUID = 1L;

	public BindChildRenderer(){
		setAttributeName(AnnotateBinderHelper.CHILDREN_KEY);
	}
	
	public void render(final Component owner, final Object data, final int index, final int size) {
		render(owner, data, index, size, false);
	}
		
	public void render(final Component owner, final Object data, final int index, final int size, final boolean isListModel){
		final Template tm = resolveTemplate(owner,owner,data,index,size,"children");
		if (tm == null) {
			Label l = new Label(data==null?"":data.toString());
			l.setParent(owner);
			return;
		}
		
		final ForEachStatus iterStatus = new ChildrenBindingForEachStatus(index, data, size);//provide iteration status in this context
		
		final String var = (String) tm.getParameters().get("var");
		final String varnm = var == null ? EACH_VAR : var; //var is not specified, default to "each"
		
		final String itervar = (String) tm.getParameters().get(STATUS_ATTR);
		final String itervarnm = itervar == null ? ( var==null?EACH_STATUS_VAR:varnm+STATUS_POST_VAR) : itervar; //provide default value if not specified

		//bug 1188, EL when nested var and itervar
		Object oldVar = owner.getAttribute(varnm);
		Object oldIter = owner.getAttribute(itervarnm);
		owner.setAttribute(varnm, data);
		owner.setAttribute(itervarnm, iterStatus);

		// For bug ZK-2552
		Component insertBefore = null;
		List<Component[]> cbrCompsList = (List<Component[]>) owner.getAttribute(BinderCtrl.CHILDREN_BINDING_RENDERED_COMPONENTS);
		if (cbrCompsList != null) {
			int newIndex = 0;
			int steps = 0;
			for (Component[] cmps : cbrCompsList) {
				if (steps++ >= index)
					break;
				newIndex += cmps.length;
			}
			if (owner.getChildren().size() > newIndex) {
				insertBefore = owner.getChildren().get(newIndex);
			} 
		}
		final Component[] items = ShadowElementsCtrl.filterOutShadows(tm.create(owner, insertBefore, null, null));
		// ZK-2552: define own iterStatus since children inside template could be more than 1 for children binding
		final ForEachStatus bindChildIterStatus = new AbstractForEachStatus() { // provide iteration status in this context
			private static final long serialVersionUID = 1L;
			
			public int getIndex() {
				return index;
			}
			
			public Object getCurrent(){
				return data;
			}
			
			public Integer getEnd(){
				return size;
			}
			 
			public int getCurrentIndex(Component comp) { 
				int result = -1;
				if (comp instanceof Listitem) {
					result = ((Listitem) comp).getIndex();
				} else if (comp instanceof Row) {
					result = ((Row) comp).getIndex();
				}  else if (comp instanceof Treeitem) {
					result = ((Treeitem) comp).getIndex();
				} else 
					result = comp.getParent().getChildren().indexOf(comp);
				
				result = result / items.length;
				return result;
			}
		};
		
		owner.setAttribute(varnm, oldVar);
		owner.setAttribute(itervarnm, oldIter);
		

		boolean templateTracked = false;
		
		//ZK-2545 - Children binding support list model
		if (isListModel) {
			cbrCompsList = (List<Component[]>) owner.getAttribute(BinderCtrl.CHILDREN_BINDING_RENDERED_COMPONENTS);
			if (cbrCompsList == null) cbrCompsList = new LinkedList<Component[]>();
			cbrCompsList.add(items);
			owner.setAttribute(BinderCtrl.CHILDREN_BINDING_RENDERED_COMPONENTS, cbrCompsList);
		}
		
		for(Component comp: items){
			comp.setAttribute(BinderImpl.VAR, varnm);
			
			// ZK-2552
			comp.setAttribute(AbstractRenderer.IS_TEMPLATE_MODEL_ENABLED_ATTR, true);
			comp.setAttribute(AbstractRenderer.CURRENT_INDEX_RESOLVER_ATTR, bindChildIterStatus);
			addItemReference(owner, comp, index, varnm); //kept the reference to the data, before ON_BIND_INIT
			comp.setAttribute(itervarnm, iterStatus);
			
			//add template dependency
			if (!templateTracked) {
				//ZK-1787 When the viewModel tell binder to reload a list, the other component that bind a bean in the list will reload again
				//move TEMPLATE_OBJECT (was set in resoloveTemplate) to current for check in addTemplateTracking
				comp.setAttribute(TemplateResolver.TEMPLATE_OBJECT, owner.removeAttribute(TemplateResolver.TEMPLATE_OBJECT));
				addTemplateTracking(owner, comp, data, index, size);
				templateTracked = true;
			}
			
			//to force init and load
			Events.sendEvent(new Event(BinderImpl.ON_BIND_INIT, comp));
		}
		
	}
	
	private class ChildrenBindingForEachStatus extends AbstractForEachStatus {
		private static final long serialVersionUID = 1L;
		
		private int index;
		private transient Object data;
		private Integer size;
		
		public ChildrenBindingForEachStatus(int index, Object data, Integer size) {
			this.index = index;
			this.data = data;
			this.size = size;
		}
		
		public int getIndex() {
			return index;
		}
		
		public Object getCurrent(){
			return data;
		}
		
		public Integer getEnd(){
			return size;
		}
	}
}
