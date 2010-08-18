package cz.datalite.utils;

import java.util.Map;
import java.util.Set;

/**
 * Special map - if the map doesn't contain the key in get() method call, it will add the key and return this new value.
 *
 * @param <K> key
 * @param <V> value
 *
 * @author Karel Čemus <cemus@datalite.cz>
 */
public interface MapAutoCreate<K, V> extends Map<K, V> {

}
