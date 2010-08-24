package cz.datalite.zk.components.list.filter.components;

import java.util.Set;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.ListModelList;

/**
 *
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class EnumFilterComponent extends AbstractFilterComponent<Combobox> {

    public EnumFilterComponent( final Object[] enums ) {
        super( new Combobox() );
        component.setModel( new ListModelList( enums ) );
    }

    @Override
    public Object getValue() {
        return component.getSelectedItem().getValue();
    }

    @Override
    public void setValue( final Object value ) {
        for ( int i = 0; i < component.getModel().getSize(); i++ ) {
            if ( component.getModel().getElementAt( i ).equals( value ) ) {
                component.setSelectedIndex( i );
            }
        }
    }
}
