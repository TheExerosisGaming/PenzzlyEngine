package com.penzzly.engine.architecture.holder.mutable;

import org.jetbrains.annotations.NotNull;

public interface Augmentable<Type> {
	boolean add(Type element);
	
	default boolean add(@NotNull Iterable<? extends Type> elements) {
		boolean added = false;
		for (Type element : elements)
			added = added | add(element);
		return added;
	}
	
	@SuppressWarnings("unchecked")
	default boolean add(@NotNull Type... elements) {
		boolean added = false;
		for (Type element : elements)
			added = added | add(element);
		return added;
	}
}