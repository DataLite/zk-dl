package cz.datalite.zk.components.list.controller.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.lang.Library;
import org.zkoss.zk.ui.Sessions;

import cz.datalite.helpers.StringHelper;
import cz.datalite.zk.components.list.DLListboxFilterController;
import cz.datalite.zk.components.list.DLListboxGeneralController;
import cz.datalite.zk.components.list.DLListboxProfile;
import cz.datalite.zk.components.list.controller.DLListboxExtController;
import cz.datalite.zk.components.list.controller.DLProfileManagerController;
import cz.datalite.zk.components.list.service.ProfileService;
import cz.datalite.zk.components.list.service.ProfileServiceFactory;
import cz.datalite.zk.components.lovbox.DLLovboxGeneralController;
import cz.datalite.zk.components.profile.DLProfileManager;

/**
 * Implementation of the controller for the Listbox profile manager which
 * provides extended tools.
 */
public class DLProfileManagerControllerImpl<T> implements DLProfileManagerController<T> {

	private static final Logger LOGGER = LoggerFactory.getLogger(DLProfileManagerControllerImpl.class);
	
	// master controller
	private final DLListboxExtController<T> masterController;
	
	// view
	private final DLProfileManager<T> dlProfileComponent;
    
    // lovbox controller
	private final DLLovboxGeneralController<DLListboxProfile> profilesCtl;
    
    /** profile service used to load/store profiles from/to persistent storage */
    private final ProfileService profileService;

	public DLProfileManagerControllerImpl(final DLListboxExtController<T> masterController, final DLProfileManager<T> dlProfileComponent) {
        this.masterController = masterController;
        this.dlProfileComponent = dlProfileComponent;
        this.dlProfileComponent.setController(this);
        
        String profileServiceFactoryClass = Library.getProperty("zk-dl.listbox.profile.provider");
        if (!StringHelper.isNull(profileServiceFactoryClass)) {
            try {
                Object profileServiceFactory = Class.forName(profileServiceFactoryClass).newInstance();
                
				if (ProfileServiceFactory.class.isAssignableFrom(profileServiceFactory.getClass())) {
					this.profileService = ((ProfileServiceFactory) profileServiceFactory).create(Sessions.getCurrent().getNativeSession());
				} else {
					throw new IllegalArgumentException();
				}
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException("ProfileServiceFactory class is not assignable from '" + profileServiceFactoryClass + "'. Check zk.xml parameter zk-dl.listbox.profile.provider.", e);
            } catch (InstantiationException e) {
                throw new IllegalStateException("Unable to instantiate class '" + profileServiceFactoryClass + "'. Check zk.xml parameter zk-dl.listbox.profile.provider.", e);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Unable to instantiate class '" + profileServiceFactoryClass + "'. Check zk.xml parameter zk-dl.listbox.profile.provider.", e);
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("Class not found: '" + profileServiceFactoryClass + "'. Check zk.xml parameter zk-dl.listbox.profile.provider.", e);
            }
        } else {
        	throw new IllegalStateException("Unable to instantiate class '" + profileServiceFactoryClass + "'. File zk.xml must contain parameter zk-dl.listbox.profile.provider to use DLProfileManager.");
        }
        
		this.profilesCtl = new DLLovboxGeneralController<DLListboxProfile>(
				new DLListboxFilterController<DLListboxProfile>(this.getClass().getName() + "ProfilesLovbox") {
					@Override
					protected List<DLListboxProfile> loadData() {
						return profileService.findAll(masterController.getSessionName());
					}
				});
		
		this.dlProfileComponent.setProfilesLovboxController(profilesCtl);
    }

	@Override
	public void onLoadProfile() {
		DLListboxProfile selectedProfile = this.profilesCtl.getSelectedItem();
		
		if (selectedProfile != null) {			
			((DLListboxGeneralController<T>) this.masterController).applyProfile(selectedProfile);
		}				
	}

	public void onSaveProfile() {
		DLListboxProfile selectedProfile = this.profilesCtl.getSelectedItem();

		if (selectedProfile != null) {
			DLListboxProfile profile = ((DLListboxGeneralController<T>) this.masterController).createProfile();

			selectedProfile.setColumnModelJsonData(profile.getColumnModelJsonData());
			selectedProfile.setFilterModelJsonData(profile.getFilterModelJsonData());
			selectedProfile.setColumnsHashCode(profile.getColumnsHashCode());

			this.profileService.makePersistent(selectedProfile);
		}
		
	}
}
