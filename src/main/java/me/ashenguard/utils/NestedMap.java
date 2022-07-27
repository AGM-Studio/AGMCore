package me.ashenguard.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class NestedMap <T, U>{
    private final Map<T, NestedMap<T, U>> maps = new HashMap<>();
    private final Map<T, U> values = new HashMap<>();

    public int size() {
        return values.size();
    }
    public boolean isEmpty() {
        return values.isEmpty();
    }

    public boolean containsKey(T key) {
        return getEntry(key) != null;
    }
    public boolean hasNest(T key) {
        return maps.get(key) != null;
    }
    public boolean hasValue(T key) {
        return values.get(key) != null;
    }

    public U getValue(T key) {
        return values.get(key);
    }
    public U getValue(T key, U def) {
        U value = getValue(key);
        return value == null ? def : value;
    }
    public NestedMap<T, U> getNest(T key) {
        return maps.get(key);
    }

    public Entry<T, U> getEntry(T key) {
        U value = values.get(key);
        NestedMap<T, U> nest = maps.get(key);
        if (value == null && nest == null) return null;
        return new Entry<>(key, value, nest);
    }

    @Nullable
    public U putValue(T key, U value) {
        return values.put(key, value);
    }
    public NestedMap<T, U> addNest(T key) {
        maps.putIfAbsent(key, new NestedMap<>());
        return maps.get(key);
    }

    public U remove(T key) {
        maps.remove(key);
        return values.remove(key);
    }

    public void clear() {
        maps.clear();
        values.clear();
    }

    @NotNull
    public Set<T> keySet() {
        Set<T> set = values.keySet();
        set.addAll(maps.keySet());
        return set;
    }

    @NotNull
    public Collection<U> values() {
        return values.values();
    }

    @NotNull
    public Set<Entry<T, U>> entrySet() {
        Set<Entry<T, U>> set = new HashSet<>();

        values.keySet().forEach(key -> set.add(new Entry<>(key, getValue(key), getNest(key))));

        return set;
    }

    public static class Entry<T, U> {
        private final T key;
        private final U value;
        private final NestedMap<T, U> nest;

        protected Entry(T key, U value, NestedMap<T, U> nest) {
            this.key = key;
            this.value = value;
            this.nest = nest;
        }

        public T getKey() {
            return key;
        }

        public U getValue() {
            return value;
        }

        public NestedMap<T, U> getNest() {
            return nest;
        }
    }
}
