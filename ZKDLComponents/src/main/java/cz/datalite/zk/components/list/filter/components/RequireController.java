package cz.datalite.zk.components.list.filter.components;

import cz.datalite.zk.components.list.controller.DLListboxExtController;

/**
 *
 * @author Karel Cemus
 */
public interface RequireController<T> {

    void setController( final DLListboxExtController<T> controller );
}
