package cz.datalite.zk.components.list.filter.components;

import org.zkoss.zul.Longbox;

/**
 * Standard implementation of the filter component for longboxes. There are
 * no validation additions, each of restrictions comes from the component.
 * @author Karel Cemus
 */
public class LongFilterComponent extends AbstractFilterComponent<Longbox> {

    public LongFilterComponent() {
        super( new Longbox() );
    }

    public FilterComponent cloneComponent() {
        return new LongFilterComponent();
    }
}
