package cz.datalite.zk.composer.listener;

import cz.datalite.zk.composer.DLComposer;

/**
 * Master/detail controller relationship, this is master.
 * It solves communication between multiple controllers on one page.
 *
 * @author Jiri Bubnik
 * @see DLDetailController
 * @see DLComposer
 */
public interface DLMasterController<T extends DLMainModel> {

    /**
     * Adds child controller. Each change will be propagated to this controller (unless it's removed)
     *
     * @param detailController detail controller
     */
    void addChildController(DLDetailController detailController);


    /**
     * Removes detail controller. It will no longer recieve master events.
     *
     * @param detailController detail controller
     */
    void removeChildController(DLDetailController detailController);

    /**
     * Detail will notify master with change.
     *
     * Usually master should send events to all listener. If the model extends DLMainModel, it should
     * call DLMainModel.clearRefreshFlags to clare all flags.
     * 
     * Default implementation of master listener automatically calls all
     * registered children (calls onMasterChanged() on each child).
     *
     * @param model new model from the child
     */
    void onDetailChanged(T model);

    /**
     * <p>Convenient method for master to hold model. Implementation will
     * usually override this method to customize model.</p>
     *
     * <p>Child may call this method anytime to get master controller.</p>
     *
     * Returns master controller if is set (used by detail). Typically master
     * controller is set in doBeforeCompose from
     * execution.getAttribute("masterController") automatically.
     *
     * @return model Default implementation will store model from childe and
     * resend new model to all children. Returns masterController if is defined,
     * null otherwise.
     */
    T getMasterControllerModel();

    /**
     * Send event to main associated component of master controller (aka self).
     * In DLComposer it is easy to catch this event with @ZkEvent annotation.
     *
     * @param eventName name of the event.
     * @param data any associated data
     */
    void postEvent(String eventName, Object data);
}
