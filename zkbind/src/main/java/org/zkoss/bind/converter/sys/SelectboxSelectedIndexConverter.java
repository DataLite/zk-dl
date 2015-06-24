/* SelectboxSelectedIndexConverter.java

	Purpose:
		
	Description:
		
	History:
		Feb 3, 2012 6:10:20 PM, Created by dennis

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.converter.sys;

import org.zkoss.zul.ListModel;
import org.zkoss.zul.Selectbox;

/**
 * Convert selected index to bean and vice versa.
 * @author dennis
 * @since 6.0.0
 */
public class SelectboxSelectedIndexConverter extends AbstractSelectedIndexConverter<Selectbox> {
	private static final long serialVersionUID = 201108171811L;
	
	protected ListModel<?> getComponentModel(Selectbox comp) {
		return comp.getModel();
	}
}
