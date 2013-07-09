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
import cz.datalite.zk.components.lovbox.DLLovboxPopupComponentPosition;

/**
 * Bar with advanced tools for managing the listbox profiles.
 */
public class DLProfileManager<T> extends Hbox {

	private static final long serialVersionUID = 1L;
	
	protected static final String CONST_EVENT = Events.ON_CLICK;
    protected static final String CONST_DEFAULT_ICON_PATH = "~./dlzklib/img/";
    protected static final String CONST_IMAGE_SIZE = "20px";
    protected static final String CONST_FILTER_MANAGER = Labels.getLabel( "listbox.tooltip.filterManager" );
    
    /**
	 * default profile will be loaded from persistent storage and applied to
	 * listbox (onCreate) if set to true.
	 */
    private boolean applyDefaultProfile = false;
    
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
		
		// hack, keep band popup open after select
		this.profilesLovbox.addEventListener(Events.ON_SELECT, new EventListener<Event>() {
    		@Override
			public void onEvent(Event event) throws Exception {
				profilesLovbox.open();
			}        	
    	});
				
		// bar with buttons to load and save profile
		Hbox buttonBar = new Hbox();		
		
		// load profile on double click
		this.profilesLovbox.addEventListener(Events.ON_DOUBLE_CLICK, new EventListener<Event>() {
    		@Override
    		public void onEvent(Event event) throws Exception {		
    			controller.onLoadProfile();
				profilesLovbox.close();
    		}        	
    	});   
		
		final Button loadProfileBtn = new Button(Labels.getLabel("listbox.profileManager.load"));
		loadProfileBtn.setTooltip(Labels.getLabel("listbox.profileManager.load.tooltip"));
		loadProfileBtn.addEventListener(CONST_EVENT, new EventListener<Event>() {
			public void onEvent(final Event event) {
				controller.onLoadProfile();
				profilesLovbox.close();
			}
		});		
		buttonBar.appendChild(loadProfileBtn);
		
		final Button saveProfileBtn = new Button(Labels.getLabel("listbox.profileManager.save"));
		saveProfileBtn.setTooltip(Labels.getLabel("listbox.profileManager.save.tooltip"));
		saveProfileBtn.addEventListener(CONST_EVENT, new EventListener<Event>() {
			public void onEvent(final Event event) {
				controller.onSaveProfile();
				profilesLovbox.close();
			}
		});		
		buttonBar.appendChild(saveProfileBtn);
		
		final Button createProfileButton = new Button("new");
		createProfileButton.setTooltip(Labels.getLabel("listbox.profileManager.save.tooltip"));
		createProfileButton.addEventListener(CONST_EVENT, new EventListener<Event>() {
			public void onEvent(final Event event) {
				controller.onEditProfile();
				profilesLovbox.close();
			}
		});		
		
		this.appendChild(createProfileButton);
		//buttonBar.appendChild(createProfileButton);
		
		this.profilesLovbox.addComponentToPopup(DLLovboxPopupComponentPosition.POSITION_BOTTOM, buttonBar);
		this.appendChild(this.profilesLovbox);
		
		// disable save button when profile is not saveable
		this.profilesLovbox.addEventListener(Events.ON_SELECT, new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				if (profilesLovbox.getSelectedItem().isSaveable()) {
					saveProfileBtn.setVisible(true);
				} else {
					saveProfileBtn.setVisible(false);
				}
			}			
		});
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


	public boolean isApplyDefaultProfile() {
		return applyDefaultProfile;
	}


	public void setApplyDefaultProfile(boolean applyDefaultProfile) {
		this.applyDefaultProfile = applyDefaultProfile;
	}	
}
