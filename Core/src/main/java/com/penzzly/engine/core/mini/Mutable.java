package com.penzzly.engine.core.mini;

public interface Mutable<T> {
	void setValue(T value);
	
	T getValue();
}
