package cz.datalite.zk.components.list.controller;

import cz.datalite.zk.components.list.filter.QuickFilterModel;
import cz.datalite.zk.components.list.model.DLColumnUnitModel;
import java.util.List;

/**
 * Interface for the quickfilter component controller.
 * @author Karel Čemus <cemus@datalite.cz>
 */
public interface DLQuickFilterController {

    /**
     * Reacts on the event when quickFilter model changed and submited.
     */
    void onQuickFilter();

    /**
     * Validate quick filter with column model - if data type is correct.
     */
    boolean validateQuickFilter();

    /**
     * Notifies component that model changed
     */
    void fireChanges();

    /**
     * Returns binding model
     * @return binding model
     */
    QuickFilterModel getBindingModel();

    /**
     * Returns component's uuid
     * @return uuid
     */
    String getUuid();

    /** initializes model based on local configuration */
    void initModel( List<DLColumnUnitModel> columnModels );
}
