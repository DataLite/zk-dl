package cz.datalite.zk.converter;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;

/**
 * TypeConverterAdapter to support converters defined in ZK 5 or prior. Since ZK6
 * there is new interface it has to implement.
 * 
 * @author Karel Cemus <cemuskar@fel.cvut.cz>
 */


public class TypeConverterAdapter implements Converter {
    
    /** old-style converter */
    private TypeConverter converter;

    public TypeConverterAdapter(TypeConverter converter ) {
        this.converter = converter;
    }
    
    public Object coerceToUi( Object value, Component component, BindContext ctx ) {
        return converter.coerceToUi( value, component );
    }

    public Object coerceToBean( Object componentAttribute, Component component, BindContext ctx ) {
        return converter.coerceToBean( componentAttribute, component );
    }
    
}
