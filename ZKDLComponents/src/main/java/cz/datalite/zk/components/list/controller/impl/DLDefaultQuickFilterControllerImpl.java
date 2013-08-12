package cz.datalite.zk.components.list.controller.impl;

import cz.datalite.zk.components.list.controller.DLQuickFilterController;
import cz.datalite.zk.components.list.filter.QuickFilterModel;
import cz.datalite.zk.components.list.model.DLColumnUnitModel;
import cz.datalite.zk.components.list.view.DLQuickFilter;

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

    @Override
    public QuickFilterModel getModel() {
        throw new UnsupportedOperationException( "Not supported yet." );
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

    public DLQuickFilter getQuickFilter() {
        throw new UnsupportedOperationException( "Not supported yet." );
    }
}
