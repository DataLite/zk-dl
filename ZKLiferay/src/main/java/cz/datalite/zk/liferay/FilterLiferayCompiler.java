package cz.datalite.zk.liferay;

import com.liferay.portal.kernel.dao.orm.Criterion;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import cz.datalite.zk.components.list.enums.DLFilterOperator;
import cz.datalite.zk.components.list.filter.compilers.AbstractFilterCompiler;

/**
 * This compiler class serves to compile filter operators to Hibernate
 * Critera which can be executed in database. There are defined methods
 * which knows how to compile each defined operator.
 *
 * To add additional one there have to be defined new method for this operator
 * and extended the compile method with additional case here or in the parent
 * class.
 *
 * @author Karel ÄŚemus
 */
public class FilterLiferayCompiler extends AbstractFilterCompiler {

    /**
     * Converts condition to the hiberanate criterion according to the operand
     * @param key column name
     * @param values operands
     * @return compiled criterion
     */
    @Override
    public Criterion compile( final DLFilterOperator operator, final String key, final Object... values ) {
        if ( operator.getArity() == 1 && values[0] == null ) {
            return RestrictionsFactoryUtil.conjunction(); // FIXME
        }
        return ( Criterion ) super.compile( operator, key, values[0], values[1] );
    }

	@Override
	protected Criterion compileOperatorEqual(final String key, final Object... values) {
		final Object value = values[0];
		// single value
		if (!(value instanceof Object[])) {
			return RestrictionsFactoryUtil.eq(key, value);
		}
		// array of values
		return RestrictionsFactoryUtil.in(key, (Object[])value);
	}

	@Override
	protected Criterion compileOperatorNotEqual(final String key, final Object... values) {
		final Object value = values[0];
		// single value
		if (!(value instanceof Object[])) {
			return RestrictionsFactoryUtil.ne(key, value);
		}
		// array of values
		return RestrictionsFactoryUtil.not(RestrictionsFactoryUtil.in(key, (Object[])value));
	}


    @Override
    protected Criterion compileOperatorEmpty( final String key, final Object... values ) {
        return RestrictionsFactoryUtil.isNull( key );
    }

    @Override
    protected Criterion compileOperatorNotEmpty( final String key, final Object... values ) {
        return RestrictionsFactoryUtil.isNotNull( key );
    }

    @Override
    protected Criterion compileOperatorLike( final String key, final Object... values ) {
        return RestrictionsFactoryUtil.ilike( key, values[0].toString() ); // FIXME

    }

    @Override
    protected Criterion compileOperatorNotLike( final String key, final Object... values ) {
        return RestrictionsFactoryUtil.not( RestrictionsFactoryUtil.ilike( key, values[0].toString() ) ); // FIXME
    }

    @Override
    protected Criterion compileOperatorStartWith( final String key, final Object... values ) {
        return RestrictionsFactoryUtil.ilike( key, values[0].toString() ); // FIXME

    }

    @Override
    protected Criterion compileOperatorEndWith( final String key, final Object... values ) {
        return RestrictionsFactoryUtil.ilike( key, values[0].toString() ); // FIXME

    }

    @Override
    protected Criterion compileOperatorGreaterThan( final String key, final Object... values ) {
        return RestrictionsFactoryUtil.gt( key, values[0] );
    }

    @Override
    protected Criterion compileOperatorGreaterEqual( final String key, final Object... values ) {
        return RestrictionsFactoryUtil.ge( key, values[0] );
    }

    @Override
    protected Criterion compileOperatorLesserThan( final String key, final Object... values ) {
        return RestrictionsFactoryUtil.lt( key, values[0] );
    }

    @Override
    protected Criterion compileOperatorLesserEqual( final String key, final Object... values ) {
        return RestrictionsFactoryUtil.le( key, values[0] );
    }

    @Override
    protected Criterion compileOperatorBetween( final String key, final Object... values ) {
        return RestrictionsFactoryUtil.between( key, values[0], values[1] );
    }
}

