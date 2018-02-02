package com.penzzly.engine.architecture.holder;

import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public interface Operators<Type, Holder extends com.penzzly.engine.architecture.holder.Holder<Type>> {
	@NotNull Holder partition(Predicate<Type> filter);
	
	@NotNull Holder union(Holder holder);
	
	@NotNull Holder difference(Holder holder);
	
	@NotNull Holder intersection(Holder holder);
}
