package com.penzzly.engine.architecture.functions;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;

public interface Like<Type, Return> extends Function<Consumer<Type>, Return> {
	
	@NotNull Return like(Consumer<Type> description);
	
	@NotNull
	@Override
	default Return apply(Consumer<Type> description) {
		return like(description);
	}
	
}