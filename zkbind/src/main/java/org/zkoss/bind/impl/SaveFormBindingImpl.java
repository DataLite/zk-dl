/* SaveFormBindingImpl.java

	Purpose:
		
	Description:
		
	History:
		Aug 9, 2011 6:30:34 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.impl;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Binder;
import org.zkoss.bind.Form;
import org.zkoss.bind.FormStatus;
import org.zkoss.bind.Property;
import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;
import org.zkoss.bind.proxy.FormProxyObject;
import org.zkoss.bind.sys.BindEvaluatorX;
import org.zkoss.bind.sys.BinderCtrl;
import org.zkoss.bind.sys.ConditionType; 
import org.zkoss.bind.sys.SaveFormBinding;
import org.zkoss.bind.sys.debugger.BindingExecutionInfoCollector;
import org.zkoss.bind.sys.debugger.impl.info.SaveInfo;
import org.zkoss.bind.sys.debugger.impl.info.ValidationInfo;
import org.zkoss.xel.ExpressionX;
import org.zkoss.xel.ValueReference;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;

/**
 * Implementation of {@link SaveFormBinding}.
 * @author henrichen
 * @since 6.0.0
 */
public class SaveFormBindingImpl extends FormBindingImpl implements	SaveFormBinding {
	private static final long serialVersionUID = 1463169907348730644L;
	private final ExpressionX _validator;
	private final Map<String, Object> _validatorArgs;
	
	private static final String $VALUEREF$ = "$VALUEREF$";
	
	public SaveFormBindingImpl(Binder binder, Component comp, String formId, String saveExpr,
			ConditionType conditionType, String command, Map<String, Object> bindingArgs, 
			String validatorExpr, Map<String,Object> validatorArgs) {
		super(binder, comp, formId, saveExpr, conditionType, command, bindingArgs);
		final BindEvaluatorX eval = binder.getEvaluatorX();
		_validator = validatorExpr==null?null:parseValidator(eval,validatorExpr);
		_validatorArgs = validatorArgs;
		
		// force to init Form for validation
		getFormBean(BindContextUtil.newBindContext(binder, this, false, null, getComponent(), null));
		
	}
	
	public Map<String, Object> getValidatorArgs() {
		return _validatorArgs;
	}
	
	
	protected boolean ignoreTracker(){
		return true;
	}
	
	private ExpressionX parseValidator(BindEvaluatorX eval, String validatorExpr) {
//		final BindContext ctx = BindContextUtil.newBindContext(getBinder(), this, false, null, getComponent(), null);
//		ctx.setAttribute(BinderImpl.IGNORE_TRACKER, Boolean.TRUE);//ignore tracker when doing el, we don't need to trace validator
		//don't provide a bindcontext when pare expression of validator with this binding,
		//do so, the tracker will not also tracking the validator dependence with this binding.
		return eval.parseExpressionX(null, validatorExpr, Object.class);
	}

	public Validator getValidator() {
		if(_validator==null) return null;

//		final BindContext ctx = BindContextUtil.newBindContext(getBinder(), this, false, null, getComponent(), null);
//		ctx.setAttribute(BinderImpl.IGNORE_TRACKER, Boolean.TRUE);//ignore tracker when doing el, we don't need to trace validator		
		final BindEvaluatorX eval = getBinder().getEvaluatorX();
		Object obj = eval.getValue(/*ctx*/null, getComponent(), _validator);
		
		if(obj instanceof Validator){
			return (Validator)obj;
		}else if(obj instanceof String){
			return getBinder().getValidator((String)obj);//binder will throw exception if not found
		}else{
			throw new ClassCastException("result of expression '"+_validator.getExpressionString()+"' is not a Validator, is "+obj);
		}
	}
	
	public Form getFormBean(BindContext ctx) {
		Form form = getFormBean();
		if (form == null) {
			final Binder binder = getBinder();
			final BindEvaluatorX eval = binder.getEvaluatorX();
			final Component comp = getComponent();

			final Object bean = eval.getValue(ctx, comp,
					_accessInfo.getProperty());;
			form = initFormBean(bean,  (Class<Object>)(bean == null ? eval.getType(ctx, comp, _accessInfo.getProperty()) : bean.getClass()));
		}
		return form;
	}
	public void save(BindContext ctx) {
		final Binder binder = getBinder();
		final Component comp = getComponent();//ctx.getComponent();
		final Form form = getFormBean(ctx);

		
		BindingExecutionInfoCollector collector = ((BinderCtrl)getBinder()).getBindingExecutionInfoCollector();
		if(collector!=null){
			collector.addInfo(new SaveInfo(SaveInfo.FORM_SAVE,comp,getConditionString(ctx),
					getFormId(),getPropertyString(),form,getArgs(),null));
		}
		
		//update form field into backing bean
		FormStatus formStatus = form.getFormStatus();
		if (formStatus.isDirty())
			formStatus.submit(ctx);

		binder.notifyChange(formStatus, ".");//notify change of fxStatus and fxStatus.*
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

	//--SaveBinding--//
	public Property getValidate(BindContext ctx) {
		//we should not check this binding need to validate or not here, 
		//since other validator may want to know the value of porperty of this binding, so just provide it 
		final Binder binder = getBinder();
		final BindEvaluatorX eval = binder.getEvaluatorX();
		final Component comp = getComponent();//ctx.getComponent();
		final Form form = getFormBean(ctx);
			
		final ExpressionX expr = getBaseExpression(eval);
		if (expr != null) {
			final Object base = eval.getValue(ctx, comp, expr);
			return new PropertyImpl(base, ".", form);
		}
		return null;
	}
	
	//--SaveFormBinding--//
	public Set<Property> getValidates(BindContext ctx) {
		final Set<Property> properties = new HashSet<Property>(2);
		//we should not check this binding need to validate or not here, 
		//since other validator may want to know the value of porperty of this binding, so just provide it 
		final Binder binder = getBinder();
		final BindEvaluatorX eval = binder.getEvaluatorX();
		final Component comp = getComponent();//ctx.getComponent();
		final Form form = getFormBean(ctx);
	
		//remember base and form field
		if(form instanceof Form){
			for (String field : ((BinderCtrl)binder).getSaveFormFieldNames(form)) {
				final ExpressionX expr = getFieldExpression(eval, field);
				if (expr != null) {
					final ValueReference valref = eval.getValueReference(ctx, comp, expr);
					if(valref==null){
						throw new UiException("value reference not found by expression ["+expr.getExpressionString()+"], check if you are trying to save to a variable only expression");
					}
					//ZK-911. Load from Form bean via expression(so will use form's AccessFieldName)
					final ExpressionX formExpr = getFormExpression(eval, field);
					final Object value = eval.getValue(null, comp, formExpr);//form.getField(field);
					properties.add(new PropertyImpl(valref.getBase(), (String) valref.getProperty(), value));
				}
			}
		}
		return properties;
	}
	
	public boolean hasValidator() {
		return _validator == null ? false : true;
	}
	
	public String getValidatorExpressionString(){
		return _validator==null?null:BindEvaluatorXUtil.getExpressionString(_validator);
	}

	public void validate(ValidationContext vctx) {
		Validator validator = getValidator();
		if(validator == null){
			throw new NullPointerException("cannot find validator for "+this);
		}
		validator.validate(vctx);
		
		BindingExecutionInfoCollector collector = ((BinderCtrl)getBinder()).getBindingExecutionInfoCollector();
		if(collector!=null){
			collector.addInfo(new ValidationInfo(ValidationInfo.PROP,getComponent(),
					getValidatorExpressionString(),validator.toString(), Boolean.valueOf(vctx.isValid()),
					((BindContextImpl)vctx.getBindContext()).getValidatorArgs(),null));
		}
//		//collect notify change
//		collectNotifyChange(validator,vctx);
	}
	
//	private void collectNotifyChange(Validator validator, ValidationContext vctx) {
//		//collect notify change
//		ValueReference ref = getValueReference(vctx.getBindContext());
//		//for special case that a form bind to vm directly, ex @form(save=vm after 'cmd1'), ref will be null
//		if(ref!=null){
//			BindELContext.addNotifys(getValidatorMethod(validator.getClass()), ref.getBase(), null, null, vctx.getBindContext());
//		}else{
//			final BindEvaluatorX eval = getBinder().getEvaluatorX();
//			final ExpressionX expr = getBaseExpression(eval);
//			if (expr != null) {
//				final Object base = eval.getValue(vctx.getBindContext(), getComponent(), expr);
//				BindELContext.addNotifys(getValidatorMethod(validator.getClass()), base, null, null, vctx.getBindContext());
//			}			
//		}
//	}
	
	//get and cache value reference of this binding
	private ValueReference getValueReference(BindContext ctx){
		ValueReference valref = (ValueReference) getAttribute(ctx, $VALUEREF$);
		if (valref == null) {
			final Component comp = getComponent();//ctx.getComponent();
			final BindEvaluatorX eval = getBinder().getEvaluatorX();
			valref = eval.getValueReference(ctx, comp, _accessInfo.getProperty());
			if(valref==null){
				throw new UiException("value reference not found by expression ["+
						_accessInfo.getProperty().getExpressionString()+"], check if you are trying to save to a variable only expression");
			}
			setAttribute(ctx, $VALUEREF$, valref);
		}
		return valref;
	}
	
	private Method getValidatorMethod(Class<? extends Validator> cls) {
		try {
			return cls.getMethod("validate", new Class[] {ValidationContext.class});
		} catch (NoSuchMethodException e) {
			//ignore
		}
		return null; //shall never come here
	}
}
