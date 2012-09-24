package cz.datalite.zk.components.list.model;

import cz.datalite.zk.components.list.filter.NormalFilterModel;
import cz.datalite.zk.components.paging.DLPagingModel;

/**
 * Master component model - groups all models used in the components.
 * @author Karel Cemus
 */
public class DLMasterModel {

    protected DLPagingModel pagingModel = new DLPagingModel();
    protected DLColumnModel columnModel = new DLColumnModel( this );
    protected DLFilterModel filterModel = new DLFilterModel();

    public DLColumnModel getColumnModel() {
        return columnModel;
    }

    public DLFilterModel getFilterModel() {
        return filterModel;
    }

    public DLPagingModel getPagingModel() {
        return pagingModel;
    }

    public void clear() {
        columnModel.clear();
        pagingModel.clear();
        filterModel.clear();
    }

    public NormalFilterModel getFilterModelInNormal() {
        return filterModel.toNormal( columnModel );
}
}
