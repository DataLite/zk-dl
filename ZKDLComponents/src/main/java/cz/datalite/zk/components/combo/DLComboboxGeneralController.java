package cz.datalite.zk.components.combo;

import cz.datalite.helpers.ZKBinderHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.zkoss.util.ModificationException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zk.ui.util.Composer;

import cz.datalite.zk.components.cascade.Cascadable;
import cz.datalite.zk.components.cascade.CascadableExt;
import cz.datalite.zk.components.cascade.CascadeUtil;
import java.util.Collections;

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
    /** defines if the first row should be automatically selected */
    protected boolean selectFirstOnCascade;

    public DLComboboxGeneralController() {
        cascadeUtil = new CascadeUtil<T>( this );
    }

    @SuppressWarnings( "unchecked" )
    public void doAfterCompose( final Component comp ) {
        combobox = ( DLCombobox<T> ) comp;
        combobox.registerController( this );
        combobox.addEventListener( Events.ON_SELECT, new SelectListener() );
        cascadeUtil.addDefaultParent( combobox );
        selectFirstOnCascade = combobox.isSelectFirstOnCascade();
    }

    public boolean isInModel( final T entity ) {
        return model.getModel().contains( entity );
    }

    public void add( final T entity ) {
        if ( isInModel( entity ) || entity == null ) {
            return;
        }
        try {
            model.getModel().add( entity );
        } catch ( UnsupportedOperationException e ) {
            if ( entity == null ) {
                // null in unmodifieable list, nothing to do
                return;
            } else {
                throw new UnsupportedOperationException( "Combobox model doesn't contain entity '" + entity.toString()
                        + "' and doesn't support add() method to automatically add entity to the model." );
            }
        }
        combobox.fireModelChanges();
    }

    public void onOpen() {
        if ( !loaded ) {
            refreshModel();
        }
    }

    abstract protected List<T> loadData( final String orderBy, final Map<String, Object> filters );

    /**
     * Loads data from the database and refreshes model
     */
    protected void refreshModel() {
        model.setModel( ( List<T> ) (model.getFilters().values().contains( null ) ? Collections.emptyList() : loadData( combobox.getLabel(), model.getFilters() )) );
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
        if ( index == model.getSelectedIndex() ) {
            return SELECT_NO_CHANGE;
        }
        if ( index == -1 ) {
            model.setSelectedIndex( -1 );
        } else {
            model.setSelectedIndex( index );
        }
        return SELECT_SELECTED;
    }

    public List<T> getModel() {
        return new ArrayList<T>( model.getModel() );
    }

    public void fireParentChanges( final Cascadable parent ) {
        model.setFilter( cascadeUtil.getParentColumn( parent ), parent.getSelectedItem() );
        refreshModel();
        System.out.println( "new model size is " + model.getModel().size() );

        final Object entity = !model.getModel().isEmpty() && selectFirstOnCascade ? model.getModel().get( 0 ) : DLSelectedComboitemConverter.DUMMY;

//      if the component has binding we set the bean value directly into binding
//      field. Load binding on select event handles that this value will be properly
//      loaded without any other complications
        if ( ZKBinderHelper.getBinder( combobox ).getBinding( combobox, "selectedItem" ) != null ) {
            combobox.setAttribute( DLSelectedComboitemConverter.ON_CASCADE_REFRESH, entity );
            ZKBinderHelper.saveComponent( combobox );
        }

        combobox.addEventListener( "onAfterRender", new EventListener() {

            public void onEvent( final Event event ) {
                combobox.removeEventListener( "onAfterRender", this );

                // if there is no binding on select item we have to set the
                // item directly to the component. it is possible only when
                // the items are rendered.
                if ( ZKBinderHelper.getBinder( combobox ).getBinding( combobox, "selectedItem" ) == null ) {
                    combobox.setSelectedIndex( entity == DLSelectedComboitemConverter.DUMMY ? -1 : 0 );
                }
                Events.postEvent( new SelectEvent( Events.ON_SELECT, combobox, Collections.singleton( combobox.getSelectedItem() ) ) );
            }
        } );
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
     *
     * If some select event is caught then
     * the setSelect is called to keep
     * model actual. If the select is
     * redundant then is ignored
     *
     */
    protected class SelectListener implements EventListener {

        public void onEvent( final Event event ) throws NoSuchMethodException, ModificationException {
            final SelectEvent selectEvent = ( SelectEvent ) event;
            int index;
            if ( selectEvent.getSelectedItems().isEmpty() ) {
                index = -1;
            } else {
                index = combobox.getItems().indexOf( selectEvent.getSelectedItems().iterator().next() );
            }
            if ( setSelect( index ) == SELECT_SELECTED ) {
                cascadeUtil.dofireParentChanges();
            }
        }
    }
}
