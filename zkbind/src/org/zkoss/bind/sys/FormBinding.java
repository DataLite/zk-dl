/* FormBinding.java

	Purpose:
		
	Description:
		
	History:
		Jul 26, 2011 4:00:09 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.sys;

import org.zkoss.bind.Form;
import org.zkoss.xel.ExpressionX;

/**
 * A binding tells how to deal with Load and Save between a form and a bean.
 * @author henrichen
 * @since 6.0.0
 */
public interface FormBinding extends Binding {
	/**
	 * Returns the implicit form associated with this form binding.
	 * @return the implicit form associated with this form binding.
	 */
	public Form getFormBean();
	
	/**
	 * Returns the associated form id of this Binding.
	 * @return the associated attribute name of this component.
	 */
	public String getFormId();
	
	/**
	 * Returns the associated command name of this binding; null if not specified.
	 * @return the associated command name of this binding; null if not specified.
	 */
	public String getCommandName();
	
	/**
	 * Returns the property expression script of this binding.
	 * @return the property expression script of this binding. 
	 */
	public String getPropertyString();
	
	/**
	 * Returns the condition type of this binding
	 */
	public ConditionType getConditionType();
	
	/**
	 * Returns the field expression from the given field.
	 * @since 8.0.0
	 */
	public ExpressionX getFieldExpression(BindEvaluatorX eval, String field);

	/**
	 * Returns the form expression from the given field.
	 * @since 8.0.0
	 */
	public ExpressionX getFormExpression(BindEvaluatorX eval, String field);

}
