/* LoadChildrenBindingImpl.java

	Purpose:
		
	Description:
		
	History:
		2012/1/2 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.impl;

import static org.zkoss.lang.Generics.cast;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Binder;
import org.zkoss.bind.Converter;
import org.zkoss.bind.sys.BindEvaluatorX;
import org.zkoss.bind.sys.BinderCtrl;
import org.zkoss.bind.sys.ConditionType;
import org.zkoss.bind.sys.LoadChildrenBinding;
import org.zkoss.bind.sys.debugger.BindingExecutionInfoCollector;
import org.zkoss.bind.sys.debugger.impl.info.LoadInfo;
import org.zkoss.bind.xel.zel.BindELContext;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.event.ListDataEvent;
import org.zkoss.zul.event.ListDataListener;

/**
 * Implementation of {@link LoadChildrenBinding}.
 * @author dennis
 * @since 6.0.0
 */
public class LoadChildrenBindingImpl extends ChildrenBindingImpl implements
		LoadChildrenBinding {
	private static final long serialVersionUID = 1463169907348730644L;
	private Set<String> _doneDependsOn;
	
	public LoadChildrenBindingImpl(Binder binder, Component comp,
		String loadExpr, ConditionType conditionType,String command,  Map<String, Object> bindingArgs,
		String converterExpr,Map<String, Object> converterArgs) {
		super(binder, comp, loadExpr, conditionType, command, bindingArgs,converterExpr,converterArgs);
	}
	
	@SuppressWarnings("unchecked")
	public void load(BindContext ctx) {
		final Component comp = getComponent();//ctx.getComponent();
		final BindEvaluatorX eval = getBinder().getEvaluatorX();
		final BindingExecutionInfoCollector collector = ((BinderCtrl)getBinder()).getBindingExecutionInfoCollector();
		//get data from property
		Object value = eval.getValue(ctx, comp, _accessInfo.getProperty());
		
		final boolean activating = ((BinderCtrl)getBinder()).isActivating();
		
		//use _converter to convert type if any
		final Converter conv = getConverter();
		Object old = value;
		if (conv != null) {
//			//if a converter depends on some property, we should also add tracker
//			//TODO, Dennis, ISSUES, currently, a base path of a converter, is its binding path.
//			//ex @bind(vm.person.firstName) , it's base path is 'vm.person.firstName', not 'vm.person'
//			//this sepc is different with DependsOn of a property
//			addConverterDependsOnTrackings(conv, ctx);
			
			if(activating) return;//don't load to component if activating
			value = conv.coerceToUi(value, comp, ctx);
			if(value == Converter.IGNORED_VALUE) {
				if(collector!=null){
					collector.addInfo(new LoadInfo(LoadInfo.CHILDREN_LOAD,comp,getConditionString(ctx),
							getPropertyString(),null,old,getArgs(),"*Converter.IGNORED_VALUE"));
				}
				return;
			}
		}
		if(activating) return;//don't load to component if activating
		
		comp.getChildren().clear();
		BindELContext.removeModel(comp);
		if(value!=null){
			List<Object> data = null;
			if(value instanceof List){
				data = (List<Object>)value;
			}else{
				throw new UiException(value+" is not a List, is "+value.getClass());
			}
			BindChildRenderer renderer = new BindChildRenderer();
			BindELContext.addModel(comp, data); //ZK-758. @see AbstractRenderer#addItemReference
			//ZK-2545 - Children binding support list model
			boolean isUsingListModel = old instanceof ListModel;
			if (isUsingListModel) {
				Object model = comp.getAttribute(BinderCtrl.CHILDREN_BINDING_MODEL);
				if (model != null && !old.equals(model)) //when model is changed
					comp.removeAttribute(BinderCtrl.CHILDREN_BINDING_RENDERED_COMPONENTS);
				ListDataListener dataListener = new ChildrenBindingListDataListener(comp, ctx, conv);
				((ListModel<?>) old).addListDataListener(dataListener);
				comp.setAttribute(BinderCtrl.CHILDREN_BINDING_MODEL, old);
				comp.setAttribute(BinderCtrl.CHILDREN_BINDING_MODEL_LISTENER, dataListener);
			}
			int size = data.size();
			for(int i = 0; i < size; i++) {
				renderer.render(comp, data.get(i), i, size, isUsingListModel);
			}
		}
		
		if(collector!=null){
			collector.addInfo(new LoadInfo(LoadInfo.CHILDREN_LOAD,comp,getConditionString(ctx),
					getPropertyString(),"",value,getArgs(),null));
		}
	}
	
	private String getConditionString(BindContext ctx){
		StringBuilder condition = new StringBuilder();
		if(getConditionType()==ConditionType.BEFORE_COMMAND){
			condition.append("before = '").append(getCommandName()).append("'");
		}else if(getConditionType()==ConditionType.AFTER_COMMAND){
			condition.append("after = '").append(getCommandName()).append("'");
		}else{
			condition.append(ctx.getTriggerEvent()==null?"":"event = "+ctx.getTriggerEvent().getName()); 
		}
		return condition.length()==0?null:condition.toString();
	}
	
//	private void addConverterDependsOnTrackings(Converter conv, BindContext ctx) {
//		final Class<? extends Converter> convClz = conv.getClass();
//		if (_doneConverterDependsOn.contains(convClz)) { //avoid to eval converter @DependsOn again if not exists
//			return;
//		}
//		_doneConverterDependsOn.add(convClz);
//		final Method m = getConverterMethod(convClz);
//		final String srcpath = getPropertyString();
//		BindELContext.addDependsOnTrackings(m, srcpath, null, this, ctx);
//	}
	
	private Method getConverterMethod(Class<? extends Converter> cls) {
		try {
			return cls.getMethod("coerceToUi", new Class[] {Object.class, Component.class, BindContext.class});
		} catch (NoSuchMethodException e) {
			//ignore
		}
		return null; //shall never come here
	}
	
	/**
	 * Internal Use Only.
	 */
	public void addDependsOnTrackings(List<String> srcpath, String basepath, String[] props) {
		if (srcpath != null) {
			final String src = BindELContext.pathToString(srcpath);
			if (_doneDependsOn != null && _doneDependsOn.contains(src)) { //this method has already done @DependsOn in this binding
				return;
			}
			_doneDependsOn = AllocUtil.inst.addSet(_doneDependsOn, src); //mark method as done @DependsOn; ZK-2289
		}
		for(String prop : props) {
			BindELContext.addDependsOnTracking(this, srcpath, basepath, prop);
		}
	}
	
}
