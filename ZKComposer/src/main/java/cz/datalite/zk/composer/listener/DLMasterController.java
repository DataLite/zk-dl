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
    public void addChildController(DLDetailController detailController);


    /**
     * Removes detail controller. It will no longer recieve master events.
     *
     * @param detailController detail controller
     */
    public void removeChildController(DLDetailController detailController);

    /**
     * Detail will notify master with change.
     *
     * Usually master should send events to all listener. If the model extends DLMainModel, it should
     * call DLMainModel.clearRefreshFlags to clare all flags.
     *
     * @param model new model from the child
     */
    public void onDetailChanged(T model);

    /**
     * Returns master model.
     * 
     * Detail can ask for master model anytime, it should be the same value as sent to detail via onMasterChange.
     *
     * @return model model of master controller
     */
    public T getMasterControllerModel();

    /**
     * Send event to main associated component of master controller (aka self).
     * In DLComposer it is easy to catch this event with @ZkEvent annotation.
     *
     * @param eventName name of the event.
     * @param data any associated data
     */
    public void postEvent(String eventName, Object data);
}
