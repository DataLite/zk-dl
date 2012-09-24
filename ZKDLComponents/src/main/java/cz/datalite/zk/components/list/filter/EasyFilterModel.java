package cz.datalite.zk.components.list.filter;

import cz.datalite.utils.HashMapAutoCreate;
import cz.datalite.zk.components.list.enums.DLFilterOperator;
import org.zkoss.lang.Strings;

/**
 * Model for the easy filter - it has to be accessible from the zul file.
 * @author Karel Cemus
 */
public class EasyFilterModel extends HashMapAutoCreate<DLFilterOperator, EasyFilterMapKeyValue> {

    public EasyFilterModel() {
        super( EasyFilterMapKeyValue.class );
    }

    @Override
    public EasyFilterMapKeyValue get( final Object key ) {
        return super.get( key instanceof String ? DLFilterOperator.strToEnum( ( String ) key ) : ( DLFilterOperator ) key );
    }

    /**
     * Returns if some easyfilter is defined or not.
     * @author Ondřej Medek
     * @return if some filter is defined
     */
    @Override
    public boolean isEmpty() {
        for ( EasyFilterMapKeyValue keyValue : values() ) {
            for ( Object value : keyValue.values() ) {
                if ( value != null ) {
                    if ( !(value instanceof String) ) {
                        return false;
                    }
                    if ( !Strings.isEmpty( ( String ) value ) ) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
