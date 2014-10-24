package cz.datalite.zk.components.list.view;

import cz.datalite.dao.DLNullPrecedence;
import cz.datalite.dao.DLSortType;
import cz.datalite.helpers.StringHelper;
import cz.datalite.zk.components.list.controller.DLListboxComponentController;
import cz.datalite.zk.components.list.enums.DLFilterOperator;
import cz.datalite.zk.components.list.filter.compilers.FilterCompiler;
import cz.datalite.zk.components.list.filter.components.CloneFilterComponentFactory;
import cz.datalite.zk.components.list.filter.components.CloneableFilterComponent;
import cz.datalite.zk.components.list.filter.components.FilterComponentFactory;
import cz.datalite.zk.components.list.filter.components.InstanceFilterComponentFactory;
import cz.datalite.zk.components.list.model.DLColumnUnitModel;
import org.zkoss.lang.Library;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.SortEvent;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.ListitemComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ZK component DLListheader is extended component from standard Listheader
 *
 * @author Jiri Bubnik
 * @author Karel Cemus
 */
public class DLListheader extends Listheader {

	private static final long serialVersionUID = 5081334044887334444L;

	/**
	 * If set to {@code true}, column width is set from profile column information. Default is {@code false}.
	 */
	private static final String LIBRARY_PROFILE_SET_COLUMN_WIDTH = "zk-dl.listbox.profile.setColumnWidth";
	private static final boolean setColumnWidth = Boolean.valueOf(Library.getProperty(LIBRARY_PROFILE_SET_COLUMN_WIDTH, "false"));

	/** should be this column sorted in the default */
    protected DLSortType defaultSort = DLSortType.NATURAL;

    /** null precedence for ORDER BY clause */
    private DLNullPrecedence nullPrecedence = DLNullPrecedence.NONE;

    /** column name in the database which coresponds to this col - for sorting */
    protected String sortColumn = "";
    
    /** will be used default zk sort or database sort */
    protected boolean sortZk;
    
    /** is column sortable - false disables sorting */
    protected boolean sortable = false;
    
    /** column name in the database which coresponds to this col */
    protected String column;
    
    /** column name used to override default behavior of xls export */
    protected String exportColumn;
    
    /** data type of this column */
    protected Class<?> columnType;
    
    /** column is enabled for quick filter */
    protected boolean quickFilter = true;
    
    /** column is enabled for export */
    protected boolean exportable = true;

    /** defines operators used in normal filter for this column */
    protected List<DLFilterOperator> operators;

    /** quick filter operator - if null, default operator for data type is used. */
    protected DLFilterOperator quickFilterOperator;
    
    /** defines class which generates default component for filter value in normal filter */
    protected FilterComponentFactory filterComponentFactory;
    
    /** defines own filter compiler for this column */
    protected FilterCompiler filterCompiler;
    
    /** is enabled for all filters */
    protected boolean filter = true;
    
    /** converter */
    protected String converter;
    
    /** model */
    protected DLColumnUnitModel model;
    
    /** controller */
    protected DLListboxComponentController controller;
    
    /** default visibility (defined in zul file) */
    private Boolean defaultVisible = null;

    public void initModel() {
        model.setLabel( getLabel() );
        model.setSortable(sortable);
        model.setSortZk(sortZk);
        model.setSortType(defaultSort);
        model.setNullPrecedence(nullPrecedence);
        model.setSortColumn( sortColumn );
        model.setVisible( (defaultVisible != null) ? defaultVisible : isVisible() );
        model.setColumn( column );
        model.setExportColumn( exportColumn );
        model.setColumnType( columnType );
        model.setConverter( converter, this, Collections.<String,String>emptyMap() );
        model.setQuickFilter( quickFilter );
        model.setQuickFilterOperator(quickFilterOperator);
        model.setFilter( filter );
        model.setExportable( exportable );
        model.setFilterOperators( operators );
        model.setFilterComponentFactory( filterComponentFactory );
        model.setFilterCompiler( filterCompiler );
		model.setWidth(getWidth());
    }



    /**
     * Sets default sort on this column
     * 
     * @param defaultSort the defaultSort to set
     */
    public void setDefaultSort( final String defaultSort ) {
        if ( "asc".equals( defaultSort ) ) {
            this.defaultSort = DLSortType.ASCENDING;
        } else if ( "desc".equals( defaultSort ) ) {
            this.defaultSort = DLSortType.DESCENDING;
        } else if ( "natural".equals( defaultSort ) ) {
            this.defaultSort = DLSortType.NATURAL;
        } else {
            throw new UnsupportedOperationException( "Unknown sortType." );
        }
    }

    public void setNullPrecedence(final String nullPrecedence) {
       this.nullPrecedence = DLNullPrecedence.parse(nullPrecedence);
    }


    /**
     * If header doesn't containt tooltiptext, label is default tooltip.
     * Usefull for long header names.
     * 
     * @return tooltip text
     */
    @Override
    public String getTooltiptext() {
        final String tooltip = super.getTooltiptext();

        return StringHelper.isNull( tooltip ) ? getLabel() : tooltip;
    }

    @Override
    public void setSort( final String type ) {
        if ( type != null ) {
            if ( type.startsWith( "auto" ) ) {
                if ( type.startsWith( "auto(" ) && type.endsWith( ")" ) ) {
                    sortColumn = type.substring( 5, type.length() - 1 );
                }
                sortable = true;
                sortZk = true;
                super.setSort( type );
            } else if ( "none".equals( type ) ) {
                sortable = false;
                super.setSort( type );
            } else if ( type.startsWith( "db(" ) && type.endsWith( ")" ) ) {
                sortColumn = type.substring( 3, type.length() - 1 );
                sortZk = false;
                sortable = true;
                setSortAscending( new ListitemComparator( this, true, true ) );
                setSortDescending( new ListitemComparator( this, false, true ) );
            } else {
                throw new UnsupportedOperationException( "Unknown sortType." );
            }
        }
    }

    @Override
    public void onSort() {
        if ( isController() ) {
            getController().onSort( this );
        } else {
            super.onSort();
        }

    }

    @Override
    public void onSort(SortEvent event) {
        if ( isController() ) {
            getController().onSort( this );
        }
        else {
            super.onSort(event);
        }
    }

    @Override
    public DLListbox getListbox() {
        return ( DLListbox ) super.getListbox();
    }

    /**
     * Sets graphical symbol for sorting
     */
    public void setSortStyle() {
        super.setSortDirection( getSortDirection() );
    }

    @Override
    public String getSortDirection() {
        if ( isController() ) {
            return getModel().getSortType().getStringValue();
        } else {
            return super.getSortDirection();
        }
    }

    /**
     * if is controller attached, this method is empty
     * @param sortDir sort direction
     * @throws WrongValueException
     */
    @Override
    public void setSortDirection( final String sortDir ) throws WrongValueException {
        if ( !isController() ) {
            super.setSortDirection( sortDir );
        }
    }

    protected DLListboxComponentController getController() {
        return getListbox().getController();
    }

    protected boolean isController() {
        return controller != null;
    }

    public void setModel( final DLColumnUnitModel model ) {
        this.model = model;
    }

    public void fireChanges() {
        if ( isVisible() != getModel().isVisible() ) {
            setVisible( getModel().isVisible() );
        }
        setSortStyle();
		setWidth();
    }

	/**
	 * Set the column width according to information from the {@link cz.datalite.zk.components.list.model.DLColumnUnitModel#width},
	 * if {@link cz.datalite.zk.components.list.view.DLListheader#LIBRARY_PROFILE_SET_COLUMN_WIDTH} is set to {@code true}.
	 */
	private void setWidth() {
		if (setColumnWidth && model.getWidth() != null) {
			setWidth(model.getWidth());
		}
	}

	public void setController( final DLListboxComponentController controller ) {
        this.controller = controller;
    }

    public DLColumnUnitModel getModel() {
        if ( model == null ) {
            throw new IllegalArgumentException( "Model is required. Please set the controller." );
        } else {
            return model;
        }
    }

    public void setColumn( final String column ) {
        this.column = column;
    }

    public void setColumnType( final String columnType ) throws ClassNotFoundException {
        this.columnType = Class.forName( columnType );
    }

    public void setConverter( final String converter ) {
        this.converter = converter;
    }

    public void setQuickFilter( final boolean quickFilter ) {
        this.quickFilter = quickFilter;
    }

    public void setFilter( final boolean filter ) {
        this.filter = filter;
    }
    
    public void setExportable(boolean exportable) {
		this.exportable = exportable;
	}
    
    public void setExportColumn(String exportColumn) {
		this.exportColumn = exportColumn;
	}

	public void setFilterOperators( final String ops ) {
        operators = new ArrayList<DLFilterOperator>();
        if ( Strings.isEmpty( ops ) ) {
            return;
        }
        for ( String op : ops.split( "," ) ) {
            if ( "none".equals( op ) ) { // none operator can be used
                operators.clear(); // remove all already define operators
                return;
            }
            operators.add( DLFilterOperator.strToEnum( op ) );
        }
    }

    public void setQuickFilterOperator(final String op) {
        this.quickFilterOperator = DLFilterOperator.strToEnum( op );
    }

    public void setFilterComponent( final CloneableFilterComponent filterComponent ) {
        if ( this.filterComponentFactory == null ) {
            this.filterComponentFactory = new CloneFilterComponentFactory( filterComponent );
        }
    }

    public void setFilterComponent( final String filterComponentClass ) {
        if ( this.filterComponentFactory == null ) {
            try {
                filterComponentFactory = new InstanceFilterComponentFactory( filterComponentClass );
            } catch ( RuntimeException ex ) {
                throw new IllegalArgumentException( "FilterComponent wasn't created. Class '"
                        + filterComponentClass + "' wasn't found.", ex );
            }
        }
    }

    public void setFilterCompiler( final FilterCompiler filterCompiler ) {
        if ( this.filterCompiler == null ) {
            this.filterCompiler = filterCompiler;
        }
    }
    
    @Override
    public boolean setVisible(boolean visible) {
    	if (this.defaultVisible == null) {
    		this.defaultVisible = visible;
    	}
    	return super.setVisible(visible);
    }

    public void setFilterCompiler( final String filterCompilerClass ) {
        if ( this.filterCompiler == null ) {
            try {
                filterCompiler = ( FilterCompiler ) Class.forName( filterCompilerClass ).newInstance();
            } catch ( InstantiationException ex ) {
                throw new IllegalArgumentException( "FilterCompiler wasn't created. For class '"
                        + filterCompilerClass + "' couldn't be created an instance.", ex );
            } catch ( IllegalAccessException ex ) {
                throw new IllegalArgumentException( "FilterCompiler wasn't created. Class '"
                        + filterCompilerClass + "' has no public constructor.", ex );
            } catch ( ClassNotFoundException ex ) {
                throw new IllegalArgumentException( "FilterCompiler wasn't created. Class '"
                        + filterCompilerClass + "' wasn't found.", ex );
            }
        }
    }

    public void setFilterGeneralComponent( final String className ) {
        if ( this.filterCompiler == null && this.filterComponentFactory == null ) {
            try {
                final Object component = Class.forName( className ).newInstance();
                setFilterGeneralComponent( component );
            } catch ( InstantiationException ex ) {
                throw new IllegalArgumentException( "GeneralFilterComponent wasn't created. For class '"
                        + className + "' couldn't be created an instance.", ex );
            } catch ( IllegalAccessException ex ) {
                throw new IllegalArgumentException( "GeneralFilterComponent wasn't created. Class '"
                        + className + "' has no public constructor.", ex );
            } catch ( ClassNotFoundException ex ) {
                throw new IllegalArgumentException( "GeneralFilterComponent wasn't created. Class '"
                        + className + "' wasn't found.", ex );
            }
        }
    }

    public void setFilterGeneralComponent( final Object component ) {
        if ( !FilterCompiler.class.isAssignableFrom( component.getClass() ) ) {
            throw new IllegalArgumentException( "FilterGeneralComponent requires to implement " + FilterCompiler.class.getCanonicalName()
                    + ". To define own FilterComponent serves attribute \"filterComponent\"." );
        }
        if ( !CloneableFilterComponent.class.isAssignableFrom( component.getClass() ) ) {
            throw new IllegalArgumentException( "FilterGeneralComponent requires to implement " + CloneableFilterComponent.class.getCanonicalName()
                    + ". To define own FilterCompiler serves attribute \"filterCompiler\"." );
        }

        setFilterComponent( ( CloneableFilterComponent ) component );
        setFilterCompiler( ( FilterCompiler ) component );
    }
}
