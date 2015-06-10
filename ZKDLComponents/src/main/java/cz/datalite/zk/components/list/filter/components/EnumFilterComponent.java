package cz.datalite.zk.components.list.filter.components;

import java.util.Collections;

import org.zkoss.lang.reflect.Fields;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ComboitemRenderer;
import org.zkoss.zul.ListModelList;

/**
 * Component for normal filter when the model is enumeration type.
 * This component displays a combobox with appropriate values.
 *
 * @author Karel Cemus
 */
public class EnumFilterComponent extends AbstractFilterComponent<Combobox> {

    protected final Object[] enums;

    public EnumFilterComponent( final Object[] enums ) {
        super( new Combobox() );
        this.enums = enums.clone();
        component.setModel( new ListModelList( enums ) );
        component.setConstraint( "strict" );
        setValue( enums[0] );
    }

    public EnumFilterComponent( final Object[] enums, final String fieldPath ) {
        this( enums );
        final ComboitemRenderer renderer = new ComboitemRenderer() {

            public void render( final Comboitem item, final Object object, final int index ) throws NoSuchMethodException  {
                item.setLabel( Fields.getByCompound( object, fieldPath ).toString() );
                item.setValue( object );
            }
        };
        component.setItemRenderer( renderer );
    }

    public EnumFilterComponent( final Object[] enums, final ComboitemRenderer renderer ) {
        this( enums );
        component.setItemRenderer( renderer );
    }

    @Override
    public Object getValue() {
        return component.getSelectedItem().getValue();
    }

    @Override
    public void setValue( final Object value ) {
        for ( int i = 0; i < component.getModel().getSize(); i++ ) {
            if ( component.getModel().getElementAt( i ).equals( value ) ) {
                if ( component.getItems().isEmpty() ) { // not rendered yet
                    final int index = i;
                    component.addEventListener( "onInitRenderLater", new EventListener() {

                        public void onEvent( final Event event ) {
                            component.removeEventListener( "onInitRenderLater", this );
                            component.setSelectedIndex( index );
                            Events.postEvent( new SelectEvent( Events.ON_CHANGE, component, Collections.singleton( component.getSelectedItem() ) ) );
                        }
                    } );
                } else { // already rendered
                    component.setSelectedIndex( i );
                }
                break;
            }
        }
    }

    public FilterComponent cloneComponent() {
        final EnumFilterComponent clone = new EnumFilterComponent( enums );
        clone.component.setItemRenderer( component.getItemRenderer() );
        return clone;
    }
}
