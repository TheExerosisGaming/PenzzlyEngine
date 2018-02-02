package com.penzzly.engine.architecture.functions.compat;

import org.jetbrains.annotations.NotNull;

public interface BiFunction<First, Second, Return> extends java.util.function.BiFunction<First, Second, Return>, io.reactivex.functions.BiFunction<First, Second, Return> {
	@NotNull
	@Override
	Return apply(First first, Second second);
}
