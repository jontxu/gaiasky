package gaia.cu9.ari.gaiaorbit.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple two-way hashmap implemented with two maps.
 * @param <K> Key type in forward map, value in backward map
 * @param <V> Value type in forward map, key in backward map
 *
 * @author Toni Sagrista
 */
public class TwoWayHashmap<K extends Object, V extends Object> {

    private Map<K, V> forward = new HashMap<K, V>();
    private Map<V, K> backward = new HashMap<V, K>();

    public synchronized void add(K key, V value) {
        forward.put(key, value);
        backward.put(value, key);
    }

    public synchronized V getForward(K key) {
        return forward.get(key);
    }

    public synchronized K getBackward(V key) {
        return backward.get(key);
    }

    public synchronized boolean containsKey(K key){
        return forward.containsKey(key);
    }

    public synchronized boolean containsValue(V value){
        return backward.containsKey(value);
    }
}
