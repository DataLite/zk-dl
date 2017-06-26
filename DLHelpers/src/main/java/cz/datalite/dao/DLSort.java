package cz.datalite.dao;

/**
 * This class is only containter for data about sort. It contains
 * column name (using dot notation) and sortType ({@link cz.datalite.dao.DLSortType})
 * @author Karel Cemus
 * @author Jiri Bubnik
 */
public class DLSort {

    /** column name */
    private final String column;
    /** sortType */
    private DLSortType sortType;
    /** custom SQL formula (add to the query as is) */
    private final String sqlFormula;

    /**
     * Create Sort container
     * @param column column name - full path
     * @param sortType sortType: 1: ascending; 0: no sort;  -1: descending
     * @deprecated use DLSort.byColumn(column, sortType) instead
     */
    @Deprecated
    public DLSort( final String column, final DLSortType sortType ) {
        assert column != null && column.length() > 0 : "Invalid value for column name.";
        assert sortType != null : "Invalid value for sortType.";
        this.column = column;
        this.sortType = sortType;
        this.sqlFormula = null;
    }

    /**
     * Create sort container from other container (copy)
     * @param sort sort to copy
     */
    public DLSort( final DLSort sort ) {
        assert sort != null : "Invalid value for new Sort. Sort to copy is missing.";
        this.column = sort.column;
        this.sortType = sort.sortType;
        this.sqlFormula = sort.sqlFormula;
    }

    /**
     * Create sort container for custom sql formula.
     *
     * @param  sqlFormula an SQL formula that will be appended to the resulting SQL query.
     *                    e.g. "(a + b) desc"
     */
    private DLSort( final String sqlFormula ) {
        assert sqlFormula != null : "Invalid value for sqlFormula. sqlFormula is missing.";
        this.sqlFormula = sqlFormula;
        this.column = null;
    }

    /**
     * Setsort type
     * @param sortType new sortType
     */
    public void setSortType( final DLSortType sortType ) {
        assert sortType != null : "Invalid value for sortType.";
        this.sortType = sortType;
    }

    @Override
    public String toString() {
        if (column != null) {
            return "Sort by " + column + " type " + sortType;
        } else {
            return "sqlFormula sort " + sqlFormula;
        }
    }

    public String getColumn() {
        return column;
    }

    public DLSortType getSortType() {
        return sortType;
    }

    public String getSqlFormula() {
        return sqlFormula;
    }

    /**
     * Create Sort container.
     *
     * @param column column name - full path
     * @param sortType sortType: 1: ascending; 0: no sort;  -1: descending
     */
    public static DLSort byColumn(final String column, final DLSortType sortType) {
        return new DLSort(column, sortType);
    }

    /**
     * Create sort container for custom sql formula.
     *
     * @param  sqlFormula an SQL formula that will be appended to the resulting SQL query.
     *                    e.g. "(a + b) desc"
     */
    public static DLSort bySqlFormula(final String sqlFormula ) {
       return new DLSort(sqlFormula);
    }

}
