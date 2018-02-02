package com.penzzly.engine.architecture.holder;


import com.google.common.collect.Iterables;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.penzzly.engine.architecture.holder.Iterators.filter;
import static com.penzzly.engine.architecture.holder.Iterators.merge;
import static java.util.Spliterator.DISTINCT;

public interface Holder<Type> extends Predicate<Type>, Iterable<Type> {
	@NotNull
	default Operators<Type, ? extends Holder<Type>> live() {
		return new Operators<Type, Holder<Type>>() {
			@NotNull
			@Override
			public Holder<Type> partition(@NotNull Predicate<Type> filter) {
				return new Holder<Type>() {
					@NotNull
					@Override
					public Iterator<Type> iterator() {
						return filter(Holder.this.iterator(), filter);
					}
					
					@Override
					public boolean test(Type element) {
						return filter.and(Holder.this).test(element);
					}
				};
			}
			
			@NotNull
			@Override
			public Holder<Type> union(@NotNull Holder<Type> holder) {
				return new Holder<Type>() {
					@NotNull
					@Override
					public Iterator<Type> iterator() {
						return merge(holder.iterator(), filter(Holder.this.iterator(), holder.negate()));
					}
					
					@Override
					public boolean test(Type element) {
						return holder.or(Holder.this).test(element);
					}
				};
			}
			
			@NotNull
			@Override
			public Holder<Type> difference(@NotNull Holder<Type> holder) {
				return new Holder<Type>() {
					@NotNull
					@Override
					public Iterator<Type> iterator() {
						return filter(Holder.this.iterator(), holder.negate());
					}
					
					@Override
					public boolean test(Type element) {
						return holder.or(Holder.this).test(element);
					}
				};
			}
			
			@NotNull
			@Override
			public Holder<Type> intersection(@NotNull Holder<Type> holder) {
				return new Holder<Type>() {
					@NotNull
					@Override
					public Iterator<Type> iterator() {
						return filter(Holder.this.iterator(), holder);
					}
					
					@Override
					public boolean test(Type element) {
						return holder.or(Holder.this).test(element);
					}
				};
			}
		};
	}
	
	default boolean contains(Object element) {
		try {
			return test((Type) element);
		} catch (ClassCastException e) {
			return false;
		}
	}
	
	default int size() {
		return Iterables.size(this);
	}
	
	@NotNull
	default Stream<Type> stream() {
		return StreamSupport.stream(spliterator(), false);
	}
	
	@NotNull
	default Stream<Type> parallelStream() {
		return StreamSupport.stream(spliterator(), true);
	}
	
	@NotNull
	@Override
	default Spliterator<Type> spliterator() {
		//TODO is DISTINCT correct?
		return Spliterators.spliterator(iterator(), size(), DISTINCT);
	}
	
	@Override
	boolean test(Type element);
}