package cz.datalite.zk.components.combo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Model for comboboxes in the MVC architecture. There is stored information
 * about selectedItem, selected index etc.
 * @param <T> main entity in the combobox
 * @author Karel Cemus
 */
public class DLComboboxModel<T> {

    /** entities in the combobox */
    protected List<T> model = new ArrayList<T>();
    /** selected index in the combobox */
    protected int selectedIndex = NOT_SELECTED;
    /** selected item in the combobox - it is the entity */
    protected T selectedItem;
    /** signalize that no item is selected */
    protected static final int NOT_SELECTED = -1;
    /** signalize that combobox is in the unknown state so NOT_SELECTED state
     * is also new state */
    protected static final int UNKNOWN = -2;
    /** map with filter columns and values = they are collected from the parents */
    protected Map<String, Object> filters = new HashMap<String, Object>();

    /**
     * returns entities which are in the model 
     * @return model
     */
    public List<T> getModel() {
        return model;
    }

    /**
     * Sets new entities for the model.
     * @param model new entites in the model
     */
    public void setModel( final List<T> model ) {
        assert model != null : "Model shouldn't be null";
        this.model = model;
    }

    /**
     * Returns selected index of the selected item
     * @return index of the selected item
     */
    public int getSelectedIndex() {
        return selectedIndex;
    }

    /**
     * Sets selected index. If index is out of range the exception
     * will be thrown
     * @param selectedIndex selected index in the entity array
     */
    public void setSelectedIndex( final int selectedIndex ) {
        if ( selectedIndex >= model.size() || ( selectedIndex < 0 && selectedIndex != NOT_SELECTED && selectedIndex != UNKNOWN ) )
            throw new IndexOutOfBoundsException( "Index out bounds! Combobox has got only " + model.size() + " items." );
        this.selectedIndex = selectedIndex;
        this.selectedItem = selectedIndex == NOT_SELECTED || selectedIndex == UNKNOWN ? null : model.get( selectedIndex );
    }

    /**
     * Returns entity which is selected in the combobox
     * @return selected entity - it can be null
     */
    public T getSelectedItem() {
        return selectedItem;
    }

    /**
     * Sets entity like a selected. The entity must be in the
     * model. If it isn't exception is thrown.
     * @param selectedItem selected entity which is in the model
     */
    public void setSelectedItem( final T selectedItem ) {
        final int index = model.indexOf( selectedItem );

        if ( index == NOT_SELECTED && selectedItem != null )
            throw new IllegalArgumentException( "Položka neexistuje" );

        selectedIndex = index;
        this.selectedItem = selectedItem;
    }

    /**
     * Sets filter - it is for cascade comboboxes where parent
     * is like a filter value
     * @param column column which is affected by this value
     * @param value parent value
     */
    public void setFilter( final String column, final Object value ) {
        filters.put( column, value );
    }

    /**
     * Returns all active filters
     * @return all active filters setted by parents
     */
    public Map<String, Object> getFilters() {
        return filters;
    }
}
