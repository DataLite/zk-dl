package cz.datalite.zk.components.list.filter.compilers;

import cz.datalite.zk.components.list.enums.DLFilterOperator;

/**
 * This interface defines methods which are used to compilation of the operators
 * to the other object or evaluation. This compiler can be defined for each
 * column in the ZUL file. For usage is reccomended to use the AbstractFilterCompiler
 * where is defined huge switch for all operators but it is not demanded. 
 * 
 * These methods are implemented with more specified return type. This
 * specification is according to the substitution principle of Barbara Liskov. 
 *
 *
 * @author Karel Cemus
 */
public interface FilterCompiler {

    /**
     * This method is called to compile operator to the specific object.
     * The required implementation is depended on the exact situation. There
     * are few very different immplementations of this method so be careful in
     * selecting of implementation.
     * 
     * @param operator filter operator which is compiled or evaluated
     * @param key name of the column in the entity ( and database but it needn't be same )
     * @param values there are few values, somewhere there are only filter
     *          parameters, elsewhere there also values to be evaluated.
     * @return result of compilation or evaluation
     */
    Object compile( final DLFilterOperator operator, final String key, final Object... values );

    /**
     * Validate the value for filter - return true, if the value is correct type and can be used
     * for filtering purpose.
     *
     * @param value the value for filter
     * @return true if the value is correct.
     */
    boolean validateValue(Object value);
}
