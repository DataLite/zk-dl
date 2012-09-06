package cz.datalite.zk.components.list.controller.impl;

import cz.datalite.zk.components.list.controller.DLEasyFilterController;
import cz.datalite.zk.components.list.controller.DLListboxExtController;
import cz.datalite.zk.components.list.DLListboxEvents;
import cz.datalite.zk.components.list.filter.EasyFilterModel;

/**
 * Implementation of the controller for the easy filter.
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class DLEasyFilterControllerImpl implements DLEasyFilterController {

    protected final DLListboxExtController masterController;
    protected final EasyFilterModel bindingModel = new EasyFilterModel();
    protected final EasyFilterModel model;

    public DLEasyFilterControllerImpl( final DLListboxExtController masterController, final EasyFilterModel model ) {
        this.masterController = masterController;
        this.model = model;
        this.bindingModel.putAll( this.model );
    }

    public void onEasyFilter() {
        if ( masterController.isLocked() ) {
            return;
        }
        model.putAll( bindingModel );
        masterController.onFilterChange( DLListboxEvents.ON_EASY_FILTER_CHANGE );
    }

    public void onClearEasyFilter( final boolean refresh ) {
        if ( masterController.isLocked() ) {
            return;
        }
        model.clear();
        bindingModel.clear();
        
        if ( refresh ) {
            masterController.onFilterChange( DLListboxEvents.ON_EASY_FILTER_CHANGE );
    }

    }

    public EasyFilterModel getBindingModel() {
        return bindingModel;
    }

    public void fireChanges() {
        bindingModel.clear();
        bindingModel.putAll( model );
        masterController.refreshBinding();
    }
}
