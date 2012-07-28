package cz.datalite.zk.components.list.filter;

import org.zkoss.lang.reflect.Fields;

/**
 *
 * Class defining utils used mostly by DLFilter
 * 
 * @author Karel Čemus <cemuskar@fel.cvut.cz>
 */
public class FilterUtils {
    
    /** Library class */
    private FilterUtils() {}
    
    public static Object getValue(final Object entity, final String address) throws NoSuchMethodException {
        if (NormalFilterModel.ALL.equals(address)) {
            // change ZK-161
            // if we are looking by ALL, no column is needed
            return entity;
        } else {
            // otherwise return given field
            return Fields.getByCompound(entity, address);
        }
    }
    
}
