package org.zkoss.bind;

import org.zkoss.bind.sys.Binding;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;

import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of BindContext.
 */
public class BindContextMock implements BindContext {

	Map<String, Object> converterArgs = new HashMap<>();
	Map<String, Object> validatorArgs = new HashMap<>();
	Map<String, Object> commandArgs = new HashMap<>();
	Map<String, Object> bindingArgs = new HashMap<>();
	Map<Object, Object> attributes = new HashMap<>();

	public Object putConverterArgs(String key, Object value) {
		return converterArgs.put(key, value);
	}

	public Object putValidatorArgs(String key, Object value) {
		return validatorArgs.put(key, value);
	}

	public Object putCommandArgs(String key, Object value) {
		return commandArgs.put(key, value);
	}

	public Object putBindingArgs(String key, Object value) {
		return bindingArgs.put(key, value);
	}

	@Override
	public Binder getBinder() {
		return null;
	}

	@Override
	public Binding getBinding() {
		return null;
	}

	@Override
	public Object getAttribute(Object key) {
		return attributes.get(key);
	}

	@Override
	public Object setAttribute(Object key, Object value) {
		return attributes.put(key, value);
	}

	@Override
	public Map<Object, Object> getAttributes() {
		return attributes;
	}

	@Override
	public boolean isSave() {
		return false;
	}

	@Override
	public String getCommandName() {
		return null;
	}

	@Override
	public Component getComponent() {
		return null;
	}

	@Override
	public Event getTriggerEvent() {
		return null;
	}

	@Override
	public Object getCommandArg(String key) {
		return commandArgs.get(key);
	}

	@Override
	public Object getBindingArg(String key) {
		return bindingArgs.get(key);
	}

	@Override
	public Object getConverterArg(String key) {
		return converterArgs.get(key);
	}

	@Override
	public Object getValidatorArg(String key) {
		return validatorArgs.get(key);
	}
}
