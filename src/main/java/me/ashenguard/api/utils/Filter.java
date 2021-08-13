package me.ashenguard.api.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public abstract class Filter<T> {
    public abstract boolean test(T item);
    public List<T> apply(Iterable<T> list) {
        List<T> items = new ArrayList<>();
        for (T item: list) if (test(item)) items.add(item);
        return items;
    }

    public Filter<T> NOT() {
        final Filter<T> current = this;
        return new Filter<>() {
            @Override
            public boolean test(T item) {
                return !current.test(item);
            }
        };
    }
    public Filter<T> AND(final Filter<T> filter) {
        final Filter<T> current = this;
        return new Filter<>() {
            @Override public boolean test(T item) {
                return current.test(item) && filter.test(item);
            }
        };
    }
    public Filter<T> OR(final Filter<T> filter) {
        final Filter<T> current = this;
        return new Filter<>() {
            @Override public boolean test(T item) {
                return current.test(item) || filter.test(item);
            }
        };
    }

    public static <E> Filter<E> fromPredicate(Predicate<E> predicate) {
        return new Filter<>() {
            @Override public boolean test(E item) {
                return predicate.test(item);
            }
        };
    }
}
