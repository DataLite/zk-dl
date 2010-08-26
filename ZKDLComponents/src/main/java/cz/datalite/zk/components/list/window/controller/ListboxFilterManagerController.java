package cz.datalite.zk.components.list.window.controller;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import org.zkoss.zul.Row;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Image;
import org.zkoss.zul.Bandbox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericAutowireComposer;

import cz.datalite.helpers.EqualsHelper;
import cz.datalite.helpers.ZKBinderHelper;
import cz.datalite.zk.components.combo.DLCombobox;
import cz.datalite.zk.components.list.controller.DLListboxExtController;
import cz.datalite.zk.components.list.enums.DLFilterOperator;
import cz.datalite.zk.components.list.enums.DLNormalFilterKeys;
import cz.datalite.zk.components.list.filter.NormalFilterModel;
import cz.datalite.zk.components.list.filter.NormalFilterUnitModel;
import cz.datalite.zk.components.list.filter.components.FilterComponent;

/**
 * Controller for the listbox filter manager which allows advanced settings for
 * filtering.
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class ListboxFilterManagerController extends GenericAutowireComposer {

    protected final static Logger LOGGER = Logger.getLogger( ListboxFilterManagerController.class );
    // model
    /** model for active filters */
    protected NormalFilterModel modelFilter;
    protected Set<Bandbox> loadedBoxes = new HashSet<Bandbox>();
    // column models for sorting
    /** model for the columns, their configuration etc. */
    protected NormalFilterModel modelTemplates;
    // master controller
    protected DLListboxExtController masterController;
    // view
    protected org.zkoss.zul.Rows rows; // řídící komponenta modelu, zde jsou v jednotlivých potomcích všechny záznamy

    @Override
    @SuppressWarnings( "unchecked" )
    public void doAfterCompose( final org.zkoss.zk.ui.Component comp ) throws Exception {
        super.doAfterCompose( comp );
        comp.setVariable( "ctl", this, true );

        // save masterController
        masterController = ( DLListboxExtController ) arg.get( "master" );

        // save model, which is setted in the main model
        modelFilter = ( NormalFilterModel ) arg.get( DLNormalFilterKeys.FILTERS.toString() );

        // load informations about columns and data types
        modelTemplates = ( NormalFilterModel ) arg.get( DLNormalFilterKeys.TEMPLATES.toString() );

        // sort by column labels
        java.util.Collections.sort( modelTemplates, new java.util.Comparator<NormalFilterUnitModel>() {

            public int compare( final NormalFilterUnitModel unit1, final NormalFilterUnitModel unit2 ) {
                return unit1.getLabel().compareTo( unit2.getLabel() );
            }
        } );

        this.rows = (( Grid ) this.self.getChildren().get( 1 )).getRows();

//        for ( Object obj : rows.getChildren() ) {
//            final Row row = ( Row ) obj;
////
//            row.getFirstChild().addEventListener( Events.ON_CREATE, new EventListener() {
//
//                public void onEvent( final Event event ) throws Exception {
//                    drawValueComponents( row );
//                }
//            } );
//
//            row.getFirstChild().addEventListener( Events.ON_RENDER, new EventListener() {
//
//                public void onEvent( final Event event ) throws Exception {
//                    drawValueComponents( row );
//                }
//            } );
//
//            row.getFirstChild().addEventListener( "onInitRenderLater", new EventListener() {
//
//                public void onEvent( final Event event ) throws Exception {
//                    Events.echoEvent( Events.ON_SELECT, row.getFirstChild(), null );
//                }
//            } );
//
//        }


    }

    public NormalFilterModel getModelFilter() {
        return modelFilter;
    }

    public NormalFilterModel getModelTemplates() {
        return modelTemplates;
    }

    /**
     * Load distinct values for coresponding row
     * @param unitModel row model
     * @return distinct hodnot distinct
     */
    @SuppressWarnings( "unchecked" )
    public List<String> getModelValues( final NormalFilterUnitModel unitModel ) {
        final NormalFilterModel normalFilterModel = modelFilter.clone();
        normalFilterModel.remove( unitModel );

        return masterController.loadDistinctValues( unitModel.getColumn(), clearFilterModel( normalFilterModel ) );
    }

    /**
     * Removes invalid units from the model
     * @param model filter model
     * @return model valid filter model
     */
    protected NormalFilterModel clearFilterModel( final NormalFilterModel model ) {
        for ( final Iterator<NormalFilterUnitModel> it = model.iterator(); it.hasNext(); ) {
            final NormalFilterUnitModel unit = it.next();
            if ( unit.getColumn() == null || unit.getColumn().length() == 0 || unit.getOperator() == null || !isValuesValid( unit ) ) {
                it.remove();
                continue;
            }
        }

        return model;
    }

    /**
     * Removes empty string values
     * @param unit
     * @return
     */
    protected boolean isValuesValid( final NormalFilterUnitModel unit ) {
        for ( int i = 1; i <= unit.getOperator().getArity(); ++i ) {
            if ( unit.getValue( i ) instanceof String && (( String ) unit.getValue( i )).length() == 0 ) {
                return false;
            }
            if ( unit.getValue( i ) == null ) {
                return false;
            }
        }
        return true;
    }

    /**
     * Finds parent instance of Row and returns its value
     * @param component child component
     * @return model value
     */
    protected NormalFilterUnitModel getModelFromComponent( final Component component ) {
        return ( NormalFilterUnitModel ) (getParentComponent( component, Row.class )).getValue();
    }

    /**
     * Returns parent component with this type
     * @param <T> searching type
     * @param component child component
     * @param type parent type
     * @return found parent
     */
    protected <T> T getParentComponent( final Component component, final Class<T> type ) {
        Component comp = component;
        while ( !(type.isInstance( comp )) ) {
            comp = comp.getParent();
        }
        return comp == null ? null : type.cast( comp );
    }

    /**
     * Returns a new instance of a component for the specific unitModel. The component
     * is depended on the entity datatype and ZUL settings. There are added eventListeners
     * with validation calls etc.
     * @param <T>
     * @param unitModel
     * @param valueIndex
     * @param parent
     * @return
     */
    protected <T> org.zkoss.zk.ui.Component createComponent( final NormalFilterUnitModel unitModel, final int valueIndex, final Component parent ) {
        final FilterComponent filterComponent = unitModel.getFilterComponent();
        final org.zkoss.zk.ui.Component component = filterComponent.getComponent();
        filterComponent.setValue( unitModel.getValue( valueIndex ) );
        filterComponent.addOnChangeEventListener( new EventListener() {

            public void onEvent( final Event event ) {
                filterComponent.validate();
                final NormalFilterUnitModel unitModel = getModelFromComponent( component );
                unitModel.setValue( valueIndex, filterComponent.getValue() );
                loadedBoxes.clear();
            }
        } );
        return component;
    }

    public void onOk() {
        org.zkoss.zk.ui.event.Events.postEvent( new Event( "onSave", self, clearFilterModel( modelFilter ) ) );
        self.detach();
    }

    /**
     * Close window
     */
    public void onStorno() {
        self.detach();
    }

    public void onRemove( final Row row ) {
        // removes actual row
        modelFilter.remove( getModelFromComponent( row ) );
        ZKBinderHelper.loadComponent( self );
    }

    public void onRemoveAll() {
        modelFilter.clear();
        ZKBinderHelper.loadComponent( self );
    }

    @SuppressWarnings( "unchecked" )
    public void onAdd() {
        final NormalFilterUnitModel unit = new NormalFilterUnitModel();
        unit.setTemplate( modelTemplates.get( 0 ) );
        modelFilter.add( unit );
        ZKBinderHelper.loadComponent( self );

        // init the first item in the columnBox
        final Row row = ( Row ) this.rows.getLastChild();
        row.getGrid().renderRow( row );
        final DLCombobox columnBox = ( DLCombobox ) row.getChildren().get( 0 );
        Events.echoEvent( Events.ON_SELECT, columnBox, null ); // is        there a better solution<
    }

    public void onSelectColumn( final Row row ) {
        onSelectColumn( ( DLCombobox ) row.getFirstChild() );
    }

    @SuppressWarnings( "unchecked" )
    public void onSelectColumn( final DLCombobox columnbox ) {        // get data type
        final NormalFilterUnitModel rowModel = getModelFromComponent( columnbox );
        final NormalFilterUnitModel templateModel = ( NormalFilterUnitModel ) columnbox.getSelectedItem().getValue();
        if ( EqualsHelper.isEquals( templateModel.getColumn(), rowModel.getColumn() ) ) { // nothing changed
            return;
        }
        rowModel.update( templateModel );

        LOGGER.debug( "Refresh operator box ." );
        final DLCombobox operatorBox = ( DLCombobox ) columnbox.getNextSibling();
        ZKBinderHelper.loadComponent( operatorBox );

        // init the first item in the operatorBox
        Events.echoEvent( Events.ON_SELECT, operatorBox, null ); // is there a better solution?
    }

    @SuppressWarnings( "unchecked" )
    public void onSelectOperator( final DLCombobox operatorbox ) {
        final NormalFilterUnitModel unitModel = getModelFromComponent( operatorbox );
        final DLFilterOperator operator = unitModel.getOperator();
//        final Class type = unitModel.getType();
//
//      Commented on 23.8.2010 on Ondrej Medek's request
//      This condition probably restricts usage of the component
//
//      Condition on type has been removed
//
        if ( operator == null ) {// || type == null
            return;
            // removes all components
        }

        drawValueComponents( ( Row ) operatorbox.getParent() );
    }

    protected void drawValueComponents( final Row row ) {

        final Component operatorbox = row.getFirstChild().getNextSibling();

        Component lastComponent;  // last component in the row

        for ( lastComponent = operatorbox; !(lastComponent instanceof Image); lastComponent = lastComponent.getNextSibling() ) {
        }

        while ( operatorbox.getNextSibling() != lastComponent ) { // removes all components except last one
            operatorbox.getParent().removeChild( operatorbox.getNextSibling() );
        }

        final NormalFilterUnitModel unitModel = getModelFromComponent( operatorbox );

        for ( int i = 1; i <= unitModel.getOperator().getArity(); ++i ) {
            final org.zkoss.zk.ui.Component component = createComponent( unitModel, i, operatorbox.getParent() );
            operatorbox.getParent().insertBefore( component, lastComponent );
        }

        for ( int i = unitModel.getOperator().getArity(); i < DLFilterOperator.MAX_ARITY; ++i ) {
            operatorbox.getParent().insertBefore( new org.zkoss.zul.Space(), lastComponent );
        }

        loadedBoxes.clear();
    }
}
