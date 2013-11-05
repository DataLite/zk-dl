package cz.datalite.zk.components.profile;

/**
 * Listbox profile is in in zero to many categories.
 */
public interface DLListboxProfileCategory {

    /**
     * ID of category in persistence storage.
     */
    Long getProfileCategoryId();

    /**
     * Profile category name - set in listbox
     */
    String getProfileCategoryName();

    /**
     * Profile category description (tooltip for the user)
     */
    String getProfileCategoryDescription();
}
