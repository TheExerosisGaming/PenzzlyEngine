package com.penzzly.engine.architecture.utilites;

import com.google.common.collect.Range;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.google.common.collect.Range.atLeast;
import static com.google.common.collect.Range.lessThan;
import static java.awt.Color.*;
import static java.util.concurrent.ThreadLocalRandom.current;

public final class Swi {
	public static void main(String[] args) {
		Swi.tch(current().nextBoolean() ? 0 : "string", ca -> {
			ca.se(String.class, () -> {
			
			});
			ca.se(Integer.class, () -> {
			
			});
		}, (Object input, Class<?> test) -> test.isInstance(input));
		Color test = Swi.tch(current().nextInt(100), ca -> {
			ca.se(atLeast(25), GREEN);
			ca.se(atLeast(50), YELLOW);
			ca.se(atLeast(75), ORANGE);
			ca.se(lessThan(100), RED);
			return () -> BLACK;
		}, (Integer input, Range<Integer> value) -> value.contains(input));
	}
	
	private Swi() {
	
	}
	
	public static <T, R> R tch(@NotNull T input, R defaultValue, @NotNull Consumer<Ca<T, R>> closure) {
		return tch(input, closure).orElse(defaultValue);
	}
	
	public static <T, R> Optional<R> tch(@NotNull T input, @NotNull Consumer<Ca<T, R>> closure) {
		return tch(input, closure, Object::equals);
	}
	
	public static <A, B, R> Optional<R> tch(@NotNull A input, @NotNull Consumer<Ca<B, R>> closure, @NotNull BiPredicate<A, B> evaluator) {
		return Optional.ofNullable(tch(input, ca -> {
			closure.accept(ca);
			return null;
		}, evaluator));
	}
	
	public static <A, B, R> R tch(@NotNull A input, @NotNull Consumer<Ca<B, R>> closure, @NotNull BiPredicate<A, B> evaluator, R defaultValue) {
		return tch(input, closure, evaluator).orElse(defaultValue);
	}
	
	public static <A, B, R> R tch(@NotNull A input, @NotNull Function<Ca<B, R>, Supplier<R>> closure, @NotNull BiPredicate<A, B> evaluator) {
		Ca<B, R> cases = new Ca<>();
		
		Supplier<R> defaultSupplier = closure.apply(cases);
		for (Map.Entry<B, Supplier<R>> entry : cases.cases.entrySet())
			if (evaluator.test(input, entry.getKey())) {
				return entry.getValue().get();
			}
		return defaultSupplier.get();
	}
	
	public static class Ca<B, Return> {
		protected final Map<B, Supplier<Return>> cases = new HashMap<>();
		
		public void se(B value, Return returnValue) {
			se(value, () -> returnValue);
		}
		
		public void se(B value, Runnable se) {
			se(value, () -> {
				se.run();
				return null;
			});
		}
		
		public void se(B value, Supplier<Return> se) {
			cases.put(value, se);
		}
	}
}