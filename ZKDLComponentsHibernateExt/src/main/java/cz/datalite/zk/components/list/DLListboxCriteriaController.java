package cz.datalite.zk.components.list;

import cz.datalite.dao.DLResponse;
import cz.datalite.dao.DLSearch;
import cz.datalite.dao.DLSort;
import cz.datalite.helpers.TypeConverter;
import cz.datalite.zk.components.list.filter.NormalFilterModel;
import cz.datalite.zk.components.list.filter.NormalFilterUnitModel;
import cz.datalite.zk.components.list.filter.compilers.FilterCompiler;
import cz.datalite.zk.components.list.filter.compilers.FilterCriterionCompiler;
import cz.datalite.zk.components.list.filter.config.FilterDatatypeConfig;
import cz.datalite.zk.components.list.model.DLColumnUnitModel;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Master extended controller for listbox. This implementation uses Hibernate
 * Criteria. This controller can be used also for the paging, quick filter and
 * manager component.
 * @param <T> main entity type
 * @author Karel Cemus
 */
public abstract class DLListboxCriteriaController<T> extends DLListboxGeneralController<T> {

    protected final FilterCriterionCompiler compiler;
    protected static final Logger LOGGER = LoggerFactory.getLogger( DLListboxCriteriaController.class );

    /**
     * Creates instance of the extended controller which uses Hibernate Criteria
     * @param identifier
     */
    public DLListboxCriteriaController( final String identifier ) {
        this( identifier, null );
    }

    public DLListboxCriteriaController( final String identifier, final Class<T> clazz ) {
        super( identifier, clazz );
        compiler = getCompiler();
    }

    /**
     * This method defines compiler which is used for compiling
     * defined filter conditions to Hibernate Criteria. This
     * method can be overriden to use different compiler.
     * @return filter compiler to criteria
     */
    protected FilterCriterionCompiler getCompiler() {
        return new FilterCriterionCompiler();
    }

    /**
     * Loads data coresponding to the defined criterias stored in the
     * search object
     * @param search object with filtering, sorting and paging parameters.
     * @return data containter with paging information and coresponding data.
     */
    protected abstract DLResponse<T> loadData( final DLSearch<T> search );

    @Override
    protected DLResponse<T> loadData( final List<NormalFilterUnitModel> filter, final int firstRow, final int rowCount, final List<DLSort> sorts ) {
        return loadData( getDefaultSearchObject( filter, firstRow, rowCount, sorts ) );
    }

    /**
     * Returns distinct data according to the criterias defined in the search
     * object. This metod <b>can be overrided</b>. In the default implementation
     * is called method loadData.
     * @param search search criterias.
     * @return distinct data list 
     */
    protected DLResponse<T> loadDistinctColumnValues( final DLSearch<T> search ) {
        return loadData( search );
    }

    @Override
    @SuppressWarnings("unchecked")
    protected DLResponse<String> loadDistinctColumnValues( final String column, final List<NormalFilterUnitModel> filter, final int firstRow, final int rowCount, final List<cz.datalite.dao.DLSort> sorts ) {
        final DLSearch<T> search = getDefaultSearchObject( filter, firstRow, rowCount, sorts );

        search.addAlias( column );
        search.addProjection( Projections.distinct( Projections.property( search.getAliasForPath( column ) ) ) );

        return ( DLResponse<String> ) loadDistinctColumnValues( search );
    }

    protected DLSearch<T> getDefaultSearchObject( final List<NormalFilterUnitModel> filter, final int firstRow, final int rowCount, final List<DLSort> sorts ) {
        // Vytvoření DLSearch objektu s nastaveným stránkováním a řazením
        final DLSearch<T> search = new DLSearch<T>( sorts, rowCount, firstRow );
        search.addFilterCriterions( compile( filter, search ) );
        return search;
    }

    protected List<Criterion> compile( final List<NormalFilterUnitModel> filter, final DLSearch<T> search ) {
        final List<Criterion> criterions = new LinkedList<Criterion>();
        for ( final Iterator<NormalFilterUnitModel> it = filter.iterator(); it.hasNext(); ) {
            final NormalFilterUnitModel unit = it.next();
            if ( NormalFilterModel.ALL.equals( unit.getColumn() ) ) {
                criterions.add( compileKeyAll( ( String ) unit.getValue( 1 ), search ) );
            } else {
                criterions.add( compileCriteria( unit, search ) );
            }
        }
        return criterions;
    }

    protected Criterion compileCriteria( final NormalFilterUnitModel unit, final DLSearch search ) {
        return compileCriteria( unit, search, Criteria.INNER_JOIN );
    }

    /**
     * Converts key ALL to disjunction
     * @param value value in Quick filter
     * @param search
     * @return disjnction
     */
    protected Criterion compileKeyAll( final String value, final DLSearch search ) {
        final Disjunction disjunction = Restrictions.disjunction();
        for ( DLColumnUnitModel unit : model.getColumnModel().getColumnModels() ) {
            if ( unit.isColumn() && unit.isQuickFilter() ) {
                final Criterion criterion = compileCriteria( new NormalFilterUnitModel( unit ), value, search, Criteria.LEFT_JOIN );
                if ( criterion == null ) {
                    disjunction.add( Restrictions.sqlRestriction( "0=1" ) );
                } else {
                    disjunction.add( criterion );
                }
            }
        }
        return disjunction;
    }

    /**
     * Compiles criteria with type conversion
     * @param type column type
     * @param key column address
     * @param value value to conversion
     * @param search search object because of aliases
     * @param joinType relation type
     * @return compiled criterion
     */
    @SuppressWarnings( "unchecked" )
    protected Criterion compileCriteria( final NormalFilterUnitModel unit, final String value, final DLSearch search, final int joinType ) {
        final Class type = unit.getType();
        if ( unit.getFilterCompiler() != null ) { // compiler is defined
            unit.setOperator( unit.getQuickFilterOperator() );
            unit.setValue( 1, value );
            return compileCriteria( unit, search, joinType );
        } else if ( FilterDatatypeConfig.DEFAULT_CONFIGURATION.containsKey( type ) ) {
            try {
                unit.setOperator( FilterDatatypeConfig.DEFAULT_CONFIGURATION.get( type ).getQuickOperator() );
                unit.setValue( 1, TypeConverter.convertTo( value, type ) );
                return compileCriteria( unit, search, joinType );
            } catch ( Exception ex ) {
                LOGGER.debug( "Error occured when Quick Filter was converted to '{}'. Column: '{}', Value: '{}'.", 
                        new Object[]{ type.getName(), (unit.getColumn() == null ? "unknown" : unit.getColumn()), value });

                return null;
            }
        } else {
            LOGGER.error( "Error occured when Quick Filter was compiled. There was request to compile unsupported datatype. Please "
                    + "define FilterCompiler. Type: '{}', Column: '{}', Value: '{}'.",
                    new Object[]{ (type == null ? "unknown" : type.getName()), (unit.getColumn() == null ? "unknown" : unit.getColumn()), value });
            return null;
            //  throw new UnsupportedOperationException( "Unknown data-type was used in listbox filter. For type " + (type == null ? "unknown" : type.getCanonicalName()) + " have to be defined special filter component." );
            // Thow was commented due to back compatibility in the projects which have not used attribute filter="false".
        }
    }

    /**
     * Compiles criteria according to the operator
     * @param key column address
     * @param value1 1st value
     * @param value2 2nd value
     * @param operator operator
     * @param search search object because of alias
     * @param joinType relation type
     * @return compiled criteria
     */
    protected Criterion compileCriteria( final NormalFilterUnitModel unit, final DLSearch search, final int joinType ) {
        if ( unit.getColumn() == null || unit.getColumn().length() == 0 ) {
            assert false;
            return null;
        }

        search.addAliases( unit.getColumn(), joinType );
        final FilterCompiler localCompiler = unit.getFilterCompiler() == null ? compiler : unit.getFilterCompiler();
        return ( Criterion ) localCompiler.compile( unit.getOperator(), search.getAliasForPath( unit.getColumn() ), unit.getValue( 1 ), unit.getValue( 2 ) );
    }
}
