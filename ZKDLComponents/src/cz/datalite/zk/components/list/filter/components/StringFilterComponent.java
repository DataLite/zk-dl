package cz.datalite.zk.components.list.filter.components;

import org.zkoss.zul.Textbox;

/**
 * Standard implementation of the filter component for text. There are
 * no validation additions, each of restrictions comes from the component.
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class StringFilterComponent extends AbstractFilterComponent<Textbox>
{

    public StringFilterComponent(  ) {
        super( new Textbox() );
    }
}
