package cz.datalite.zk.components.list.controller.impl;

import cz.datalite.zk.components.list.controller.DLProfileManagerController;

/**
 * Default implementation of controller. It is used because of
 * back compatibility.
 */
public class DLDefaultProfileManagerControllerImpl<T> implements DLProfileManagerController<T> {

	@Override
	public void onLoadProfile() {
		throw new UnsupportedOperationException("Not supported yet.");		
	}

	@Override
	public void onSaveProfile() {
		throw new UnsupportedOperationException("Not supported yet.");		
	}
	
}
