/* Property.java

	Purpose:
		
	Description:
		
	History:
		Jul 26, 2011 5:00:34 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind;

/**
 * Represent a property of a base object in the binding.
 * @author henrichen
 * @since 6.0.0
 */
public interface Property {
	/**
	 * Returns the base object of this property.
	 * @return the base object of this property.
	 */
	public Object getBase();
	
	/**
	 * Returns the value object of this property.
	 * @return the value object of this property.
	 */
	public Object getValue();
	
	/**
	 * Returns the name of this property. 
	 * @return the name of this property. 
	 */
	public String getProperty();
}
