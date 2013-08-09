package cz.datalite.zk.components.list.controller;

import cz.datalite.zk.components.list.DLListboxProfile;

public interface DLProfileManagerController<T> {

	void onLoadProfile();

	void onEditProfile(Long idProfile);

	void onEditProfileOk(DLListboxProfile profile, boolean saveColumnModel, boolean saveFilterModel);

	void onDeleteProfile(Long idProfile);

	boolean selectDefaultProfile(boolean onCreate);

	void fireChanges();

}