package cz.datalite.zk.components.list.enums;

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
 * @author Karel Čemus <cemus@datalite.cz>
 */
public enum DLFilterOperator {

    EQUAL( "Rovná se", 1, "eq" ),
    NOT_EQUAL( "Nerovná se", 1, "neq" ),
    LIKE( "Obsahuje", 1, "like" ),
    NOT_LIKE( "Neobsahuje", 1, "nlike" ),
    START_WITH( "Začíná na", 1, "start" ),
    END_WITH( "Končí na", 1, "end" ),
    GREATER_THAN( "Větší než", 1, "gt" ),
    GREATER_EQUAL( "Větší nebo rovno", 1, "ge" ),
    LESSER_THAN( "Menší než", 1, "lt" ),
    LESSER_EQUAL( "Menší nebo rovno", 1, "le" ),
    EMPTY( "Prázdný", 0, "empty" ),
    NOT_EMPTY( "Neprázdný", 0, "nempty" ),
    BETWEEN( "Je mezi", 2, "between" );
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
        throw new UnsupportedOperationException( "Unknown filter operator." );
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
