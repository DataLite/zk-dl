package cz.datalite.zk.components.list.controller;

import cz.datalite.zk.components.profile.DLListboxProfile;

public interface DLProfileManagerController<T> {

	void onLoadProfile(boolean refresh);

	void onEditProfile(Long idProfile);

	void onEditProfileOk(DLListboxProfile profile, boolean saveColumnModel, boolean saveFilterModel);

	void onDeleteProfile(Long idProfile);

	boolean selectDefaultProfile(boolean onCreate);

}