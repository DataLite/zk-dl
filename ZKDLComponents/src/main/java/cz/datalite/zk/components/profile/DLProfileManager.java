package cz.datalite.zk.components.profile;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;

import cz.datalite.zk.components.list.DLListboxProfile;
import cz.datalite.zk.components.list.controller.DLProfileManagerController;
import cz.datalite.zk.components.lovbox.DLLovbox;
import cz.datalite.zk.components.lovbox.DLLovboxGeneralController;

/**
 * Bar with advanced tools for managing the listbox profiles.
 */
public class DLProfileManager<T> extends Hbox {

	private static final long serialVersionUID = 1L;
	
	protected static final String CONST_EVENT = Events.ON_CLICK;
    protected static final String CONST_DEFAULT_ICON_PATH = "~./dlzklib/img/";
    protected static final String CONST_IMAGE_SIZE = "20px";
    protected static final String CONST_FILTER_MANAGER = Labels.getLabel( "listbox.tooltip.filterManager" );
    
    private DLProfileManagerController<T>	controller;
    private DLLovbox<DLListboxProfile> 		profilesLovbox;

    public DLProfileManager() {
		super();
		this.setPack("center");
		this.setSclass("datalite-listbox-manager");
		
		this.profilesLovbox = new DLLovbox<DLListboxProfile>();
		this.profilesLovbox.setCreateQuickFilter(true);
		this.profilesLovbox.setQuickFilterAll(true);
		this.profilesLovbox.setCreatePaging(false);
		this.profilesLovbox.setClearButton(false);
		this.profilesLovbox.setLabelProperty("name");
		this.profilesLovbox.setMultiple(false);     
				
		this.appendChild(this.profilesLovbox);
		
		Button loadProfileBtn = new Button(Labels.getLabel("listbox.profileManager.load"));
		loadProfileBtn.setTooltip(Labels.getLabel("listbox.profileManager.load.tooltip"));
		loadProfileBtn.addEventListener(CONST_EVENT, new EventListener<Event>() {
			public void onEvent(final Event event) {
				controller.onLoadProfile();
			}
		});		
		this.appendChild(loadProfileBtn);
		
		Button saveProfileBtn = new Button(Labels.getLabel("listbox.profileManager.save"));
		saveProfileBtn.setTooltip(Labels.getLabel("listbox.profileManager.save.tooltip"));
		saveProfileBtn.addEventListener(CONST_EVENT, new EventListener<Event>() {
			public void onEvent(final Event event) {
				controller.onSaveProfile();
			}
		});		
		this.appendChild(saveProfileBtn);
    }
    
    
	public void setProfilesLovboxController(DLLovboxGeneralController<DLListboxProfile> controller) {
        AnnotateDataBinder binder = new AnnotateDataBinder();
        this.profilesLovbox.setAttribute("binder", binder);

        this.profilesLovbox.afterCompose();
		this.profilesLovbox.setController(controller);
		
        binder.init(this.profilesLovbox, true);
        binder.loadAll();
    }

	public void setController(final DLProfileManagerController<T> controller) {
		this.controller = controller;
	}
	
}
