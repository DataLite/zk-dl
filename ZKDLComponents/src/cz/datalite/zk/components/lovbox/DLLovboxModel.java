package cz.datalite.zk.components.lovbox;

/**
 * Model for bandbox which can substitude combobox. There is stored
 * selected entity. This is a object which is setted in listbox or
 * is setted throw databinding. This value needn't be in the
 * listbox model.
 * @param <T> entity for which one is bandbox created
 * @author Karel Čemus <cemus@datalite.cz>
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
}
