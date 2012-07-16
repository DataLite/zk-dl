package cz.datalite.zk.components.list.controller.impl;

import cz.datalite.helpers.DateHelper;
import cz.datalite.zk.components.list.controller.DLListboxExtController;
import cz.datalite.zk.components.list.controller.DLManagerController;
import cz.datalite.zk.components.list.enums.DLNormalFilterKeys;
import cz.datalite.zk.components.list.filter.NormalFilterModel;
import cz.datalite.zk.components.list.filter.NormalFilterUnitModel;
import cz.datalite.zk.components.list.filter.config.FilterDatatypeConfig;
import cz.datalite.zk.components.list.model.DLColumnUnitModel;
import cz.datalite.zk.components.list.view.DLListboxManager;
import org.apache.log4j.Logger;
import org.zkoss.lang.Strings;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Messagebox;

import java.util.*;

/**
 * Implementation of the controller for the Listbox manager which
 * provides extended tools.
 * @param <T> type of the main entity in the listbox
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class DLManagerControllerImpl<T> implements DLManagerController {

    protected static final Logger LOGGER = Logger.getLogger( DLManagerControllerImpl.class );
    // master controller
    protected final DLListboxExtController<T> masterController;
    // view
    protected final DLListboxManager manager;

    public DLManagerControllerImpl( final DLListboxExtController<T> masterController, final DLListboxManager manager ) {
        this.masterController = masterController;
        this.manager = manager;
        manager.setController( this );
    }

    /**
     * Invokes modal window
     * @param page page url
     * @param args arguments
     * @param listener on close listner
     */
    protected void showPage( final String page, final Map<String, Object> args, final EventListener listener ) {
        args.put( "master", masterController );
        final String uri = "~./dlzklib/resource/" + page;
        final org.zkoss.zul.Window win = ( org.zkoss.zul.Window ) org.zkoss.zk.ui.Executions.createComponents( uri, null, args );
        win.addEventListener( "onSave", listener );
        win.doHighlighted();
    }

    public void onColumnManager() {
        if ( isLocked() ) {
            return;
        }

        final Map<String, Object> args = new HashMap<String, Object>();
        final List<Map<String, Object>> columnModels = new LinkedList<Map<String, Object>>();

        for ( int index = 0; index < masterController.getColumnModel().getColumnModels().size(); index++ ) {
            final DLColumnUnitModel unit = masterController.getColumnModel().getColumnModels().get( index );

            final Map<String, Object> unitMap = new HashMap<String, Object>();
            unitMap.put( "label", unit.getLabel() );
            unitMap.put( "index", index );
            unitMap.put( "order", unit.getOrder() );
            unitMap.put( "visible", unit.isVisible() );

            columnModels.add( unitMap );
        }

        args.put( "columnModels", columnModels );
        final EventListener listener = new EventListener() {

            @SuppressWarnings( "unchecked" )
            public void onEvent( final Event event ) {
                masterController.onColumnManagerOk( ( List<Map<String, Object>> ) event.getData() );
            }
        };
        showPage( "listboxColumnManagerWindow.zul", args, listener );
    }

    public void onSortManager() {
        if ( isLocked() ) {
            return;
        }

        final Map<String, Object> args = new HashMap<String, Object>();
        final List<Map<String, Object>> columnModels = new LinkedList<Map<String, Object>>();

        for ( int index = 0; index < masterController.getColumnModel().getColumnModels().size(); index++ ) {
            final DLColumnUnitModel unit = masterController.getColumnModel().getColumnModels().get( index );
            if ( !unit.isDBSortable() || !unit.isVisible() ) {
                continue;
            }
            final Map<String, Object> unitMap = new HashMap<String, Object>();

            unitMap.put( "label", unit.getLabel() );
            unitMap.put( "column", unit.getSortColumn() );
            unitMap.put( "index", index );
            unitMap.put( "order", unit.getOrder() );
            unitMap.put( "sortType", unit.getSortType() );
            unitMap.put( "sortOrder", unit.getSortOrder() );

            columnModels.add( unitMap );
        }

        args.put( "columnModels", columnModels );
        final EventListener listener = new EventListener() {

            public void onEvent( final Event event ) {
                masterController.onSortManagerOk( ( List<Map<String, Object>> ) event.getData() );
            }
        };
        showPage( "listboxSortManagerWindow.zul", args, listener );
    }

    public void onFilterManager() {
        if ( isLocked() ) {
            return;
        }

        final Map<String, Object> args = new HashMap<String, Object>();
//        final List<Map<String, Object>> columnModels = new LinkedList<Map<String, Object>>();
        final NormalFilterModel templateModels = new NormalFilterModel();

        for ( int index = 0; index < masterController.getColumnModel().getColumnModels().size(); index++ ) {
            final DLColumnUnitModel unit = masterController.getColumnModel().getColumnModels().get( index );
            if ( !unit.isColumn() || unit.getLabel() == null || unit.getLabel().length() == 0 || !unit.isFilter() ) {
                continue;
            }
            if ( (unit.getColumnType() == null || !FilterDatatypeConfig.DEFAULT_CONFIGURATION.containsKey( unit.getColumnType() ))
                    && !unit.isFilterComponent() && !unit.isFilterOperators() ) {
                LOGGER.debug( "Column '" + unit.getColumn() + "' was ignored in filter due to not have the filter component or filter operators." );
                continue;
            }
            templateModels.add( new NormalFilterUnitModel( unit ) );
        }

//        final NormalFilterModel bindingModels = new NormalFilterModel();
//        for ( final Iterator<NormalFilterUnitModel> it = masterController.getNormalFilterModel().iterator(); it.hasNext(); ) {
//            final NormalFilterUnitModel unitModel = it.next();
//            for ( Map<String, Object> map : columnModels ) {
//                if ( unitModel.getColumn().equals( map.get( "column" ) ) ) {
//                    bindingModels.add( new NormalFilterUnitBindingModel( unitModel, ( String ) map.get( "label" ), ( Class ) map.get( "type" ) ) );
//                    // there is allowed mapping 1 column : 1 col. In case that more
//                    // cols has got same column will be setted first occurance.
//                    break;
//                }
//            }
//        }
        final NormalFilterModel filterModels = masterController.getNormalFilterModel().clone();

        args.put( DLNormalFilterKeys.TEMPLATES.toString(), templateModels );
        args.put( DLNormalFilterKeys.FILTERS.toString(), filterModels );
        final EventListener listener = new EventListener() {

            public void onEvent( final Event event ) {
                masterController.onFilterManagerOk( ( NormalFilterModel ) event.getData() );
            }
        };
        showPage( "listboxFilterManagerWindow.zul", args, listener );
    }

    public void onExportManager() {
        final Map<String, Object> args = new HashMap<String, Object>();
        final List<Map<String, Object>> columnModels = new LinkedList<Map<String, Object>>();

        for ( int index = 0; index < masterController.getColumnModel().getColumnModels().size(); index++ ) {
            final DLColumnUnitModel unit = masterController.getColumnModel().getColumnModels().get( index );

//          ****************************************
//            
//            if ( !unit.isColumn() ) {
//                continue;
//            }
//            modified by Karel Čemus on Ondřej Medek's suggestion
//            on 17.8.2010 due to cells with full entity binding
//            without specified field. eg.: value="@{listEach,converter=....}"
//            
//          ****************************************
//            edit begins

            final String label = unit.getLabel();
            if ( Strings.isEmpty( label ) ) {
                continue;
            }
//            edit ends
//          ****************************************

            final Map<String, Object> unitMap = new HashMap<String, Object>();
            unitMap.put( "label", unit.getLabel() );
            unitMap.put( "column", unit.getColumn() );
            unitMap.put( "index", index );
            unitMap.put( "order", unit.getOrder() );
            unitMap.put( "visible", unit.isVisible() );
            unitMap.put( "isConverter", unit.isConverter() );
            unitMap.put( "converter", unit.getConverter() );

            columnModels.add( unitMap );
        }

        args.put( "columnModels", columnModels );
        args.put( "rows", Math.max( 0, masterController.getModel().getPagingModel().getTotalSize() ) );
        args.put( "windowCtl", masterController.getWindowCtl() );
        final EventListener listener = new EventListener() {

            public void onEvent( final Event event ) {
                masterController.onExportManagerOk( ( AMedia ) event.getData() );
            }
        };
        showPage( "listboxExportManagerWindow.zul", args, listener );
    }

    public void onResetFilters() throws InterruptedException {
        if ( isLocked() ) {
            return;
        }

        Messagebox.show(Labels.getLabel("listbox.manager.resetFilter.message.text"),
                Labels.getLabel("listbox.manager.resetFilter.message.title"),
                Messagebox.OK | Messagebox.NO, Messagebox.QUESTION, new EventListener() {

            public void onEvent( final Event event ) {
                if ( event.getData().equals( Messagebox.OK ) ) {
                    masterController.clearFilterModel();
                }
            }
        } );
    }

    public void onResetAll() throws InterruptedException {
        if ( isLocked() ) {
            return;
        }

        Messagebox.show( Labels.getLabel("listbox.manager.reset.message.text"),
                Labels.getLabel("listbox.manager.reset.message.title"),
                Messagebox.OK | Messagebox.NO, Messagebox.QUESTION, new EventListener() {

            public void onEvent( final Event event ) {
                if ( event.getData().equals( Messagebox.OK ) ) {
                    masterController.clearAllModel();
                }
            }
        } );
    }

    /**
     * Checks if the master model is locked or not
     * @return returns if model is locked or not
     */
    protected boolean isLocked() {
        if ( masterController.isLocked() ) {
            try {
                Messagebox.show( "Listbox is locked.", "Locked", Messagebox.OK, Messagebox.INFORMATION );
            } catch ( InterruptedException ex ) {
                LOGGER.debug( "Messagebox failed.", ex );
            }
            return true;
        } else {
            return false;
        }
    }

    public List<String> getFilters() {
        final List<String> filters = new LinkedList<String>();
        for ( NormalFilterUnitModel unit : masterController.getNormalFilterModel() ) {
            final StringBuffer filter = new StringBuffer();
            filter.delete( 0, filter.length() );
            filter.append( masterController.getModel().getColumnModel().getByName( unit.getColumn() ).getLabel() );
            filter.append( ' ' ).append( unit.getOperator().getLabel().toLowerCase() ).append( ' ' );
            for ( int i = 1; i <= unit.getOperator().getArity(); ++i ) {
                if ( i > 1 ) {
                    filter.append( " a " );
                }
                if ( unit.getValue( i ) instanceof Date ) {
                    filter.append( DateHelper.dateToString( ( Date ) unit.getValue( i ) ) );
                } else {
                    // TODO if the value is complex obejct, there is no way how to override it's printout (always toString())
                    // this value is then printed in listbox manager as tooltip
                    filter.append( unit.getValue( i ) );
                }
            }
            filters.add( filter.toString() );
        }
        return filters;
    }

    public void fireChanges() {
        manager.fireChanges();
    }
}
