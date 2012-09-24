package cz.datalite.zk.components.combo;

import cz.datalite.dao.DLSearch;
import cz.datalite.dao.DLSort;
import cz.datalite.dao.DLSortType;
import java.util.List;
import java.util.Map;
import org.hibernate.criterion.Restrictions;

/**
 * Implementation of the ComboboxController which is using Hibernate Criterions
 * @param <T> main entity in the combobox
 * @author Karel Cemus
 */
public abstract class DLComboboxCriteriaController<T> extends DLComboboxGeneralController<T> {

    @Override
    protected List<T> loadData( final String orderBy, final Map<String, Object> filters ) {
        return loadData( getSearchObject( orderBy, filters ) );
    }

    /**
     * Load data from the datastore corresponding conditions in the filter object
     * @param search There are informations about filter etc.
     * @return loaded data
     */
    protected abstract List<T> loadData( final DLSearch<T> search );

    /**
     * Fills the DLSearch object with filter informations setted in the map.
     * @param orderBy column to sorting
     * @param filters map with pairs column - value to application EQUAL filter
     * @return prepared search object
     */
    protected DLSearch<T> getSearchObject( final String orderBy, final Map<String, Object> filters ) {
        final DLSearch<T> search = new DLSearch<T>();
        for ( String key : filters.keySet() ) {
            search.addAlias( key );
            search.addFilterCriterion( Restrictions.eq( search.getAliasForPath( key ), filters.get( key ) ) );
        }

        if ( orderBy != null )
            search.addSort( new DLSort( orderBy, DLSortType.ASCENDING ) );

        return search;
    }
}
