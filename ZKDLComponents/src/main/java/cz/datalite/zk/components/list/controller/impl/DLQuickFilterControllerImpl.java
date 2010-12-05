package cz.datalite.zk.components.list.controller.impl;

import cz.datalite.helpers.ZKBinderHelper;
import cz.datalite.zk.components.list.controller.DLListboxExtController;
import cz.datalite.zk.components.list.controller.DLQuickFilterController;
import cz.datalite.zk.components.list.DLListboxEvents;
import cz.datalite.zk.components.list.filter.QuickFilterModel;
import cz.datalite.zk.components.list.model.DLColumnModel;
import cz.datalite.zk.components.list.model.DLColumnUnitModel;
import cz.datalite.zk.components.list.view.DLQuickFilter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import org.zkoss.lang.Classes;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;

/**
 * Implementation of the controller for the quick filter component.
 * It is quick and simple tool for searching in the model.
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class DLQuickFilterControllerImpl implements DLQuickFilterController {

    // master controller
    protected final DLListboxExtController masterController;
    // model
    protected final QuickFilterModel model;
    protected final QuickFilterModel bindingModel;
    // view
    protected final DLQuickFilter quickFilter;

    public DLQuickFilterControllerImpl( final DLListboxExtController masterController, final QuickFilterModel model, final DLQuickFilter quickFilter ) {
        this.masterController = masterController;
        this.quickFilter = quickFilter;
        this.model = model;

        // defaultni hodnota a je inicializovane
        if ( quickFilter.getQuickFilterDefault() != null && QuickFilterModel.CONST_ALL.equals( this.model.getKey() ) ) {
            this.model.setKey(quickFilter.getQuickFilterDefault());
        }

        bindingModel = new QuickFilterModel( model.getKey(), model.getValue() );
        quickFilter.setController( this );
    }

    public void onQuickFilter() {
        if ( masterController.isLocked() ) {
            return;
        }
        model.setValue( bindingModel.getValue() == null ? null : bindingModel.getValue().trim());
        model.setKey( bindingModel.getKey() );
        masterController.onFilterChange( DLListboxEvents.ON_QUICK_FILTER_CHANGE );
    }

    public boolean validateQuickFilter()
    {
        DLColumnUnitModel columnUnitModel = masterController.getColumnModel().getByName(bindingModel.getKey());
        if (columnUnitModel != null && columnUnitModel.getColumnType() != null && bindingModel.getValue() != null)
        {
            try
            {
                Classes.coerce(columnUnitModel.getColumnType(), bindingModel.getValue());
            }
            catch (Exception e)
            {
                return false;
            }
        }

        return true;
    }

    public void fireChanges() {
        quickFilter.setModel( getModel() );
        bindingModel.setValue( model.getValue() );
        bindingModel.setKey( model.getKey() );
        quickFilter.fireChanges();
        ZKBinderHelper.loadComponent( quickFilter );
    }

    /**
     * Prepares model for quick filter from the column model.
     * @return model for the quick filter
     */
    protected List<Entry<String, String>> getModel() {
        final List<Entry<String, String>> quickFilterModel = new LinkedList<Entry<String, String>>();
        for ( final DLColumnUnitModel unit : masterController.getColumnModel().getColumnModels() ) {
            if ( unit.isColumn() && unit.isQuickFilter() && unit.isFilter() ) {
                quickFilterModel.add( new Entry<String, String>() {

                    public String getKey() {
                        return unit.getColumn();
                    }

                    public String getValue() {
                        return unit.getLabel();
                    }

                    public String setValue( final String value ) {
                        throw new UnsupportedOperationException( "Not supported yet." );
                    }
                } );
        }
        }
        return quickFilterModel;
    }

    public QuickFilterModel getBindingModel() {
        return this.bindingModel;
    }

    public String getUuid() {
        return quickFilter.getUuid();
    }
}
