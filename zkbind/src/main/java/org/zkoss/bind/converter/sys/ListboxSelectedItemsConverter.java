/* ListboxSelectedItemsConverter.java

	Purpose:
		
	Description:
		
	History:
		Aug 17, 2011 6:10:20 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.converter.sys;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.lang.Classes;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ext.Selectable;

/**
 * Convert listbox selected listitems to bean and vice versa.
 * @author dennis
 * @since 6.0.0
 */
public class ListboxSelectedItemsConverter implements Converter, java.io.Serializable {
	private static final long serialVersionUID = 201108171811L;
	
	@SuppressWarnings("unchecked")
	public Object coerceToUi(Object val, Component comp, BindContext ctx) {
		Listbox lbx = (Listbox) comp;
		final ListModel<?> model = lbx.getModel();
		if(model !=null && !(model instanceof Selectable)){
			//model has to implement Selectable if binding to selectedItems
  			throw new UiException("model doesn't implement Selectable");
  		}
		
  		final Set<Listitem> items = new LinkedHashSet<Listitem>();
		Set<Object> vals = val == null ? null : (Set<Object>) Classes.coerce(LinkedHashSet.class, val);
		
	  	if (vals != null && vals.size()>0) {
	  		if(model!=null){
	  			((Selectable<Object>)model).setSelection(vals);
	  		}else{
	  			//no model case
			  	for (final Iterator<?> it = lbx.getItems().iterator(); it.hasNext();) {
			  		final Listitem li = (Listitem) it.next();
			  		Object bean = li.getValue();
			  		
			  		if (vals.contains(bean)) {
			  			items.add(li);
			  		}
			  	}
	  		}
	  	}else if(model!=null && !((Selectable<Object>)model).isSelectionEmpty()){//model !=null and no selection
	  		((Selectable<Object>)model).clearSelection();
	  	}
	  	
	  	return model == null ? items : IGNORED_VALUE;
	}

	@SuppressWarnings("unchecked")
	public Object coerceToBean(Object val, Component comp, BindContext ctx) {
		Set<Object> vals = new LinkedHashSet<Object>();
		if (val != null) {
			final Listbox lbx = (Listbox) comp;
	  		final ListModel<?> model = lbx.getModel();
	  		if(model !=null && !(model instanceof Selectable)){
	  			throw new UiException("model doesn't implement Selectable");
	  		}
	  		
	  		if(model!=null){
	  			Set<?> selection = ((Selectable<?>)model).getSelection();
	  			if(selection!=null && selection.size()>0){
	  				for(Object o:selection){
	  					vals.add(o);
	  				}
	  			}
	  		} else{//no model
	  			final Set<Listitem> items = (Set<Listitem>)Classes.coerce(HashSet.class, val);
		  		for(Listitem item : items){
			  		vals.add(item.getValue());
		  		}
	  		}
	  	}
	 	return vals;
	}

}
