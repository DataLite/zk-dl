package cz.datalite.zk.components.combo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zkoss.util.ModificationException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zkplus.databind.Binding;
import org.zkoss.zul.Comboitem;

import cz.datalite.helpers.ZKBinderHelper;
import cz.datalite.zk.components.cascade.Cascadable;
import cz.datalite.zk.components.cascade.CascadableExt;
import cz.datalite.zk.components.cascade.CascadeUtil;

/**
 * Controller for the extended component combobox which allows data loading on onOpen event.
 * There is automatically implemented cascade of comboboxes. In this case parent must be registered
 * on the follower and that is all.
 * @param <T> main entity in the combobox
 * @author Karel Čemus <cemus@datalite.cz>
 */
public abstract class DLComboboxGeneralController<T> implements DLComboboxExtController<T>, Composer {

	// cascade
	protected final CascadeUtil<T> cascadeUtil;
	// model
	protected DLComboboxModel<T> model = new DLComboboxModel<T>();
	// view
	protected DLCombobox<T> combobox;
	/** combobox is created without model - unloaded. When model is setted combobox became loaded */
	protected boolean loaded = false;
	// constants
	/** return value when new item is selected */
	protected static final int SELECT_SELECTED = 1;
	/** return value when new item equals old item */
	protected static final int SELECT_NO_CHANGE = 0;
	/** event when combobox have to be refreshed */
	protected static final String ON_COMBOBOX_REFRESH = "onComboboxRefresh";

	public DLComboboxGeneralController() {
		cascadeUtil = new CascadeUtil<T>( this);
	}

	@SuppressWarnings( "unchecked" )
	public void doAfterCompose( final Component comp ) {
		combobox = (DLCombobox<T>) comp;
		combobox.registerController( this );
		combobox.addEventListener( Events.ON_SELECT, new SelectListener() );
		combobox.addEventListener( ON_COMBOBOX_REFRESH, new SelectListener() );
		cascadeUtil.addDefaultParent( combobox );
	}

	public boolean isInModel( final T entity ) {
		return model.getModel().contains( entity );
	}

	public void add( final T entity ) {
		if ( isInModel( entity ) ) return;
		try
		{
			model.getModel().add( entity );
		}
		catch (UnsupportedOperationException e)
		{
			if (entity != null)
			{
				throw new UnsupportedOperationException("Combobox model doesn't contain entity '" + entity.toString() +
				"' and doesn't support add() method to automatically add entity to the model.");
			}
			else
			{
				// null in unmodifieable list, nothing to do
				return;
			}
		}
		combobox.fireModelChanges();
	}

	public void onOpen() {
		if ( !loaded )
			refreshModel();
	}

	abstract protected List<T> loadData( final String orderBy, final Map<String, Object> filters );

	/**
	 * Loads data from the database and refreshes model
	 */
	protected void refreshModel() {
		model.setModel( model.getFilters().values().contains( null ) ? new ArrayList<T>() : loadData( combobox.getLabel(), model.getFilters() ) );
		model.setSelectedIndex( DLComboboxModel.UNKNOWN );
		combobox.fireModelChanges();
		loaded = true;
	}

	/**
	 * Sets selected index
	 * @param index new selected index
	 * @return action result - SELECT_NO_CHANGE || SELECT_SELECTED
	 */
	protected int setSelect( final int index ) {
		if ( index == model.getSelectedIndex() )
			return SELECT_NO_CHANGE;
		if ( index == -1 )
			model.setSelectedIndex( -1 );
		else
			model.setSelectedIndex( index );
		return SELECT_SELECTED;
	}

	public List<T> getModel() {
		return new ArrayList<T>( model.getModel() );
	}

	public void fireParentChanges( final Cascadable parent ) {
		model.setFilter( cascadeUtil.getParentColumn( parent ), parent.getSelectedItem() );

		refreshModel();

		// nacteni bindingu
		final Binding bind = ZKBinderHelper.getBinder( combobox ).getBinding( combobox, "selectedItem" );

		// nastavení bindingu
		if ( bind == null )
			combobox.addEventListener( "onInitRenderLater", new EventListener() {

				@SuppressWarnings( "unchecked" )
				public void onEvent( final Event event ) {
					combobox.removeEventListener( "onInitRenderLater", this );

					final Comboitem item = (Comboitem) ( combobox.getItems().size() == 0 ? null : combobox.getItems().get( 0 ) );
					combobox.setSelectedItem( item );
					if ( item == null ) {
						Events.postEvent( new SelectEvent( ON_COMBOBOX_REFRESH, combobox, new HashSet() ) );
						return;
					}
					final Set items = new HashSet();
					if ( combobox != null ) items.add( item );
					Events.postEvent( new SelectEvent( Events.ON_SELECT, combobox, items, item ) );
				}
			} );
		else {
			// FIXME KC Predelat jako listbox
			//bind.saveAttributeValue( combobox, model.getModel().size() == 0 ? null : model.getModel().get( 0 ) );

			if ( model.getModel().size() == 0 ) // pokud je null musíme si o to říct sami
				Events.postEvent( new SelectEvent( ON_COMBOBOX_REFRESH, combobox, new HashSet() ) );
		}
	}

	public int getSelectedIndex() {
		return model.getSelectedIndex();
	}

	public T getSelectedItem() {
		return model.getSelectedItem();
	}

	public void addParent( final Cascadable parent, final String column ) {
		cascadeUtil.addParent( parent, column );
	}

	public void addFollower( final CascadableExt follower ) {
		cascadeUtil.addFollower( follower );
	}

	/**
	 * Listener which reacts on onSelect event
	 */
	protected class SelectListener implements EventListener {

		public void onEvent( final Event event ) throws NoSuchMethodException, ModificationException {
			final SelectEvent selectEvent = (SelectEvent) event;
			if ( selectEvent.getSelectedItems().size() == 0 ) {
				if ( setSelect( -1 ) == SELECT_SELECTED )
					cascadeUtil.dofireParentChanges();
			}
			else if ( setSelect( combobox.getItems().indexOf( selectEvent.getSelectedItems().iterator().next() ) ) == SELECT_SELECTED )
				cascadeUtil.dofireParentChanges();
		}
	}
}
