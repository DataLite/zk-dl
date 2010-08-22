package cz.datalite.zk.components.list.filter;

import java.util.HashMap;

/**
 * Easy filter unit model - groups key and values for the each
 * operator.
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class EasyFilterMapKeyValue extends HashMap<String, Object> {

    @Override
    public Object put( final String key, final Object value ) {
        assert key != null && key.length() > 0 : "Invalid key for Easy filter. Key cannot be null or empty. ";
        return super.put( key, value );
    }
}
