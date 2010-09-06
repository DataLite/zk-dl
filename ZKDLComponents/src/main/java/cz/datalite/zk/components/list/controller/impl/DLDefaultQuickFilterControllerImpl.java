package cz.datalite.zk.components.list.controller.impl;

import cz.datalite.zk.components.list.controller.DLQuickFilterController;
import cz.datalite.zk.components.list.filter.QuickFilterModel;

/**
 * Default implementation of controller. It is used because of
 * back compatibility.
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class DLDefaultQuickFilterControllerImpl implements DLQuickFilterController {

    public void onQuickFilter() {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    public void fireChanges() {
    }

    public QuickFilterModel getBindingModel() {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    public String getUuid() {
        return "";
    }
}
