package cz.datalite.zk.components.list.controller;

import cz.datalite.zk.components.list.filter.EasyFilterModel;

/**
 * This controller manage easyFilter, which is used to filter model using
 * data which are setted throw data binding.
 * @author Karel Čemus <cemus@datalite.cz>
 */
public interface DLEasyFilterController {

    /**
     * Reaction on filter event
     */
    void onEasyFilter();

    /**
     * Reaction on clear filter event.
     *
     * @param refresh true means refresh listbox data, false is only clear filter values
     */
    public void onClearEasyFilter(boolean refresh);


    /**
     * Returns binding model - values which shoudl be in components
     * @return binding model.
     */
    EasyFilterModel getBindingModel();

    /**
     * Notifies component that model changed
     */
    void fireChanges();
}
