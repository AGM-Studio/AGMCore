package me.ashenguard.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@SuppressWarnings("unused")
public class NestedMap <T, U>{
    private final Map<T, NestedMap<T, U>> maps = new HashMap<>();
    private final Map<T, U> values = new HashMap<>();

    public void addKey(T key) {
        maps.putIfAbsent(key, new NestedMap<>());
        values.putIfAbsent(key, null);
    }
    public void addKey(T key, U value) {
        maps.putIfAbsent(key, new NestedMap<>());
        values.putIfAbsent(key, value);
    }

    public int size() {
        return values.size();
    }
    public boolean isEmpty() {
        return values.isEmpty();
    }

    public boolean containsKey(T key) {
        return values.containsKey(key);
    }

    public U getValue(T[] keys) {
        NestedMap<T, U> map = this;
        for (int i = 0; i < keys.length - 1; i++) map = map.getNest(keys[i]);
        return map.getValue(keys[keys.length - 1]);
    }
    public U getValue(T key) {
        return values.get(key);
    }
    public U getValue(T[] keys, U def) {
        U value = getValue(keys);
        return value == null ? def : value;
    }
    public U getValue(T key, U def) {
        U value = getValue(key);
        return value == null ? def : value;
    }
    public NestedMap<T, U> getNest(T key) {
        NestedMap<T, U> nest = maps.get(key);
        if (nest == null) throw new NotFoundException(String.format("Nest \"%s\" is not found in this map", key));
        return nest;
    }

    @Nullable
    public U putValue(T key, U value) {
        maps.putIfAbsent(key, new NestedMap<>());
        return values.put(key, value);
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
        return values.keySet();
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

    public static class NotFoundException extends RuntimeException {
        protected NotFoundException() {
            super();
        }
        protected NotFoundException(String message) {
            super(message);
        }
        protected NotFoundException(Throwable cause) {
            super(cause);
        }
        protected NotFoundException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
