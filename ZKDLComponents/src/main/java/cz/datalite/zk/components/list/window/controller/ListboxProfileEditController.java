package cz.datalite.zk.components.list.window.controller;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;

import cz.datalite.zk.components.list.DLListboxProfile;
import cz.datalite.zk.components.list.controller.DLProfileManagerController;

/**
 * Controller for the popup window used to create/edit profile.
 */
public class ListboxProfileEditController {
	
	DLListboxProfile 	profile;	
	
	Component 			view;
	DLProfileManagerController profileManagerController;
	
	@Init
	public void init(@ContextParam(ContextType.VIEW) Component view, @ExecutionArgParam("profile") DLListboxProfile profile,
			@ExecutionArgParam("profileManagerController") DLProfileManagerController profileManagerController) {
		Selectors.wireComponents(view, this, false);
		
		this.profile = profile;
		this.view = view;		
		this.profileManagerController = profileManagerController;
    }
	
	@Command
	public void close() {
		this.view.detach();
	}
	
	@Command
	public void save() {
		this.profileManagerController.onEditProfileOk(this.profile);
		this.view.detach();
	}

	public DLListboxProfile getProfile() {
		return profile;
	}

	public void setProfile(DLListboxProfile profile) {
		this.profile = profile;
	}    
}
