package cz.datalite.zk.components.profile;

import java.util.List;

public interface ProfileService {

	/**
	 * Method returns all profiles in persistent storage, usage e.g. for system
	 * admin.
	 * 
	 * @return	list of all profiles in persistent storage
	 */
	public List<? extends DLListboxProfile> findAll();

	/**
	 * Method returns all profiles for specified DLListbox id. List of profiles
	 * is in lovbox in component {@link DLProfileManager}.
	 * 
	 * The implementor MUST ensure that method returns only list of public
	 * profiles and private profiles owned by logged user (user is owner of
	 * the profile).
	 * 
	 * @param	dlListboxId the id of listbox
	 * @return	list of profiles with specified DListbox id
	 */
	public List<? extends DLListboxProfile> findAll(String dlListboxId);

	/**
	 * Method returns profile by id.
	 * 
	 * @param	id the id of profile
	 * @return	profile with specified id
	 */
	public DLListboxProfile findById(Long id);

	/**
	 * Method returns default profile for specified DLListbox id.
	 * 
	 * The implementor MUST ensure that exactly one profile is marked as
	 * default.
	 * 
	 * @param	dlListboxId the id of listbox
	 * @return	default profile for specified DLListbox id or null if listbox
	 * 			does not have default profile
	 */
	public DLListboxProfile getByDefault(String dlListboxId);

	/**
	 * Method saves profile.
	 * 
	 * The implementor MUST ensure that exactly one profile is marked as default.
	 * The implementor MUST ensure that user (profile owner) is fulfilled before
	 * save.
	 * 
	 * @param	dlListboxProfile profile to be saved
	 * @return	persisted profile
	 */
	public DLListboxProfile save(DLListboxProfile dlListboxProfile);

	/**
	 * Method deletes profile.
	 * 
	 * @param	dlListboxProfile profile to be deleted
	 */
	public void delete(DLListboxProfile dlListboxProfile);

    /**
     * Returns all categories.
     */
    public List<DLListboxProfileCategory> getAllCategories();

}
