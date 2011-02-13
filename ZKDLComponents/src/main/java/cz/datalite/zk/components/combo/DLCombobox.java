package cz.datalite.zk.components.combo;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zkplus.databind.BindingListModelList;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ComboitemRenderer;
import org.zkoss.zul.Constraint;

import cz.datalite.helpers.ZKBinderHelper;
import cz.datalite.zk.components.cascade.CascadableComponent;
import cz.datalite.zk.components.cascade.CascadableExt;
import cz.datalite.zk.components.constraint.DLConstraint;
import org.zkoss.zk.ui.HtmlBasedComponent;

/**
 * <p>Extended combobox which allows loading data from the datastore on the onOpen
 * event. Also there is implemented combobox cascade for simply usage.</p>
 * <p>The goal is dramatically faster page loading, because model isn't generated
 * during page creating.</p>
 * <p>Recommended maximal size of the model is 1000 records. Comboboxes on the page
 * are slow but it is still acceptable.</p>
 *
 * @param <T> main entity in the combobox
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class DLCombobox<T> extends Combobox implements CascadableComponent {

    /** Controller for the combobox which reacts on the events generated on the component. */
    protected DLComboboxExtController<T> controller = new DLComboboxDefaultController<T>( this );
    /** Contraint setted by user - it is cleared when "readonly" is setted because there are some bugs  */
    private Constraint _constraint;
    /** object is clone or original */
    protected boolean cloned = false;
    /** onChange listener which reacts onDelete event because onSelect doesn't proceed */
    protected EventListener onOk;
    /** Cascading combo parent component. ZUL component ID (either absolute ID, or binding sibling ID) */
    private String parentCascadeId;
    /** Cascading combo parent property name. This property got from selectedItem in parentComboId component and used as a filter for this combo. */
    private String parentCascadeColumn;
    /** label read from binding - used to sql as sort column */
    protected String label;
    /** defines if the select first row in cascade refresh */
    protected boolean selectFirstOnCascade = true;

    /**
     * Creates component with default settings. In this method is called
     * initialization.
     */
    public DLCombobox() {
        super();
        init();

        addEventListener( "onLoadOnSave", new EventListener() {

            public void onEvent( final Event event )  {
                //
                // HACK!!!!
                //
                // @since 16.9.2010
                // @author Karel Cemus
                // @author Jiri Bubnik
                //
                // ZK databinder registers own listener is no other is found.
                // Load on Save is very buggy feature, we dont want to use it.
                // There is on library property to disable it, so we disable it this way
            }
        } );
    }

    /**
     * Initializes component with registering listeners and model
     */
    protected void init() {
        // when user types something into component or delete it
        // and then presses enter it is not caught. Due to this bug
        // there is method which catches it and sets focus on its
        // parent component.
        onOk = new EventListener() {

            public void onEvent( final Event event ) {
                if ( getParent() instanceof HtmlBasedComponent ) {
                    (( HtmlBasedComponent ) getParent()).focus();
                }
            }
        };
        addEventListener( Events.ON_OK, onOk );
    }

    /**
     * Sets on of the standard contraints
     *
     * @param constr constraint (ex. NO EMPTY)
     */
    @Override
    public void setConstraint( final String constr ) {
        setConstraint( DLConstraint.getDLConstraint( constr ) );
    }

    @Override
    public void setConstraint( final Constraint constr ) {
        _constraint = constr;

        if ( !isReadonly() ) {
            super.setConstraint( _constraint );
        }
    }

    public String getParentCascadeId() {
        return parentCascadeId;
    }

    public void setParentCascadeId( final String parentComboId ) {
        this.parentCascadeId = parentComboId;
    }

    public String getParentCascadeColumn() {
        return parentCascadeColumn;
    }

    public void setParentCascadeColumn( final String parentComboColumn ) {
        this.parentCascadeColumn = parentComboColumn;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<Comboitem> getChildren() {
        return super.getChildren();
    }

    /**
     * Creates binding model from the ordinary list and sets it like a model
     * @param model new combobox model
     */
    protected void setListModel( final List<T> model ) {
        setModel( new BindingListModelList( model, true ) );
    }

    /**
     * Notifies combobox that model changed so model load is required
     */
    public void fireModelChanges() {
        setListModel( controller.getModel() );
    }

    /**
     * Catches event onOpen and notifies controller
     */
    public void onOpen() {
        getController().onOpen();
    }

    /**
     * Returns controller. If it is null exception is generated
     * @return controller or exception
     */
    protected DLComboboxExtController<T> getController() {
        if ( controller == null ) {
            throw new IllegalStateException( "Combobox controller is mising." );
        }
        return controller;
    }

    /**
     * Saves controller from the zul. Use it if combobox is used like a template
     * in the renderer. This method <b>ONCE</b> sets controller and it can be
     * called with binding.
     *
     * @param controller component's controller
     * @throws Exception
     */
    public void setController( final DLComboboxController<T> controller ) throws Exception {
        if ( this.controller instanceof DLComboboxDefaultController ) {
            controller.doAfterCompose( this );
        }
    }

    /**
     * Sets component controller - it is call from the controller, no from
     * outside. It must be do during doAfterCompose
     * @param controller component's controller
     */
    public void registerController( final DLComboboxExtController<T> controller ) {
        this.controller = controller;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Object clone() {
        // if combobox is used in the renderer there is following calls:
        // - create
        // - set model and set attribute "zul.Combobox.ON_INITRENDER"
        // - clone
        // - do rendrer
        // - do render later = binding
        //
        // so if we don't remove that attribute model won't be rendered
        // because there is origin empty model and new won't be setted.
        // there is also required call initialize method because of listeners
        if ( !cloned ) {
            removeAttribute( "zul.Combobox.ON_INITRENDER" );
            removeEventListener( Events.ON_OK, onOk );
            cloned = true;
        }

        final DLCombobox<T> clone = ( DLCombobox<T> ) super.clone();
        clone.init();
        clone.cloned = false;
        clone.controller = new DLComboboxDefaultController<T>( clone );
        return clone;
    }

    @Override
    public void onChildAdded( final Component child ) {
        super.onChildAdded( child );

        // When a Combobox is created the property readonly is evaluated
        // before children are added.
        // So we need to set the initial status of the child (disabled true/false)
        // when each child is added the first time
        if ( child instanceof Comboitem ) {
            (( Comboitem ) child).setDisabled( isReadonly() );
        }
    }

    @Override
    public void setReadonly( final boolean readonly ) {
        super.setReadonly( readonly );

        // as the children drop list is shown on clicking on the field area
        // even if the button is not visible, we set the children status
        // (disabled if readonly) so that the user can't click on them
        for ( Object element : getChildren() ) {
            final Component c = ( Component ) element;
            if ( c instanceof Comboitem ) {
                (( Comboitem ) c).setDisabled( isReadonly() );
            }
        }

        // contraint is changed according to readonly or not
        if ( readonly ) {
            super.setConstraint( ( Constraint ) null );
        } else {
            super.setConstraint( _constraint );
        }
    }

    /**
     * Returns binding label field - <b>finds binding expression from comboitem template</b>
     * @return binding label field
     */
    public String getLabel() {
        return label;
    }

    public CascadableExt getCascadableController() {
        return controller;
    }

    @Override
    public void setItemRenderer( final ComboitemRenderer renderer ) {
        super.setItemRenderer( renderer );
        final Comboitem template = getItemAtIndex( 0 );
        if ( template == null ) {
            return;
        }
        label = ZKBinderHelper.getDefaultAnnotation( template, "label", "value" );

        if ( label != null && label.indexOf( '.' ) > -1 ) {
            label = label.substring( label.indexOf( '.' ) + 1 );
        } else {
            label = null;
        }
    }

    /**
     * Sets if the first row should be selected on cascade refresh. Otherwise
     * the null is selected
     * @param bool
     */
    public void setSelectFirstOnCascade( final boolean bool ) {
        selectFirstOnCascade = bool;
    }

    public boolean isSelectFirstOnCascade() {
        return selectFirstOnCascade;
    }
}
