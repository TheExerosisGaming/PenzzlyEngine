package com.penzzly.engine.architecture.functions.compat;


public interface Predicate<Type> extends java.util.function.Predicate<Type>, Function<Type, Boolean>, io.reactivex.functions.Predicate<Type> {
	
	@Override
	default Boolean apply(Type input) {
		return test(input);
	}
	
	@Override
	boolean test(Type type);
}
