package com.penzzly.engine.architecture.utilites;

import com.google.common.collect.Iterables;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Extensions {
	
	//--Method Reference--
	public static <T> Predicate<T> not(@NotNull Predicate<T> predicate) {
		return predicate.negate();
	}
	
	
	//--Bundle--
	public static <A, B> Pair<A, B> bundle(A first, B second) {
		return new Pair<>(first, second);
	}
	
	public static <A, B, C> Triplet<A, B, C> bundle(A first, B second, C third) {
		return new Triplet<>(first, second, third);
	}
	
	
	//--Runnable--
	public static <T> Runnable runnable(@NotNull Consumer<T> consumer, T first) {
		return () -> consumer.accept(first);
	}
	
	public static <T> Runnable runnable(@NotNull Supplier<T> first, @NotNull Consumer<T> consumer) {
		return () -> consumer.accept(first.get());
	}
	
	public static <A, B> Runnable runnable(@NotNull BiConsumer<A, B> consumer, A first, B second) {
		return () -> consumer.accept(first, second);
	}
	
	public static <A, B> Runnable runnable(@NotNull BiConsumer<A, B> consumer, A first, B second, Runnable... runnables) {
		return () -> {
			forEach(Runnable::run, runnables);
			consumer.accept(first, second);
		};
	}
	
	
	//--Consumer--
	public static <T> Consumer<T> consumer(@NotNull Runnable runnable) {
		return (first) -> runnable.run();
	}
	
	public static <T> Consumer<T> consumer(@NotNull Consumer<T> consumer, @NotNull Predicate<T> filter) {
		return param -> {
			if (filter.test(param)) {
				consumer.accept(param);
			}
		};
	}
	
	public static <A, B> Consumer<A> biConsumer(@NotNull BiConsumer<A, B> consumer, B second) {
		return first -> consumer.accept(first, second);
	}
	
	public static <A, B> Consumer<B> consumer(A second, @NotNull Consumer<A> consumer) {
		return first -> consumer.accept(second);
	}

/*    public static <A, R> Consumer<A> biConsumer(Function<A, R> function) {
        return function::apply;
    }

    public static <A, B, R> Consumer<A> biConsumer(BiFunction<A, B, R> function, B second) {
        return first -> function.apply(first, second);
    }*/
	
	
	//--Function--
	public static <A, B, R> Function<A, R> function(@NotNull BiFunction<A, B, R> function, B second) {
		return first -> function.apply(first, second);
	}
	
	//--Predicate
	public static <A, B> Predicate<A> predicate(@NotNull BiPredicate<A, B> predicate, B second) {
		return first -> predicate.test(first, second);
	}
	
	public static <A, B> Predicate<A> predicate(@NotNull Predicate<B> predicate, @NotNull Function<A, B> converter) {
		return param -> predicate.test(converter.apply(param));
	}
	
	//--Chain--
	public static Runnable chain(Runnable... runnables) {
		return () -> forEach(Runnable::run, runnables);
	}
	
	@SafeVarargs
	public static <T> Consumer<T> chain(Consumer<T>... consumers) {
		return param -> forEach(consumer -> consumer.accept(param), consumers);
	}
	
	//--Utilities
	public static void range(int min, int max, @NotNull Consumer<Integer> consumer) {
		for (int i = min; i <= max; i++)
			consumer.accept(i);
	}
	
	public static void range(int max, @NotNull Consumer<Integer> consumer) {
		range(0, max, consumer);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Optional<T> cast(Object object) {
		try {
			return Optional.of((T) object);
		} catch (ClassCastException e) {
			return Optional.empty();
		}
	}
	
	public static <A, B> Consumer<A> convert(@NotNull Consumer<B> consumer, @NotNull Function<A, B> converter) {
		return first -> consumer.accept(converter.apply(first));
	}
	
	@SuppressWarnings("unchecked")
	public static <A, B> Consumer<A> cast(@NotNull Consumer<B> consumer) {
		return param -> consumer.accept((B) param);
	}
	
	public static <T> Stream<T> stream(@NotNull Iterable<T> iterable) {
		return StreamSupport.stream(iterable.spliterator(), false);
	}
	
	public static <T> Stream<T> concatStream(@NotNull Iterable<? extends Iterable<T>> iterables) {
		return StreamSupport.stream(Iterables.concat(iterables).spliterator(), false);
	}
	
	@SafeVarargs
	public static <T> Iterable<T> iterate(@NotNull T... array) {
		return () -> new Iterator<T>() {
			private int index = 0;
			@Nullable
			private T next = getNext();
			private boolean done = false;
			
			@Override
			public boolean hasNext() {
				return !done;
			}
			
			@Nullable
			@Override
			public T next() {
				if (done) {
					throw new NoSuchElementException();
				}
				return next;
			}
			
			private T getNext() {
				if (array.length > index) {
					return array[index++];
				} else {
					done = true;
				}
				return null;
			}
		};
	}
	
	@SafeVarargs
	public static <T> void forEach(@NotNull Consumer<T> consumer, @NotNull T... array) {
		for (T element : array)
			consumer.accept(element);
	}
	
	public static <T> void forEach(@NotNull Iterable<T> iterable, @NotNull Consumer<T> consumer) {
		iterable.forEach(consumer);
	}
	
	public static <T> List<T> list(@NotNull Stream<T> stream) {
		return stream.collect(Collectors.toList());
	}
	
	public static <T> Set<T> set(@NotNull Stream<T> stream) {
		return stream.collect(Collectors.toSet());
	}
}