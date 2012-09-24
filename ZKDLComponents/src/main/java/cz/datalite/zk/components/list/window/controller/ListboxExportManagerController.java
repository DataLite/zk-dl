package cz.datalite.zk.components.list.window.controller;

import cz.datalite.zk.components.list.controller.DLListboxExtController;
import cz.datalite.zk.components.list.view.DLListbox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zk.ui.util.GenericAutowireComposer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;

/**
 * Controller for the export manager
 * @author Karel Cemus
 */
@SuppressWarnings( "unchecked" )
public class ListboxExportManagerController extends GenericAutowireComposer {

    // model
    protected List<Map<String, Object>> usedModel = new ArrayList<Map<String, Object>>();
    protected List<Map<String, Object>> unusedModel = new ArrayList<Map<String, Object>>();
    protected Integer rows;
    protected String sheetName = "data";
    protected String fileName = "report";
    // view
    DLListbox usedListbox;
    DLListbox unusedListbox;
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
            if ( ( Boolean ) map.get( "visible" ) ) {
                usedModel.add( prepareMap( map ) );
            } else {
                unusedModel.add( prepareMap( map ) );
            }
        }

        java.util.Collections.sort( usedModel, new java.util.Comparator<Map<String, Object>>() {

            public int compare( final Map<String, Object> o1, final Map<String, Object> o2 ) {
                return ( Integer ) o1.get( "order" ) - ( Integer ) o2.get( "order" );
            }
        } );

        selector = new ListboxSelectorController( usedModel, unusedModel, usedListbox, unusedListbox );
    }

    protected Map<String, Object> prepareMap(final Map<String, Object> map) {
        return new java.util.HashMap<String, Object>(map);
    }
    
    public void onExport() {
        try {
            Map<String, Object> args = new HashMap<String, Object>();
            args.put( "filename", fileName );
            args.put( "sheetname", sheetName );
            args.put( "model", usedModel );
            args.put( "rows", rows );

            Events.postEvent( new org.zkoss.zk.ui.event.Event( "onSave", self, args ) );
            self.detach();
        } finally {
            Clients.clearBusy();
        }
    }

    public void onOk() throws FileNotFoundException, IOException {
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
}
