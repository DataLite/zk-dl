package cz.datalite.zk.components.profile;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
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
		
		final Button loadProfileBtn = this.createButtonWithTooltip(buttonBar, "listbox.profileManager.load", true, new EventListener<Event>() {
			public void onEvent(final Event event) {
				controller.onLoadProfile();
				profilesLovbox.close();
			}
		});
		
		this.createButtonWithTooltip(buttonBar, "listbox.profileManager.create", true, new EventListener<Event>() {
			public void onEvent(final Event event) {
				controller.onEditProfile(true);
				profilesLovbox.close();
			}
		});
		
		final Button saveProfileBtn = this.createButtonWithTooltip(buttonBar, "listbox.profileManager.save", false, new EventListener<Event>() {
			public void onEvent(final Event event) {
				controller.onSaveProfile();
				profilesLovbox.close();
			}
		});		
		
		final Button editProfileBtn = this.createButtonWithTooltip(buttonBar, "listbox.profileManager.edit", false, new EventListener<Event>() {
			public void onEvent(final Event event) {
				controller.onEditProfile(false);
				profilesLovbox.close();
			}
		});
		
		final Button deleteProfileBtn = this.createButtonWithTooltip(buttonBar, "listbox.profileManager.delete", false, new EventListener<Event>() {
			public void onEvent(final Event event) {
				controller.onDeleteProfile();
				profilesLovbox.close();
			}
		});

		this.profilesLovbox.addComponentToPopup(DLLovboxPopupComponentPosition.POSITION_BOTTOM, buttonBar);
		this.appendChild(this.profilesLovbox);
		
		// show button on select profile, disable save button when profile is not editable
		this.profilesLovbox.addEventListener(Events.ON_SELECT, new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				// hack, keep popup open after select
				profilesLovbox.open();
				
				loadProfileBtn.setVisible(true);				
				
				if (profilesLovbox.getSelectedItem().isEditable()) {
					saveProfileBtn.setVisible(true);
					editProfileBtn.setVisible(true);
					deleteProfileBtn.setVisible(true);
				} else {
					saveProfileBtn.setVisible(false);
					editProfileBtn.setVisible(false);
					deleteProfileBtn.setVisible(false);
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

	public Button createButtonWithTooltip(Component parent, String labelKey, boolean visible, EventListener<Event> listener) {
		final Button button = new Button(Labels.getLabel(labelKey));
		button.setTooltiptext(Labels.getLabel(labelKey + ".tooltip"));
		button.setVisible(visible);

		button.addEventListener(CONST_EVENT, listener);
		parent.appendChild(button);

		return button;
	}
}
