package cz.datalite.zk.components.list.controller;

import cz.datalite.zk.components.list.filter.QuickFilterModel;

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
     * Notifies component that model changed
     */
    void fireChanges();

    /**
     * Returns binding model
     * @return binding model
     */
    QuickFilterModel getBindingModel();
}
