package cz.datalite.zk.components.list.window.controller;

import cz.datalite.helpers.ZKBinderHelper;
import cz.datalite.zk.components.list.controller.DLListboxExtController;
import cz.datalite.zk.components.list.enums.DLFilterOperator;
import cz.datalite.zk.components.list.enums.DLNormalFilterKeys;
import cz.datalite.zk.components.list.filter.NormalFilterModel;
import cz.datalite.zk.components.list.filter.NormalFilterUnitModel;
import cz.datalite.zk.components.list.filter.components.FilterComponent;
import cz.datalite.zk.components.list.filter.components.RequireColumnModel;
import cz.datalite.zk.components.list.filter.components.RequireController;
import cz.datalite.zk.components.list.model.RowModel;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericAutowireComposer;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for the listbox filter manager which allows advanced settings for
 * filtering.
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class ListboxFilterManagerController extends GenericAutowireComposer {

    protected final static Logger LOGGER = LoggerFactory.getLogger( ListboxFilterManagerController.class );
    // model
    // column models for sorting
    /** model for the columns, their configuration etc. */
    protected NormalFilterModel modelTemplates;
    // master controller
    protected DLListboxExtController masterController;
    // view
    protected List<RowModel> rows = new LinkedList<RowModel>();

    @Override
    @SuppressWarnings( "unchecked" )
    public void doAfterCompose( final org.zkoss.zk.ui.Component comp ) throws Exception {
        super.doAfterCompose( comp );
        comp.setAttribute( "ctl", this, Component.SPACE_SCOPE);

        // save masterController
        masterController = ( DLListboxExtController ) arg.get( "master" );

        // save model, which is setted in the main model
        final NormalFilterModel modelFilter = ( NormalFilterModel ) arg.get( DLNormalFilterKeys.FILTERS.toString() );

        // load informations about columns and data types
        modelTemplates = ( NormalFilterModel ) arg.get( DLNormalFilterKeys.TEMPLATES.toString() );

        // sort by column labels
        java.util.Collections.sort( modelTemplates, new java.util.Comparator<NormalFilterUnitModel>() {

            public int compare( final NormalFilterUnitModel unit1, final NormalFilterUnitModel unit2 ) {
                return unit1.getLabel().compareTo( unit2.getLabel() );
            }
        } );

        for ( NormalFilterUnitModel unit : modelFilter ) {

            // if column was added manally, template might not be set - enforce template
            if (unit.getTemplate() == null)
                unit.setTemplate(modelTemplates.findUnitModelByColumnName(unit.getColumn()));

            rows.add( new RowModel( unit ) );
        }
    }

    public List<RowModel> getRows() {
        return rows;
    }

    public NormalFilterModel getModelTemplates() {
        return modelTemplates;
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
    protected RowModel getModelFromComponent( final Component component ) {
        return ( RowModel ) (getParentComponent( component, Row.class )).getValue();
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
        while ( !(type.isInstance( comp )) && comp.getParent() != null ) {
            comp = comp.getParent();
        }
        return (comp == null || !(type.isInstance( comp ))) ? null : type.cast( comp );
    }

    public void onOk() {
        final NormalFilterModel modelFilter = new NormalFilterModel();
        for ( RowModel row : rows ) {
            modelFilter.add( row.getModel() );
        }
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
        final RowModel model = getModelFromComponent( row );
        rows.remove( model );
        ZKBinderHelper.loadComponent( self );
    }

    public void onRemoveAll() {
        rows.clear();
        ZKBinderHelper.loadComponent( self );
    }

    @SuppressWarnings( "unchecked" )
    public void onAdd() {
        final NormalFilterUnitModel unit = new NormalFilterUnitModel();
        unit.update( modelTemplates.get( 0 ) );
        rows.add( new RowModel( unit ) );
        ZKBinderHelper.loadComponent( self );
    }

    public void onSelectColumn( final Combobox columnbox ) {
        final Combobox operatorBox = ( Combobox ) columnbox.getNextSibling();
        ZKBinderHelper.loadComponent( operatorBox );
        refreshComponents( ( Row ) columnbox.getParent() );
    }

    public void onSelectOperator( final Combobox combobox ) {
        refreshComponents( ( Row ) combobox.getParent() );
    }

    public void onRenderComponents( final Combobox combobox ) {
        onSelectColumn( combobox );
        onRenderComponents( ( Row ) combobox.getParent() );
    }

    public void onRenderComponents( final Row row ) {
        final Component lastChild = row.getLastChild(); // image button remove
        final RowModel model = getModelFromComponent( row );
        for ( int i = 0; i < DLFilterOperator.MAX_ARITY; ++i ) { // remove all
            lastChild.getPreviousSibling().setParent( null );
        }
        for ( int i = 0; i < DLFilterOperator.MAX_ARITY; ++i ) { // insert all
            row.insertBefore( model.getPosition( i + 1 ), lastChild );
        }
        LOGGER.debug( "Components have been rendered." );
    }

    public void refreshComponents( final Row row ) {
        final RowModel model = getModelFromComponent( row );
        final int oldArity = model.getRenderedArity();
        final int newArity = model.getModel().getOperator().getArity();
        if ( model.isRerender() ) {
            for ( int i = 1; i <= newArity; ++i ) {
                model.setPosition( i, createComponent( model.getModel(), i ) );
            }
            for ( int i = newArity + 1; i <= DLFilterOperator.MAX_ARITY; ++i ) {
                model.setPosition( i, new Space() );
            }
            model.rendered( newArity );
            Events.postEvent( "onRenderComponents", row, null );
            LOGGER.debug( "Components have been regenerated due to factory changed." );
        } else if ( oldArity != newArity ) {
            for ( int i = oldArity + 1; i <= newArity; ++i ) {
                model.setPosition( i, createComponent( model.getModel(), i ) );
            }
            for ( int i = newArity + 1; i <= oldArity; ++i ) {
                model.setPosition( i, new Space() );
                model.getModel().setValue( i, null ); // uncomment of this row
                // causes that the value will be reset after the component is hidden
                // now the value persists till the factory is not changed
            }
            model.rendered( newArity );
            Events.postEvent( "onRenderComponents", row, null );
            LOGGER.debug( "Components have been modified due to arity changed." );
        }
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
    protected <T> org.zkoss.zk.ui.Component createComponent( final NormalFilterUnitModel unitModel, final int valueIndex ) {
        final FilterComponent filterComponent = unitModel.createFilterComponent();
        final org.zkoss.zk.ui.Component component = filterComponent.getComponent();
        filterComponent.setValue( unitModel.getValue( valueIndex ) );
        filterComponent.addOnChangeEventListener( new EventListener() {

            public void onEvent( final Event event ) {

                // premature Event - while in component setup
                if (component.getParent() == null)
                    return;
                
                filterComponent.validate();
                final RowModel unitModel = getModelFromComponent( component );
                unitModel.getModel().setValue( valueIndex, filterComponent.getValue() );
            }
        } );

        if ( filterComponent instanceof RequireColumnModel ) {
            (( RequireColumnModel ) filterComponent).setColumnModel( unitModel.getTemplate().getColumnModel() );
        }

        if ( filterComponent instanceof RequireController ) {
            (( RequireController ) filterComponent).setController( masterController );
        }

        return component;
    }
}
