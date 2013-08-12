package cz.datalite.zk.components.list.controller.impl;

import cz.datalite.zk.components.list.DLListboxEvents;
import cz.datalite.zk.components.list.controller.DLListboxExtController;
import cz.datalite.zk.components.list.controller.DLQuickFilterController;
import cz.datalite.zk.components.list.enums.DLFilterOperator;
import cz.datalite.zk.components.list.filter.QuickFilterModel;
import cz.datalite.zk.components.list.model.DLColumnUnitModel;
import cz.datalite.zk.components.list.view.DLQuickFilter;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

/**
 * Implementation of the controller for the quick filter component.
 * It is quick and simple tool for searching in the model.
 * @author Karel Cemus
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

        bindingModel = new QuickFilterModel( model.getKey(), model.getValue(), model.getModel() );
        quickFilter.setController( this );
    }

    public void onQuickFilter() {
        if ( masterController.isLocked() ) {
            return;
        }                       
        model.setValue( bindingModel.getValue() == null ? null : bindingModel.getValue().trim());
        model.setKey( bindingModel.getKey() );
        model.setModel( bindingModel.getModel() );
        masterController.onFilterChange( DLListboxEvents.ON_QUICK_FILTER_CHANGE );
    }

    public boolean validateQuickFilter()
    {
        // ZK-164 Validation disabled
        //   Since 1.4.0 changed on 10. August 2012 the validation
        //   has been disabled because the quickfilter since now 
        //   is not interested in a datatype and delegates that 
        //   responsibility on the filter handler like a database
        //   or DLFilter implementation
        //
        //
        //    DLColumnUnitModel columnUnitModel = bindingModel.getModel();
        //    if (columnUnitModel != null && columnUnitModel.getColumnType() != null && !StringHelper.isNull(bindingModel.getValue()))
        //    {
        //        if (columnUnitModel.getFilterCompiler() != null)
        //            columnUnitModel.getFilterCompiler().validateValue(bindingModel.getValue());
        //        else {
        //            // validate primitive value with a type (number, date, ...)
        //            try {
        //                Classes.coerce(columnUnitModel.getColumnType(), bindingModel.getValue());
        //            } catch (Exception e) {
        //                return false;
        //            }
        //        }
        //    }

        return true;
    }

    public void fireChanges() {
        quickFilter.setModel( getQuickFilterModel() );

        // if the column is not visible anymore then reset the filter
        if ( model.getModel() != null && !model.getModel().isVisible() ) model.clear();
        
        bindingModel.setValue( model.getValue() );
        bindingModel.setKey( model.getKey() );
        bindingModel.setModel( model.getModel() );
        quickFilter.fireChanges();
    }

    /**
     * Prepares model for quick filter from the column model.
     * @return model for the quick filter
     */
    protected List<Entry<DLColumnUnitModel, String>> getQuickFilterModel() {
        final List<Entry<DLColumnUnitModel, String>> quickFilterModel = new LinkedList<Entry<DLColumnUnitModel, String>>();
        final boolean wysiwyg = masterController.getModel().getFilterModel().isWysiwyg();
        for ( final DLColumnUnitModel unit : masterController.getColumnModel().getColumnModels() ) {
            if ( unit.isColumn() && unit.isQuickFilter() && unit.isFilter() ) {
                // set to use wysiwyg search
                if ( wysiwyg ) unit.setQuickFilterOperator( DLFilterOperator.LIKE );
                quickFilterModel.add( new Entry<DLColumnUnitModel, String>() {

                    public DLColumnUnitModel getKey() {
                        return unit;
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

    public QuickFilterModel getModel() {
        return this.model;
    }

    public QuickFilterModel getBindingModel() {
        return this.bindingModel;
    }

    public String getUuid() {
        return quickFilter.getUuid();
    }

    public DLQuickFilter getQuickFilter() {
        return quickFilter;
    }
}
