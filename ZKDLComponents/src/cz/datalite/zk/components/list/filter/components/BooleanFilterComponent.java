package cz.datalite.zk.components.list.filter.components;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Checkbox;

/**
 * Standard implementation of the filter component for intboxes. There are
 * no validation additions, each of restrictions comes from the component.
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class BooleanFilterComponent implements FilterComponent {

    protected final Checkbox component;

    public BooleanFilterComponent() {
        component = new Checkbox();
    }

    public Component getComponent() {
        return component;
    }

    public Object getValue() {
        return component.isChecked();
    }

    public void setValue( final Object value ) {
        component.setChecked( value == null ? false : ( Boolean ) value );
    }

    public void addOnChangeEventListener( final EventListener listener ) {
        component.addEventListener( Events.ON_CHECK, listener );
    }

    public void validate() throws WrongValueException {
        // no validation required
    }
}
