package cz.datalite.zk.components.profile.impl;

import cz.datalite.zk.components.profile.DLListboxProfile;
import cz.datalite.zk.components.profile.DLListboxProfileCategory;
import cz.datalite.zk.components.profile.ProfileService;
import org.zkoss.zk.ui.Sessions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProfileServiceSessionImpl implements ProfileService {

	public static final String PROFILE_SESS_ATTR = "__dlListbox__profile__";

	@Override
	public List<DLListboxProfile> findAll() {
		throw new IllegalAccessError("Method not valid for session implementation.");
	}

	@Override
	public List<DLListboxProfile> findAll(String dlListboxId) {
		DLListboxProfile profile = (DLListboxProfile) Sessions.getCurrent().getAttribute(PROFILE_SESS_ATTR + dlListboxId);

		ArrayList<DLListboxProfile> profiles = new ArrayList<>(1);

		if (profile != null) {
			profiles.add(profile);
		}

		return profiles;
	}

	@Override
	public DLListboxProfile findById(Long id) {
		throw new IllegalAccessError("Method not valid for session implementation.");
	}

	@Override
	public DLListboxProfile findByName(String profileName) {
		throw new IllegalAccessError("Method not valid for session implementation.");
	}

	@Override
	public DLListboxProfile save(DLListboxProfile dlListboxProfile) {
		Sessions.getCurrent().setAttribute(PROFILE_SESS_ATTR + dlListboxProfile.getDlListboxId(), dlListboxProfile);
		return dlListboxProfile;
	}

	@Override
	public void delete(DLListboxProfile dlListboxProfile) {
		throw new IllegalAccessError("Method not valid for session implementation.");
	}

	@Override
	public List<DLListboxProfileCategory> getAllCategories() {
		return Collections.emptyList();
	}

	@Override
	public DLListboxProfile createEmptyProfile() {
		return new DLListboxProfileImpl();
	}

	@Override
	public DLListboxProfile getByDefault(String dlListboxId) {
		throw new IllegalAccessError("Method not valid for session implementation.");
	}
}
