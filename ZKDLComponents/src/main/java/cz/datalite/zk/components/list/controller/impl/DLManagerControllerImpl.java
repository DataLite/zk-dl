package cz.datalite.zk.components.list.controller.impl;

import cz.datalite.helpers.DateHelper;
import cz.datalite.helpers.ZKDLResourceResolver;
import cz.datalite.helpers.excel.export.poi.POICell;
import cz.datalite.helpers.excel.export.poi.POICellStyles;
import cz.datalite.helpers.excel.export.poi.POIExcelExportUtils;
import cz.datalite.zk.components.list.controller.DLListboxExtController;
import cz.datalite.zk.components.list.controller.DLManagerController;
import cz.datalite.zk.components.list.enums.DLNormalFilterKeys;
import cz.datalite.zk.components.list.filter.NormalFilterModel;
import cz.datalite.zk.components.list.filter.NormalFilterUnitModel;
import cz.datalite.zk.components.list.filter.config.FilterDatatypeConfig;
import cz.datalite.zk.components.list.model.DLColumnUnitModel;
import cz.datalite.zk.components.list.view.DLListboxManager;
import cz.datalite.zk.components.list.window.controller.ListboxExportManagerController;
import cz.datalite.zk.converter.ZkConverter;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.lang.Classes;
import org.zkoss.lang.Library;
import org.zkoss.lang.Strings;
import org.zkoss.lang.reflect.Fields;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Messagebox;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Implementation of the controller for the Listbox manager which
 * provides extended tools.
 * @param <T> type of the main entity in the listbox
 * @author Karel Cemus
 */
public class DLManagerControllerImpl<T> implements DLManagerController {

    protected static final Logger LOGGER = LoggerFactory.getLogger( DLManagerControllerImpl.class );

    // should the user be asked for reset confirmation?
    private static boolean confirmReset = Boolean.valueOf(Library.getProperty("zk-dl.listbox.confirmReset", "true"));


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
    protected void showPage( final String page, final Map<String, Object> args, final EventListener<Event> listener ) {
        args.put( "master", masterController );
        final org.zkoss.zul.Window win;
        win = ( org.zkoss.zul.Window ) ZKDLResourceResolver.resolveAndCreateComponents(page, null, args);
        win.addEventListener( "onSave", listener );
        win.doHighlighted();
    }

    @Override
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
        final EventListener<Event> listener = new EventListener<Event>() {
            @SuppressWarnings( "unchecked" )
            public void onEvent( final Event event ) {
                masterController.onColumnManagerOk( ( List<Map<String, Object>> ) event.getData() );
            }
        };
        showPage( "listboxColumnManagerWindow.zul", args, listener );
    }

    @Override
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
        final EventListener<Event> listener = new EventListener<Event>() {
            @SuppressWarnings("unchecked")
			public void onEvent( final Event event ) {
                masterController.onSortManagerOk( ( List<Map<String, Object>> ) event.getData() );
            }
        };
        showPage( "listboxSortManagerWindow.zul", args, listener );
    }

    @Override
    public void onFilterManager() {
        if ( isLocked() ) {
            return;
        }

        final Map<String, Object> args = new HashMap<String, Object>();
//        final List<Map<String, Object>> columnModels = new LinkedList<Map<String, Object>>();
        final NormalFilterModel templateModels = new NormalFilterModel();

        final boolean wysiwyg = masterController.getModel().getFilterModel().isWysiwyg();

        for ( int index = 0; index < masterController.getColumnModel().getColumnModels().size(); index++ ) {
            final DLColumnUnitModel unit = masterController.getColumnModel().getColumnModels().get( index );
            if ( !unit.isColumn() || unit.getLabel() == null || unit.getLabel().length() == 0 || !unit.isFilter() ) {
                continue;
            }
            if ( (unit.getColumnType() == null || !FilterDatatypeConfig.DEFAULT_CONFIGURATION.containsKey( unit.getColumnType() ))
                    && !unit.isFilterComponent() && !unit.isFilterOperators() && !wysiwyg ) {
                LOGGER.debug( "Column '{}' was ignored in filter due to not have the filter component or filter operators.", unit.getColumn() );
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
        final EventListener<Event> listener = new EventListener<Event>() {
            public void onEvent( final Event event ) {
                masterController.onFilterManagerOk( ( NormalFilterModel ) event.getData() );
            }
        };
        showPage( "listboxFilterManagerWindow.zul", args, listener );
    }

    /**
     * exports current view in listbox and sends the file as a response
     */
    @Override
    public void exportCurrentView() {
        try {
            // grab model of current view
            final Map<String, Object> args = takeCurrentViewSnapshoot();

            @SuppressWarnings("unchecked")
			List<Map<String, Object>> columnModels = (List<Map<String, Object>>) args.get("columnModels");

            for (Iterator<Map<String, Object>> it = columnModels.iterator(); it.hasNext();) {
                Map<String, Object> item = it.next();
                // remove invisible columns
                if (!(Boolean) item.get("visible")) {
                    it.remove();
                }
            }
            Integer rows = (Integer) args.get("rows");

            export("report", "data", columnModels, rows);
        } catch (IOException ex) {
            LOGGER.error( "Something went wrong.", ex );
        }
    }

    /**
     * exports current view in listbox and returns the AMedia response.
     */
    @Override
    public AMedia directExportCurrentView() throws IOException {
        try {
            // grab model of current view
            final Map<String, Object> args = takeCurrentViewSnapshoot();

            @SuppressWarnings("unchecked")
			List<Map<String, Object>> columnModels = (List<Map<String, Object>>) args.get("columnModels");

            for (Iterator<Map<String, Object>> it = columnModels.iterator(); it.hasNext();) {
                Map<String, Object> item = it.next();
                // remove invisible columns
                if (!(Boolean) item.get("visible")) {
                    it.remove();
                }
            }
            Integer rows = (Integer) args.get("rows");

            return this.exportDirect("report", "data", columnModels, rows);
        } finally { }
    }

    @Override
    public void onExportManager() {

        // grab model of current view
        final Map<String, Object> args = takeCurrentViewSnapshoot();

        // add master component
        args.put("windowCtl", masterController.getWindowCtl());

        final EventListener<Event> listener = new EventListener<Event>() {
            public void onEvent(final Event event) throws IOException {
                @SuppressWarnings("unchecked")
				Map<String, Object> args = (Map<String, Object>) event.getData();
                String fileName = (String) args.get("filename");
                String sheetName = (String) args.get("sheetname");
                @SuppressWarnings("unchecked")
				List<Map<String, Object>> model = (List<Map<String, Object>>) args.get("model");
                Integer rows = (Integer) args.get("rows");
                export(fileName, sheetName, model, rows);
            }
        };

        // invoke form allowing to grabbed model change
        // process data when model modification is over
        showPage("listboxExportManagerWindow.zul", args, listener);
    }

    /**
     * Collects all information about current view in listbox. Such model
     * contains state of each column, their order, mapping and so on. The new
     * model can be used for data exportation.
     *
     * @return new model containing information about current view
     */
    private Map<String, Object> takeCurrentViewSnapshoot() {
        final Map<String, Object> args = new HashMap<String, Object>();
        final List<Map<String, Object>> columnModels = new LinkedList<Map<String, Object>>();

        for ( int index = 0; index < masterController.getColumnModel().getColumnModels().size(); index++ ) {
            final DLColumnUnitModel unit = masterController.getColumnModel().getColumnModels().get( index );

//          ****************************************
//            
//            if ( !unit.isColumn() ) {
//                continue;
//            }
//            modified by Karel Cemus on Ondřej Medek's suggestion
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
            unitMap.put( "column", (unit.getExportColumn() != null) ? unit.getExportColumn() : unit.getColumn() );
            unitMap.put("columnType", unit.getColumnType());
            unitMap.put( "index", index );
            unitMap.put( "order", unit.getOrder() );
            unitMap.put( "visible", unit.isVisible() );
            unitMap.put( "exportable", unit.isExportable() );
            unitMap.put( "isConverter", unit.isConverter() );
            unitMap.put( "converter", unit.getConverter() );

            columnModels.add( unitMap );
        }

        args.put("columnModels", columnModels);
        args.put("rows", Math.max(0, masterController.getModel().getPagingModel().getTotalSize()));

        return args;
    }

    @Override
    public void onResetFilters() throws InterruptedException {
        if ( isLocked() ) {
            return;
        }

        if (confirmReset)
            Messagebox.show(Labels.getLabel("listbox.manager.resetFilter.message.text"),
                    Labels.getLabel("listbox.manager.resetFilter.message.title"),
                    Messagebox.OK | Messagebox.NO, Messagebox.QUESTION, new EventListener<Event>() {

                public void onEvent( final Event event ) {
                    if ( event.getData().equals( Messagebox.OK ) ) {
                        masterController.clearFilterModel();
                    }
                }
            } );
        else
            masterController.clearFilterModel();

    }

    @Override
    public void onResetAll() throws InterruptedException {
        if ( isLocked() ) {
            return;
        }

        if (confirmReset)
            Messagebox.show( Labels.getLabel("listbox.manager.reset.message.text"),
                    Labels.getLabel("listbox.manager.reset.message.title"),
                    Messagebox.OK | Messagebox.NO, Messagebox.QUESTION, new EventListener<Event>() {

                public void onEvent( final Event event ) {
                    if ( event.getData().equals( Messagebox.OK ) ) {
                        masterController.clearAllModel();
                    }
                }
            } );
        else
            masterController.clearAllModel();
    }

    /**
     * Checks if the master model is locked or not
     * @return returns if model is locked or not
     */
    protected boolean isLocked() {
        if ( masterController.isLocked() ) {
            Messagebox.show( "Listbox is locked.", "Locked", Messagebox.OK, Messagebox.INFORMATION );
            return true;
        } else {
            return false;
        }
    }

    @Override
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

    @Override
    public void fireChanges() {
        manager.fireChanges();
    }

    protected void export(final String fileName, final String sheetName, final List<Map<String, Object>> model, int rows) throws IOException {

		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		final Workbook workbook = POIExcelExportUtils.createWorkbook();

		List<List<POICell>> cells = prepareSource(model, rows);
		headerStyle(cells.get(0), workbook);
		final AMedia file = POIExcelExportUtils.exportSimple(fileName, sheetName, cells, os, workbook);
        masterController.onExportManagerOk(file);
    }

	protected AMedia exportDirect(final String fileName, final String sheetName, final List<Map<String, Object>> model, int rows) throws IOException {

		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		final Workbook workbook = POIExcelExportUtils.createWorkbook();

		List<List<POICell>> cells = prepareSource(model, rows);
		headerStyle(cells.get(0), workbook);
		final AMedia file = POIExcelExportUtils.exportSimple(fileName, sheetName, cells, os, workbook);
        return file;
    }

	private void headerStyle(List<POICell> row, Workbook workbook) {
		CellStyle cellStyle = POICellStyles.headerCellStyle(workbook);
		for (POICell cell : row) {
			cell.setStyle(cellStyle);
		}
	}

	private List<List<POICell>> prepareSource(List<Map<String, Object>> model, int rows) {

		List<List<POICell>> result = new LinkedList<>();

        final List<POICell> heads = new ArrayList<>();

        // list of columns that need to be visible only for the purpose of export
        // (listbox controller may skip hidden columns for performance reasons, so we need to make them "visible" and hide them back in the end of export)
        final List<DLColumnUnitModel> hideOnFinish = new LinkedList<>();


		for (Map<String, Object> unit : model) {
            heads.add(new POICell(unit.get("label")));
        }

        // load data
        List<T> data;
        try {
            // ensure, that column is visible in the model (is hidden if the user has added it only for export)
            for (Map<String, Object> unit : model) {
                DLColumnUnitModel columnUnitModel = masterController.getModel().getColumnModel().getColumnModel((Integer) unit.get("index") + 1);
                if (!columnUnitModel.isVisible()) {
                    columnUnitModel.setVisible(true);
                    hideOnFinish.add(columnUnitModel);
                }
            }

            // and load data
			int exportMaxRows = ListboxExportManagerController.exportMaxRows;
            data = masterController.loadData((rows == 0) ? exportMaxRows : Math.min(rows, exportMaxRows)).getData();
        } finally {
            // after processing restore previous state
            for (DLColumnUnitModel hide : hideOnFinish) {
                hide.setVisible(false);
            }
        }

        if (masterController.getListbox().getAttribute("disableExcelExportHeader") == null) {
            result.add(heads);
        }

        for (Object entity : data) {
			final List<POICell> row = new LinkedList<>();
            for (Map<String, Object> unit : model) {
                try {
                    final String columnName = (String) unit.get("column");

                    Object value;

                    if (entity instanceof Map) {
                        value = ((Map) entity).get(columnName);
                    } else {
                        value = (Strings.isEmpty(columnName)) ? entity : Fields.getByCompound(entity, columnName);
                    }

                    Class columnType = (Class) unit.get("columnType");
                    if ( value != null && columnType != null) {
                        try {
                            value = Classes.coerce(columnType, value);
                        } catch (ClassCastException e) {
                            LOGGER.trace("Unable to convert export value {} to columnType {} - {}.", value, columnType, e);
                        }
                    }

                    if ((Boolean) unit.get("isConverter")) {
                        ZkConverter converter = (ZkConverter) unit.get("converter");
                        value = converter.convertToView( value );
                    }

                    row.add(new POICell(value));
                } catch (Exception ex) { // ignore
                    LOGGER.warn("Error occured during exporting column '{}'.", unit.get("column"), ex);
                }
            }
			result.add(row);

        }
        return result;
    }

}
