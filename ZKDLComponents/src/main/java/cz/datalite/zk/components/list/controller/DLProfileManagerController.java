package cz.datalite.zk.components.list.controller;

import cz.datalite.zk.components.list.DLListboxProfile;

public interface DLProfileManagerController<T> {

	void onLoadProfile();
	
	void onSaveProfile();
	
	void onEditProfile();
	
	void onEditProfileOk(DLListboxProfile profile);

	boolean selectDefaultProfile(boolean onCreate);

}