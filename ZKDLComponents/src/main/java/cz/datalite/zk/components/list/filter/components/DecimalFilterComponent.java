package cz.datalite.zk.components.list.filter.components;

import org.zkoss.zul.Decimalbox;

/**
 * Standard implementation of the filter component for doubleboxes. There are
 * no validation additions, each of restrictions comes from the component.
 * @author Karel Cemus
 */
public class DecimalFilterComponent extends AbstractFilterComponent<Decimalbox> {

    public DecimalFilterComponent() {
        super( new Decimalbox() );
    }

    public FilterComponent cloneComponent() {
        return new DecimalFilterComponent();
    }
}
