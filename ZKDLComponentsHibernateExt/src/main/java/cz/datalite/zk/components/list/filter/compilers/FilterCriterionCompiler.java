package cz.datalite.zk.components.list.filter.compilers;

import cz.datalite.zk.components.list.enums.DLFilterOperator;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.zkoss.lang.Library;

/**
 * This compiler class serves to compile filter operators to Hibernate
 * Critera which can be executed in database. There are defined methods
 * which knows how to compile each defined operator.
 *
 * To add additional one there have to be defined new method for this operator
 * and extended the compile method with additional case here or in the parent
 * class.
 *
 * @author Karel Cemus
 */
public class FilterCriterionCompiler extends AbstractFilterCompiler {

    // Operator LIKE and NOT LIKE default match mode
    private static MatchMode FILTER_CRITERION_LIKE_MATCH_MODE = MatchMode.ANYWHERE;

    static {
        String property = Library.getProperty(FilterCriterionCompiler.class.getName() + ".FILTER_CRITERION_LIKE_MATCH_MODE");
        if (property != null) {
            try {
                FILTER_CRITERION_LIKE_MATCH_MODE = MatchMode.valueOf(property);
            } catch (IllegalArgumentException e) {

            }
        }
    }

    /**
     * Converts condition to the hiberanate criterion according to the operand
     * @param key column name
     * @param values[0] 1st operand
     * @param values[1] 2nd operand
     * @return compiled criterion
     */
    @Override
    public Criterion compile( final DLFilterOperator operator, final String key, final Object... values ) {
        if ( operator.getArity() == 1 && values[0] == null ) {
            return Restrictions.sqlRestriction( "0=1" );
        }
        return ( Criterion ) super.compile( operator, key, values[0], values[1] );
    }

	@Override
	protected Criterion compileOperatorEqual(final String key, final Object... values) {
		final Object value = values[0];
		// single value
		if (!(value instanceof Object[])) {
			return Restrictions.eq(key, value);
		}
		// array of values
		return Restrictions.in(key, (Object[])value);
	}

	@Override
	protected Criterion compileOperatorNotEqual(final String key, final Object... values) {
		final Object value = values[0];
		// single value
		if (!(value instanceof Object[])) {
			return Restrictions.ne(key, value);
		}
		// array of values
		return Restrictions.not(Restrictions.in(key, (Object[])value));
	}


    @Override
    protected Criterion compileOperatorEmpty( final String key, final Object... values ) {
        return Restrictions.isNull( key );
    }

    @Override
    protected Criterion compileOperatorNotEmpty( final String key, final Object... values ) {
        return Restrictions.isNotNull( key );
    }

    @Override
    protected Criterion compileOperatorLike( final String key, final Object... values ) {
        return Restrictions.ilike( key, values[0].toString(), FILTER_CRITERION_LIKE_MATCH_MODE );

    }

    @Override
    protected Criterion compileOperatorNotLike( final String key, final Object... values ) {
        return Restrictions.not( Restrictions.ilike( key, values[0].toString(), FILTER_CRITERION_LIKE_MATCH_MODE ) );
    }

    @Override
    protected Criterion compileOperatorStartWith( final String key, final Object... values ) {
        return Restrictions.ilike( key, values[0].toString(), MatchMode.START );

    }

    @Override
    protected Criterion compileOperatorEndWith( final String key, final Object... values ) {
        return Restrictions.ilike( key, values[0].toString(), MatchMode.END );

    }

    @Override
    protected Criterion compileOperatorGreaterThan( final String key, final Object... values ) {
        return Restrictions.gt( key, values[0] );
    }

    @Override
    protected Criterion compileOperatorGreaterEqual( final String key, final Object... values ) {
        return Restrictions.ge( key, values[0] );
    }

    @Override
    protected Criterion compileOperatorLesserThan( final String key, final Object... values ) {
        return Restrictions.lt( key, values[0] );
    }

    @Override
    protected Criterion compileOperatorLesserEqual( final String key, final Object... values ) {
        return Restrictions.le( key, values[0] );
    }

    @Override
    protected Criterion compileOperatorBetween( final String key, final Object... values ) {
        return Restrictions.between( key, values[0], values[1] );
    }
}
