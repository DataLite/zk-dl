package cz.datalite.zk.components.paging;

/**
 * Class which implements this interface is usable for affecting DLPagingModel
 * when PagingEvents are posted from DLPaging.
 * @author Karel Čemus <cemus@datalite.cz>
 */
public interface DLPagingController {

    /**
     * Event is posted when is changed page number. This metod is like
     * event listener.
     * @param page new page number
     */
    void onPaging( Integer page );

    /**
     * Count items on one page
     * @param pageSize count items on page
     */
    void onPageSize( Integer pageSize );

    /**
     * Zavolá fireChanges na paging
     */
    void fireChanges();

    /**
     * Returns paging component
     * @return UI component
     */
    DLPaging getPaging();

    /**
     * Returns true, if associated listbox controller can count total number of rows in databaset.
     * @return true or false
     */
    boolean supportsRowCount();

    /**
     * Returns total number of rows in database. Note, that listbox controller must support this feature, check #supportsRowCount first.
     *
     * @return total number of rows in database.
     */
    int getRowCount();
    

    String getUuid();
}
