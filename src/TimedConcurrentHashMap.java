

import java.lang.annotation.Retention;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * A map backed by a ConcurrentMap that add knowledge abput when and how long elements are added to the map.
 * A decorator that implements ConcurrentMap and delegates to ConcurrentHashMap.
 * It add one new method {@link #getElapsedTime(Object)}
 */
public class TimedConcurrentHashMap<K, V> implements ConcurrentMap<K, V> {

    private final ConcurrentHashMap<K, Entry<V, Long>> backingMap;

    public TimedConcurrentHashMap(int initialCapacity,
                                  float loadFactor, int concurrencyLevel) {
        backingMap = new ConcurrentHashMap<>(initialCapacity, loadFactor, concurrencyLevel);
    }

    public TimedConcurrentHashMap(int initialCapacity,
                                  float loadFactor) {
        backingMap = new ConcurrentHashMap<>(initialCapacity, loadFactor);
    }

    public TimedConcurrentHashMap(int initialCapacity) {
        backingMap = new ConcurrentHashMap<>(initialCapacity);
    }

    public TimedConcurrentHashMap() {
        backingMap = new ConcurrentHashMap<>();
    }

    public TimedConcurrentHashMap(Map<? extends K, ? extends Entry<V, Long>> m) {
        backingMap = new ConcurrentHashMap<>(m);
    }

    public long getElapsedTime(K key) {
        Entry<V, Long> entry = backingMap.get(key);

        // Not found. Dont throw exception , return big number
        Long startTime = (entry == null ?
                Long.MAX_VALUE : entry.getValue());
        return (System.nanoTime() - startTime) / 1000000;
    }

    @Override
    public Collection<V> values() {
        return backingMap.values().stream().map(x -> x.getKey()).collect(Collectors.toList());
    }

    @Override
    public int size() {
        return backingMap.size();
    }

    @Override
    public boolean isEmpty() {
        return backingMap.isEmpty();
    }


    @Override
    public boolean containsValue(Object value) {
        if (value == null)
            return false;

        Collection<Entry<V, Long>> values = backingMap.values();
        for (Entry<V, Long> mapValue : values) {
            if (mapValue.getKey().equals(value)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public V get(Object key) {
        Entry<V, Long> entry = backingMap.get(key);
        return entry == null ? null : entry.getKey();
    }

    @Override
    public V put(K key, V value) {
        Entry<V, Long> entry = backingMap.put(key,
                new AbstractMap.SimpleEntry<>(value, System.nanoTime()));
        return entry == null ? null : entry.getKey();
    }

    @Override
    public V remove(Object key) {
        Entry<V, Long> entry = backingMap.remove(key);
        return entry == null ? null : entry.getKey();
    }

    @Override
    public synchronized boolean remove(Object key, Object value) {
        Entry<V, Long> entry = backingMap.get(key);
        if (entry == null)
            return false;

        return backingMap.remove(key, new AbstractMap.SimpleEntry<>(value, entry.getValue()));
    }

    @Override
    public synchronized boolean replace(K key, V oldValue, V newValue) {
        Entry<V, Long> entry = backingMap.get(key);
        if (entry == null)
            return false;

        return backingMap.replace(key, new AbstractMap.SimpleEntry<>(oldValue, entry.getValue()),
                new AbstractMap.SimpleEntry<>(newValue, System.nanoTime()));
    }

    @Override
    public V replace(K key, V value) {
        Entry<V, Long> entry = backingMap.replace(key,
                new AbstractMap.SimpleEntry<>(value, System.nanoTime()));
        return entry == null ? null : entry.getKey();
    }

    @Override
    public boolean containsKey(Object key) {
        return backingMap.containsKey(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Entry<? extends K, ? extends V> entry : m.entrySet()) {
            backingMap.put(entry.getKey(), new AbstractMap.SimpleEntry<>(entry.getValue(), System.nanoTime()) {
            });
        }

    }

    @Override
    public void clear() {
        backingMap.clear();
    }

    @Override
    public Set<K> keySet() {
        return backingMap.keySet();
    }


    @Override
    public Set<Entry<K, V>> entrySet() {
        class SimpleEntry implements Map.Entry<K, V> {
            Map.Entry<K, Entry<V, Long>> input;

            SimpleEntry(Map.Entry<K, Entry<V, Long>> input) {
                this.input = input;
            }


            @Override
            public K getKey() {
                return input.getKey();
            }

            @Override
            public V getValue() {
                Entry<V, Long> value = input.getValue();
                return value == null ? null : value.getKey();
            }

            @Override
            public V setValue(V value_) {
                Entry<V, Long> value = input.getValue();
                input.setValue(new AbstractMap.SimpleEntry<>(value_, value.getValue()));
                return value.getKey();
            }
        }

        Set<Map.Entry<K, Entry<V, Long>>> entrySet = backingMap.entrySet();
        Set<Map.Entry<K, V>> finalEntrySet = new HashSet<>(entrySet.size());

        for (Map.Entry<K, Entry<V, Long>> entry : entrySet) {
            finalEntrySet.add(new SimpleEntry(entry));
        }

        return finalEntrySet;
    }


    @Override
    public V putIfAbsent(K key, V value) {
        Entry<V, Long> previous = backingMap.putIfAbsent(key, new AbstractMap.SimpleEntry<>(value, System.nanoTime()));
        return previous == null ? null : previous.getKey();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = prime + (backingMap == null ? 0 : backingMap.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) return false;
        if (this == object) return true;
        if (this.getClass() != object.getClass()) return false;
        TimedConcurrentHashMap other = (TimedConcurrentHashMap) object;
        if (backingMap == null) {
            if (other.backingMap != null) {
                return false;
            } else if (!backingMap.equals(other.backingMap)) {
                return false;
            }
        }
        return true;
    }
}
