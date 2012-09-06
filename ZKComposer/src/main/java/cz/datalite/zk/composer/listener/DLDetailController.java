package cz.datalite.zk.composer.listener;

import cz.datalite.zk.composer.DLComposer;

/**
 * Master/detail controller relationship, this is detail.
 * Master will notify this object with onMasterChanged() after model changes.
 *
 * @author Jiri Bubnik
 * @see DLMasterController
 * @see DLComposer
 */
public interface DLDetailController<T extends DLMainModel> {

    /**
     * Model is changed.
     *
     * Detail should always check if the change is relevant, while master may
     * notify all details on any change. (e.g. if (model.property <> savedProperty) { ... } ).
     *
     *
     * @param model new model with data.
     */
    void onMasterChanged(T model);

}
