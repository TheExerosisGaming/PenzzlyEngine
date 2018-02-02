package com.penzzly.engine.architecture.utilites;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

import static java.util.concurrent.ThreadLocalRandom.current;

public final class Swi {
	public static void main(String[] args) {
		Swi.tch(current().nextBoolean() ? 0 : "string", ca -> {
			ca.se(String.class, () -> {
			
			});
			ca.se(Integer.class, () -> {
			
			});
		}, (Object input, Class<?> test) -> test.isInstance(input));
	}
	
	private Swi() {
	
	}
	
	public static <T> void tch(@NotNull T input, @NotNull Consumer<Ca<T>> closure) {
		tch(input, closure, Object::equals);
	}
	
	public static <A, B> void tch(@NotNull A input, @NotNull Consumer<Ca<B>> closure, @Nullable BiPredicate<A, B> evaluator) {
		Ca<B> cases = new Ca<>();
		
		closure.accept(cases);
		if (evaluator != null) {
			cases.cases.forEach((value, se) -> {
				if (evaluator.test(input, value)) {
					se.run();
				}
			});
		} else {
			cases.cases.forEach((value, se) -> {
				if (input.equals(value)) {
					se.run();
				}
			});
		}
	}
	
	public static class Ca<A> {
		protected final Map<A, Runnable> cases = new HashMap<>();
		
		public void se(A value, Runnable se) {
			cases.put(value, se);
		}
	}
}