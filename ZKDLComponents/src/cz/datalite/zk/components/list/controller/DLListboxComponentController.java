package cz.datalite.zk.components.list.controller;

import cz.datalite.zk.components.list.view.DLListbox;
import cz.datalite.zk.components.list.view.DLListheader;
import java.util.List;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zul.Listitem;

/**
 * Interface for the listbox component controller which reacts
 * on event invoked on the component and needen't be served by
 * the master controller.
 * @param <T> main entity
 * @author Karel Čemus <cemus@datalite.cz>
 */
public interface DLListboxComponentController<T> {

    /**
     * Reacts on onSort event.
     * @param listheader listheader which generated event
     */
    void onSort( DLListheader listheader );

    /**
     * Reacts on onCreate event.
     */
    void onCreate();

    /**
     * Notifies compnent that model changed.
     */
    void fireChanges();

    /**
     * Zavolá refresh na data
     */
    void fireDataChanges();

    /**
     * Notifies component that column model changed.
     */
    void fireColumnModelChanges();

    /**
     * Notifies component that order of columns changed.
     * Renderer templates is modifies according to new
     * model.
     */
    void fireOrderChanges();

    /**
     * Sets listbox model ordinary list which is converted to the binding list.
     * @param model new model - ordinary list
     */
    void setListboxModel( List<T> model );

    /**
     * Sets renderer template.
     * @param item renderer template
     */
    void setRendererTemplate( Listitem item );

    /**
     * Refreshes all binding.
     */
    void refreshBindingAll();

    /**
     * Returns selected item in the component.
     * @return selected item
     */
    T getSelectedItem();

    /**
     * Sets selected item to the component.
     * @param selectedItem selected item
     */
    void setSelectedItem( T selectedItem );

    /**
     * Sets selected index to the component.
     * @param selectedIndex index of the selected item
     */
    void setSelectedIndex( int selectedIndex );

    /**
     * Returns component for this identifier.
     * @param compId component identifier.
     * @return component
     */
    org.zkoss.zk.ui.Component getFellow( String compId );

    /**
     * Adds forward on the component.
     * @param originEvent source event
     * @param targetComponent target component identifier
     * @param targetEvent target event
     */
    void addForward( String originEvent, String targetComponent, String targetEvent );

    /**
     * Adds forward on the component.
     * @param originalEvent source event
     * @param target target component
     * @param targetEvent target event
     */
    void addForward( String originalEvent, Component target, String targetEvent );

    /**
     * Returns window component controller
     * @return window controller
     */
    Composer getWindowCtl();

    /**
     * Returns index of the selected item.
     * @return selected index
     */
    int getSelectedIndex();

    /**
     * Sets focus on the component.
     */
    void focus();

    /**
     * Should the listbox start with data loaded on create?
     * 
     * @return true if open page with loaded data, false otherwise
     */
    boolean isLoadDataOnCreate();

    /**
     * Returns listbox component
     * @return UI component
     */
    DLListbox getListbox();
}
