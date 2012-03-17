/**
 * Copyright 26.2.11 (c) DataLite, spol. s r.o. All rights reserved.
 * Web: http://www.datalite.cz    Mail: info@datalite.cz
 */
package cz.datalite.zk.composer.listener;

/**
 * Interface for main model to send berween master and detail composers.
 */
public interface DLMainModel {

    /**
     * Main model can contain refresh flags for some parts of model to force update of detail composers
     * even if the model is not changed (e.g. only some property are changed after object update).
     *
     * These flags should be sent only ones to all listeners, because otherwise the listener will refresh
     * after all events. Master model will call this method to
     */
    public void clearRefreshFlags();

}
