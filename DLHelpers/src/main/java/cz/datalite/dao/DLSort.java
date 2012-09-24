package cz.datalite.dao;

/**
 * This class is only containter for data about sort. It contains
 * column name (using dot notation) and sortType ({@link cz.datalite.dao.DLSortType})
 * @author Karel Cemus
 */
public class DLSort {

    /** column name */
    private final String column;
    /** sortType */
    private DLSortType sortType;

    /**
     * Create Sort container
     * @param column column name - full path
     * @param sortType sortType: 1: ascending; 0: no sort;  -1: descending
     */
    public DLSort( final String column, final DLSortType sortType ) {
        assert column != null && column.length() > 0 : "Invalid value for column name.";
        assert sortType != null : "Invalid value for sortType.";
        this.column = column;
        this.sortType = sortType;
    }

    /**
     * Create sort container from other container (copy)
     * @param sort sort to copy
     */
    public DLSort( final DLSort sort ) {
        assert sort != null : "Invalid value for new Sort. Sort to copy is missing.";
        this.column = sort.column;
        this.sortType = sort.sortType;
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
        return "Sort by " + column + " type " + sortType;
    }

    public String getColumn() {
        return column;
    }

    public DLSortType getSortType() {
        return sortType;
    }
}
