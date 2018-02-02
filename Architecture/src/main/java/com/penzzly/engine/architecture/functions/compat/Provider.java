package com.penzzly.engine.architecture.functions.compat;

import io.reactivex.Observable;
import io.reactivex.Observer;
import org.jetbrains.annotations.NotNull;

public abstract class Provider<Type> extends Observable<Type> implements Supplier<Type> {
	
	@Override
	protected void subscribeActual(@NotNull Observer<? super Type> observer) {
		super.subscribe(observer);
	}
	
	public static <Type> Provider<Type> create(@NotNull java.util.function.Supplier<Type> supplier) {
		return new Provider<Type>() {
			@Override
			public Type get() {
				return supplier.get();
			}
		};
	}
}