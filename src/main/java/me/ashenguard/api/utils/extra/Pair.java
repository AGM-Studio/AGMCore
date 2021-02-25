package me.ashenguard.api.utils.extra;

import java.util.Map;

@SuppressWarnings("unused")
public class Pair<K, V> implements Map.Entry<K, V> {
    private final K key;
    private V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public Pair(Map.Entry<K, V> entry) {
        this(entry.getKey(), entry.getValue());
    }

    public Pair(K key) {
        this(key, null);
    }

    @Override
    public K getKey() {
        return this.key;
    }

    @Override
    public V getValue() {
        return this.value;
    }

    @Override
    public V setValue(V value) {
        V last = getValue();
        this.value = value;
        return last;
    }
}
