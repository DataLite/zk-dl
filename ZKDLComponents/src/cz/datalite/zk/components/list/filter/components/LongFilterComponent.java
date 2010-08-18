package cz.datalite.zk.components.list.filter.components;

import org.zkoss.zul.Longbox;

/**
 * Standard implementation of the filter component for longboxes. There are
 * no validation additions, each of restrictions comes from the component.
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class LongFilterComponent extends AbstractFilterComponent<Longbox> {

    public LongFilterComponent() {
        super( new Longbox() );
    }
}
