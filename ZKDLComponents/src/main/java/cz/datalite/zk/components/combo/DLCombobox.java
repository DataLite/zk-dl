package cz.datalite.zk.components.combo;

import java.util.HashSet;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zkplus.databind.Binding;
import org.zkoss.zkplus.databind.BindingListModelList;
import org.zkoss.zkplus.databind.DataBinder;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Constraint;

import cz.datalite.helpers.ZKBinderHelper;
import cz.datalite.zk.components.cascade.CascadableComponent;
import cz.datalite.zk.components.cascade.CascadableExt;
import cz.datalite.zk.components.constraint.DLConstraint;

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
	protected EventListener onChange;
	/** Cascading combo parent component. ZUL component ID (either absolute ID, or binding sibling ID) */
	private String parentCascadeId;
	/** Cascading combo parent property name. This property got from selectedItem in parentComboId component and used as a filter for this combo. */
	private String parentCascadeColumn;

	/**
	 * Creates component with default settings. In this method is called
	 * initialization.
	 */
	public DLCombobox() {
		super();
		init();
	}

	/**
	 * Initializes component with registering listeners and model
	 */
	protected void init() {
		// onChange event - use when null is setted - onSelect event isn't generated
		// so it will be posted here
		onChange = new EventListener() {

			public void onEvent( final Event event ) {
				if ( getSelectedItem() == null || ( getSelectedItem() != null && controller != null && controller.getSelectedItem() == null ) )
					Events.postEvent( new SelectEvent( Events.ON_SELECT, DLCombobox.this, new HashSet() ) );
			}
		};
		addEventListener( Events.ON_CHANGE, onChange );
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

		if ( !isReadonly() )
			super.setConstraint( _constraint );
	}

	public String getParentCascadeId() {
		return parentCascadeId;
	}

	public void setParentCascadeId( String parentComboId ) {
		this.parentCascadeId = parentComboId;
	}

	public String getParentCascadeColumn() {
		return parentCascadeColumn;
	}

	public void setParentCascadeColumn( String parentComboColumn ) {
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
		if ( controller == null )
			throw new IllegalStateException( "Combobox controller is mising." );
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
		if ( this.controller instanceof DLComboboxDefaultController )
			controller.doAfterCompose( this );
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
			removeEventListener( Events.ON_CHANGE, onChange );
			cloned = true;
		}

		final DLCombobox<T> clone = (DLCombobox<T>) super.clone();
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
		if ( child instanceof Comboitem )
			( (Comboitem) child ).setDisabled( isReadonly() );
	}

	@Override
	public void setReadonly( final boolean readonly ) {
		super.setReadonly( readonly );

		// as the children drop list is shown on clicking on the field area
		// even if the button is not visible, we set the children status
		// (disabled if readonly) so that the user can't click on them
		for (Object element : getChildren()) {
			final Component c = (Component) element;
			if ( c instanceof Comboitem )
				( (Comboitem) c ).setDisabled( isReadonly() );
		}

		// contraint is changed according to readonly or not
		if ( readonly )
			super.setConstraint( (Constraint) null );
		else
			super.setConstraint( _constraint );
	}

	/**
	 * Returns binding label field - <b>finds binding expression from comboitem template</b>
	 * @return binding label field
	 */
	public String getLabel() {
		final DataBinder binder = ZKBinderHelper.getBinder( this );
		if ( binder == null )
			return null;

		// FIXME KC predelat jako listbox
		final Comboitem template = null; //(Comboitem) binder.getItemTemplate( this );
		if ( template == null )
			return null;

		final Binding bind = binder.getBinding( template, "label" );
		if ( bind == null )
			return null;


		String label = bind.getExpression();
		if ( label != null && label.indexOf( '.' ) > -1 )
			label = label.substring( label.indexOf( '.' ) + 1 );

		return label;
	}

	public CascadableExt getCascadableController() {
		return controller;
	}
}
