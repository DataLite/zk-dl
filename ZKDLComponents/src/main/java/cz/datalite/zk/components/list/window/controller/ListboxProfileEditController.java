package cz.datalite.zk.components.list.window.controller;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zul.Messagebox;

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
	public void save(@BindingParam("saveAgendaSettings") Boolean saveAgendaSettings) {
		Events.postEvent(new Event("onSave", this.view, saveAgendaSettings));
		this.view.detach();
	}
	
	@Command	
	public void delete() {
		Messagebox.show(Labels.getLabel("listbox.profileManager.delete.confirm", new String[]{this.profile.getName()}),
				Labels.getLabel("listbox.profileManager.delete.tooltip"),
				Messagebox.OK | Messagebox.NO, Messagebox.QUESTION,
				new EventListener<Event>() {
					public void onEvent(final Event event) {
						if (event.getData().equals(Messagebox.OK)) {
							Events.postEvent(new Event("onDelete", view, null));
							view.detach();
						}
					}
		});
	}

	public DLListboxProfile getProfile() {
		return profile;
	}

	public void setProfile(DLListboxProfile profile) {
		this.profile = profile;
	}    
}
