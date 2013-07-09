package cz.datalite.zk.components.list.window.controller;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.Selectors;

import cz.datalite.zk.components.list.DLListboxProfile;

/**
 * Controller for the popup window used to create/edit profile.
 */
public class ListboxProfileEditController {
	
	DLListboxProfile 	profile;	
	Component 			view;
	
	@Init
	public void init(@ContextParam(ContextType.VIEW) Component view, @ExecutionArgParam("profile") DLListboxProfile profile) {
		Selectors.wireComponents(view, this, false);
		
		this.profile = profile;
		this.view = view;
    }
	
	@Command
	public void close() {
		this.view.detach();
	}
	
	@Command
	public void save() {
		Events.postEvent(new Event("onSave", this.view, null));
		this.view.detach();
	}

	public DLListboxProfile getProfile() {
		return profile;
	}

	public void setProfile(DLListboxProfile profile) {
		this.profile = profile;
	}    
}
