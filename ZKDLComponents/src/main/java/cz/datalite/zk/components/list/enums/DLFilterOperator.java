package cz.datalite.zk.components.list.enums;

import org.zkoss.util.resource.Labels;

/**
 * Enumeration defines available filter operators, defines their name, shortcut
 * and arity. This class serves as a parameter in a few switches where are
 * these operators evaluated. It is in the compilers where are operators
 * evaluated or compiled to another objects with appropriate parameters.
 *
 * Shortcut is used to define the operator in the ZUL file, arity serves to decide
 * how many components have to be shown in the normal filter. Current maximum
 * arity is 2. For extension this value have to be redesigned NormalFilter form,
 * inner implementation of NormalFilterUnitModel and few calls, because their are
 * based on exact enumeration of desired values instead of eg. List.
 * @author Karel Cemus
 */
public enum DLFilterOperator {

    EQUAL( Labels.getLabel( "filter.operators.equals" ), 1, "eq" ),
    NOT_EQUAL( Labels.getLabel( "filter.operators.nequals" ), 1, "neq" ),
    LIKE( Labels.getLabel( "filter.operators.like" ), 1, "like" ),
    NOT_LIKE( Labels.getLabel( "filter.operators.nlike" ), 1, "nlike" ),
    START_WITH( Labels.getLabel( "filter.operators.starts" ), 1, "start" ),
    END_WITH( Labels.getLabel( "filter.operators.ends" ), 1, "end" ),
    GREATER_THAN( Labels.getLabel( "filter.operators.greaterThan" ), 1, "gt" ),
    GREATER_EQUAL( Labels.getLabel( "filter.operators.greaterEqual" ), 1, "ge" ),
    LESSER_THAN( Labels.getLabel( "filter.operators.lesserThan" ), 1, "lt" ),
    LESSER_EQUAL( Labels.getLabel( "filter.operators.lesserEqual" ), 1, "le" ),
    EMPTY( Labels.getLabel( "filter.operators.empty" ), 0, "empty" ),
    NOT_EMPTY( Labels.getLabel( "filter.operators.nempty" ), 0, "nempty" ),
    BETWEEN( Labels.getLabel( "filter.operators.between" ), 2, "between" );
    /** maximum of the supported operator arity */
    public static final int MAX_ARITY = 2;
    protected final String label;
    protected final int arity;
    protected final String shortName;

    /**
     * Create filter operator
     * @param label user-friendly operator
     * @param arity operator arity
     */
    private DLFilterOperator( final String label, final int arity, final String shortName ) {
        this.label = label;
        assert (arity <= MAX_ARITY);
        this.arity = arity;
        this.shortName = shortName;
    }

    /**
     * Convert shortcut to enumeration
     * @param key shortcut
     * @return enumeration
     */
    public static DLFilterOperator strToEnum( final String key ) {
        for ( DLFilterOperator op : values() ) {
            if ( op.shortName.equals( key ) ) {
                return op;
            }
        }
        throw new UnsupportedOperationException( "Unknown filter operator: " + key );
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return getLabel();
    }

    public int getArity() {
        return arity;
    }
}
