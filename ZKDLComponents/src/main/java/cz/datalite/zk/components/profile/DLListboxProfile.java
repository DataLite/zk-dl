package cz.datalite.zk.components.profile;

import com.sun.tools.jdeps.JdepsFilter;

import java.util.List;

/**
 * Listbox profile.
 */
public interface DLListboxProfile {

	Long getId();

	void setId(Long id);

	String getDlListboxId();

	void setDlListboxId(String dlListboxId);

	String getName();

	void setName(String name);

	boolean isPublicProfile();

	void setPublicProfile(boolean publicProfile);

	boolean isDefaultProfile();

	void setDefaultProfile(boolean defaultProfile);

	boolean isHidden();

	void setHidden(boolean hidden);

	boolean isEditable();

	void setEditable(boolean editable);

	String getUser();

	void setUser(String user);

	String getColumnModelJsonData();

	void setColumnModelJsonData(String columnModelJsonData);

	String getFilterModelJsonData();

	void setFilterModelJsonData(String filterModelJsonData);

    String getCustomJsonData();

    void setCustomJsonData(String customJsonData);


	Integer getColumnsHashCode();

	void setColumnsHashCode(Integer columnsHashCode);

    /**
     * Add this profile to the category.
     */
    void addCategory(DLListboxProfileCategory category);

    /**
     * Remove this profile from the category.
     */
    void removeCategory(DLListboxProfileCategory category);

    /**
     * Get all categories this profile is in - zero to many.
     * @return list of categories.
     */
    List<DLListboxProfileCategory> getCategories();

	/**
	 * @return jméno role, kterou musí mít uživatel aby mohl editovat profil
	 */
	default String getPermissionName() {
		return null ;
	}
}
