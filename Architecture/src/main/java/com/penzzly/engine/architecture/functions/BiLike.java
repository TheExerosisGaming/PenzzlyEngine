package com.penzzly.engine.architecture.functions;

import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Function;

public interface BiLike<First, Second, Return> extends Function<BiConsumer<First, Second>, Return> {
	
	@NotNull Return like(BiConsumer<First, Second> description);
	
	@NotNull
	@Override
	default Return apply(BiConsumer<First, Second> description) {
		return like(description);
	}
	
}
