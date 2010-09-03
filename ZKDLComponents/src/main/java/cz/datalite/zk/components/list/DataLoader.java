package cz.datalite.zk.components.list;

import cz.datalite.dao.DLResponse;
import cz.datalite.dao.DLSort;
import cz.datalite.zk.components.list.filter.NormalFilterUnitModel;
import java.util.List;

/**
 *
 * @author Karel Čemus <cemus@datalite.cz>
 */
public interface DataLoader {

    /**
     * Load distinct values for coresponding row
     * @param unitModel row model
     * @return distinct hodnot distinct
     */
    DLResponse<String> loadData( final List<NormalFilterUnitModel> filter, final int firstRow, final int rowCount, final List<DLSort> sorts ) ;
}
