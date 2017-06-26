package cz.datalite.zk.liferay;

import com.liferay.portal.kernel.dao.orm.*;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import cz.datalite.dao.DLResponse;
import cz.datalite.dao.DLSort;
import cz.datalite.helpers.TypeConverter;
import cz.datalite.zk.components.list.DLListboxGeneralController;
import cz.datalite.zk.components.list.filter.NormalFilterModel;
import cz.datalite.zk.components.list.filter.NormalFilterUnitModel;
import cz.datalite.zk.components.list.filter.config.FilterDatatypeConfig;
import cz.datalite.zk.components.list.model.DLColumnUnitModel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.zkoss.zk.ui.UiException.Aide;

/**
 * Master extended controller for listbox. This implementation uses Hibernate
 * Criteria. This controller can be used also for the paging, quick filter and
 * manager component.
 * @param <T> main entity type
 * @author Karel ÄŚemus
 */
public abstract class DLListboxLiferayController<T> extends DLListboxGeneralController<T> {

    final FilterLiferayCompiler compiler;
    
    /**
     * Creates instance of the extended controller which uses Hibernate Criteria
     * @param identifier
     */
    public DLListboxLiferayController( final String identifier ) {
        this( identifier, null );
    }

    public DLListboxLiferayController( final String identifier, final Class<T> clazz ) {
        super( identifier, clazz );
        compiler = getCompiler();
    }

    /**
     * This method defines compiler which is used for compiling
     * defined filter conditions to Hibernate Criteria. This
     * method can be overriden to use different compiler.
     * @return filter compiler to criteria
     */
    protected FilterLiferayCompiler getCompiler() {
        return new FilterLiferayCompiler();
    }

    /**
     * Loads data coresponding to the defined criterias stored in the
     * search object
     * @param dynamicQuery Liferay dynamic query
     * @return data containter with paging information and coresponding data.
     */
    protected abstract DLResponse<T> loadData( final DynamicQuery dynamicQuery ) throws SystemException;

    // Main method for loading data as declared in GeneralController
    @Override
    protected DLResponse<T> loadData( final List<NormalFilterUnitModel> filter, final int firstRow, final int rowCount, final List<DLSort> sorts ) {
        try {
            return loadData(getDefaultDynamicQuery(filter, firstRow, rowCount, sorts));
        } catch (SystemException ex) {
            LOGGER.error( "Something went wrong.", ex );
            throw Aide.wrap(ex, "Error in Liferay dynamic query (loadData)");
        }
    }


    private DynamicQuery getDefaultDynamicQuery(List<NormalFilterUnitModel> filter, int firstRow, int rowCount, List<DLSort> sorts)
    {
        DynamicQuery dQuery = DynamicQueryFactoryUtil.forClass(getEntityClass(), PortalClassLoaderUtil.getClassLoader());

        for (Criterion c : compile(filter, dQuery)) {
            dQuery.add(c);
        }


        // if row count is defined write limits
        if ( rowCount != 0 ) {
            dQuery.setLimit( firstRow, firstRow + rowCount );
        }

        return dQuery;

    }


    /**
     * Returns distinct data according to the criterias defined in the search
     * object. This metod <b>can be overrided</b>. In the default implementation
     * is called method loadData.
     * @param dQuery the Query
     * @return distinct data list
     */
    protected DLResponse<T> loadDistinctColumnValues( final DynamicQuery dQuery ) {
        try {
            return loadData(dQuery);
        } catch (SystemException ex) {
            LOGGER.error( "Something went wrong.", ex );
            throw Aide.wrap(ex, "Error in Liferay dynamic query (loadData)");
        }
    }

    @Override
    protected DLResponse<String> loadDistinctColumnValues( final String column, final List<NormalFilterUnitModel> filter, final int firstRow, final int rowCount, final List<cz.datalite.dao.DLSort> sorts ) {
        final DynamicQuery dQuery = getDefaultDynamicQuery( filter, firstRow, rowCount, sorts );

        dQuery.setProjection( ProjectionFactoryUtil.distinct( ProjectionFactoryUtil.property( column  ) ) );

        return ( DLResponse<String> ) loadDistinctColumnValues( dQuery );
    }


    protected List<Criterion> compile( final List<NormalFilterUnitModel> filter, final DynamicQuery dynamicQuery ) {
        final List<Criterion> criterions = new LinkedList<Criterion>();
        for ( final Iterator<NormalFilterUnitModel> it = filter.iterator(); it.hasNext(); ) {
            final NormalFilterUnitModel unit = it.next();
            if (NormalFilterModel.ALL.equals(unit.getColumn())) {
                criterions.add( compileKeyAll( ( String ) unit.getValue( 1 ), dynamicQuery ) );
            } else {
                criterions.add( compileCriteria( unit, dynamicQuery ) );
            }
        }
        return criterions;
    }

    /**
     * Converts key ALL to disjunction
     * @param value value in Quick filter
     * @return disjnction
     */
    protected Criterion compileKeyAll( final String value, final DynamicQuery dynamicQuery ) {
        final Disjunction disjunction = RestrictionsFactoryUtil.disjunction();
        for ( DLColumnUnitModel unit : model.getColumnModel().getColumnModels() ) {
            if ( unit.isColumn() && unit.isQuickFilter() ) {
                final Criterion criterion = compileCriteria( new NormalFilterUnitModel( unit ), value, dynamicQuery );
                if ( criterion == null ) {
                    // FIXME
                    // disjunction.add( RestrictionsFactoryUtil.sqlRestriction( "0=1" ) );
                } else {
                    disjunction.add( criterion );
                }
            }
        }
        return disjunction;
    }

    /**
     * Compiles criteria with type conversion
     * @param value value to conversion
     * @return compiled criterion
     */
    @SuppressWarnings( "unchecked" )
    protected Criterion compileCriteria( final NormalFilterUnitModel unit, final String value, final DynamicQuery dynamicQuery ) {
        final Class type = unit.getType();
        if ( unit.getFilterCompiler() != null ) { // compiler is defined
            unit.setOperator( unit.getQuickFilterOperator() );
            unit.setValue( 1, value );
            return compileCriteria( unit, dynamicQuery );
        } else if ( FilterDatatypeConfig.DEFAULT_CONFIGURATION.containsKey( type ) ) {
            try {
                unit.setOperator( FilterDatatypeConfig.DEFAULT_CONFIGURATION.get( type ).getQuickOperator() );
                unit.setValue( 1, TypeConverter.convertTo( value, type ) );
                return compileCriteria( unit, dynamicQuery );
            } catch ( Exception ex ) {
                LOGGER.debug( "Error occured when Quick Filter was converted to '{}'. Column: '{}', Value: '{}'.",
                        new Object[]{type.getName(), (unit.getColumn() == null ? "unknown" : unit.getColumn()), value});

                return null;
            }
        } else {
            LOGGER.error( "Error occured when Quick Filter was compiled. There was request to compile unsupported datatype. Please "
                    + "define FilterCompiler. Type: '{}', Column: '{}', Value: '{}'.",
                        new Object[]{(type == null ? "unknown" : type.getName()), (unit.getColumn() == null ? "unknown" : unit.getColumn()), value} );
            return null;
            //  throw new UnsupportedOperationException( "Unknown data-type was used in listbox filter. For type " + (type == null ? "unknown" : type.getCanonicalName()) + " have to be defined special filter component." );
            // Thow was commented due to back compatibility in the projects which have not used attribute filter="false".
        }
    }

    /**
     * Compiles criteria according to the operator
     * @return compiled criteria
     */
    protected Criterion compileCriteria( final NormalFilterUnitModel unit, final DynamicQuery dynamicQuery ) {
        if ( unit.getColumn() == null || unit.getColumn().length() == 0 ) {
            assert false;
            return null;
        }

        // search.addAliases( unit.getColumn(), joinType );
        final FilterLiferayCompiler localCompiler = unit.getFilterCompiler() == null ? compiler : (FilterLiferayCompiler) unit.getFilterCompiler();
        return localCompiler.compile( unit.getOperator(), unit.getColumn(), unit.getValue( 1 ), unit.getValue( 2 ) );
    }
}
