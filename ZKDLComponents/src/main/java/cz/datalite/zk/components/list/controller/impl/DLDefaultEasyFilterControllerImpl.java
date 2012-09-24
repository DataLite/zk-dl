package cz.datalite.zk.components.list.controller.impl;

import cz.datalite.zk.components.list.controller.DLEasyFilterController;
import cz.datalite.zk.components.list.filter.EasyFilterModel;

/**
 * Default implementation of controller. It is used because of
 * back compatibility.
 * @author Karel Cemus
 */
public class DLDefaultEasyFilterControllerImpl implements DLEasyFilterController {

    public void onEasyFilter() {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    public EasyFilterModel getBindingModel() {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    public void onClearEasyFilter(boolean refresh) {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    public void fireChanges() {
    }
}
