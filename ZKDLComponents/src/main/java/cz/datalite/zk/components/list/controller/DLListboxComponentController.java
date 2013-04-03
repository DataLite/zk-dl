package cz.datalite.zk.components.list.controller;

import cz.datalite.zk.components.list.view.DLListbox;
import cz.datalite.zk.components.list.view.DLListheader;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zul.Listitem;

import java.util.List;
import java.util.Set;
import org.zkoss.zk.ui.util.Template;

/**
 * Interface for the listbox component controller which reacts
 * on event invoked on the component and needen't be served by
 * the master controller.
 * @param <T> main entity
 * @author Karel Cemus
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
     *
     * @param item renderer template
     * @deprecated since ZK 6 and databinding 2.0. This method worked in
     * Databinding 1.0
     */
    @Deprecated
    void setRendererTemplate( Listitem item );
    
    /**
     * Sets renderer template which come with Databinding 2.0
     *
     * @param item renderer template
     *
     * @since ZK 6
     */
    void setRendererTemplate( Template template );


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
    @Deprecated
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
    @Deprecated
    void setSelectedItems(Set<T> selectedItems);

    /**
     * Sets selected index to the component.
     * @param selectedIndex index of the selected item
     */
    @Deprecated
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

    /**
     * Returns uuid of listbox, paging and quick filter.
     * This method is used in selenium test for building
     * component's mirror
     * @return listbox uuid, paging uuid, qf uuid
     */
    String getUuidsForTest();
    
    /**
     * Updates the listitem to reflect desired column order as is defined in the
     * model. This approach is used since ZK 6 because in this version the
     * Listbox template is share accross all the users and the sessions so there
     * is no possible to change the template directly. Instead of that the
     * renderer asks the controller for each listitem to "fix" the column order.
     * 
     * This methos is intended to be called from the renderer only.
     *
     * @param item freshly rendered listitem
     */
    void updateListItem( Listitem item );
    
    /**
     * Sets selected item to the listbox
     *
     * @param selectedItems selected items
     */
    void setSelected( final T selectedItem );

    /**
     * Sets selected items to the listbox
     *
     * @param selectedItems selected items
     */
    void setSelected( final T selectedItem, final Set<T> selectedItems );

    /**
     * <p>Update an item in the data model with a new value.</p>
     * <p>Typical scenario is after entity update in a detail window refresh the row in the listbox.</p>
     * <p>The item to update is found by equals method (using indexof on listbox data model). If not found,
     * the item is added as a first item in listbox and selected.</p>
     *
     * @return true if item was found, false otherwise. Typical nvocation should be from
     *      DLListboxController.updateItem, which will notify listeners and paging of a change.
     */
    boolean updateItem(T item);
}
