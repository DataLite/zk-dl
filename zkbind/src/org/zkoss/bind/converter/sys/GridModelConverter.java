/* GridModelConverter.java

	Purpose:
		
	Description:
		
	History:
		2011/12/12 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.converter.sys;

import org.zkoss.bind.Converter;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModel;

/**
 * The {@link Converter} implementation of the grid for converting collection to ListModel and vice versa.
 * @author dennis
 * @since 6.0.0
 */
public class GridModelConverter extends AbstractListModelConverter<Grid>{
	private static final long serialVersionUID = 1463169907348730644L;
	
	protected ListModel<?> getComponentModel(Grid comp) {
		return comp.getListModel();
	}
}
