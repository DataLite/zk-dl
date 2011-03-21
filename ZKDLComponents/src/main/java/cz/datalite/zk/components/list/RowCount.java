package cz.datalite.zk.components.list;

import cz.datalite.zk.components.list.filter.NormalFilterUnitModel;

import java.util.List;

/**
 * Count number of rows. For large dataset rowcount is loaded on user query in paging component.
 *
 * This is not mandatory interface, because for large database it can be really slow.
 *
 * @author Jiri Bubnik
 */
public interface RowCount
{
    int rowCount( final List<NormalFilterUnitModel> filter ) ;
}
