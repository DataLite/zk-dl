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
public interface DLMasterController {

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
     * @param model předávaný dítětem
     */
    public void onDetailChanged(Object model);

    /**
     * Returns master model.
     * 
     * Detail can ask for master model anytime, it should be the same value as sent to detail via onMasterChange.
     *
     * @return model model of master controller
     */
    public Object getMasterControllerModel();
}
