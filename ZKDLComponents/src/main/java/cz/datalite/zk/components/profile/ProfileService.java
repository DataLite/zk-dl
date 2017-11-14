package cz.datalite.zk.components.profile;

import java.util.List;

public interface ProfileService<T extends DLListboxProfile, C extends DLListboxProfileCategory> {

	/**
	 * Method returns all profiles in persistent storage, usage e.g. for system
	 * admin.
	 *
	 * @return list of all profiles in persistent storage
	 */
	public List<T> findAll();

	/**
	 * Method returns all profiles for specified DLListbox id. List of profiles
	 * is in lovbox in component {@link DLProfileManager}.
	 * <p>
	 * The implementor MUST ensure that method returns only list of public
	 * profiles and private profiles owned by logged user (user is owner of
	 * the profile).
	 *
	 * @param    dlListboxId the id of listbox
	 * @return list of profiles with specified DListbox id
	 */
	List<T> findAll(String dlListboxId);

	/**
	 * Method returns profile by id.
	 *
	 * @param    id the id of profile
	 * @return profile with specified id
	 */
	T findById(Long id);

	/**
	 * Method try search profile by name
	 *
	 * @param profileName name of profile
	 * @return searched profile
	 */
	public T findByName(String profileName);

	/**
	 * Method returns default profile for specified DLListbox id.
	 * <p>
	 * The implementor MUST ensure that exactly one profile is marked as
	 * default.
	 *
	 * @param    dlListboxId the id of listbox
	 * @return default profile for specified DLListbox id or null if listbox
	 * does not have default profile
	 */
	T getByDefault(String dlListboxId);

	/**
	 * Method saves profile.
	 * <p>
	 * The implementor MUST ensure that exactly one profile is marked as default.
	 * The implementor MUST ensure that user (profile owner) is fulfilled before
	 * save.
	 *
	 * @param    dlListboxProfile profile to be saved
	 * @return persisted profile
	 */
	T save(T dlListboxProfile);

	/**
	 * Method deletes profile.
	 *
	 * @param    dlListboxProfile profile to be deleted
	 */
	void delete(T dlListboxProfile);

	/**
	 * Returns all categories.
	 */
	List<C> getAllCategories();

	/**
	 * Create empty "default new" profile
	 *
	 * @return new instance
	 */
	T createEmptyProfile();

}
