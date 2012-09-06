package cz.datalite.zk.components.list.filter.components;

import cz.datalite.zk.components.list.controller.DLListboxExtController;

/**
 *
 * @author Karel Čemus <cemus@datalite.cz>
 */
public interface RequireController<T> {

    void setController( final DLListboxExtController<T> controller );
}
