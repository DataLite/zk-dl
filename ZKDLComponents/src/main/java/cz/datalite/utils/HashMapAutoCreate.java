package cz.datalite.utils;

import java.util.HashMap;
import org.apache.log4j.Logger;

/**
 * Mapa, která pokaždé vrací existující instanci. Pokud je požadován záznam z nepoužitého klíče,
 * tak vytvoří novou instanci a uloží. Také eviduje seznam použitých klíčů
 * @param <K>
 * @param <V>
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class HashMapAutoCreate<K, V> extends HashMap<K, V> implements MapAutoCreate<K, V> {

    protected final Class<V> entityClass;

    public HashMapAutoCreate( final Class<V> entityClass ) {
        super();
        this.entityClass = entityClass;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public V get( final Object key ) {
        try {
            if ( !containsKey( key ) ) {
                super.put( (K) key, (V) entityClass.newInstance() );
            }
            return super.get( (K) key );
        }
        catch ( InstantiationException ex ) {
            Logger.getLogger( HashMapAutoCreate.class.getName() ).error("HashMapAutoCreate failed!", ex );
            return null;
        }
        catch ( IllegalAccessException ex ) {
            Logger.getLogger( HashMapAutoCreate.class.getName() ).error( "HashMapAutoCreate failed!", ex );
            return null;
        }
    }

    @Override
    public V put( final K key, final V value ) {
        if ( !containsKey( key ) )
            get( key );
        return super.put( key, value );
    }
}
