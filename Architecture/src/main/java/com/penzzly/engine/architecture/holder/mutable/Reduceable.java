package com.penzzly.engine.architecture.holder.mutable;

import org.jetbrains.annotations.NotNull;

public interface Reduceable<Type> {
	
	boolean remove(Object element);
	
	default boolean remove(@NotNull Iterable<Object> elements) {
		boolean removed = false;
		for (Object element : elements)
			removed = removed | remove(element);
		return removed;
	}
	
	default boolean remove(@NotNull Object[] elements) {
		boolean removed = false;
		for (Object element : elements)
			removed = removed | remove(element);
		return removed;
	}
}