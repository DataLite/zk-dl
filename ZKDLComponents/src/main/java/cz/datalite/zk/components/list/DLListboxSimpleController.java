package cz.datalite.zk.components.list;

import cz.datalite.dao.DLResponse;
import cz.datalite.zk.components.list.filter.NormalFilterUnitModel;
import java.util.List;

/**
 * Simple implementation of the listbox controller. This implementations can
 * operate over the list without database. Useful tool for this controller is
 * Filter which can sort and filter data.
 * @param <T> 
 * @author Karel Cemus
 */
public abstract class DLListboxSimpleController<T> extends DLListboxGeneralController<T> {

    /**
     * {@inheritDoc}
     */
    public DLListboxSimpleController( ) {
        this( null );
    }

    /**
     * {@inheritDoc}
     */
    public DLListboxSimpleController( final String identifier ) {
        this( identifier, null );
    }

    /**
     * {@inheritDoc}
     */
    public DLListboxSimpleController( final String identifier, final Class<T> clazz ) {
        super( identifier, clazz );
    }

    @Override
    protected DLResponse<String> loadDistinctColumnValues( final String column, final List<NormalFilterUnitModel> filter, final int firstRow, final int rowCount, final List<cz.datalite.dao.DLSort> sorts ) {
        throw new UnsupportedOperationException( "Distinct column values are not implemented." );
    }
}
