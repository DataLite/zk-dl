package cz.datalite.zk.components.list.filter;

import cz.datalite.zk.components.list.model.DLColumnUnitModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.lang.reflect.Fields;

/**
 *
 * Class defining utils used mostly by DLFilter
 *
 * @author Karel Cemus
 */
public class FilterUtils {

    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger( FilterUtils.class );

    /** Library class */
    private FilterUtils() {
    }

    public static Object getValue( final Object entity, final String address ) throws NoSuchMethodException {
        if ( NormalFilterModel.ALL.equals( address ) ) {
            // change ZK-161
            // if we are looking by ALL, no column is needed
            return entity;
        } else {
            // otherwise return given field
            return Fields.getByCompound( entity, address );
        }
    }

    public static Object getConvertedValue( Object value, DLColumnUnitModel columnModel ) {
        // try to convert value
        if ( columnModel != null && columnModel.isConverter() ) {

            try {
                return columnModel.getConverter().convertToView( value );
            } catch ( Exception ex ) {
                LOGGER.warn( "Conversion in DLFilter failed!", ex );
            }
        }
        return value;
    }
}
