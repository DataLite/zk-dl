package cz.datalite.zk.components.list.controller.impl;

import cz.datalite.zk.components.list.controller.DLQuickFilterController;
import cz.datalite.zk.components.list.filter.QuickFilterModel;
import cz.datalite.zk.components.list.model.DLColumnUnitModel;
import java.util.List;

/**
 * Default implementation of controller. It is used because of
 * back compatibility.
 * @author Karel Cemus
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

    public boolean validateQuickFilter()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void initModel( List<DLColumnUnitModel> columnModels ) {
    }
}
