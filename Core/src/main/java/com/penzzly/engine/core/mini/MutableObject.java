package com.penzzly.engine.core.mini;

import java.util.Objects;

public class MutableObject<T> implements Mutable<T> {
	private T value;
	
	public MutableObject(T value) {
		this.value = value;
	}
	
	public MutableObject() {
	}
	
	@Override
	public void setValue(T value) {
		this.value = value;
	}
	
	@Override
	public T getValue() {
		return value;
	}
	
	@Override
	public boolean equals(Object obj) {
		return Objects.equals(obj, value);
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(value);
	}
}
