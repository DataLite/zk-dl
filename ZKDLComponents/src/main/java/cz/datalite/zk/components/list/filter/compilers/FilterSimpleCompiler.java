package cz.datalite.zk.components.list.filter.compilers;

import cz.datalite.zk.components.list.enums.DLFilterOperator;
import org.apache.commons.collections.comparators.ComparableComparator;

/**
 * This class implements simple compiler which evaluates expressions just in time
 * and place whithout another compilation. These methods returns if the
 * expression is true or false. For extension the set of operators have to be
 * extended compile method here on in its parent. 
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class FilterSimpleCompiler extends AbstractFilterCompiler {

    public static final FilterSimpleCompiler INSTANCE = new FilterSimpleCompiler();

    /**
     * Evaluation according operator. First value is first operand, other operands are
     * optional according to operand arity
     * @param values operands
     * @return result of the evaluation
     */
    @Override
    public Boolean compile( final DLFilterOperator operator, final String key, final Object... values ) {
        return ( Boolean ) super.compile( operator, key, values );
    }

    protected Boolean compileOperatorEqual( final String key, final Object... values ) {
        return ComparableComparator.getInstance().compare( values[0], values[1] ) == 0;
    }

    protected Boolean compileOperatorNotEqual( final String key, final Object... values ) {
        return ComparableComparator.getInstance().compare( values[0], values[1] ) != 0;
    }

    protected Boolean compileOperatorEmpty( final String key, final Object... values ) {
        return values[0] == null || values[0].toString().length() == 0;
    }

    protected Boolean compileOperatorNotEmpty( final String key, final Object... values ) {
        return values[0] != null && values[0].toString().length() > 0;
    }

    protected Boolean compileOperatorLike( final String key, final Object... values ) {
        return values[0].toString().toLowerCase().contains( values[1].toString().toLowerCase() );
    }

    protected Boolean compileOperatorNotLike( final String key, final Object... values ) {
        return !values[0].toString().toLowerCase().contains( values[1].toString().toLowerCase() );
    }

    protected Boolean compileOperatorStartWith( final String key, final Object... values ) {
        return values[0].toString().toLowerCase().startsWith( values[1].toString().toLowerCase() );
    }

    protected Boolean compileOperatorEndWith( final String key, final Object... values ) {
        return values[0].toString().toLowerCase().endsWith( values[1].toString().toLowerCase() );

    }

    protected Boolean compileOperatorGreaterThan( final String key, final Object... values ) {
        return ComparableComparator.getInstance().compare( values[0], values[1] ) > 0;
    }

    protected Boolean compileOperatorGreaterEqual( final String key, final Object... values ) {
        return ComparableComparator.getInstance().compare( values[0], values[1] ) >= 0;
    }

    protected Boolean compileOperatorLesserThan( final String key, final Object... values ) {
        return ComparableComparator.getInstance().compare( values[0], values[1] ) < 0;
    }

    protected Boolean compileOperatorLesserEqual( final String key, final Object... values ) {
        return ComparableComparator.getInstance().compare( values[0], values[1] ) <= 0;
    }

    protected Boolean compileOperatorBetween( final String key, final Object... values ) {
        return ComparableComparator.getInstance().compare( values[0], values[1] ) >= 0
                && ComparableComparator.getInstance().compare( values[0], values[2] ) <= 0;
    }
}
