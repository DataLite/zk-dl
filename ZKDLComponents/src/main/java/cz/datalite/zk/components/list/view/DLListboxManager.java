package cz.datalite.zk.components.list.view;

import cz.datalite.zk.components.list.controller.DLManagerController;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.impl.XulElement;

import java.util.List;

/**
 * Bar with advanced tools for managing the listbox.
 *
 * @author Karel Cemus
 * @author Ondrej Medek <xmedeko@gmail.com>
 */
public class DLListboxManager extends XulElement {

    static {
        addClientEvent(DLListboxManager.class, "onColumnManager", CE_NON_DEFERRABLE);
        addClientEvent(DLListboxManager.class, "onSortManager", CE_NON_DEFERRABLE);
        addClientEvent(DLListboxManager.class, "onFilterManager", CE_NON_DEFERRABLE);
        addClientEvent(DLListboxManager.class, "onExportManager", CE_NON_DEFERRABLE);
        addClientEvent(DLListboxManager.class, "onResetFilters", CE_NON_DEFERRABLE);
        addClientEvent(DLListboxManager.class, "onResetAll", CE_NON_DEFERRABLE);
    }

    // enable/disable controls
    protected boolean columnManager = true;
    protected boolean sortManager = true;
    protected boolean filterManager = true;
    protected boolean exportManager = true;
    protected boolean resetFilters = false;
    protected boolean resetAll = true;

    protected static final String CONST_FILTER_MANAGER = Labels.getLabel( "listbox.tooltip.filterManager" );
    protected DLManagerController controller;

    public DLListboxManager() {
        super();
        addEventListener( "onColumnManager", new EventListener() {
            public void onEvent( final Event event ) {
                controller.onColumnManager();
            }
        } );
        addEventListener( "onSortManager", new EventListener() {
            public void onEvent( final Event event ) {
                controller.onSortManager();
            }
        } );
        addEventListener( "onFilterManager", new EventListener() {
            public void onEvent( final Event event ) {
                controller.onFilterManager();
            }
        } );
        addEventListener( "onExportManager", new EventListener() {
            public void onEvent( final Event event ) {
                controller.onExportManager();
            }
        } );
        addEventListener( "onResetFilters", new EventListener() {
            public void onEvent( final Event event ) throws InterruptedException {
                controller.onResetFilters();
            }
        } );
        addEventListener( "onResetAll", new EventListener() {
            public void onEvent( final Event event ) throws InterruptedException {
                controller.onResetAll();
            }
        } );
    }

    public void setController( final DLManagerController controller ) {
        this.controller = controller;
    }

    public String getFilterTooltip() {
        StringBuilder sb = new StringBuilder();

        if (controller != null) {
            List<String> filters = controller.getFilters();
            if ( !filters.isEmpty() ) {
                for ( String filter : filters ) {
                    sb.append( filter ).append( ", " );
                }
            }
        }

        return sb.toString();
    }

    public void fireChanges() {
        smartUpdate("filterTooltip", getFilterTooltip());
    }

    protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
            throws java.io.IOException {
        super.renderProperties(renderer);

        if (!columnManager) renderer.render("columnManager", columnManager);
        if (!sortManager) renderer.render("sortManager", sortManager);
        if (!filterManager) renderer.render("filterManager", filterManager);
        if (!exportManager) renderer.render("exportManager", exportManager);
        if (!resetFilters) renderer.render("resetFilters", resetFilters);
        if (!resetAll) renderer.render("resetAll", resetAll);

        render(renderer, "filterTooltip", getFilterTooltip());
    }

    public boolean isColumnManager() {
        return columnManager;
    }

    public void setColumnManager(boolean columnManager) {
        this.columnManager = columnManager;
        invalidate();
    }

    public boolean isSortManager() {
        return sortManager;
    }

    public void setSortManager(boolean sortManager) {
        this.sortManager = sortManager;
        invalidate();
    }

    public boolean isFilterManager() {
        return filterManager;
    }

    public void setFilterManager(boolean filterManager) {
        this.filterManager = filterManager;
        invalidate();
    }

    public boolean isExportManager() {
        return exportManager;
    }

    public void setExportManager(boolean exportManager) {
        this.exportManager = exportManager;
        invalidate();
    }

    public boolean isResetFilters() {
        return resetFilters;
    }

    public void setResetFilters(boolean resetFilters) {
        this.resetFilters = resetFilters;
        invalidate();
    }

    public boolean isResetAll() {
        return resetAll;
    }

    public void setResetAll(boolean resetAll) {
        this.resetAll = resetAll;
        invalidate();
    }
}
