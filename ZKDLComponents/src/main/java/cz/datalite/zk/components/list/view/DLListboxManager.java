package cz.datalite.zk.components.list.view;

import cz.datalite.zk.components.list.controller.DLManagerController;
import java.util.List;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Separator;

/**
 * Bar with advanced tools for managing the listbox.
 *
 * @author Karel Čemus <cemus@datalite.cz>
 * @author Ondrej Medek <xmedeko@gmail.com>
 */
public class DLListboxManager extends Hbox {

    protected static final String CONST_EVENT = Events.ON_CLICK;
    protected static final String CONST_DEFAULT_ICON_PATH = "~./dlzklib/img/";
    protected static final String CONST_IMAGE_SIZE = "20px";
    protected static final String CONST_FILTER_MANAGER = Labels.getLabel( "listbox.tooltip.filterManager" );
    protected DLManagerController controller;
    protected Image filterManager;

    public DLListboxManager() {
        super();
        this.setPack( "center" );
        this.setSclass( "datalite-listbox-manager" );

        // icon separator
        final Separator imgSeparator = new Separator();
        imgSeparator.setBar( true );
        imgSeparator.setOrient( "vertical" );

        Image image = this.createImage();

        image.setTooltiptext( Labels.getLabel( "listbox.tooltip.columnManager" ) );
        image.setSrc( CONST_DEFAULT_ICON_PATH + "menu_items_small.png" );
        image.addEventListener( CONST_EVENT, new EventListener() {

            public void onEvent( final Event event ) {
                controller.onColumnManager();
            }
        } );
        this.appendChild( image );
        this.appendChild( ( Separator ) imgSeparator.clone() );

        image = this.createImage();
        image.setTooltiptext( Labels.getLabel( "listbox.tooltip.sortingManager" ) );
        image.addEventListener( CONST_EVENT, new EventListener() {

            public void onEvent( final Event event ) {
                controller.onSortManager();
            }
        } );
        image.setSrc( CONST_DEFAULT_ICON_PATH + "sort_small.png" );
        this.appendChild( image );
        this.appendChild( ( Separator ) imgSeparator.clone() );

        this.filterManager = this.createImage();
        this.filterManager.setTooltiptext( CONST_FILTER_MANAGER );
        this.filterManager.addEventListener( CONST_EVENT, new EventListener() {

            public void onEvent( final Event event ) {
                controller.onFilterManager();
            }
        } );
        this.appendChild( this.filterManager );
        this.appendChild( ( Separator ) imgSeparator.clone() );

        image = this.createImage();
        image.setTooltiptext( Labels.getLabel( "listbox.tooltip.export" ) );
        image.addEventListener( CONST_EVENT, new EventListener() {

            public void onEvent( final Event event ) {
                controller.onExportManager();
            }
        } );
        image.setSrc( CONST_DEFAULT_ICON_PATH + "excel_small.png" );
        this.appendChild( image );
        this.appendChild( ( Separator ) imgSeparator.clone() );

        image = this.createImage();
        image.setTooltiptext( Labels.getLabel( "listbox.tooltip.resetFilters" ) );
        image.addEventListener( CONST_EVENT, new EventListener() {

            public void onEvent( final Event event ) throws InterruptedException {
                controller.onResetFilters();
            }
        } );
        image.setSrc( CONST_DEFAULT_ICON_PATH + "cancel_filter.png" );
        this.appendChild( image );
        this.appendChild( ( Separator ) imgSeparator.clone() );

        image = this.createImage();
        image.setTooltiptext( Labels.getLabel( "listbox.tooltip.resetAll" ) );
        image.addEventListener( CONST_EVENT, new EventListener() {

            public void onEvent( final Event event ) throws InterruptedException {
                controller.onResetAll();
            }
        } );
        image.setSrc( CONST_DEFAULT_ICON_PATH + "trash.png" );
        this.appendChild( image );
    }

    /**
     * @return A new {@link Image} with CSS class, height and width.
     */
    private Image createImage() {
        final Image image = new Image();
        image.setStyle("cursor: pointer;");
        image.setWidth( CONST_IMAGE_SIZE );
        image.setHeight( CONST_IMAGE_SIZE );
        image.setClass( "datalite-listbox-manager-image" );
        return image;
    }

    public void setController( final DLManagerController controller ) {
        this.controller = controller;
    }

    public void onColumnManager() {
        controller.onColumnManager();
    }

    public void onSortManager() {
        controller.onSortManager();
    }

    public void onFilterManager() {
        controller.onFilterManager();
    }

    public void onExportManager() {
        controller.onExportManager();
    }

    public void fireChanges() {
        final StringBuffer tooltip = new StringBuffer( CONST_FILTER_MANAGER + ": " );
        final List<String> filters = controller.getFilters();

        if ( filters.isEmpty() ) {
            tooltip.append( Labels.getLabel( "listbox.tooltip.noActiveFilter" ) );
            filterManager.setSrc( CONST_DEFAULT_ICON_PATH + "filter_small.png" );
        } else {
            for ( String filter : filters ) {
                tooltip.append( filter ).append( ", " );
            }
            tooltip.delete( tooltip.length() - 2, tooltip.length() );
            filterManager.setSrc( CONST_DEFAULT_ICON_PATH + "filter_small_active.png" );
        }

        if ( tooltip.length() < 150 ) {
            filterManager.setTooltiptext( tooltip.toString() );
        } else {
            filterManager.setTooltiptext( tooltip.substring( 0, 150 ) + "..." );
        }
    }
}
