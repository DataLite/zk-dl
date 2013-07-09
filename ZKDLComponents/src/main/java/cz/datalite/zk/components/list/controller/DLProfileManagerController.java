package cz.datalite.zk.components.list.controller;

import cz.datalite.zk.components.list.DLListboxProfile;

public interface DLProfileManagerController<T> {

	void onLoadProfile();
	
	void onSaveProfile();
	
	void onEditProfile(boolean create);
	
	void onEditProfileOk(DLListboxProfile profile);
	
	void onDeleteProfile();

	boolean selectDefaultProfile(boolean onCreate);

}