package cz.datalite.zk.components.list;

import cz.datalite.zk.components.list.controller.DLQuickFilterController;
import cz.datalite.zk.components.list.enums.DLFilterOperator;
import cz.datalite.zk.components.list.model.DLMasterModel;
import cz.datalite.zk.components.list.view.DLListbox;
import org.zkoss.zk.ui.event.EventListener;

import java.util.Set;

/**
 * Interface for work with controller for extended component 
 * {@link cz.datalite.zk.components.list.view.DLListbox}.
 * It allows using services for filtering, sorting, paging
 * and column managing. All operations are done externaly
 * for example in the database. Exactly implementation 
 * relies on the user which type he will choose.
 *
 * @param <T> main entity
 * @author Karel Cemus
 * @author Jiri Bubnik
 */
public interface DLListboxController<T> {

    /**
     * <p><b>Usage:</b> EasyFilter</p>
     * <p>With this name easy filter model will be published in ZUL files.</p>
     * <p><b>Usage example:</b><br/
     * <i>java:</i> controller.registerEasyFilterVariable(comp, "filter");<br/>
     * <i>zul:</i>
     * <ul>
     * <li>&lt;textbox value="@{filter.LIKE.name}"/&gt;</li>
     * <li>&lt;intbox value="@{filter.GT.priority}"/&gt;</li>
     * </ul></p>
     *
     * <p><b>Available operators (always use uppercase to distinct it from EL reserved words):</b><br />
     * <dl>
     * <dt>LIKE</dt>
     *    <dd>%needle%, *needle*</dd>
     * <dt>GE</dt>
     *    <dd>greater equal: &gt;=</dd>
     * <dt>GT</dt>
     *    <dd>greater than: &gt;</dd>
     * <dt>LE</dt>
     *    <dd>lesser equal: &lt;=</dd>
     * <dt>LT</dt>
     *    <dd>lesser than: &lt;</dd>
     * <dt>EQ</dt>
     *    <dd>equal: =</dd>
     * </dl>
     * </p>
     *
     *
     * @param comp compnent with variable with easy filter model - ordinary
     * it is window
     * @param variableName pseudonym for easyFilter model in the ZUL files
     */
    void registerEasyFilterVariable( org.zkoss.zk.ui.Component comp, String variableName );

    /**
     * {@link cz.datalite.zk.components.list.DLListboxController#registerEasyFilterVariable(org.zkoss.zk.ui.Component, java.lang.String) }
     * @param compId component identifier
     * @param variableName easyFilter model pseudonym in the ZUL files
     */
    void registerEasyFilterVariable( String compId, String variableName );


    /**
     * Confirm values in easy filter and refresh data in listbox.
     */
    void confirmEasyFilter();

    /**
     * Clear values in easy filter and refresh data in listbox.
     */
    void clearEasyFilter();

    /**
     * Clear values in easy filter.
     *
     * @param refresh true if immediately refresh data in listbox
     */
    void clearEasyFilter( boolean refresh );

    /**
     * <p>Sets automatic model saving. If value is <b>true</b> then model is
     * save everytime when it changed. If value is <b>true</b> then on page
     * refresh model is loaded from the session. Only thing, which doesn't save
     * ,is default filter.
     * u uložení celého modelu komponenty do session při každé změně. Při načítání strany se automaticky
     * model načte ze session, takže uživatel pracuje stále nad stejným zobrazením. Jediné, co se neukládá je
     * default filter.</p>
     * @param autosave hodnota, na kterou autosave nastavujeme
     */
    void setAutoSaveModel( boolean autosave );

    /**
     * <p>Saves model to the session. For identification in the session is used
     * identified defined in the constructor.</p>
     */
    void saveModel();

    /**
     * <p>Loads model from the session. For identification in the session is used
     * identifier defined in the constructor. If model exists returns true else
     * returns false. If model doesn't exist old model won't be replaced.
     * @return successful loaded
     */
    boolean loadModel();

    /**
     * Returns master model.
     * @return master model
     */
    DLMasterModel getModel();

    /**
     * <p>Clears all models and if autosave is true then empty model is saved.</p>
     */
    void clearAllModel();

    /**
     * <p>Clears all filters' models, refreshes listbox model and if autosave is true then models are
     * saved to the session.</p>
     */
    void clearFilterModel();

    /**
     * <p>Adds filter which isn't saved to the session and cannot be changed.
     * This initialization should be called in doAfterCompose.</p>
     * @param property property to filter
     * @param operator operator which will be used
     * @param value1 value for binary operators
     * @param value2 value for ternary operators - if operator is binary it can be null
     */
    void addDefaultFilter( String property, DLFilterOperator operator, Object value1, Object value2 );

    /**
     * <p>Updates listbox model using filtering, sorting and paging criterias.</p>
     */
    void refreshDataModel();

    /**
     * <p>Updates listbox model using filtering, sorting and paging criterias.</p>
     *
     * @param clearPaging if set, clear paging to default values before loading data
     */
    void refreshDataModel( boolean clearPaging );

    /**
     * <p>Clear all data from the listbox.</p>
     */
    void clearDataModel();

    /**
     * <p>Returns selected item in the listbox.</p>
     * @return selectedItem
     */
    T getSelectedItem();

    /**
     * Sets selected item to the listbox and send select event (if enabled for the listnbox).
     *
     * @param selectedItem selected item
     */
    void setSelected( final T selectedItem );

    /**
     * Sets selected item to the listbox (without notification)
     * @param selectedItem selected item
     */
    void setSelectedItem( T selectedItem );

    /**
     * <p>Returns selected items in the listbox.</p>
     * @return selectedItems
     */
    Set<T> getSelectedItems();

    /**
     * Sets seltected items to the listbox
     * @param selectedItems selected items
     */
    void setSelectedItems(Set<T> selectedItems);

    /**
     * <p>Update an item in the data model with a new value.</p>
     * <p>Typical scenario is after entity update in a detail window refresh the row in the listbox.</p>
     * <p>The item to update is found by equals method (using indexof on listbox data model). If not found,
     * the item is added as a first item in listbox and selected.</p>
     */
    void updateItem(T item);

    /**
     * <p>Locks model - since this time method refreshData model is disable.
     * Also events which can modify model are disabled.</p>
     */
    void lockModel();

    /**
     * <p>Unlocks model - since this time all changes will be again enabled.</p>
     */
    void unlockModel();

    /**
     * <p>Returns if model is locked or not. If it is locked then
     * no changes can be done.</p>
     * @return model is locked or not
     */
    boolean isLocked();

    /**
     * <p>Sets focus on the listbox component</p>
     */
    void focus();

    /**
     * <p>Returns ZK DLListbox component</p>
     * @return UI component
     */
    DLListbox getListbox();

    /**
     * Set quick filter column and value.
     * @param column column name from listbox model, NormalFilterModel.ALL constatnt may be used as well.
     * @param value new value of filter
     */
    void setQuickFilter(String column, String value);


    /**
     * <p>Adds listener on the specific event.</p>
     *
     * <p><b>Supported events are:</b></p>
     * <ul>
     *      <li>{@link org.zkoss.zk.ui.event.Events#ON_SELECT} - emitted when value is changed</li>
     *      <li>{@link cz.datalite.zk.components.list.enums.DLFilterEvents} - emitted when filters are changed</li>
     *</ul>
     *
     * @param event event name
     * @param listener listener
     */
    void addListener( String event, EventListener listener );

    /**
     * Removes listener
     * @param event event name
     * @param listener listener
     * @return if the listener existed
     */
    boolean removeListener( String event, EventListener listener );
}
