package cz.datalite.zk.components.list.controller;

import cz.datalite.dao.DLResponse;
import cz.datalite.zk.components.list.DLListboxController;
import cz.datalite.zk.components.list.filter.NormalFilterModel;
import cz.datalite.zk.components.list.model.DLColumnModel;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.Composer;

import java.util.List;
import java.util.Map;

/**
 * Interface for the master listbox controller. This is private api.
 * @param <T> main entity type
 * @author Karel Cemus
 */
public interface DLListboxExtController<T> extends DLListboxController<T>, Composer<Component> {

    /**
     * Reacts on paging model change.
     */
    void onPagingModelChange();

    /**
     * Reacts on filter model change.
     */
    void onFilterChange( String filterType );

    /**
     * Reacts on sorting model change.
     */
    void onSortChange();

    /**
     * Returns column model which is used for listheaders.
     * @return column model
     */
    DLColumnModel getColumnModel();

    /**
     * Returns normal filter model.
     * @return normal filter model
     */
    NormalFilterModel getNormalFilterModel();

    /**
     * Reacts on sort manager OK.
     * @param data data from manager
     */
    void onSortManagerOk( List<Map<String, Object>> data );

    /**
     * Reacts on column manager OK.
     * @param data data from manager
     */
    void onColumnManagerOk( List<Map<String, Object>> data );

    /**
     * Reacts on filter manager OK.
     * @param data data form manager
     */
    void onFilterManagerOk( NormalFilterModel data );

    /**
     * Reacts on export manager OK.
     * @param data data from the manager
     */
    void onExportManagerOk( AMedia data );

    /**
     * Reacts on onCreate event.
     */
    void onCreate();

    /**
     * Returns instance of type of the main entity
     * @return class of entity type
     */
    Class<?> getEntityClass();

    /**
     * Returns column distinct list.
     * @param column column wiith unique data
     * @param normalFilter normal filter model
     * @return data list
     */
    DLResponse<String> loadDistinctValues( String column, String qFilterValue, int firstRow, int rowCount );

    /**
     * Refreshes binding.
     */
    void refreshBinding();

    /**
     * Returns easyFilter controller
     * @return easyFilter controller
     */
    DLEasyFilterController getEasyFilterController();


    /**
     * Loads data with actual filter model model but not with paging.
     * @param rowLimit limit of entities
     * @return loaded data
     */
    DLResponse<T> loadData( int rowLimit );

    /**
     * Returns window controller - main controller on the page.
     * @return window controller
     */
    Composer getWindowCtl();

    /**
     * Reacts on onSelect event.
     * @throws Exception
     */
    void onSelect();

    /**
     * Sets selected item to the listbox and send select event (if enabled).
     *
     * @param selectedItem selected item
     */
    void setSelected( final T selectedItem );

    /**
     * Sets selected item.
     * @param selectedItem selected item
     */
    void setSelectedItem( T selectedItem );

    /**
     * Sets selected index
     * @param index selected index
     */
    void setSelectedIndex( int index );

    /**
     * Returns component's uuid
     * @return uuid
     */
    String getPagingUuid();

    /**
     * Returns component's uuid
     * @return uuid
     */
    String getQuickFilterUuid();

    /**
     * Returns component's uuid
     * @return uuid
     */
    String getListboxUuid();
    
    /**
     * Request for direct export to MS Excel
     * 
     * The result is file send to the user
     */
    void onDirectExport();

    /**
     * Returns session name
     * @return session name
     */
	String getSessionName();

    /**
     * Vynulování příznaku, že se jedná o změnu stránky
     *
     * @return původní hodnota příznaku
     */
    boolean clearInPagingEvents() ;
}
