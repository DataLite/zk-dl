/** BeanProxyHandler.java.

	Purpose:
		
	Description:
		
	History:
		12:16:01 PM Dec 25, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.bind.proxy;

import java.io.Serializable;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.impl.AllocUtil;
import org.zkoss.bind.xel.zel.BindELContext;
import org.zkoss.lang.Classes;
import org.zkoss.lang.reflect.Fields;
import org.zkoss.zk.ui.UiException;

import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;

/**
 * A bean proxy handler
 * 
 * @author jumperchen
 * @since 8.0.0
 */
public class BeanProxyHandler<T> implements MethodHandler, Serializable {
	protected static MethodFilter BEAN_METHOD_FILTER = new MethodFilter() {
		public boolean isHandled(Method m) {
			if (m.getName().startsWith("set")
					|| m.getName().startsWith("get")
					|| m.getName().startsWith("is")
					|| m.getName().equals("hashCode"))
				return true;
			try {
				FormProxyObject.class.getMethod(m.getName(), m.getParameterTypes());
			    return true;
			} catch (NoSuchMethodException e) {
			    return false;
			}
		}

	};
	protected T _origin;

	protected Map<String, Object> _cache;
	protected Set<String> _dirtyFieldNames; // field name that is dirty

	public BeanProxyHandler(T origin) {
		_origin = origin;
	}

	private void addCache(String key, Object value) {
		_cache = AllocUtil.inst.putMap(_cache, key, value);
	}

	private void addDirtyField(String field) {
		_dirtyFieldNames = AllocUtil.inst.addSet(_dirtyFieldNames, field);
	}

	public Object invoke(Object self, Method method, Method proceed,
			Object[] args) throws Exception {
		try {
			if (method.getName().equals("hashCode")) {
				int a = (_origin != null) ? (Integer) method.invoke(_origin, args) : 0;
				return 37 * 31 + a; 
			}
			if (method.getDeclaringClass().isAssignableFrom(
					FormProxyObject.class)) {
				if ("submitToOrigin".equals(method.getName())) {
					if (_dirtyFieldNames != null && _origin != null) {
						for (Map.Entry<String, Object> me : _cache.entrySet()) {
							final Object value = me.getValue();
							if (value instanceof FormProxyObject) {
								((FormProxyObject) value)
										.submitToOrigin((BindContext) args[0]);
							} else if (_dirtyFieldNames.contains(me.getKey())) {
								final String mname = toSetter(me.getKey());
								try {
									final Method m = Classes.getMethodByObject(_origin.getClass(), mname, new Object[]{value});
									m.invoke(_origin, Classes.coerce(m.getParameterTypes()[0], value));
									BindELContext.addNotifys(m, _origin, me.getKey(),
											value, (BindContext) args[0]);
								} catch (NoSuchMethodException e) {
									throw UiException.Aide.wrap(e);
								}
							}
						}
						_dirtyFieldNames.clear();
					}
				} else if ("getOriginObject".equals(method.getName())) {
					return _origin;
				} else if ("resetFromOrigin".equals(method.getName())) {
					if (_dirtyFieldNames != null)
						_dirtyFieldNames.clear();
					if (_cache != null)
						_cache.clear();
				} else if ("isFormDirty".equals(method.getName())) {
						boolean dirty = false;
						
						if (_dirtyFieldNames != null && _cache != null) {
							// If the dirty field is a form proxy object it may not be dirty.
							// But once it contains a non-form proxy object, it must be dirty.
							for (Map.Entry<String, Object> me : _cache.entrySet()) {
								final Object value = me.getValue();
								if (value instanceof FormProxyObject) {
									if (((FormProxyObject) value).isFormDirty()) {
										dirty = true;
										break;
									}
								} else if (_dirtyFieldNames.contains(me.getKey())) {
									dirty = true;
									break;
								}
							}
						}
						return dirty;
				} else if ("addFormProxyObjectListener".equals(method.getName())) {
					//F80: formProxyObject support notifyChange with Form.isDirty
					return null;
				} else {
					throw new IllegalAccessError("Not implemented yet for FormProxyObject interface: [" + method.getName() + "]");
				}
			} else {
				if (method.getName().startsWith("get")) {
					final String attr = toAttrName(method);
					Object value = null;
					if (_cache == null) {
						if (_origin == null)
							return null;
						Object invoke = method.invoke(_origin, args);
						if (invoke != null) {
							value = ProxyHelper.createProxyIfAny(invoke);
							addCache(attr, value);
							if (value instanceof FormProxyObject) {
								addDirtyField(attr); // it may be changed.
							}
						}
					} else {
						value = _cache.get(attr);
						if (!_cache.containsKey(attr)) {
							if (_origin == null)
								return null;
							Object invoke = method.invoke(_origin, args);
							if (invoke != null) {
								value = ProxyHelper.createProxyIfAny(invoke);
								addCache(attr, value);
								if (value instanceof FormProxyObject) {
									addDirtyField(attr); // it may be changed.
								}
							}
						}
					}
					return value;
				} else if (method.getName().startsWith("is")) {
					final String attr = toAttrName(method, 2);
					Object value = null;
					if (_cache == null) {
						if (_origin == null)
							return false;
						value = method.invoke(_origin, args);
					} else {
						value = _cache.get(attr);
						if (!_cache.containsKey(attr)) {
							if (_origin == null)
								return false;
							value = method.invoke(_origin, args);
						}
					}
					return value;
				} else {
					String attrName = toAttrName(method);
					addCache(attrName, args[0]);
					addDirtyField(attrName);
				}
			}
		} catch (Exception e) {
			throw UiException.Aide.wrap(e);
		}
		return null;
	}

	protected static String toSetter(String attr) {
		return capitalize("set", attr);
	}

	protected static String toGetter(String attr) {
		return capitalize("get", attr);
	}

	protected static String capitalize(String prefix, String attr) {
		return new StringBuilder(prefix)
				.append(Character.toUpperCase(attr.charAt(0)))
				.append(attr.substring(1)).toString();
	}

	protected static String toAttrName(Method method, int prefix) {
		final String name = method.getName();
		final String attrName = name.substring(prefix, name.length());
		return new StringBuilder(attrName.length()).append(Character.toLowerCase(attrName.charAt(0)))
		.append(attrName.substring(1)).toString();
	}

	protected static String toAttrName(Method method) {
		return toAttrName(method, 3);
	}
}
