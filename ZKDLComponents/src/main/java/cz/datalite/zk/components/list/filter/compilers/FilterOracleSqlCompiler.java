package cz.datalite.zk.components.list.filter.compilers;

import cz.datalite.zk.components.list.enums.DLFilterOperator;

import java.util.Map;

/**
 * Special compiler for direct Oracle specific SQL.
 *
 * Key contains sql column name (e.g. table.COLUMN_NAME)
 * First argument must be String and contain column name (e.g. myNiceColumn)
 * Second argument must be Map<String, Object> - additional parameters indexed by column name will be added here
 * Third and more arguments for actual operands.
 *
 * @author Jiri Bubnik
 */
public class FilterOracleSqlCompiler  implements  FilterCompiler {

    public static final FilterOracleSqlCompiler INSTANCE = new FilterOracleSqlCompiler();

    public Object compile( final DLFilterOperator operator, final String key, final Object... values )
    {
        if (values.length < 2 || !(values[0] instanceof String) || !(values[1] instanceof Map))
            throw new IllegalArgumentException("FilterSqlCompiler has to be called with first two values param as : columnName (String) and bindParams (Map<String, Object>)");

        final String column = (String) values[0];
        final Map<String, Object> bindParams = (Map<String, Object>) values[1];

        for (int i=1; i<=operator.getArity(); i++)
        {
            if (values.length < i+2)
                throw new IllegalArgumentException("FilterSqlCompiler - operator " + operator.getLabel() +
                        " arity is " + operator.getArity() + " but only " + (values.length - 2) + " actual parameters found.");
        }

        // indexes of arguments
        final int arg1 = 2;
        final int arg2 = 3;
        final String col1 = column + "1";
        final String col2 = column + "2";

        switch ( operator ) {
            case BETWEEN:
                bindParams.put(col1, values[arg1]);
                bindParams.put(col2, values[arg2]);
                return key + " between :" + col1 + " and :" + col2;
            case EMPTY:
                return key + " is null";
            case END_WITH:
                bindParams.put(col1, values[arg1] + "%");
                return key + " like :" + col1;
            case EQUAL:
                bindParams.put(col1, values[arg1]);
                return key + " = :" + col1;
            case GREATER_EQUAL:
                bindParams.put(col1, values[arg1]);
                return key + " >= :" + col1;
            case GREATER_THAN:
                bindParams.put(col1, values[arg1]);
                return key + " > :" + col1;
            case LESSER_EQUAL:
                bindParams.put(col1, values[arg1]);
                return key + " <= :" + col1;
            case LESSER_THAN:
                bindParams.put(col1, values[arg1]);
                return key + " < :" + col1;
            case LIKE:
                bindParams.put(col1, "%" + values[arg1] + "%");
                return key + " like :" + col1;
            case NOT_EMPTY:
                return key + " is not null";
            case NOT_EQUAL:
                bindParams.put(col1, values[arg1]);
                return key + " != :" + col1;
            case NOT_LIKE:
                bindParams.put(col1, "%" + values[arg1] + "%");
                return key + " not like :" + col1;
            case START_WITH:
                bindParams.put(col1, "%" + values[arg1]);
                return key + " like :" + col1;

            default:
                throw new UnsupportedOperationException( "Error occured during filter compilation. Unknown operator was found." );
        }


    }

    /**
     * This compiler works only with String values.
     *
     * @param value the value for filter
     * @return true if value is null or String.
     */
    public boolean validateValue(Object value) {
        if (value == null)
            return true;
        if (value instanceof String)
            return true;
        else
            return false;
    }

}