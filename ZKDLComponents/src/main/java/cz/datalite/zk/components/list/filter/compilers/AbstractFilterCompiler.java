package cz.datalite.zk.components.list.filter.compilers;

import cz.datalite.zk.components.list.enums.DLFilterOperator;

/**
 * This class is a general compiler where is defined huge switch according to
 * the operator. All needed methods are defined as abstract and have to be
 * implemented in child classes. This compiler needn't be used. It can be
 * skipped using new redefinition of FilterCompiler interface.
 * @author Karel Čemus <cemus@datalite.cz>
 */
abstract public class AbstractFilterCompiler implements FilterCompiler {

    /**
     * This implementation of this method defines a huge switch which calls the
     * specific method according to the filter operator. These methods have to
     * be redefined in the children. 
     * @param operator filter operator which is evaluated
     * @param key name of the property in the entity
     * @param values values of the filter settings
     * @return result of evaluation
     */
    public Object compile( final DLFilterOperator operator, final String key, final Object... values ) {
        switch ( operator ) {
            case BETWEEN:
                return compileOperatorBetween( key, values );
            case EMPTY:
                return compileOperatorEmpty( key, values );
            case END_WITH:
                return compileOperatorEndWith( key, values );
            case EQUAL:
                return compileOperatorEqual( key, values );
            case GREATER_EQUAL:
                return compileOperatorGreaterEqual( key, values );
            case GREATER_THAN:
                return compileOperatorGreaterThan( key, values );
            case LESSER_EQUAL:
                return compileOperatorLesserEqual( key, values );
            case LESSER_THAN:
                return compileOperatorLesserThan( key, values );
            case LIKE:
                return compileOperatorLike( key, values );
            case NOT_EMPTY:
                return compileOperatorNotEmpty( key, values );
            case NOT_EQUAL:
                return compileOperatorNotEqual( key, values );
            case NOT_LIKE:
                return compileOperatorNotLike( key, values );
            case START_WITH:
                return compileOperatorStartWith( key, values );

            default:
                throw new UnsupportedOperationException( "Error occured during filter compilation. Unknown operator was found." );
        }
    }

    protected abstract Object compileOperatorEqual( final String key, final Object... values );

    protected abstract Object compileOperatorNotEqual( final String key, final Object... values );

    protected abstract Object compileOperatorEmpty( final String key, final Object... values );

    protected abstract Object compileOperatorNotEmpty( final String key, final Object... values );

    protected abstract Object compileOperatorLike( final String key, final Object... values );

    protected abstract Object compileOperatorNotLike( final String key, final Object... values );

    protected abstract Object compileOperatorStartWith( final String key, final Object... values );

    protected abstract Object compileOperatorEndWith( final String key, final Object... values );

    protected abstract Object compileOperatorGreaterThan( final String key, final Object... values );

    protected abstract Object compileOperatorGreaterEqual( final String key, final Object... values );

    protected abstract Object compileOperatorLesserThan( final String key, final Object... values );

    protected abstract Object compileOperatorLesserEqual( final String key, final Object... values );

    protected abstract Object compileOperatorBetween( final String key, final Object... values );
}
