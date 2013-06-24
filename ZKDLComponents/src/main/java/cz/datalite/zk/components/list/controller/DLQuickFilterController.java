package cz.datalite.zk.components.list.controller;

import cz.datalite.zk.components.list.filter.QuickFilterModel;
import cz.datalite.zk.components.list.view.DLQuickFilter;

/**
 * Interface for the quickfilter component controller.
 * @author Karel Cemus
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

    /**
     * Returns the quickfilter component.
     * @return the quickfilter component.
     */
    DLQuickFilter getQuickFilter();
}
