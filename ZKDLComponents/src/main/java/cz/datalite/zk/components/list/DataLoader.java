package cz.datalite.zk.components.list;

import cz.datalite.dao.DLResponse;
import cz.datalite.dao.DLSort;
import cz.datalite.zk.components.list.filter.NormalFilterUnitModel;

import java.util.List;

/**
 * Load distinct values from controller (usefull especially for filters).
 * This is not mandatory interface, because for large data it can be really slow.
 *
 * @author Karel Cemus
 */
public interface DataLoader {

    /**
     * Load distinct values for coresponding row
     * @param filter row model
     * @return distinct hodnot distinct
     */
    DLResponse<String> loadData( final List<NormalFilterUnitModel> filter, final int firstRow, final int rowCount, final List<DLSort> sorts ) ;
}
