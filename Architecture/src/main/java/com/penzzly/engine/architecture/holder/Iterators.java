package com.penzzly.engine.architecture.holder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.function.Predicate;

public interface Iterators {
	static <T> Iterator<T> filter(@NotNull Iterator<T> iterator, @NotNull Predicate<T> filter) {
		return new Iterator<T>() {
			@Nullable
			private T next = getNext();
			
			@Override
			public boolean hasNext() {
				return next != null;
			}
			
			@Nullable
			@Override
			public T next() {
				try {
					return next;
				} finally {
					getNext();
				}
			}
			
			@Nullable
			private T getNext() {
				do {
					if (iterator.hasNext()) {
						next = iterator.next();
					} else {
						next = null;
					}
				}
				while (next != null && !filter.test(next));
				return next;
			}
		};
	}
	
	//FIXME Not yet done
	static <T> Iterator<T> iteratorDifference(@NotNull Iterator<T> first, @NotNull Iterator<T> second) {
		return new Iterator<T>() {
			private T next = getNext();
			
			@Override
			public boolean hasNext() {
				return next != null || first.hasNext() || second.hasNext();
			}
			
			@Override
			public T next() {
				try {
					return next;
				} finally {
					getNext();
				}
			}
			
			private T getNext() {
				return next = first.hasNext() ? first.next() : second.next();
			}
		};
	}
	
	static <T> Iterator<T> merge(@NotNull Iterator<T> first, @NotNull Iterator<T> second) {
		return new Iterator<T>() {
			private T next = getNext();
			
			@Override
			public boolean hasNext() {
				return next != null || first.hasNext() || second.hasNext();
			}
			
			@Override
			public T next() {
				try {
					return next;
				} finally {
					getNext();
				}
			}
			
			private T getNext() {
				return next = first.hasNext() ? first.next() : second.next();
			}
		};
	}
	
	static int size(@NotNull Iterator<?> iterator) {
		return com.google.common.collect.Iterators.size(iterator);
	}
}