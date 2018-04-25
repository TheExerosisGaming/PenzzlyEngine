package com.penzzly.engine.core.utilites;

public final class PartitionUtil {
    /*
    private PartitionUtil() {
    }

    public static <T> Pair<PartitionHolder<T>, PartitionHolder<T>> splitPartition(Collection<T> list, Predicate<T> split) {
        Holder<T> parent = new Holder<T>() {
            @Override
            public int size() {
                return list.size();
            }

            @Override
            public Iterator<T> iterator() {
                return list.iterator();
            }

            @Override
            public boolean test(T item) {
                return list.contains(item);
            }
        };
        return Pair.with(new PartitionHolder<T>() {
            @Override
            public Predicate<T> getFilter() {
                return split;
            }

            @Override
            public Holder<T> getParent() {
                return parent;
            }
        }, new PartitionHolder<T>() {
            @Override
            public Predicate<T> getFilter() {
                return split.negate();
            }

            @Override
            public Holder<T> getParent() {
                return parent;
            }
        });
    }

    public static <T> PartitionHolder<T> partition(Predicate<T> filter, T... items) {
        return partition(Arrays.asList(items), filter);
    }

    public static <T> PartitionHolder<T> partition(Collection<T> list, Predicate<T> filter) {
        return new Holder<T>() {
            @Override
            public int size() {
                return list.size();
            }

            @Override
            public Iterator<T> iterator() {
                return list.iterator();
            }

            @Override
            public boolean test(T item) {
                return list.contains(item);
            }
        }.partition(filter);
    }*/
}