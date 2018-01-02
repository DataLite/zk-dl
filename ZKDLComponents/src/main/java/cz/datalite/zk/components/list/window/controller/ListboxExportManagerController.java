package cz.datalite.zk.components.list.window.controller;

import cz.datalite.helpers.StringHelper;
import cz.datalite.zk.components.list.controller.DLListboxExtController;
import cz.datalite.zk.components.list.view.DLListbox;
import org.zkoss.bind.BindUtils;
import org.zkoss.lang.Library;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zk.ui.util.GenericAutowireComposer;
import org.zkoss.zul.Textbox;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for the export manager
 * @author Karel Cemus
 */
@SuppressWarnings( "unchecked" )
public class ListboxExportManagerController extends GenericAutowireComposer {

    /**
     * Global event when exporting data
     * @see org.zkoss.bind.annotation.GlobalCommand
     * @see #onExport()
     */
    public static final String ON_EXPORT_GLOBAL = "zk-dl.listbox.onExportGlobal";
    // model
    protected List<Map<String, Object>> usedModel = new ArrayList<>();
    protected List<Map<String, Object>> unusedModel = new ArrayList<>();
    protected Integer rows;
    protected String sheetName = "data";
    protected String fileName = "report";


	/**
	 * Max rows allowed to be exported
	 */
	public static int exportMaxRows = Integer.valueOf(Library.getProperty("zk-dl.listbox.export.maxRows", "65535"));
    
    // view
    DLListbox usedListbox;
    DLListbox unusedListbox;
    Textbox   quickFilter;
    
    // controller
    protected ListboxSelectorController selector;
    protected DLListboxExtController masterController;
    protected Composer windowCtl;

    @Override
    public void doAfterCompose( final Component comp ) throws Exception {
        super.doAfterCompose( comp );
        comp.setAttribute( "ctl", this, Component.SPACE_SCOPE);

        // save masterController
        masterController = ( DLListboxExtController ) arg.get( "master" );
        // save componentu okna
        windowCtl = ( Composer ) arg.get( "windowCtl" );

        // save row count
        rows = ( Integer ) arg.get( "rows" );

        final List<Map<String, Object>> columnModels = ( List<Map<String, Object>> ) arg.get( "columnModels" );

        for ( Map<String, Object> map : columnModels ) {
        	// add columns to export model settings (only exportable)
        	if ((Boolean) map.get("exportable")) {
        	 	if ((Boolean) map.get("visible") ) {
        			usedModel.add( prepareMap( map ) );
        		} else {
        			unusedModel.add( prepareMap( map ) );
        		}
        	}
        }

        java.util.Collections.sort( usedModel, new java.util.Comparator<Map<String, Object>>() {

            public int compare( final Map<String, Object> o1, final Map<String, Object> o2 ) {
                return ( Integer ) o1.get( "order" ) - ( Integer ) o2.get( "order" );
            }
        } );

        selector = new ListboxSelectorController( usedModel, unusedModel, usedListbox, unusedListbox, quickFilter );
    }

    protected Map<String, Object> prepareMap(final Map<String, Object> map) {
        return new java.util.HashMap<>(map);
    }
    
    public void onExport() {
        try {
            Map<String, Object> args = new HashMap<>();
            args.put("filename", fileName);
            args.put( "sheetname", StringHelper.isNull(sheetName) ? "data" : sheetName );
            args.put("model", usedModel);
            args.put("rows", rows);
            args.put("entityClass", masterController != null ? masterController.getEntityClass() : null);// not used in export - for global-command only

            Events.postEvent( new org.zkoss.zk.ui.event.Event( "onSave", self, args ) );
            BindUtils.postGlobalCommand(null, EventQueues.DESKTOP, ON_EXPORT_GLOBAL, args);
            self.detach();
        } finally {
            Clients.clearBusy();
        }
    }

    public void onOk() throws FileNotFoundException, IOException, WrongValueException {

        if(rows == null || rows < 0){
            Component intBox = self.getFellowIfAny("intboxRows", true);
            throw new WrongValueException(intBox != null ? intBox : self, Labels.getLabel("listbox.exportManager.error.rows.message" , new Object[] {exportMaxRows}));
        }

        if(StringHelper.isNull(fileName)){
            Component textBox = self.getFellowIfAny("textboxFileName", true);
            throw new WrongValueException(textBox != null ? textBox : self, Labels.getLabel("listbox.exportManager.error.fileName.message" ));
        }

        Clients.showBusy( Labels.getLabel( "listbox.exportManager.wait") );
        Events.echoEvent( "onExport", self, null);
    }

    public void onStorno() {
        self.detach();
    }

    public ListboxSelectorController getSelector() {
        return selector;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows( final Integer rows ) {
        this.rows = rows;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName( final String fileName ) {
        this.fileName = fileName;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName( final String sheetName ) {
        this.sheetName = sheetName;
    }

	public String getRecordCountLabel() {
		// ${c:l2(...) did not work...
		return Labels.getLabel("listbox.exportManager.recordCount", new Object[] {exportMaxRows});
	}
}
