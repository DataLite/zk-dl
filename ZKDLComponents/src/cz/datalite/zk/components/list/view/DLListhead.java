package cz.datalite.zk.components.list.view;

import org.zkoss.zul.Listhead;

/**
 * ZK component DLListheader is extended component from standard Listhead.
 *
 * Change default sizable to "true".
 *
 * @author Jiri Bubnik
 */
public class DLListhead extends Listhead {

    public DLListhead()
    {
        super();
        setSizable(true);
    }

}
