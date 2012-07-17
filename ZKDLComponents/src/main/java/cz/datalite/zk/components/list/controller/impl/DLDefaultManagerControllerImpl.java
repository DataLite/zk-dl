package cz.datalite.zk.components.list.controller.impl;

import cz.datalite.zk.components.list.controller.DLManagerController;
import java.util.List;

/**
 * Default implementation of controller. It is used because of
 * back compatibility.
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class DLDefaultManagerControllerImpl implements DLManagerController {

    public void onColumnManager() {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    public void onSortManager() {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    public void onFilterManager() {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    public void onExportManager() {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    public void onResetFilters() throws InterruptedException {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    public void onResetAll() throws InterruptedException {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    public List<String> getFilters() {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    public void fireChanges() {
    }

    public void exportCurrentView() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
