package cz.datalite.zk.components.list.filter.components;

import org.zkoss.zul.Datebox;

/**
 * Standard implementation of the filter component for dateboxes. There are
 * no validation additions, each of restrictions comes from the component.
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class DateFilterComponent extends AbstractFilterComponent<Datebox> {

    public DateFilterComponent() {
        super( new Datebox() );
    }
}
