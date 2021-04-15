package cz.datalite.zk.components.list.model;

import cz.datalite.dao.DLNullPrecedence;
import cz.datalite.dao.DLSortType;
import cz.datalite.helpers.StringHelper;
import cz.datalite.zk.components.list.enums.DLFilterOperator;
import cz.datalite.zk.components.list.filter.compilers.FilterCompiler;
import cz.datalite.zk.components.list.filter.components.FilterComponent;
import cz.datalite.zk.components.list.filter.components.FilterComponentFactory;
import cz.datalite.zk.components.list.filter.config.FilterDatatypeConfig;
import cz.datalite.zk.converter.ConverterResolver;
import cz.datalite.zk.converter.ZkConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.Composer;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Column model - model for the listheader in the listbox - it
 * is used like a model for whole col.
 * @author Karel Cemus
 */
public class DLColumnUnitModel implements Comparable<DLColumnUnitModel> {

    // ************* MAIN ************* //
    /** column name in the database - loaded throw data binding or attribute */
    protected String column;
    
    /** column data type - used for conversion in the filters */
    protected Class<?> columnType;
    
    /** converter for the modifying value */
    protected ZkConverter converter;
    
    /** instance of controller to allow to use convertors defined as "ctl.coerce..." */
    protected Composer<?> controller;
    
    /** column label */
    protected String label;
    
    /** column order - 1 starts, 0 turned off, hidden */
    protected Integer order = 0;
    
    /** is visible */
    protected boolean visible = true;
    
    /** main component */
    protected final DLColumnModel columnModel;
    
    // ************* SORT ************* //
    /** sort order - 1 start, 0 not sorted */
    protected Integer sortOrder = 0;
    
    /** actual sort type */
    protected DLSortType sortType = DLSortType.NATURAL;

    /** null precedence for ORDER BY clause */
    private DLNullPrecedence nullPrecedence = DLNullPrecedence.NONE;
    
    /** column name for the sorting */
    protected String sortColumn;
    
    /** definition if is used default zk sort od database sort */
    protected boolean sortZk = false;
    
    /** is possible to sort by this column */
    protected boolean sortable = false;
    
    /** defines participation of this column in the quick filter */   
    protected boolean quickFilter;
    
    /** defines quick filter operator */
    protected DLFilterOperator quickFilterOperator;
    
    /** defines if this column can participate in normal filter */
    protected boolean filter;
    
    /** column is enabled for export */
    protected boolean exportable;

    /** column is enabled for listing in column manager */
    protected boolean columnManager = true;
    
    /** redefines filter operators for this column */
    protected List<DLFilterOperator> filterOperators;
    
    /** redefines filter component which is used in normal filter for this column */
    protected FilterComponentFactory filterComponentFactory;
    
    /** column name for exporting */
    private String exportColumn;
        
    /** redefines filter compiler which is used to compile filter operators */
    private FilterCompiler filterCompiler;

	/** Column width */
	private String width;
    
    /** logger */
    protected final static Logger LOGGER = LoggerFactory.getLogger( DLColumnUnitModel.class );

    public DLColumnUnitModel( final DLColumnModel columnModel ) {
        this.columnModel = columnModel;
        this.order = columnModel.autoIncOrder();
    }

    // ************* MAIN ************* //
    public void setLabel( final String label ) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /**
     * Order starts at 1, 0 off - hidden
     * @return show order
     */
    public Integer getOrder() {
        return order;
    }

    /**
     * Order starts at 1, 0 off - hidden
     * @param order show order
     */
    public void setOrder( final Integer order ) {
        // move all columns which are behind me
        for ( DLColumnUnitModel unit : columnModel.getColumnModels() ) {
            unit.autodecrementOrder( this.order );
        }

        if ( order > 0 ) { // if it is moving or inserting
            // move all which will be behind me
            for ( DLColumnUnitModel unit : columnModel.getColumnModels() ) {
                unit.autoincrementOrder( order );
            }

            // if it wasn't shown show it
            if ( this.order == 0 ) {
                columnModel.autoIncOrder();
                visible = true;
            }
        } else {
            visible = false;
            columnModel.autoDecOrder();
        }

        this.order = order;
    }

    /**
     * Order starts at 1, 0 off - hidden.
     * Use this method only if you set all columns programatically at once.
     *
     * @param order show order
     */
    public void setOrderDirectly( final Integer order )
    {
        this.order = order;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn( final String column ) {
        if ( this.column == null || this.column.length() == 0 ) {
            this.column = column;
        }
    }

    public boolean isColumn() {
        return this.column != null && this.column.length() > 0;
    }

    public Class<?> getColumnType() {
        return columnType;
    }

    public void setColumnType( final Class<?> columnType ) {
        if ( this.columnType == null ) {
            this.columnType = columnType;
        }
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible( final boolean visible ) {
        if ( this.visible == visible ) {
            return;
        }
        if ( visible ) { // if it is setted on visible true move it to the end
            if ( order == 0 ) {
                order = columnModel.autoIncOrder();
            }
        } else { // if invisible is setting we have to remove a change order index of all other columns
            columnModel.autoDecOrder();
            for ( DLColumnUnitModel unit : columnModel.getColumnModels() ) {
                unit.autodecrementOrder( order );
            }
            order = 0;
        }

        this.visible = visible;
    }

    public void setVisibleDirectly(Boolean visible) {
        this.visible = visible;
    }

    // ************* SORT ************* //

    public boolean isSortZk() {
        return sortable && sortZk;
    }

    public void setSortZk( final boolean sortZk ) {
        this.sortZk = sortZk;
    }

    public DLSortType getSortType() {
        return sortType;
    }

    /**
     * Setup sort type for this column and recalculate sortOrder of other columns to hold consistent state.
     * @param sortType new sort type
     */
    public void setSortType( final DLSortType sortType ) {
        // if there was setted NATURAL and won't be then active sort order
        if ( DLSortType.NATURAL.equals( this.sortType ) && !DLSortType.NATURAL.equals( sortType ) ) {
            sortOrder = columnModel.autoIncSortMaxIndex();
        }
        // if there wasn't setted NATURAL and will be then deactive sort order
        if ( !DLSortType.NATURAL.equals( this.sortType ) && DLSortType.NATURAL.equals( sortType ) ) {
            columnModel.autoDecSortMaxIndex();
            for ( DLColumnUnitModel unit : columnModel.getColumnModels() ) {
                unit.autodecrementSortOrder( sortOrder );
            }
            sortOrder = 0;
        }

        setSortTypeDirectly(sortType);
    }

    /**
     * Directly set the value of sort type without modification of other columns (e.g. setup of sortOrder)
     * @param sortType new sort type
     */
    public void setSortTypeDirectly(final DLSortType sortType) {
        this.sortType = sortType;
    }


    public boolean isDBSortable() {
        return sortable && !sortZk && existSortColumn();
    }

    public String getSortColumn() {
        return sortColumn == null || sortColumn.length() == 0 ? column : sortColumn;
    }

    public void setSortColumn( final String sortColumn ) {
        this.sortColumn = sortColumn;
    }

    public boolean isSortable() {
        return sortable && existSortColumn() && (isSortZk() || isDBSortable());
    }

    public void setSortable( final boolean sortable ) {
        this.sortable = sortable;
    }

    protected boolean existSortColumn() {
        return getSortColumn() != null && getSortColumn().length() > 0;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder( final Integer sortOrder ) {
        this.sortOrder = sortOrder;
        //DTL-VK: Zdůvodů ukládání položky sortOrder do profilu musím posunout maximální id až za nastavovanou hodnotu
        while ( this.sortOrder > columnModel.getSortMaxIndex() )
        {
            columnModel.autoIncSortMaxIndex() ;
        }
    }

    /**
     * Autodecrement sortOrder if it is greater than param
     * @param greaterThan removed order index
     */
    public void autodecrementSortOrder( final Integer greaterThan ) {
        if ( sortOrder > greaterThan ) {
            sortOrder = sortOrder - 1;
        }
    }

    /**
     * Autodecrement order if it is greater than param
     * @param greaterThan removed order index
     */
    public void autodecrementOrder( final Integer greaterThan ) {
        if ( order > greaterThan ) {
            order = order - 1;
        }
    }

    /**
     * Autoincrement order if it is greater or equal than param
     * @param greaterEqual inserted order index
     */
    public void autoincrementOrder( final Integer greaterEqual ) {
        if ( order >= greaterEqual ) {
            order = order + 1;
        }
    }

    public boolean isSorted() {
        return !DLSortType.NATURAL.equals( sortType );
    }

    public boolean isConverter() {
        return converter != null;
    }

    public ZkConverter getConverter() {
        return converter;
    }

    public void setConverter( final ZkConverter converter ) {
        this.converter = converter;
    }

    public void setConverter( final String converter, final Component comp, final Map<String,String> attributes ) {
        this.converter = ConverterResolver.resolve( converter, comp, controller, attributes );
    }

    public void setQuickFilter( final boolean quickFilter ) {
        this.quickFilter = quickFilter;
    }

    public boolean isQuickFilter() {
        return quickFilter;
    }

    public void setFilter( final boolean filter ) {
        this.filter = filter;
    }

    public boolean isFilter() {
        return filter;
    }

    public void setFilterOperators( final List<DLFilterOperator> operators ) {
        this.filterOperators = operators;
        /**
         * if operators is null then wasn't redefined and default operators will be used
         * if operators are empty then all operators was disabled and filter is disabled
         * if operators aren't empty then was redefined
         */
        this.filter &= operators == null ? true : !operators.isEmpty();
    }

    public List<DLFilterOperator> getFilterOperators() {
        if ( filterOperators == null ) { // No
            LOGGER.debug( "Get operators for type: '{}'.", (getColumnType() == null ? "null" : getColumnType().getCanonicalName()) );
            if ( columnType == null && column == null ) { // empty model
                filterOperators = Collections.emptyList();
            } else if ( columnType == null ) { // Is this type defined? know name but known type
                filterOperators = Collections.singletonList( DLFilterOperator.EQUAL );
            } else if ( FilterDatatypeConfig.DEFAULT_CONFIGURATION.containsKey( columnType ) ) { // Is the default configuration known for this type?
                filterOperators = FilterDatatypeConfig.DEFAULT_CONFIGURATION.get( columnType ).getOperators();
            } else if ( columnModel.getMaster().getFilterModel().isWysiwyg() ) {
                return FilterDatatypeConfig.DEFAULT_CONFIGURATION.get( String.class ).getOperators();
            } else {
                throw new UnsupportedOperationException( "Unknown datatype was used in listbox filter. For type " + columnType.getCanonicalName() + " have to be defined special filter component." );
            }
            LOGGER.debug( "Operator list: '{}'.", filterOperators.size() );
        }
        return filterOperators;
    }

    public boolean isFilterOperators() {
        try {
            return !getFilterOperators().isEmpty();
        } catch ( UnsupportedOperationException ex ) {
            return false;
        }
    }

    public DLFilterOperator getQuickFilterOperator() {
        return getQuickFilterOperator(null);
    }

    /**
     * Return quickFilterOperator based on a value. If quickFilterOperator is set directly to the listheader,
     * it is always returned as is. However, for operator based on datatype it may be changed according to a value.
     * For example - compare strings with EQUAL operator until it contains some kind of wildcard - than use LIKE
     * @param value actual filter value for which the operator should be created
     * @return operator - always not null, if not defined use EQUAL
     */
    public DLFilterOperator getQuickFilterOperator(String value) {
        if ( quickFilterOperator != null ) {
            return quickFilterOperator;
        }

        if ( columnType == null ) { // Is this type defined?
            return DLFilterOperator.EQUAL;
        } else {
            if ( FilterDatatypeConfig.DEFAULT_CONFIGURATION.containsKey( columnType ) ) { // Is the default configuration known for this type?
                if (value != null)
                    return FilterDatatypeConfig.DEFAULT_CONFIGURATION.get( columnType ).getQuickOperator(value);
                else
                    return FilterDatatypeConfig.DEFAULT_CONFIGURATION.get( columnType ).getQuickOperator();
            } else {
                LOGGER.debug( "Unknown datatype was used in listbox quick filter. For type '{}' have been used EQUAL operator.", columnType.getCanonicalName() );
                return DLFilterOperator.EQUAL;
            }
        }
    }


    public FilterComponent<?> createFilterComponent() {
        return getFilterComponentFactory().createFilterComponent();
    }

    public boolean isFilterComponent() {
        return filterComponentFactory != null || (columnType != null && FilterDatatypeConfig.DEFAULT_CONFIGURATION.containsKey( columnType ));
    }

    public Class<?> getTypeOfFilterComponent() {
        return getFilterComponentFactory().getComponentClass();
    }

    public void setFilterComponentFactory( final FilterComponentFactory filterComponentFactory ) {
        this.filterComponentFactory = filterComponentFactory;
    }

    public FilterComponentFactory getFilterComponentFactory() {
        if ( filterComponentFactory == null ) {
            // Is the default configuration known for this type?
            if ( columnType != null && FilterDatatypeConfig.DEFAULT_CONFIGURATION.containsKey( columnType ) ) {
                return FilterDatatypeConfig.DEFAULT_CONFIGURATION.get( columnType );
            } else if ( columnModel.getMaster().getFilterModel().isWysiwyg() ) {
                return FilterDatatypeConfig.DEFAULT_CONFIGURATION.get( String.class );
            } else {
                throw new UnsupportedOperationException( "Unknown datatype was used in listbox filter for column '" + column + "'. For type "
                        + (columnType == null ? "unknown" : columnType.getCanonicalName()) + " have to be defined special filter component." );
            }
        } else {
            return filterComponentFactory;
        }
    }

    public void setFilterCompiler( final FilterCompiler filterCompiler ) {
        this.filterCompiler = filterCompiler;
    }

    public FilterCompiler getFilterCompiler() {
        return filterCompiler;
    }

    /**
     * Unit Model is comperable by order.
     * @param o order
     * @return -1 if first less then other, 0 if same, 1 otherwise.
     */
	public int compareTo(DLColumnUnitModel o) {
		if (o == null)
			return 0;

		if ( o.isColumnManager() && isColumnManager() ) {
            return getOrder().compareTo(o.getOrder());
        } else if ( ! o.isColumnManager() ) {
		    return 1 ;
        } else if ( ! isColumnManager() ) {
		    return -1 ;
        } else if ( StringHelper.isNull(o.label) && ! StringHelper.isNull(label) ) {
		    return 0 ;
        } else if ( StringHelper.isNull(o.label) ) {
		    return -1 ;
        } else {
		    return 1 ;
        }
	}

    public Composer<?> getController() {
        return controller;
    }

    public void setQuickFilterOperator( DLFilterOperator filterOperator ) {
        this.quickFilterOperator = filterOperator;
    }

	public boolean isExportable() {
		return exportable;
	}

	public void setExportable(boolean exportable) {
		this.exportable = exportable;
	}

    public boolean isColumnManager() {
        return columnManager;
    }

    public void setColumnManager(boolean columnManager) {
        this.columnManager = columnManager;
    }

    public String getExportColumn() {
		return exportColumn;
	}

	public void setExportColumn(String exportColumn) {
		this.exportColumn = exportColumn;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

    public DLNullPrecedence getNullPrecedence() {
        return nullPrecedence;
    }

    public void setNullPrecedence(DLNullPrecedence nullPrecedence) {
        this.nullPrecedence = nullPrecedence;
    }

    @Override
	public String toString() {
		return "DLColumnUnitModel [column=" + column + ", columnType=" + columnType + ", converter=" + converter
				+ ", controller=" + controller + ", label=" + label + ", order=" + order + ", visible=" + visible
				+ ", sortOrder=" + sortOrder + ", sortType=" + sortType + ", nullPrecedence=" + nullPrecedence + ", sortColumn=" + sortColumn + ", sortZk="
				+ sortZk + ", sortable=" + sortable + ", quickFilter=" + quickFilter + ", quickFilterOperator="
				+ quickFilterOperator + ", filter=" + filter + ", exportable=" + exportable + ", columnManager=" + columnManager + ", filterOperators="
				+ filterOperators + ", filterComponentFactory=" + filterComponentFactory + ", exportColumn="
				+ exportColumn + ", filterCompiler=" + filterCompiler + ", width=" + width + "]";
	}
}
