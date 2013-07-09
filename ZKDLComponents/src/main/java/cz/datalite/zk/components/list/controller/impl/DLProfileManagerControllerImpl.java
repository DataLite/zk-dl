package cz.datalite.zk.components.list.controller.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.lang.Library;
import org.zkoss.zk.ui.Sessions;

import cz.datalite.helpers.StringHelper;
import cz.datalite.helpers.ZKDLResourceResolver;
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
	private final DLProfileManager<T> dlProfileManagerComponent;

	// lovbox controller
	private final DLLovboxGeneralController<DLListboxProfile> profilesCtl;

	/** profile service used to load/store profiles from/to persistent storage */
	private final ProfileService profileService;

	@SuppressWarnings("unchecked")
	public DLProfileManagerControllerImpl(final DLListboxExtController<T> masterController, final DLProfileManager<T> dlProfileComponent, ProfileService service) {
		this.masterController = masterController;
		this.dlProfileManagerComponent = dlProfileComponent;
		this.dlProfileManagerComponent.setController(this);

		if (service != null) {
			this.profileService = service;
		} else {        
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
				throw new IllegalStateException("Unable to instantiate ProfileServiceFactory class. File zk.xml must contain parameter zk-dl.listbox.profile.provider to use DLProfileManager.");
			}
		}

		this.profilesCtl = new DLLovboxGeneralController<DLListboxProfile>(
				new DLListboxFilterController<DLListboxProfile>(this.getClass().getName() + "ProfilesLovbox") {
					@Override
					protected List<DLListboxProfile> loadData() {
						return profileService.findAll(masterController.getSessionName());
					}
				});

		this.dlProfileManagerComponent.setProfilesLovboxController(this.profilesCtl);
	}

	@Override
	public boolean selectDefaultProfile(boolean onCreate) {
		if (onCreate && !this.dlProfileManagerComponent.isApplyDefaultProfile()) {
			return false;
		}

		// TODO findDefault method
		List<DLListboxProfile> profiles = this.profileService.findAll();

		for (DLListboxProfile profile : profiles) {
			if (profile.isDefaultProfile()) {
				this.profilesCtl.getLovBox().setSelectedItem(profile);
				return true;
			}
		}

		return false;
	}

	@Override
	public void onLoadProfile() {
		DLListboxProfile selectedProfile = this.profilesCtl.getSelectedItem();

		if (selectedProfile != null) {			
			((DLListboxGeneralController<T>) this.masterController).applyProfile(selectedProfile);
		}
	}

	@Override
	public void onSaveProfile() {
		DLListboxProfile selectedProfile = this.profilesCtl.getSelectedItem();

		if (selectedProfile != null) {
			// save to existing profile			
			DLListboxProfile profile = ((DLListboxGeneralController<T>) this.masterController).createProfile();

			selectedProfile.setColumnModelJsonData(profile.getColumnModelJsonData());
			selectedProfile.setFilterModelJsonData(profile.getFilterModelJsonData());
			selectedProfile.setColumnsHashCode(profile.getColumnsHashCode());

			
		} 
	}

	@Override
	public void onEditProfile() {
		DLListboxProfile selectedProfile = this.profilesCtl.getSelectedItem();
		DLListboxProfile editProfile;
		
		if (selectedProfile == null) {
			editProfile = new DLListboxProfile();
		} else {
			editProfile = selectedProfile;
		}
		
		final Map<String, Object> args = new HashMap<String, Object>();
		args.put("profileManagerController", this);
		args.put("profile", editProfile);

		final org.zkoss.zul.Window win;
		win = (org.zkoss.zul.Window) ZKDLResourceResolver.resolveAndCreateComponents("listboxProfileEditWindow.zul", null, args);
		// win.addEventListener( "onSave", listener );
		win.doHighlighted();
	}	
	
	@Override
	public void onEditProfileOk(DLListboxProfile profile) {
		this.profileService.makePersistent(profile);
	}	
}
