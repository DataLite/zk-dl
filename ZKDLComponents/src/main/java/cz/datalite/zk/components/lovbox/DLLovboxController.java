package cz.datalite.zk.components.lovbox;

import cz.datalite.zk.components.cascade.Cascadable;
import cz.datalite.zk.components.list.DLListboxController;
import org.zkoss.zk.ui.event.EventListener;

/**
 * This is the public api for bandbox and defines method which users can call.
 * This bandbox substitudes combobox but in contrast to it this object can
 * quickly operate with huge data models.
 * @param <T> main entity in the bandbox
 * @author Karel Cemus
 */
public interface DLLovboxController<T> extends Cascadable<T> {

    /**
     * Set entity like a selected one. This one
     * needn't be in the listbox model, but if
     * model doesn't contain it, this value won't
     * be able to be selected.
     * @param selectedItem new selected entity
     */
    void setSelectedItem( T selectedItem );

    /**
     * Returns listbox controller. Listbox is mandatory in lovbox constructor and holds data with selection.
     *
     * @return listbox controller.
     */
    DLListboxController<T> getListboxController();

    /**
     * Returns lovbox component.
     * @return UI component
     */
    DLLovbox<T> getLovBox();

    /**
     * Invalidate underlying listbox.
     * <p/>
     * Data are cleared and listbox is locked - it simulates the state of listbox before lovbox is opened for the
     * first time.
     * <p/>
     * Typical usage is if different filter should be applied on the listbox data. listbox.refreshDataModel() is not
     * optimal, because data are loaded from DB immediately. If you use invalidateListboxModel() instead, data are loaded
     * only after the user opens the lovbox.
     */
    void invalidateListboxModel();

    /**
     * <p>Adds listener on the specific event.</p>
     *
     * <p><b>Supported events are:</b></p>
     * <ul>
     *      <li>{@link org.zkoss.zk.ui.event.Events#ON_SELECT} - emitted when value is changed</li>
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
