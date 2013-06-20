package cz.datalite.zk.components.paging;

import org.zkoss.zk.ui.WrongValueException;

/**
 * Model useable for component {@link cz.datalite.zk.components.paging.DLPaging}.
 * This supports MVC architecture.
 * @author Karel Cemus
 */
public class DLPagingModel {

    // Constants
    /** Not paging */
    public static final int NOT_PAGING = -1;
    /** Unknown page count **/
    public static final int UNKNOWN_PAGE_COUNT = -1;
    public static final int UNKNOWN_TOTAL_SIZE = -1;
    // Page count
    protected Integer pageCount = UNKNOWN_PAGE_COUNT;
    // Actual page - 0 is first
    protected Integer actualPage = 0;
    // Page size - 0 is not supported
    protected Integer pageSize = NOT_PAGING;
    // Total size - count total items
    protected Integer totalSize = UNKNOWN_TOTAL_SIZE;
    // Rows on actual page
    protected Integer rowsOnPage = 0;

    // Uses total size
    @Deprecated // value is infered from totalSize value (null or not null)
    protected boolean countPages = true;

    public Integer getActualPage() {
        return actualPage;
    }

    public void setActualPage( final Integer actualPage ) {
        this.actualPage = actualPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize( final Integer pageSize ) {
        assert pageSize != null && ( pageSize > 0 || pageSize == NOT_PAGING ) : "Invalid value for pageSize";
        this.pageSize = pageSize;
        setPagesCount();
    }

    public Integer getPageCount() {
        return pageCount == UNKNOWN_PAGE_COUNT ? actualPage + 2 : pageCount;
    }

    protected void setPagesCount() {
        if ( totalSize != UNKNOWN_TOTAL_SIZE )
            this.pageCount = (int) Math.ceil( totalSize * 1.0 / pageSize );
    }

    public Integer getTotalSize() {
        return totalSize;
    }

    public void setTotalSize( final Integer totalItems, final int rowsOnThisPage ) {
        if ( totalItems == null || !countPages ) {
            if ( pageSize >= rowsOnThisPage)
            {
                // special case when rows  = 0 and actual page > 0 - we are out of page range -
                // still do not know total size.
                if (getActualPage() == 0 || rowsOnThisPage != 0)
                    totalSize = actualPage * pageSize + rowsOnThisPage;
            }
        }
        else {
            if ( totalItems < 0 )
                throw new WrongValueException( "Total size have to be possitive or zero." );
            this.totalSize = totalItems;
        }
        this.rowsOnPage = rowsOnThisPage;
        setPagesCount();
    }

    public void clear() {
        actualPage = 0;
        pageCount = UNKNOWN_PAGE_COUNT;
        totalSize = UNKNOWN_TOTAL_SIZE;
    }

    @Deprecated
    public boolean isCountPages() {
        return countPages;
    }

    @Deprecated
    public void setCountPages( final boolean countPages ) {
        this.countPages = countPages;
    }

    public boolean isKnownPageCount() {
        return pageCount != UNKNOWN_PAGE_COUNT;
    }

    public Integer getRowsOnPage() {
        return rowsOnPage;
    }

    public void setRowsOnPage(Integer rowsOnPage) {
        this.rowsOnPage = rowsOnPage;
    }
}
