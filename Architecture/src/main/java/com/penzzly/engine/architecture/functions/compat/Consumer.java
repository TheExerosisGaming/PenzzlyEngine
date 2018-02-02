package com.penzzly.engine.architecture.functions.compat;

public interface Consumer<Type> extends java.util.function.Consumer<Type>, io.reactivex.functions.Consumer<Type> {
	@Override
	void accept(Type t);
}
