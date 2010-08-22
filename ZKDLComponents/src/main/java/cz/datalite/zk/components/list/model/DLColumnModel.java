package cz.datalite.zk.components.list.model;

import cz.datalite.dao.DLSort;
import cz.datalite.dao.DLSortType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Master column model - groups column unit models and makes global changes.
 * This component also caches counts of the sorted and visible columns.
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class DLColumnModel {

    // maximal used index in the sorting
    protected int sortMaxIndex = 0;
    // all column model
    protected List<DLColumnUnitModel> columnModels = new LinkedList<DLColumnUnitModel>();
    // maximal used index for visible columns
    protected int orderMaxIndex = 0;

    public List<DLColumnUnitModel> getColumnModels() {
        return columnModels;
    }

    public Integer getSortMaxIndex() {
        return sortMaxIndex;
    }

    /**
     * Corresponds to ++sortMaxIndex
     * @return increased sortIndex
     */
    public Integer autoIncSortMaxIndex() {
        return ++sortMaxIndex;
    }

    /**
     * Corresponds to --sortMaxIndex
     * @return decreased sortIndex
     */
    public Integer autoDecSortMaxIndex() {
        return --sortMaxIndex;
    }

    public Integer getOrderMaxIndex() {
        return orderMaxIndex;
    }

    /**
     * Corresponds to ++orderMaxIndex
     * @return increased order index
     */
    public Integer autoIncOrder() {
        return ++orderMaxIndex;
    }

    /**
     * Corresponds to --orderMaxIndex
     * @return decreased order index
     */
    public Integer autoDecOrder() {
        return --orderMaxIndex;
    }

    /**
     * Removes all zk sorts
     */
    public void clearSortZk() {
        for ( DLColumnUnitModel unit : getColumnModels() ) {
            if ( unit.isSorted() && unit.isSortZk() ) {
                unit.setSortType( DLSortType.NATURAL );
        }
    }
    }

    /**
     * Removes all sorts
     */
    public void clearSortAll() {
        for ( DLColumnUnitModel unit : getColumnModels() ) {
            if ( unit.isSorted() ) {
                unit.setSortType( DLSortType.NATURAL );
        }
    }
    }

    /**
     * Returns model for the i column index
     * @param index column index
     * @return corresponding model
     */
    public DLColumnUnitModel getColumnModel( final int index ) {
        if ( index <= getColumnModels().size() ) {
            return getColumnModels().get( index - 1 );
        }
        final DLColumnUnitModel unit = new DLColumnUnitModel( this );
        columnModels.add( unit );
        return unit;
    }

    public List<DLSort> getSorts() {
        final List<DLSort> sorts = new ArrayList<DLSort>();
        for ( int i = 0; i < sortMaxIndex; i++ ) {
            sorts.add( null );
        }

        // writing db sorts
        for ( DLColumnUnitModel unit : getColumnModels() ) {
            if ( unit.isSorted() && unit.isDBSortable() ) {
                sorts.set( unit.getSortOrder() - 1, new DLSort( unit.getSortColumn(), unit.getSortType() ) );
        }
        }


        // removing null - was created after ZK sorts
        for ( final Iterator<DLSort> it = sorts.iterator(); it.hasNext(); ) {
            final DLSort dLSort = it.next();
            if ( dLSort == null ) {
                it.remove();
        }
        }
        return sorts;
    }

    public void clear() {
        sortMaxIndex = 0;
        orderMaxIndex = 0;
        columnModels.clear();
    }

    public DLColumnUnitModel getByName( final String name ) {
        for ( DLColumnUnitModel unit : columnModels ) {
            if ( unit.isColumn() && unit.getColumn().equals( name ) ) {
                return unit;
        }
        }
        return null;
    }
}
