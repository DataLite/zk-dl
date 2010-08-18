package cz.datalite.zk.components.list.view;

import cz.datalite.helpers.ZKBinderHelper;
import cz.datalite.zk.components.list.controller.DLQuickFilterController;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Textbox;

/**
 * Compnent for tool which allows user to quickly filter in the listbox
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class DLQuickFilter extends org.zkoss.zul.Hbox {

    // Controller
    protected DLQuickFilterController controller;
    // Model
    protected List<Entry<String, String>> model;
    protected boolean quickFilterAll = true;
    protected String quickFilterDefault;
    // View
    protected final Menupopup popup;
    protected final Textbox textbox;
    protected final Label selector;
    // Constants
    protected static final String CONST_DEFAULT_ICON_PATH = "~./dlzklib/img/";
    protected static final String CONST_IMAGE_SIZE = "20px";
    protected static final String CONST_IMAGE_STYLE = "";

    public DLQuickFilter() {
        super();

        final Hbox hbox = new Hbox();
        hbox.setSclass( "datalite-listbox-qfiltr" );
        appendChild( hbox );

        selector = new Label();
        selector.setSclass( "datalite-listbox-qfiltr-selector" );
        selector.setTooltiptext( "Rozsah filtrování" );
        selector.addEventListener( Events.ON_CLICK, new EventListener() {

            public void onEvent( final Event event ) {
                popup.open( selector );
            }
        } );
        hbox.appendChild( selector );

        popup = new Menupopup();
        popup.setSclass( "datalite-listbox-qfiltr-popup" );
        popup.setStyle( "z-index: 100000 !important;" );
        hbox.appendChild( popup );

        final Image open = new Image();
        open.setSclass( "datalite-listbox-qfiltr-open" );
        open.setTooltiptext( "Otevřít filtr" );
        open.setSrc( CONST_DEFAULT_ICON_PATH + "open.png" );
        open.addEventListener( Events.ON_CLICK, new org.zkoss.zk.ui.event.EventListener() {

            public void onEvent( final org.zkoss.zk.ui.event.Event event ) {
                popup.open( selector );
            }
        } );
        hbox.appendChild( open );

        textbox = new Textbox();
        textbox.setSclass( "datalite-listbox-qfiltr-textbox" );
        ZKBinderHelper.registerAnnotation( textbox, "value", "value", getId() + "_model.bindingModel.value" );
        textbox.addEventListener( Events.ON_OK, new org.zkoss.zk.ui.event.EventListener() {

            public void onEvent( final org.zkoss.zk.ui.event.Event event ) {
                onQuickFilter();
            }
        } );
        hbox.appendChild( textbox );

        final Image image = new Image();
        image.setSclass( "datalite-listbox-qfiltr-image" );
        image.setSrc( CONST_DEFAULT_ICON_PATH + "search25x25.png" );
        image.setStyle( CONST_IMAGE_STYLE );
        image.setWidth( CONST_IMAGE_SIZE );
        image.setHeight( CONST_IMAGE_SIZE );
        image.addEventListener( Events.ON_CLICK, new org.zkoss.zk.ui.event.EventListener() {

            public void onEvent( final org.zkoss.zk.ui.event.Event event ) {
                onQuickFilter();
            }
        } );
        appendChild( image );
    }

    public void setController( final DLQuickFilterController controller ) {
        this.controller = controller;
    }

    public void fireChanges() {
        // odstraníme všechny položky
        while ( popup.getChildren().size() > 0 ) {
            popup.removeChild( popup.getLastChild() );
        }

        // znovu vytvoříme položky, podle modelu
        if ( quickFilterAll ) {
            popup.appendChild( new Menuitem( "Vše" ) {

                {
                    setValue( cz.datalite.zk.components.list.filter.QuickFilterModel.CONST_ALL );
                    addEventListener( Events.ON_CLICK, new SelectMenuItemEventListener( this ) );
                }
            } );
        }

        for ( final Entry<String, String> entry : model ) {
            popup.appendChild( new Menuitem( entry.getValue() ) {

                {
                    setValue( entry.getKey() );
                    addEventListener( Events.ON_CLICK, new SelectMenuItemEventListener( this ) );
                }
            } );
        }


        setActiveFilter();
    }

    protected void setActiveFilter() {
        // Actual button selection
        final String key = controller.getBindingModel().getKey();
        if ( key != null && key.length() > 0 ) {
            for ( Entry<String, String> entry : model ) {
                if ( entry.getKey().equals( key ) ) {
                    setActiveFilter( ( Menuitem ) popup.getChildren().get( model.indexOf( entry ) + (quickFilterAll ? 1 : 0) ) );
                    return;
                }
            }
        }
        if ( popup.getChildren().size() > 0 ) {
            setActiveFilter( ( Menuitem ) popup.getFirstChild() );
            setVisible( true );
        } else {
            setVisible( false );
        }
    }

    protected void setActiveFilter( final Menuitem item ) {
        selector.setValue( item.getLabel() );
        controller.getBindingModel().setKey( item.getValue() );
    }

    public void onQuickFilter() {
        controller.onQuickFilter();
    }

    public void setModel( final List<Entry<String, String>> model ) {
        for ( final Iterator<Entry<String, String>> it = model.iterator(); it.hasNext(); ) {
            final Entry<String, String> entry = it.next();
            if ( entry.getValue() == null || Strings.isEmpty( entry.getValue() ) ) {
                it.remove();
            }
        }
        this.model = model;
    }

    public void setQuickFilterAll( final boolean quickFilterAll ) {
        this.quickFilterAll = quickFilterAll;
    }

    public void setQuickFilterDefault( final String quickFilterDefault ) {
        this.quickFilterDefault = quickFilterDefault;
    }

    public String getQuickFilterDefault() {
        return this.quickFilterDefault;
    }

    class SelectMenuItemEventListener implements org.zkoss.zk.ui.event.EventListener {

        protected final Menuitem item;
        protected final String column;

        public SelectMenuItemEventListener( final Menuitem item ) {
            this.item = item;
            this.column = item.getValue();
        }

        public void onEvent( final org.zkoss.zk.ui.event.Event event ) {
            controller.getBindingModel().setKey( column );
            setActiveFilter( item );
        }
    }

    @Override
    public String getSclass() {
        // listControll parent may contain default values for class
        if ( getParent() instanceof DLListControl ) {
            return super.getSclass() + " " + (( DLListControl ) getParent()).getQFilterClass();
        } else {
            return super.getSclass();
        }
    }

    @Override
    public String getStyle() {
        // listControll parent may contain default values for style
        if ( getParent() instanceof DLListControl ) {
            return super.getStyle() + " " + (( DLListControl ) getParent()).getQFilterStyle();
        } else {
            return super.getStyle();
        }
    }
}
