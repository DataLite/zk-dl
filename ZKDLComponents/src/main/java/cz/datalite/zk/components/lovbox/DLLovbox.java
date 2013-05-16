package cz.datalite.zk.components.lovbox;

import cz.datalite.zk.bind.ZKBinderHelper;
import cz.datalite.zk.components.cascade.CascadableComponent;
import cz.datalite.zk.components.cascade.CascadableExt;
import cz.datalite.zk.components.list.controller.DLListboxExtController;
import cz.datalite.zk.components.list.filter.compilers.FilterCompiler;
import cz.datalite.zk.components.list.view.DLListbox;
import cz.datalite.zk.components.list.view.DLListhead;
import cz.datalite.zk.components.list.view.DLListheader;
import cz.datalite.zk.components.list.view.DLQuickFilter;
import cz.datalite.zk.components.paging.DLPaging;
import org.slf4j.LoggerFactory;
import org.zkoss.lang.Library;
import org.zkoss.lang.Objects;
import org.zkoss.lang.Strings;
import org.zkoss.lang.reflect.Fields;
import org.zkoss.util.Locales;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.OpenEvent;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zul.*;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Component simulating combobox behaviour. It is usable for huge
 * data model, when whole list can cause freezing client's  browser.
 * User can use filter and paging so required record can be found
 * quickly. Listbox model is loaded onOpen event, so whole page is
 * also quickly loaded. One of the disadvantages is intellisense,
 * which isn't supported in contrast to combobox.
 * @param <T>
 * @author Karel Cemus
 */
public class DLLovbox<T> extends Bandbox implements AfterCompose, CascadableComponent {

    public static final String LOVBOX_CLEAR_IMAGE = "~./dlzklib/img/remove16x16.png";
    /** Pattern to split {@link #labelProperties}/ */
    private static final Pattern PATTERN_LABEL_PROPERTIES = Pattern.compile( "\\s*,\\s*" );
    /** default component page size */
    private static final Integer PAGE_SIZE;
    /** default listbox rows */
    private static final Integer ROWS;
    /** logger */
    protected final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger( DLLovbox.class );
    
    static {
        // init page size
        PAGE_SIZE = Library.getIntProperty( "zk-dl.lovbox.pageSize", 10);   
        LOGGER.debug( "Lovbox default page size is defined as '" + PAGE_SIZE +  "'." );
        
        // init rows
         ROWS = Library.getIntProperty( "zk-dl.lovbox.rows", 10);
         LOGGER.debug( "Lovbox default number of rows in a listbox is defined as '" + ROWS + "'.");
    }
    
    // controller
    protected DLLovboxExtController<T> controller;
    // view
    protected Bandpopup popup;
    protected DLQuickFilter filter;
    protected DLPaging paging;
    protected DLListbox listbox;
    // params
    /** defines page size for paging comonent */
    protected Integer pageSize = PAGE_SIZE;
    /** number of rows  shown on the list, default value is same as pageSize to prevent page scrolling */
    protected Integer rows = ROWS;
    /** defines listbox width for component in the popup */
    protected String listWidth = "400px";
    /** defines names of properties which are shown in the lovbox value - Array of properties */
    protected String[] labelProperties = new String[] {};
    /** Format for properties */
    private String labelFormat;
    /** defines name of property, which is searched in database. */
    protected String searchProperty;
    /** defines filter compiler for shorten implementation */
    protected FilterCompiler filterCompiler;
    /** defines name of description property which is used whed
    one-row lovbox is defined. This property is used as the second
    column to specifie first {main) column with label. */
    protected String descriptionProperty;
    /** defines popup height because of display bug */
    protected String popupHeight;
    /** should be clear button present (to clear lovbox value) */
    protected boolean clearButton = true;
    /** allow filter by all button in quickfilter */
    protected boolean quickFilterAll = true;
    /** Cascading  parent component. ZUL component ID (either absolute ID, or binding sibling ID) */
    private String parentCascadeId;
    /** Cascading  parent property name. This property got from selectedItem in parentCascadeId component and used as a filter for this combo. */
    private String parentCascadeColumn;
    /** Readonly is implemented by disabled. */
    private boolean readonly;
    /** If true, lovbox will create paging component on the listbox. */
    private boolean createPaging = true;
    /** If true, lovbox will create quick filter on the listbox.  */
    private boolean createQuickFilter = true;
    /** Allow to select multiple values from the lovbox.  */
    private boolean multiple = false;
    /** defines hflex for each column in listbox. */
    protected String[] hflexes;

    /**
     * Create component without any parameter
     */
    public DLLovbox() {
        super();
        super.setReadonly( true );
        setAutodrop( true );

        // Bandbox component overrides onOk - we need it for quick filter
        setWidgetOverride("enterPressed_", "function (evt) {}");
        // default tooltip
        setTooltiptext(getValue());
    }

    /**
     * This method creates components like quick filter, paging and if it
     * is necessary also creates listbox.
     */
    public void afterCompose() {
        setClass( "z-lovbox" );
        popup = getDropdown();
        if ( popup == null ) { // if popup isn't defined in zul
            popup = new Bandpopup();
            popup.setParent( this );
        } else // try to find user added components
        {
            for ( Component child : ( List<Component> ) popup.getChildren() ) {
                if ( child instanceof DLListbox ) {
                    listbox = ( DLListbox ) child;
                    if ( listbox.getRows() > 0 ) {
                        pageSize = listbox.getRows();
                    }
                } else if ( child instanceof DLQuickFilter ) {
                    filter = ( DLQuickFilter ) child;
                } else if ( child instanceof DLPaging ) {
                    paging = ( DLPaging ) child;
                }
            }
        }

        popup.setSclass( "z-lovbox-popup" );
        
        if ( listbox == null ) { // if listbox isn't defined in zul
            listbox = new DLListbox(); // create component
            final Listhead head = new DLListhead(); // create lishead and listitem
            listbox.appendChild( head );

            if ( ZKBinderHelper.version( this ) == 1 ) {
                // version 1 does not support lovbox on a object
                if ( this.labelProperties.length == 0 )
                    throw new IllegalArgumentException( "Please define labelProperty in lovbox." );

                // binding template for databinding version 1.0
                final Listitem item = new Listitem();
                listbox.appendChild( item );
                // add renderer annotation
                item.addAnnotation( null, "default", Collections.singletonMap( "each", new String[]{ "row" + getUuid() } ) );

                //int i = 0;
                for ( String property : this.labelProperties ) {
                    //String hflex = hflexes == null ? null : hflexes.length >= i ? null : hflexes[i++];
                    this.createCell( property ); // create columns with properties
                }

                if ( descriptionProperty != null ) // if it is defined create description column
                    createCell( descriptionProperty );
            } else if (ZKBinderHelper.version( this ) == 2 ) {
                // binding template for databinding version 2.0
                // labelProperties and descriptionProperty may be null - then only one cell is created
                // and it will contain model value (i.e. toString() ).
                listbox.setTemplate( "model", new ListitemTemplate( labelProperties, descriptionProperty ) );

                for ( String property : this.labelProperties ) {
                    // header
                    final DLListheader header = new DLListheader();
                    header.setColumn( property );
                    head.appendChild( header );
                }
                
                if ( searchProperty != null ) // if search property is defined create search column
                    ( ( DLListheader ) head.getFirstChild() ).setColumn( searchProperty );
                if (descriptionProperty != null) {
                    final DLListheader header = new DLListheader();
                    header.setColumn( descriptionProperty );
                    head.appendChild( header );
                }
            }
            
            if ( filterCompiler != null ) {
                (( DLListheader ) listbox.getFirstChild().getFirstChild()).setFilterCompiler( filterCompiler );
            }

            if ( multiple ) {
                listbox.setCheckmark(true);
                listbox.setMultiple(true);
            }

            Events.postEvent( new Event( Events.ON_CREATE, listbox ) );

            listbox.setParent( popup );
        }
        
        listbox.setSelectFirstRow( false );

        if ( filter == null && isCreateQuickFilter()) {
            filter = new DLQuickFilter();
            filter.setQuickFilterAll(quickFilterAll);
            filter.setStyle( "margin: 5px;");
            if (searchProperty != null)
                filter.setQuickFilterDefault(searchProperty);
            popup.insertBefore( filter, listbox );            
        }                

        if ( paging == null && isCreatePaging()) {
            paging = new DLPaging();
            paging.setAutohide(true);
            popup.appendChild( paging );
        }

        // clear button is on absolute position in top right corner
        if ( clearButton ) {
            Button button = createClearButton();
            popup.appendChild( button );
        }
    }

    /**
     * Initializes component - copy setted properties and attach
     * controllers.
     * @throws Exception
     */
    public void init() throws Exception {
        listbox.setWidth( listWidth );
        if ( paging != null ) paging.setWidth( listWidth );
         
        if ( paging != null && pageSize != null ) {
            paging.setPageSize( pageSize );            
        }
        if (paging!=null) {
            paging.setStyle("background: none; border: 0;");
        }
        if ( pageSize != null && listbox.getRows() == 0 ) {
            listbox.setRows( rows );
        }
        
        controller.getListboxExtController().doAfterCompose( filter );
        controller.getListboxExtController().doAfterCompose( paging );
        controller.getListboxExtController().doAfterCompose( listbox );
    }

    /**
     * Lovbox component requires usage of DataBinding. If the component is created manunally (out of ZUL
     * and without databinding), it is mandatory to call lifecycle and binding method explicitely.
     *
     * Mandatory lifecycle methods:<ul>
     * <li> DLLovbox lovbox = new DLLovbox();
     * <li> lovbox.setLabelProperty("xxx"); // and other lovbox parameters
     * <li> lovbox.afterCompose();  // after compose lifecycle
     * <li> lovbox.setController( controller ); // custom controller to load data
     * <li> lovbox.initSelfBinding(); // create custom databinder for this component only and init binding.
     * </ul>
     */
    public void initSelfBinding()
    {
        AnnotateDataBinder binder = new AnnotateDataBinder(this);
        this.setAttribute("binder", binder);
        binder.loadAll();
    }

    /**
     * Set controller - this method is called from doAfterCompose
     * @param controller  controller for this component
     */
    public void registerController( final DLLovboxExtController<T> controller ) {
        this.controller = controller;
    }

    /**
     * This method can be call from zul throw databinding. This method sets
     * controller only if actual controller is null
     * @param controller controller for this component
     * @throws Exception
     */
    public void setController( final DLLovboxExtController<T> controller ) {
        try {
            if ( this.controller == null ) {
                controller.doAfterCompose( this );
            }
        } catch ( Exception ex ) {
            throw new RuntimeException( ex );
        }
    }

    public DLLovboxExtController<T> getController() {
        return controller;
    }

    /**
     * Sets width of the listbox in this popup
     * @param listWidth listbox width
     */
    public void setListWidth( final String listWidth ) {
        this.listWidth = listWidth;
    }

    /**
     * Sets page size for paging in the popup
     * @param pageSize page size
     */
    public void setPageSize( final int pageSize ) {
        this.pageSize = pageSize;
    }

    public void setClearButton( boolean clearButton ) {
        this.clearButton = clearButton;
    }

    /**
     * Method for databinding - returns selected item
     * @return selected item
     */
    public T getSelectedItem() {
        return controller.getSelectedItem();
    }

    /**
     * Method for databinding. Sets selected item if is different to
     * actual selected item.
     * @param selectedItem new selected item
     */
    public void setSelectedItem( final T selectedItem ) {
        if ( controller == null ) {
            throw new IllegalStateException( "Lovbox controller is missing (id=" + getId() + ")" );
        }
        final boolean isChange = !Objects.equals( selectedItem, controller.getSelectedItem() );

        controller.getListboxExtController().setSelectedItem( selectedItem );
        controller.getModel().setSelectedItem( selectedItem );

        if ( isChange ) {
            fireChanges();
        }
    }

    /**
     * Method for databinding - returns selected items (if multiple)
     * @return selected items
     */
    public Set<T> getSelectedItems() {
        return controller.getModel().getSelectedItems();
    }

    /**
     * Method for databinding. Sets selected items.
     * @param selectedItems list of selected items
     */
    public void setSelectedItems( final Set<T> selectedItems ) {
        if ( controller == null ) {
            throw new IllegalStateException( "Lovbox controller is missing (id=" + getId() + ")" );
        }
        final boolean isChange = !Objects.equals( selectedItems, getSelectedItems() );

        controller.getListboxExtController().setSelectedItems(selectedItems);
        controller.getModel().setSelectedItems(selectedItems);

        if ( isChange ) {
            fireChanges();
        }
    }

    /**
     * Refresh this component from model. Sets new lovbox value
     */
    public void fireChanges() {
        if ( this.labelProperties == null ) {
            throw new IllegalArgumentException( "Please define labelProperty in lovbox." );
        }

        if (!multiple)
        {
            final T selectedItem = getSelectedItem();
            if ( selectedItem == null ) {
                this.setValue( "" ); // nothing selected
                return;
            }

            // get values for properties
            this.setValue( getDispalyValueForModel(this.controller.getSelectedItem()) );
        }
        else
        {
            StringBuilder value = new StringBuilder();
            for (T selectedItem : getSelectedItems())
            {
                if (value.length() > 0)
                    value.append(",");

                value.append(getDispalyValueForModel(selectedItem));
            }
            this.setValue( value.toString() );
        }
    }

    /**
     * Get and format display value according to labelProperties and/or labelFormat.
     *
     * @param model model to get value
     * @return formated string
     */
    protected String getDispalyValueForModel(T model) {

        final int size = this.labelProperties.length;

        // special case without labelProperties -> just return toString().
        if (size == 0)
            return model == null ? "" : model.toString();

        Object[] values = new Object[ size ]; // values of properties
        for ( int i = 0; i < size; i++ ) {
            try {
                // null nahrad za prazdny retezec
                final Object value = Fields.getByCompound(model, this.labelProperties[i]);
                values[i] = value == null ? "" : value;
            } catch ( NoSuchMethodException e ) {
                final String msg = "Unknown value for: " + this.labelProperties[i];
                LOGGER.error( msg, e );
            }
        }

        // format values by the MessageFormat and this.labelFormat
        if ( this.labelFormat != null && !Strings.isEmpty(this.labelFormat) ) {
            String strValue;
            try {
                final MessageFormat messageFormat = new MessageFormat( this.labelFormat, Locales.getCurrent() );
                strValue = messageFormat.format( values );
            } catch ( IllegalArgumentException e ) {
                strValue = "Cannot format values by format: " + this.labelFormat;
                LOGGER.error( strValue, e );
            }
            return strValue;
        }

        // just append values to the String separated spaces
        final StringBuilder builder = new StringBuilder();
        for ( int i = 0; i < size; i++ ) {
            if ( i > 0 ) {
                builder.append( ' ' );
            }
            builder.append( String.valueOf( values[i] ) );
        }
        return builder.toString();
    }

    /**
     * This property is used in one-row lovbox
     * defines the second column in the listbox for specifiing
     * values;
     * @param descriptionProperty property in the main entity
     * @deprecated
     */
    public void setDescriptionProperty( final String descriptionProperty ) {
        this.descriptionProperty = descriptionProperty;

    }

    /**
     * This is obligated attribute - defines label property in the main
     * entity. This property is shown on lovbox value and if it is one-row
     * lovbox in the zul it is used also like a first column in the listbox
     * @param labelProperty property in the main entity
     */
    public void setLabelProperty( final String labelProperty ) {
        if ( labelProperty != null ) {
            this.labelProperties = PATTERN_LABEL_PROPERTIES.split( labelProperty );
            if ( this.labelProperties.length <= 0 ) {
                this.labelProperties = null;
            }
        }
    }

    /**
     * Creates cell in listhead and 1st listitem and registers binding of this field
     * to selected entity property.
     * @param field field of the main entity
     */
    protected void createCell( final String field ) {
        final Listcell cell = new Listcell();
        ZKBinderHelper.helper( this ).registerAnnotation( cell, "label", "value", "row" + getUuid() + "." + field );
        listbox.getLastChild().appendChild( cell );

        // header
        final DLListheader header = new DLListheader();
        if ( searchProperty != null ) // if search property is defined create search column
        {
            header.setColumn( searchProperty );
        }
        listbox.getFirstChild().appendChild( header );
    }

    /**
     * Sets number of visible rows in the listbox in the popup
     * @param rows number of visible rows
     * 
     * @since 1.4.0 replaced by pageSize
     */
    @Override
    public void setRows( final int rows ) {
        this.rows = rows;
    }

    /**
     * Sets popup height - it is usable because of display
     * bug of popup. Listbox often overflow from the popup.
     * @param popupHeight popup height
     */
    @Deprecated
    public void setPopupHeight( final String popupHeight ) {
        this.popupHeight = popupHeight;
    }

    /**
     * Readonly is implemented by disabled. Texbox is always readonly (Lovbox is for selecting items).
     *
     * @return true if is readonly
     */
    @Override
    public boolean isReadonly() {
        return readonly;
    }

    /**
     * Readonly is implemented by disabled. Texbox is always readonly (Lovbox is for selecting items).
     * @param readonly true to set readonly
     */
    @Override
    public void setReadonly( final boolean readonly ) {
        this.readonly = readonly;

        setAutodrop(!readonly);
        setButtonVisible(!readonly);
    }

    /**
     * Reacts on the onOpen event
     */
    public void onOpen(OpenEvent event) {
        // prevent reloading model on popup close
        if (!event.isOpen()) return;
        
        final DLListboxExtController<T> listboxExtController = controller.getListboxExtController();
        if ( listboxExtController.isLocked() ) {
            // if listbox model is locked - it is used for
            // the lazy loading - in the default, listbox model
            // is refreshed on onCreate event and if page has got
            // more pieces of this component it will be unnecessary
            // slow. So this controllers are locked in the default
            // state and on the first onOpen event are unlocked and
            // their models are refreshed.
            listboxExtController.unlockModel();
            if (listboxExtController.getListbox().isLoadDataOnCreate())
            {
                listboxExtController.refreshDataModel();
                listboxExtController.setSelectedItem( this.getSelectedItem() );
                listboxExtController.setSelectedItems( Collections.singleton( this.getSelectedItem() ) );
            }
        }

        // paging width is not properly calculated on the first opening
        // to ensure the correct appearance of the component
        if ( paging != null )
            paging.invalidate();
        
         // default focus on textbox of quickFilter
        if (filter != null)
            filter.setFocus(true);
    }

    protected Button createClearButton() throws UiException {
        final Button button = new Button();

        button.setLabel(Labels.getLabel("lovbox.clear"));
        button.setImage( LOVBOX_CLEAR_IMAGE );
        button.setStyle( "position: absolute; top: 5px; right: 2%; width: 90px" );
        button.addEventListener( Events.ON_CLICK, new EventListener() {

            public void onEvent( final Event event ) throws Exception {
                controller.setSelectedItem( null );
            }
        } );

        return button;
    }

    public void setParentCascadeColumn( final String parentCascadeColumn ) {
        this.parentCascadeColumn = parentCascadeColumn;
    }

    public void setParentCascadeId( final String parentCascadeId ) {
        this.parentCascadeId = parentCascadeId;
    }

    public CascadableExt getCascadableController() {
        return controller;
    }

    public String getParentCascadeColumn() {
        return parentCascadeColumn;
    }

    public String getParentCascadeId() {
        return parentCascadeId;
    }

    public String getSearchProperty() {
        return searchProperty;
    }

    public void setSearchProperty( final String searchProperty ) {
        this.searchProperty = searchProperty;
    }

    public void setFilterCompiler( final FilterCompiler filterCompiler ) {
        if ( this.filterCompiler == null ) {
            this.filterCompiler = filterCompiler;
        }
    }

    /**
     * <p>Sets label format for more than one label. To format is used {@link java.text.MessageFormat}.</p>
     * <b>Example:</b>
     * <p>labelFormat="{0} {1} - {2,date}"</p>
     *
     * @param labelFormat
     */
    public void setLabelFormat( final String labelFormat ) {
        this.labelFormat = labelFormat;
    }

    /**
     *
     * @return formatovaci retezec.
     */
    public String getLabelFormat() {
        return labelFormat;
    }

    /** If true, lovbox will create paging component on the listbox (default true). */
    public boolean isCreatePaging()
    {
        return createPaging;
    }

    /** If true, lovbox will create paging component on the listbox (default true). */
    public void setCreatePaging(boolean createPaging)
    {
        this.createPaging = createPaging;
    }

    /** If true, lovbox will create quick filter component on the listbox (default true). */
    public boolean isCreateQuickFilter()
    {
        return createQuickFilter;
    }

    /** If true, lovbox will create quick filter component on the listbox (default true). */
    public void setCreateQuickFilter(boolean createQuickFilter)
    {
        this.createQuickFilter = createQuickFilter;
    }

    /**
     * Allow to select multiple values from the lovbox.
     * @return true if multiple values
     */
    public boolean isMultiple() {
        return multiple;
    }

    /**
     * Allow to select multiple values from the lovbox.
     * @param multiple true if multiple values
     */
    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }

    public void setQuickFilterAll( boolean quickFilterAll ) {
        this.quickFilterAll = quickFilterAll;
    }

}
