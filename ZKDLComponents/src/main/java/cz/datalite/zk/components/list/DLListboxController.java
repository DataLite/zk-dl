package cz.datalite.zk.components.list;

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
 * @author Karel Čemus <cemus@datalite.cz>
 */
public interface DLListboxController<T> {

    /**
     * <p><b>Usage:</b> EasyFilter</p>
     * <p>With this name easy filter model will be published in ZUL files.</p>
     * <p><b>Usage example:</b><br/><br/>
     * <i>java:</i> controller.registerEasyFilterVariable(comp, "filter");<br/>
     * <i>zul:</i><br/>
     * <ul>
     * <li>&lt;textbox value="@{filter.like.name}"/&gt;</li>
     * <li>&lt;intbox value="@{filter.gt.priority}"/&gt;</li>
     * </ul></p>
     *
     * <p><b>Available operators:</b><br />
     * <dl>
     * <dt>like</dt>
     *    <dd>%needle%, *needle*</dd>
     * <dt>ge</dt>
     *    <dd>greater equal: >=</dd>
     * <dt>gt</dt>
     *    <dd>greater than: ></dd>
     * <dt>le</dt>
     *    <dd>lesser equal: <=</dd>
     * <dt>lt</dt>
     *    <dd>lesser than: <</dd>
     * <dt>eq</dt>
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
     * <p>Like {@link cz.datalite.zk.components.list.DLListboxController#registerEasyFilterOnFilter(org.zkoss.zk.ui.Component, java.lang.String)}
     * with event <b>onClick</b></p>
     * @param comp component with event listener
     */
    void registerEasyFilterOnFilter( org.zkoss.zk.ui.Component comp );

    /**
     * <p>Like {@link cz.datalite.zk.components.list.DLListboxController#registerEasyFilterOnFilter(org.zkoss.zk.ui.Component, java.lang.String)}
     * with event <b>onClick</b></p>
     * @param compId component with listeners identifier
     */
    void registerEasyFilterOnFilter( String compId );

    /**
     * <p>Register event listener on the specified event on this component.
     * When event is captured easy filter is saved from binding model to the
     * normal model and listbox data model is refreshed. If autosaving is setted
     * then is model automatically saved to the session.</p>
     * @param comp component with listener
     * @param event event name which will be captured
     */
    void registerEasyFilterOnFilter( org.zkoss.zk.ui.Component comp, String event );

    /**
     * <p>Like {@link cz.datalite.zk.components.list.DLListboxController#registerEasyFilterOnFilter(org.zkoss.zk.ui.Component, java.lang.String)}.</p>
     * @param compId component identifier
     * @param event event name
     */
    void registerEasyFilterOnFilter( String compId, String event );

    /**
     * <p>Like {@link cz.datalite.zk.components.list.DLListboxController#registerEasyFilterOnClear(org.zkoss.zk.ui.Component, java.lang.String) }
     * with event name <b>onClick</b></p>
     * @param comp compnent with event listener
     */
    void registerEasyFilterOnClear( org.zkoss.zk.ui.Component comp );

    /**
     * <p>Like {@link cz.datalite.zk.components.list.DLListboxController#registerEasyFilterOnClear(org.zkoss.zk.ui.Component, java.lang.String) }
     * with event name <b>onClick</b></p>
     * @param compId component identifier
     */
    void registerEasyFilterOnClear( String compId );

    /**
     * <p>Registers event listener on this component. When event is captured
     * easy filter model is cleared and listbox data model is refreshed. If
     * autosave is setted then model will be save to the session.</p>
     * @param comp component with listener
     * @param event event name
     */
    void registerEasyFilterOnClear( org.zkoss.zk.ui.Component comp, String event );

    /**
     * <p>Like {@link cz.datalite.zk.components.list.DLListboxController#registerEasyFilterOnClear(org.zkoss.zk.ui.Component, java.lang.String) }</p>
     * @param compId component identifier
     * @param event event name
     */
    void registerEasyFilterOnClear( String compId, String event );

    /**
     * Reaction on filter event.
     * You can raise easy filter event manually by calling this function instead of registering 
     * event automatically by calling one of registerEasyFiterOnFilter method 
     * (e.g. {@link cz.datalite.zk.components.list.DLListboxController#registerEasyFilterOnFilter( java.lang.String )})
     */
    void confirmEasyFilter();

    /**
     * Reaction on clear filter event.
     * You can raise easy filter event manually by calling this function instead of registering 
     * event automatically by calling one of registerEasyFiterOnClear method 
     * (e.g. {@link cz.datalite.zk.components.list.DLListboxController#registerEasyFilterOnFilter( java.lang.String )})
     */
    void clearEasyFilter();

    /**
     * <p>Like {@link cz.datalite.zk.components.list.DLListboxController#clearEasyFilter() }</p>
     * Call this method if you want to disable automatic refresh.
     *
     * @param refresh set to true to disable automatic refresh and only clear filter fields.
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
     * <p>Returns selected item in the listbox.</p>
     * @return selectedItem
     */
    T getSelectedItem();

    /**
     * Sets selected item to the listbox.
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
