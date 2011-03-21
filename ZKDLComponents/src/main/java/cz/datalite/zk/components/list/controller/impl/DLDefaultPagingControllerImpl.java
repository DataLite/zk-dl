package cz.datalite.zk.components.list.controller.impl;

import cz.datalite.zk.components.paging.DLPaging;
import cz.datalite.zk.components.paging.DLPagingController;

/**
 * Default implementation of controller. It is used because of
 * back compatibility.
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class DLDefaultPagingControllerImpl implements DLPagingController {

    public void onPaging( final Integer page ) {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    public void onPageSize( final Integer pageSize ) {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    public void fireChanges() {
    }

    public DLPaging getPaging() {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    public String getUuid() {
        return "";
    }

    public boolean supportsRowCount()
    {
        return false;
    }

    public int getRowCount()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
