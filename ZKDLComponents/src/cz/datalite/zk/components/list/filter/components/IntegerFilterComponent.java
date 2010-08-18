package cz.datalite.zk.components.list.filter.components;

import org.zkoss.zul.Intbox;
import org.zkoss.zk.ui.WrongValueException;

/**
 * Standard implementation of the filter component for intboxes. There are
 * no validation additions, each of restrictions comes from the component.
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class IntegerFilterComponent extends AbstractFilterComponent<Intbox> {

    /** max number value */
    protected int maxValue;
    /** min number value*/
    protected int minValue;

    public IntegerFilterComponent( final int minValue, final int maxValue ) {
        super( new Intbox() );
        this.maxValue = maxValue;
        this.minValue = minValue;
    }

    @Override
    public void validate() throws WrongValueException {
        final int number = ( Integer ) (getValue() == null ? 0 : getValue());
        if ( number > maxValue || number < minValue ) {
            throw new WrongValueException( component, "Value is out of bounds." );
        }
    }
}
