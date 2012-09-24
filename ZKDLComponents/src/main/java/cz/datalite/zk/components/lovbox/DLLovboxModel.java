package cz.datalite.zk.components.lovbox;

import java.util.Collections;
import java.util.Set;

/**
 * Model for bandbox which can substitude combobox. There is stored
 * selected entity. This is a object which is setted in listbox or
 * is setted throw databinding. This value needn't be in the
 * listbox model.
 * @param <T> entity for which one is bandbox created
 * @author Karel Cemus
 */
public class DLLovboxModel<T> {

    /**
     * selected entity in bandbox. The label property is
     * shown in bandbox value
     */
    protected T selectedEntity;

    /**
     * Returns selected entity in bandbox. This value can be
     * setted throw databinding. This value needn't be in the
     * listbox model.
     * @return selected entity in bandbox.
     */
    public T getSelectedEntity() {
        return selectedEntity;
    }

    /**
     * Set object like a selected. It cause, that this one will be shown
     * in the bandbox value and also can be selected in the listbox if
     * model contains this value.
     * @param selectedEntity selected entity
     */
    public void setSelectedItem( final T selectedEntity ) {
        this.selectedEntity = selectedEntity;
    }

    /**
     * selected entities in bandbox. The label property is
     * shown in bandbox value
     */
    protected Set<T> selectedEntities;

    /**
     * Returns selected entities in bandbox. This value can be
     * setted throw databinding. This value needn't be in the
     * listbox model.
     * @return selected entities in bandbox.
     */
    public Set<T> getSelectedItems() {
        return selectedEntities == null ? Collections.<T>emptySet() : selectedEntities;
    }

    /**
     * Set objects like a selected. It cause, that these ones will be shown
     * in the bandbox value and also can be selected in the listbox if
     * model contains this value.
     * @param selectedEntities selected entities
     */
    public void setSelectedItems( final Set<T> selectedEntities ) {
        this.selectedEntities = selectedEntities;
    }
}
