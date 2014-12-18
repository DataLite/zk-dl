package cz.datalite.concurrent;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Serializovatelny slovnik se zamkem; urceny pro caste cteni a obcasne prepsani.
 * 
 * @see ReentrantReadWriteLock
 * @author <a href="mailto:mkouba@itsys.cz">Martin Kouba</a>
 */
public class ReadWriteDictionary<K, V> implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Map<K, V> map = new HashMap<>();

	private final ReadWriteLock rwl = new ReentrantReadWriteLock();

	private final Lock rl = rwl.readLock();

	private final Lock wl = rwl.writeLock();

	/**
	 * 
	 * @param key
	 * @return
	 */
	public V get(K key) {
		rl.lock();
		try {
			return map.get(key);
		} finally {
			rl.unlock();
		}
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	public boolean containsKey(K key) {
		rl.lock();
		try {
			return map.containsKey(key);
		} finally {
			rl.unlock();
		}
	}

	/**
	 * 
	 * @return
	 */
	public Set<K> allKeys() {
		rl.lock();
		try {
			return map.keySet();
		} finally {
			rl.unlock();
		}
	}

	/**
	 * 
	 * @param key
	 * @param value
	 */
	public V put(K key, V value) {

		wl.lock();
		try {
			return map.put(key, value);
		} finally {
			wl.unlock();
		}
	}

	/**
	 * Vlozi hodnotu ke klici pouze v pripade, ze klic v mape jeste neni (opak bezne metody put, ktera hodnotu
	 * prepisuje)
	 * 
	 * @param key
	 * @param value
	 * @return puvodni hodnotu pod danym klicem; <code>null</code>, pokud doslo k vlozeni nove hodnoty
	 */
	public V putNew(K key, V value) {

		wl.lock();
		try {
			if (!map.containsKey(key)) {
				return map.put(key, value);
			}
			return map.get(key);
		} finally {
			wl.unlock();
		}
	}

	/**
	 * 
	 * @param newValues
	 */
	public void clearAndPutAll(Map<K, V> newValues) {

		wl.lock();
		try {
			this.map.clear();
			this.map.putAll(newValues);
		} finally {
			wl.unlock();
		}
	}

	/**
	 * Odebere klic/hodnotu ze slovniku.
	 * 
	 * @param key
	 * @return zda hodnota pro klic ve slovniku existovala
	 */
	public boolean remove(K key) {

		wl.lock();
		try {
			return this.map.remove(key) != null;
		} finally {
			wl.unlock();
		}
	}

	/**
	 * Vycisti slovnik (mapu).
	 */
	public void clear() {
		wl.lock();
		try {
			map.clear();
		} finally {
			wl.unlock();
		}
	}

	/**
	 * @return <code>true</code> je-li slovnik prazdny, jinak <code>false</code>
	 */
	public boolean isEmpty() {
		rl.lock();
		try {
			return map.isEmpty();
		} finally {
			rl.unlock();
		}
	}

	@Override
	public String toString() {
		return "ReadWriteDictionary" + map.toString() + rwl.toString();
	}

}
