package cz.datalite.zk.components.list;

import cz.datalite.dao.DLResponse;
import cz.datalite.dao.DLSort;
import cz.datalite.dao.DLSortType;
import cz.datalite.helpers.JsonHelper;
import cz.datalite.helpers.ReflectionHelper;
import cz.datalite.utils.HashMapAutoCreate;
import cz.datalite.zk.components.list.controller.*;
import cz.datalite.zk.components.list.controller.impl.*;
import cz.datalite.zk.components.list.enums.DLFilterOperator;
import cz.datalite.zk.components.list.enums.DLFiterType;
import cz.datalite.zk.components.list.filter.NormalFilterModel;
import cz.datalite.zk.components.list.filter.NormalFilterUnitModel;
import cz.datalite.zk.components.list.filter.QuickFilterModel;
import cz.datalite.zk.components.list.model.DLColumnModel;
import cz.datalite.zk.components.list.model.DLColumnUnitModel;
import cz.datalite.zk.components.list.model.DLFilterModel;
import cz.datalite.zk.components.list.model.DLMasterModel;
import cz.datalite.zk.components.list.service.ProfileService;
import cz.datalite.zk.components.list.service.impl.ProfileServiceSessionImpl;
import cz.datalite.zk.components.list.view.DLListControl;
import cz.datalite.zk.components.list.view.DLListbox;
import cz.datalite.zk.components.list.view.DLListboxManager;
import cz.datalite.zk.components.list.view.DLQuickFilter;
import cz.datalite.zk.components.paging.DLPaging;
import cz.datalite.zk.components.paging.DLPagingController;
import cz.datalite.zk.components.paging.DLPagingModel;
import cz.datalite.zk.components.profile.DLProfileManager;

import org.slf4j.LoggerFactory;
import java.io.IOException;

import org.zkoss.json.JSONObject;
import org.zkoss.json.JSONValue;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zul.Paging;

import java.util.*;

/**
 * Main controller for the extended listbox component. This controller is the
 * master for other controllers like listboxComponent, paging, quickFilter, etc.
 * This supervisor reacts on the events which are propagated from the parts.
 * This implementation is independent on the used technology like hibernate,
 * toplink or simply list. To specify implementation you must use on of special
 * implementation.
 * @param <T> main entity
 * @author Karel Cemus
 */
public abstract class DLListboxGeneralController<T> implements DLListboxExtController<T> {

    /** logger */
    protected final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger( DLListboxGeneralController.class );
    /** Complete listbox, filter and paging model. This is storable to the session. */
    protected DLMasterModel model = new DLMasterModel();
    /** Controller for the listbox component */
    protected DLListboxComponentController<T> listboxController;
    /** Controller for the listbox manager  */
    protected DLManagerController managerController = new DLDefaultManagerControllerImpl();
    /** Controller for the paging component */
    protected DLPagingController pagingController = new DLDefaultPagingControllerImpl();
    /** Controller for the easy filter components */
    protected DLEasyFilterController easyFilterController = new DLDefaultEasyFilterControllerImpl();
    /** Controller for the quick filter component */
    protected DLQuickFilterController quickFilterController = new DLDefaultQuickFilterControllerImpl();
    /** Controller for the profile component */
    protected DLProfileManagerController<T> profileManagerController;
    /** main entity class */
    protected final Class<T> entityClass;
    /** automatically save model */
    protected boolean autosave = true;
    /** component identifier */
    protected final String identifier;
    /** list of the listeners */
    protected Map<String, EventListeners> listeners = new HashMapAutoCreate<String, EventListeners>( EventListeners.class );
    /** model lock - if model is locked it cannot be changed */
    protected boolean lock = false;
    
    /** profile service used to load/store profiles from/to session */
    private ProfileService profileServiceSessionImpl;
    
    /**
     * Create instance of the general controller
     * @param identifier identifier which is used to saving model to the session. It must be uniqueue in scope of same composer and entity.
     * @param clazz enity class
     */
    @SuppressWarnings( "unchecked" )
    public DLListboxGeneralController( final String identifier, final Class<T> clazz ) {
        if ( clazz == null ) {
            entityClass = ( Class<T> ) ReflectionHelper.getTypeArguments( DLListboxGeneralController.class, getClass() ).get( 0 );
        } else {
            this.entityClass = clazz;
        }
        this.identifier = identifier;
      
        this.profileServiceSessionImpl = new ProfileServiceSessionImpl();
        
        easyFilterController = new DLEasyFilterControllerImpl( this, model.getFilterModel().getEasy() );
    }

    /**
     * Create instance of the general controller.
     * Model class is infered from generics via reflection.
     * @param identifier identifier which is used to saving model to the session. It must be uniqueue in scope of same composer and entity.
     */
    public DLListboxGeneralController( final String identifier ) {
        this( identifier, null );
    }

    /**
     * Create instance of the general controller.
     * Identifier is infered from composer / request path and listbox component id.
     * Model class is infered from generics via reflection.
     */
    public DLListboxGeneralController() {
        this( null );
    }

    @SuppressWarnings("unchecked")
	@Override
    public void doAfterCompose( final Component comp ) {
        if ( comp instanceof DLListbox ) {
            initListbox( ( DLListbox ) comp );
        } else if ( comp instanceof DLPaging ) {
            initPaging( ( DLPaging ) comp );
        } else if ( comp instanceof DLListboxManager ) {
            initManager( ( DLListboxManager ) comp );
        } else if ( comp instanceof DLQuickFilter ) {
            initQuickFilter( ( DLQuickFilter ) comp );
        } else if ( comp instanceof DLProfileManager ) {
			initProfileManager((DLProfileManager<T>) comp);
        } else if ( comp instanceof DLListControl ) { // řídící komponenta
            final DLListControl listControl = ( DLListControl ) comp;
            listControl.init();
            if ( listControl.isQfilter() ) {
                initQuickFilter( listControl.getQFilterComponent() );
            }
            if ( listControl.isManager() ) {
                initManager( listControl.getManagerComponent() );
            }
            model.getFilterModel().setWysiwyg( listControl.isWysiwyg() );
        } else if ( comp instanceof Paging ) {
            throw new IllegalArgumentException( "If you want to use paging, you have to use DLPaging component. In ZUL file it is called \"dlpaging\"." );
        } else if (comp != null ) {
                throw new IllegalArgumentException( String.format( "DLListboxGeneralController is not applicable on '%s'.", comp.getClass() ) );
        }
    }

    /**
     * Initialize paging component
     * @param comp paging component
     */
    protected void initPaging( final DLPaging comp ) {
        pagingController = new DLPagingControllerImpl( this, model.getPagingModel(), comp );
    }

    /**
     * Initialize listbox component
     * @param comp listbox component
     */
    protected void initListbox( final DLListbox comp ) {
        listboxController = new DLListboxComponentControllerImpl<T>( this, model.getColumnModel(), comp );
        this.autosave = comp.isAutosave();
    }

    /**
     * Initialize manager component
     * @param comp manager component
     */
    protected void initManager( final DLListboxManager comp ) {
        managerController = new DLManagerControllerImpl<T>( this, comp );
    }

    /**
     * Initialize quick filter compnent
     * @param comp quick filter component
     */
    protected void initQuickFilter( final DLQuickFilter comp ) {
        quickFilterController = new DLQuickFilterControllerImpl( this, model.getFilterModel().getQuick(), comp );
    }
    
    /**
     * Initialize profile manager component
     * @param comp profile manager component
     */
	protected void initProfileManager(final DLProfileManager<T> comp) {
		this.profileManagerController = new DLProfileManagerControllerImpl<T>(this, comp, this.getProfileService());
    }
	
	/**
     * Method can be overridden to inject profile service into this controller
     */
	protected ProfileService getProfileService() {
		return null;
    }

	public void onCreate() {
		if (!lock) {
			// try to load model in session if any
			boolean modelInSession = this.loadModel();

			if (!modelInSession) {
				if (this.profileManagerController != null) {
					if (this.profileManagerController.selectDefaultProfile(true)) {
						this.profileManagerController.onLoadProfile();
					}
				}
			} 
		}
    	
        getListboxController().fireOrderChanges();
        quickFilterController.fireChanges();
        managerController.fireChanges();

        if ( listboxController.isLoadDataOnCreate() ) {
            refreshDataModel();
        }
    }

    public void onPagingModelChange() {
        if ( lock ) {
            return;
        }
        refreshDataModel();
        pagingController.fireChanges();
        autosaveModel();
    }

    public void onFilterChange( final String filterType ) {
        if ( lock ) {
            return;
        }
        model.getPagingModel().clear();
        managerController.fireChanges();
        getListboxController().fireChanges();
        refreshDataModel();
        autosaveModel();

        final boolean easy = model.getFilterModel().getEasy().isEmpty();
        final boolean quick = model.getFilterModel().getQuick().isEmpty();
        final boolean normal = model.getFilterModel().getNormal().isEmpty();

        // There are defined active filters - true means that filter is active
        final Map<DLFiterType, Boolean> filters = new EnumMap<DLFiterType, Boolean>( DLFiterType.class );
        filters.put( DLFiterType.EASY, !easy );
        filters.put( DLFiterType.QUICK, !quick );
        filters.put( DLFiterType.NORMAL, !normal );
        filters.put( DLFiterType.ALL, quick || easy || normal );

        callListeners( new Event( filterType, getListbox(), filters ) );
        callListeners( new Event( DLListboxEvents.ON_FILTER_CHANGE, getListbox(), filters ) );
    }

    public void onSortChange() {
        if ( lock ) {
            return;
        }
        refreshDataModel();
        getListboxController().fireChanges();
        fireProfileManagerChanges();
        autosaveModel();
    }

    public void confirmEasyFilter() {
        if ( lock ) {
            return;
        }
        getEasyFilterController().onEasyFilter();
    }

    public void clearEasyFilter() {
        clearEasyFilter( true );
    }

    public void clearEasyFilter( final boolean refresh ) {
        if ( lock ) {
            return;
        }
        getEasyFilterController().onClearEasyFilter( refresh );
    }

    public DLColumnModel getColumnModel() {
        return model.getColumnModel();
    }

    public NormalFilterModel getNormalFilterModel() {
        return model.getFilterModel().getNormal();
    }

    public void onSortManagerOk( final List<Map<String, Object>> data ) {
        if ( lock ) {
            return;
        }
        // reset sorting settings
        for ( DLColumnUnitModel unit : model.getColumnModel().getColumnModels() ) {
            unit.setSortType( DLSortType.NATURAL );
        }

        // updating the models
        for ( Map<String, Object> map : data ) {
            // if natural is setted nothing has to be done
            if ( DLSortType.NATURAL.equals( map.get( "sortType" ) ) ) {
                continue;
            }
            // search coresponing model and update
            @SuppressWarnings( "unchecked" )
            final java.util.Map.Entry<Integer, String> column = (( java.util.Map.Entry<Integer, String> ) map.get( "column" ));
            model.getColumnModel().getColumnModels().get( column.getKey() ).setSortType( ( DLSortType ) map.get( "sortType" ) );
        }

        onSortChange();
    }

    public void onColumnManagerOk( final List<Map<String, Object>> data ) {
        if ( lock ) {
            return;
        }
        // reset visibility settings
        for ( DLColumnUnitModel unit : model.getColumnModel().getColumnModels() ) {
            unit.setVisible( false );
        }

        // set visible value on true where user wanted
        for ( Map<String, Object> map : data ) {
            DLColumnUnitModel column = model.getColumnModel().getColumnModels().get( ( Integer ) map.get( "index" ) );
            column.setVisible( true );
            column.setOrderDirectly( ( Integer ) map.get("order")); // Set the order directly
        }

        getListboxController().fireOrderChanges(); 
        // update list of available columns to filter by
        getQuickFilterController().fireChanges();
        
        autosaveModel();

        fireProfileManagerChanges();
        refreshDataModel(); // JB if data for hidden columns are not available, need to reload data (not only set model)
    }
    
    public void onFilterManagerOk( final NormalFilterModel data ) {
        if ( lock ) {
            return;
        }
        model.getFilterModel().getNormal().clear();
        model.getFilterModel().getNormal().addAll( data );

        onFilterChange( DLListboxEvents.ON_NORMAL_FILTER_CHANGE );
    }

    public void onExportManagerOk( final org.zkoss.util.media.AMedia data ) {
        org.zkoss.zul.Filedownload.save( data );
    }

    protected DLColumnUnitModel getColumnUnitModel( final Integer order ) {
        for ( DLColumnUnitModel unit : model.getColumnModel().getColumnModels() ) {
            if ( order.equals( unit.getOrder() ) ) {
                return unit;
            }
        }
        return null;
    }

    protected DLColumnUnitModel getColumnUnitModel( final String column ) {
        for ( DLColumnUnitModel unit : model.getColumnModel().getColumnModels() ) {
            if ( column.equals( unit.getColumn() ) ) {
                return unit;
            }
        }
        return null;
    }

    public void refreshDataModel() {
        refreshDataModel( false );
    }

    public void refreshDataModel( final boolean clearPaging ) {
        if ( lock ) {
            return;
        }

        if ( clearPaging ) {
            model.getPagingModel().clear();
        }

        final cz.datalite.dao.DLResponse<T> response =
                loadData( model.getFilterModel().toNormal( model.getColumnModel() ),
                model.getPagingModel().getPageSize() * model.getPagingModel().getActualPage(),
                model.getPagingModel().getPageSize() == 0 ? 0 : model.getPagingModel().getPageSize() + 1,
                model.getColumnModel().getSorts() );

        if ( model.getPagingModel().getPageSize() == DLPagingModel.NOT_PAGING ) {
            getListboxController().setListboxModel( response.getData() );
        } else {
            getListboxController().setListboxModel(
                    response.getData().size() > 1 ? response.getData().subList(
                    0,
                    Math.max( 0, Math.min( model.getPagingModel().getPageSize(), response.getData().size() ) ) ) : response.getData() );
        }

        model.getPagingModel().setTotalSize( response.getRows(), response.getData().size() );

        getListboxController().fireDataChanges();
        pagingController.fireChanges();

        callListeners( new Event( DLListboxEvents.ON_MODEL_CHANGE, getListbox() ) );
    }

    public void clearDataModel() {
        model.getPagingModel().clear();
        getListboxController().setListboxModel(Collections.<T>emptyList());
        getListboxController().fireDataChanges();
        setSelectedItem(null);
    }

    public DLResponse<T> loadData( final int rowLimit ) {
        return loadData( model.getFilterModelInNormal(), 0, rowLimit, model.getColumnModel().getSorts() );
    }

    /**
     * Returns data list coresponding to search criterias.
     * @param filter filter criterias
     * @param firstRow index of the first rows begins at 0
     * @param rowCount row count in the result
     * @param sorts sort settings
     * @return coresponing data list
     */
    protected abstract cz.datalite.dao.DLResponse<T> loadData( final List<NormalFilterUnitModel> filter, final int firstRow, final int rowCount, final List<cz.datalite.dao.DLSort> sorts );

    /**
     * Returns distinct values in this column. Rows have to corespond to
     * filter criterias.
     * @deprecated since 2.9.2010 - for distinct components there are {@link cz.datalite.zk.components.list.filter.components.DistinctFilterComponent}
     * @param column column name with distinct
     * @param filter criterias
     * @return coresponding model
     */
    protected abstract DLResponse<String> loadDistinctColumnValues( final String column, final List<NormalFilterUnitModel> filter, final int firstRow, final int rowCount, final List<cz.datalite.dao.DLSort> sorts );

    public DLResponse<String> loadDistinctValues( final String column, final String qFilterValue, final int firstRow, final int rowCount ) {
//        final DLFilterModel filterModel = new DLFilterModel() {
//
//            {
//                _easy.putAll( model.getFilterModel().getEasy() );
//                _quick.setKey( model.getFilterModel().getQuick().getKey() );
//                _quick.setValue( model.getFilterModel().getQuick().getValue() );
//                _default.addAll( model.getFilterModel().getDefault() );
//                _normal.addAll( normalFilter );
//            }
//        };

        List<NormalFilterUnitModel> filterModel;
        if ( qFilterValue == null ) {
            filterModel = Collections.emptyList();
        } else {
            final NormalFilterUnitModel filterUnit = new NormalFilterUnitModel( model.getColumnModel().getByName( column ) );
            filterUnit.setValue( 1, qFilterValue );
            filterUnit.setOperator( filterUnit.getQuickFilterOperator() );
            filterModel = Collections.singletonList( filterUnit );
        }

        final DLSort sort = new DLSort( column, DLSortType.ASCENDING );

        return loadDistinctColumnValues( column, filterModel, firstRow, rowCount, Collections.singletonList( sort ) );
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public void registerEasyFilterOnFilter( final String compId ) {
        registerEasyFilterOnFilter( getListboxController().getFellow( compId ) );
    }

    public void registerEasyFilterOnFilter( final Component comp ) {
        registerEasyFilterOnFilter( comp, "onClick" );
    }

    public void registerEasyFilterOnFilter( final String compId, final String event ) {
        registerEasyFilterOnFilter( getListboxController().getFellow( compId ), event );
    }

    public void registerEasyFilterOnFilter( final Component comp, final String event ) {
        getListboxController().addForward( event, comp, "onEasyFilter" );
    }

    public void registerEasyFilterVariable( final String compId, final String variableName ) {
        registerEasyFilterVariable( getListboxController().getFellow( compId ), variableName );
    }

    public void registerEasyFilterVariable( final Component comp, final String variableName ) {
        comp.setAttribute( variableName, easyFilterController.getBindingModel(), Component.SPACE_SCOPE );
    }

    public void registerEasyFilterOnClear( final String compId ) {
        registerEasyFilterOnClear( getListboxController().getFellow( compId ) );
    }

    public void registerEasyFilterOnClear( final Component comp ) {
        registerEasyFilterOnClear( comp, "onClick" );
    }

    public void registerEasyFilterOnClear( final String compId, final String event ) {
        registerEasyFilterOnClear( getListboxController().getFellow( compId ), event );
    }

    public void registerEasyFilterOnClear( final Component comp, final String event ) {
        getListboxController().addForward( event, comp, "onEasyFilterClear" );
    }

    public boolean loadModel() {
    	@SuppressWarnings("unchecked")
		List<DLListboxProfile> profiles = (List<DLListboxProfile>) this.profileServiceSessionImpl.findAll(this.getSessionName());
    	
		if (profiles.isEmpty()) {
			return false;
		} else {
			LOGGER.info("load model stored in session, session = {}", this.getSessionName());
			this.applyProfile(profiles.get(0), false);
			return true;
		}
	}

    /**
     * If autoset model then save model.
     */
    protected void autosaveModel() {
        if ( autosave ) {
            saveModel();
        }
    }

    public void saveModel() {
        // Executions.getCurrent().getDesktop().getSession().setAttribute( getSessionName() + "cache", model );
    	if (!lock) {
    		LOGGER.info("save model to session, session = {}", this.getSessionName());
    		this.profileServiceSessionImpl.save(this.createProfile());
    	}
    }

    public void setAutoSaveModel( final boolean autosave ) {
        this.autosave = autosave;
    }

    /**
     * Returns session name for save/load model.
     * <p>Session name is composed as "composer#entity$id"<ul>
     * <li>Composer name via attribute $composer. If no composer is found, request path is used.</li>
     * <li>Resolved entity class (set via constructor or from generic)</li>
     * <li>ID - identifier set via constructor or listbox id. If neither is set, this part is ommited</li>
     * </ul> </p>
     *
     * @return session name
     */
    @Override
    public String getSessionName() {
        Object composer = getListbox().getAttribute("$composer", Component.SPACE_SCOPE);
        String composerClass = composer != null ? composer.getClass().getName() : null;
        String requestPath = org.zkoss.zk.ui.Executions.getCurrent().getDesktop().getRequestPath();
        String entityClass = getEntityClass().getName();
        String id = identifier != null ? identifier : getListbox().getId();

        return (composerClass != null ? composerClass : requestPath) + "#" + entityClass + (id == null ? "" : "$" + id);
    }

    public void refreshBinding() {
        getListboxController().refreshBindingAll();
    }

    public void clearAllModel() {
        model.clear();
        
        getListboxController().fireColumnModelChanges();
        getListboxController().fireOrderChanges();        
        
        autosaveModel();
        
        managerController.fireChanges();        
        quickFilterController.fireChanges();
        easyFilterController.fireChanges();        
        fireProfileManagerChanges();
        
        refreshDataModel();
    }

    public void clearFilterModel() {
        model.getFilterModel().clear();
        
        quickFilterController.fireChanges();
        easyFilterController.fireChanges();
        managerController.fireChanges();
        fireProfileManagerChanges();
        
        refreshDataModel();
    }

    public void addDefaultFilter( final String property, final DLFilterOperator operator, final Object value1, final Object value2 ) {
        final NormalFilterUnitModel unit = new NormalFilterUnitModel( property );
        unit.setOperator( operator );
        unit.setValue( 1, value1 );
        unit.setValue( 2, value2 );
        model.getFilterModel().getDefault().add( unit );
    }

    public T getSelectedItem() {
        return getListboxController().getSelectedItem();
    }

    public void setSelected( final T selecteItem ) {
        getListboxController().setSelected( selecteItem );
    }

    public void setSelectedItem( final T selecteItem ) {
        getListboxController().setSelectedItem( selecteItem );
    }

    public void setSelectedIndex( final int index ) {
        getListboxController().setSelectedIndex( index );
    }

    public Set<T> getSelectedItems() {
        return getListboxController().getSelectedItems();
    }

    public void setSelectedItems( final Set<T> selecteItems ) {
        getListboxController().setSelectedItems( selecteItems );
    }

    public void updateItem(T item) {
        boolean found = getListboxController().updateItem(item);

        // update paging (+1 row)
        if (!found) {
            model.getPagingModel().setTotalSize(model.getPagingModel().getTotalSize()+1,
                    getListboxController().getListbox().getModel().getSize());
            pagingController.fireChanges();
        }
    }

    public DLEasyFilterController getEasyFilterController() {
        return easyFilterController;
    }

    public DLQuickFilterController getQuickFilterController() {
        return quickFilterController;
    }

    public DLMasterModel getModel() {
        return model;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	public Composer getWindowCtl() {
        return getListboxController().getWindowCtl();
    }

    public void onSelect() {
        if ( lock ) {
            return;
        }
        callListeners( new SelectEvent( DLListboxEvents.ON_DLSELECT, getListbox(), Collections.singleton( getListboxController().getSelectedItem() ) ) );
    }

    public void addListener( final String event, final EventListener listener ) {
        listeners.get( event ).add( listener );
    }

    public boolean removeListener( final String event, final EventListener listener ) {
        return listeners.get( event ).remove( listener );
    }

    public boolean isLocked() {
        return lock;
    }

    public void lockModel() {
        this.lock = true;
    }

    public void unlockModel() {
        this.lock = false;
    }

    public void focus() {
        getListboxController().focus();
    }

    protected DLListboxComponentController<T> getListboxController() {
        if ( listboxController == null ) {
            throw new IllegalStateException( "Listbox controller is mising! You are using component without controller (old version) for extended functions." );
        }
        return listboxController;
    }

    public DLListbox getListbox() {
        return listboxController.getListbox();
    }
    
    public void fireProfileManagerChanges() {
    	if (this.profileManagerController != null) {
        	this.profileManagerController.fireChanges();
        }
    }

    public void setQuickFilter(String column, String value) {
        // setup binding model
        QuickFilterModel model = getQuickFilterController().getModel();
        model.setKey(column);
        model.setValue(value);
        model.setModel(getColumnUnitModel(column));
    }

    protected void callListeners( final Event event ) {
        Events.postEvent( event );
        for ( EventListener listener : listeners.get( event.getName() ) ) {
            try {
                listener.onEvent( event );
            } catch ( Exception ex ) {
                LOGGER.error( "Event couldn't be send. Error has occured in DLListboxGeneralController.", ex );
            }
        }
    }

    public String getListboxUuid() {
        return listboxController.getListbox().getUuid();
    }

    public String getPagingUuid() {
        return pagingController.getUuid();
    }

    public String getQuickFilterUuid() {
        return quickFilterController.getUuid();
    }

    public void onDirectExport() {
        managerController.exportCurrentView();
    }
    
    public AMedia exportDirect() throws IOException {
        return managerController.directExportCurrentView();
    }

    public static class EventListeners extends LinkedList<EventListener> {
    }
    
    /**
     * This method applies {@link DLListboxProfile} to agenda and refresh data.
     */
    public void applyProfile(DLListboxProfile profile) {
        applyProfile(profile, true);
    }

    /**
     * This method applies {@link DLListboxProfile} to agenda.
     */
    protected void applyProfile(DLListboxProfile profile, boolean refreshData) {
    	LOGGER.info("applyProfile = " + profile);
    	
    	// reset to default
    	this.model.clear();
        this.getListboxController().fireColumnModelChanges();
    	
    	// load new model profile
		String columnModelJsonData = profile.getColumnModelJsonData();
		JSONObject columnModelJsonObject = null;
		if (columnModelJsonData != null && columnModelJsonData.length() > 0) {
			//LOGGER.info("Column model JSON: " + columnModelJsonData);
			try {
				columnModelJsonObject = (JSONObject) JSONValue.parse(columnModelJsonData);
			} catch (Exception ex) {
				LOGGER.error("Unable to parse column model JSONObject: " + columnModelJsonData, ex);
			}
		}
		
		String filterModelJsonData = profile.getFilterModelJsonData();
		JSONObject filterModelJsonObject = null;
		if (filterModelJsonData != null && filterModelJsonData.length() > 0) {
			//LOGGER.info("Filter model JSON: " + filterModelJsonData);	
			try {
				filterModelJsonObject = (JSONObject) JSONValue.parse(filterModelJsonData);
			} catch (Exception ex) {
				LOGGER.error("Unable to parse filter model JSONObject: " + filterModelJsonData, ex);
			}			
		}		
		
		DLMasterModel masterModel = this.getModel();
		DLColumnModel columnModel = masterModel.getColumnModel();
		
		NormalFilterModel savedModel = new NormalFilterModel();
		
		int i = 0;
		List<String> columns = new ArrayList<String>();

		for (DLColumnUnitModel unit : columnModel.getColumnModels()) {
			String column = unit.getColumn();
			if (column == null) {				
				column = "column" + i;
			}
			
			if (columnModelJsonObject != null && columnModelJsonObject.containsKey(column)) {				
				String visible = (((JSONObject) columnModelJsonObject.get(column)).get("visible")).toString();
				String order = (((JSONObject) columnModelJsonObject.get(column)).get("order")).toString();
				String sortOrder = (((JSONObject) columnModelJsonObject.get(column)).get("sortOrder")).toString();
				String sortType = (((JSONObject) columnModelJsonObject.get(column)).get("sortType")).toString();
				
				unit.setVisibleDirectly(Boolean.valueOf(visible));
				unit.setOrderDirectly(Integer.valueOf(order));		
				unit.setSortOrder(Integer.valueOf(sortOrder));
				unit.setSortType(DLSortType.getByStringValue(sortType));				
			} 
			
			/*
			 * FILTER 
			 */
			if (filterModelJsonObject != null && filterModelJsonObject.containsKey(column)) {
				String operator = (((JSONObject) filterModelJsonObject.get(column)).get("operator")).toString();
				Object value1 = (((JSONObject) filterModelJsonObject.get(column)).get("value1"));
				Object value2 = (((JSONObject) filterModelJsonObject.get(column)).get("value2"));
				String value1Type = (String) (((JSONObject) filterModelJsonObject.get(column)).get("value1Type"));
				String value2Type = (String) (((JSONObject) filterModelJsonObject.get(column)).get("value2Type"));
				
				NormalFilterUnitModel normalFilterUnitModel = new NormalFilterUnitModel(unit);
				normalFilterUnitModel.setOperator(DLFilterOperator.strToEnum(operator));
				normalFilterUnitModel.setValue(1, JsonHelper.fromJsonObject(value1, value1Type));
				normalFilterUnitModel.setValue(2, JsonHelper.fromJsonObject(value2, value2Type));
				savedModel.add(normalFilterUnitModel);
			}
			
			columns.add(column);
			i++;
		}
		
		// notify view about new model load
		this.getListboxController().fireOrderChanges();

		// refresh filters
        model.getFilterModel().getNormal().clear();
        model.getFilterModel().getNormal().addAll( savedModel );
        if (refreshData)
            onFilterChange( DLListboxEvents.ON_NORMAL_FILTER_CHANGE );
		
		// TODO inform user that profile may not be valid
//		if (profile.getColumnsHashCode() == null || columns.hashCode() != profile.getColumnsHashCode()) {			
//			LOGGER.warn("DLListbox columns has changed, profile may not be valid.");
//		}		
    }
    
    /**
     * This method creates {@link DLListboxProfile} from actual agenda settings.
     */    
    public DLListboxProfile createProfile() {
    	LOGGER.info("create profile");
    	
    	DLListboxProfile profile = new DLListboxProfileImpl();
    	profile.setDlListboxId(this.getSessionName());
    	
    	DLMasterModel masterModel = this.getModel();
    	DLColumnModel columnModel = masterModel.getColumnModel(); // linkedlist		
    	DLFilterModel filterModel = masterModel.getFilterModel();

    	HashMap<String, Map<String, Object>> allColumnsInfo = new HashMap<String, Map<String,Object>>();
    	List<String> columns = new ArrayList<String>();

    	int i = 0;
    	for (DLColumnUnitModel unit : columnModel.getColumnModels()) {
    		HashMap<String, Object> columnInfo = new HashMap<String, Object>();
    		columnInfo.put("visible", String.valueOf(unit.isVisible()));
    		columnInfo.put("order", String.valueOf(unit.getOrder().toString()));
    		columnInfo.put("sortOrder", String.valueOf(unit.getSortOrder()));
    		columnInfo.put("sortType", unit.getSortType().getStringValue());

    		String column = unit.getColumn();
    		if (column == null) {    			
    			column = "column" + i;
    		}
    		
    		columns.add(column);
        	allColumnsInfo.put(column, columnInfo);    		
    			
    		i++;
    	}		

    	JSONObject json = new JSONObject();
    	json.putAll(allColumnsInfo);

    	// LOGGER.info( "Column model JSON: {}", json.toJSONString());
    	// LOGGER.info( "hash: {}", columns.hashCode());

    	// data
    	profile.setColumnModelJsonData(json.toJSONString());

    	// hash
    	profile.setColumnsHashCode(columns.hashCode());

    	/**
    	 * FILTER
    	 */		
    	allColumnsInfo = new HashMap<String, Map<String,Object>>();
    	Iterator<NormalFilterUnitModel> nfumIterator = filterModel.getNormal().iterator();

		while (nfumIterator.hasNext()) {
			NormalFilterUnitModel normalFilterUnitModel = nfumIterator.next();
			HashMap<String, Object> filterInfo = new HashMap<String, Object>();
			try {
				Object value1 = normalFilterUnitModel.getValue(1);
				Object value2 = normalFilterUnitModel.getValue(2);
				
				String value1Type = JsonHelper.getType(value1);
				String value2Type = JsonHelper.getType(value2);
				
				filterInfo.put("operator", normalFilterUnitModel.getOperator().getShortName());
				filterInfo.put("value1", JsonHelper.toJsonObject(value1));
				filterInfo.put("value1Type", value1Type);
				filterInfo.put("value2", JsonHelper.toJsonObject(value2));
				filterInfo.put("value2Type", value2Type);			
				
				String column = normalFilterUnitModel.getColumn();

				// filter unit must have column
				if (column != null) {
					allColumnsInfo.put(column, filterInfo);
				}
			} catch (IllegalArgumentException iex) {
				iex.printStackTrace();
			}
    	}		

    	json = new JSONObject();
    	json.putAll(allColumnsInfo);

    	// LOGGER.info( "Filter model JSON: {}", json.toJSONString());		
    	
    	// data
    	profile.setFilterModelJsonData(json.toJSONString());	

    	return profile;
    }
}
