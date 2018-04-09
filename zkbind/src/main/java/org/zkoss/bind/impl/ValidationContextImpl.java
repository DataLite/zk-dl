/* ValidationContextImpl.java

	Purpose:
		
	Description:
		
	History:
		2011/9/29 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.bind.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Property;
import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.proxy.FormProxyObject;
/**
 * the default implementation of validation context
 * @author dennis
 * @since 6.0.0
 */
public class ValidationContextImpl implements ValidationContext{

	private boolean _valid = true;//default validation result is true
	
	//ZK-1819 ValidationContext support a new flag that can identify the value of single field whether is valid when use form binding
	private boolean _localValid = true;
	private String _command;
	private Property _property; //main property
	private Map<String,Property[]> _properties; //related properties
	private BindContext _ctx;
	
	private static final String BASED_VALIDATION_PROPERTIES = "$BASED_VALIDATION_PROPS$";
	
	public ValidationContextImpl(String command, Property property,Map<String,Property[]> properties, BindContext ctx,boolean valid){
		this._command = command;
		this._property = property;
		this._properties = properties;
		this._ctx = ctx;
		this._valid = valid;
	}

	
	public BindContext getBindContext() {
		return _ctx;
	}

	
	public String getCommand() {
		return _command;
	}

	
	public Map<String,Property[]> getProperties() {
		return _properties;
	}
	
	
	public Property[] getProperties(String name) {
		return _properties.get(name);
	}
	
	
	@SuppressWarnings("unchecked")
	public Map<String,Property> getProperties(Object base){
		if(base==null) throw new IllegalArgumentException("base object is null");
		Map<Object,Map<String,Property>> m =  (Map<Object,Map<String,Property>>)_ctx.getAttribute(BASED_VALIDATION_PROPERTIES);
		if(m==null){
			_ctx.setAttribute(BASED_VALIDATION_PROPERTIES,m = new HashMap<Object,Map<String,Property>>());
		}
			
		Map<String,Property> mp = m.get(base);
		if(mp!=null) return mp;
		mp = new HashMap<String,Property>();
		m.put(base, mp);
		
		for(Entry<String, Property[]> e:_properties.entrySet()){
			for(Property p:e.getValue()) {
				if (base.equals(p.getBase())) {
					mp.put(e.getKey(), p);
				}
			}
		}
		return mp;
	}
	
	
	public Object getValidatorArg(String key) {
		return _ctx.getValidatorArg(key);
	}
	
	
	public Property getProperty() {
		return _property;
	}

	
	public boolean isValid() {
		return _valid;
	}

	public boolean isLocalValid(){
		return _localValid;
	}
	
	public void setInvalid(){
		this._valid = this._localValid = false;
	}

}
