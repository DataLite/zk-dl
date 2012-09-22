package cz.datalite.zk.converter;

import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;

/**
 * Adapter above {@link TypeConverter}
 *
 * @author Karel Cemus
 */
public class ZkTypeConverterAdapter implements ZkConverter {

    private final TypeConverter converter;
    
    private final Component component;

    public ZkTypeConverterAdapter( TypeConverter converter, Component component ) {
        this.converter = converter;
        this.component = component;
    }

    public Object convertToView( Object value ) {
        return converter.coerceToUi( value, component );
    }
}
