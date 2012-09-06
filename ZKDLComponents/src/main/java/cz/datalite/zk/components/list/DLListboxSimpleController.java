package cz.datalite.zk.components.list;

import cz.datalite.dao.DLResponse;
import cz.datalite.zk.components.list.filter.NormalFilterUnitModel;
import java.util.List;

/**
 * Simple implementation of the listbox controller. This implementations can
 * operate over the list without database. Useful tool for this controller is
 * Filter which can sort and filter data.
 * @param <T> 
 * @author Karel Čemus <cemus@datalite.cz>
 */
public abstract class DLListboxSimpleController<T> extends DLListboxGeneralController<T> {

    /**
     * Creates simple controller which can operate without database. This can
     * work with list.
     * @param identifier unique identifier to set data model to the session
     */
    public DLListboxSimpleController( final String identifier ) {
        this( identifier, null );
    }

    public DLListboxSimpleController( final String identifier, final Class<T> clazz ) {
        super( identifier, clazz );
    }

    @Override
    protected DLResponse<String> loadDistinctColumnValues( final String column, final List<NormalFilterUnitModel> filter, final int firstRow, final int rowCount, final List<cz.datalite.dao.DLSort> sorts ) {
        throw new UnsupportedOperationException( "Distinct column values are not implemented." );
    }
}
