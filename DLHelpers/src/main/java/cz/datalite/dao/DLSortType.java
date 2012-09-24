package cz.datalite.dao;

/**
 * Enumeration defines sortTypes like ascending, descending and natural
 * @author Karel Cemus
 */
public enum DLSortType {

    ASCENDING( 1, "ascending" ),
    NATURAL( 0, "natural" ),
    DESCENDING( -1, "descending" );
    protected int value;
    protected String stringValue;

    private DLSortType( final int value, final String stringValue ) {
        this.value = value;
        this.stringValue = stringValue;
    }

    /**
     * Returns sortType integer value used in the code (application-friendly)
     * @return sortType int definition
     */
    public int getValue() {
        return value;
    }

    /**
     * Returns sortType string value used in ZK framework (zk-friendly)
     * @return sortType string definition
     */
    public String getStringValue() {
        return stringValue;
    }

    /**
     * Converts string value to enumeration
     * @param stringValue value used in ZK framework
     * @return coresponding enum
     */
    public static DLSortType getByStringValue( final String stringValue ) {
        if ( "ascending".equals( stringValue ) ) {
            return ASCENDING;
        } else if ( "descending".equals( stringValue ) ) {
            return DESCENDING;
        } else if ( "natural".equals( stringValue ) ) {
            return NATURAL;
        } else {
            return null;
        }
    }

    /**
     * Returns next enum - like switch
     * @return switched enum
     */
    public DLSortType next() {
        switch ( this ) {
            case DESCENDING:
                return NATURAL;
            case ASCENDING:
                return DESCENDING;
            default:
                return ASCENDING;
        }
    }

    /**
     * Returns next enum for ZK - difference is in Natural type.
     * ZK sorting doesn't support it.
     * @return switched enum
     */
    public DLSortType nextZK() {
        if ( ASCENDING.equals( this ) ) {
            return DESCENDING;
        } else {
            return ASCENDING;
        }
    }
}
