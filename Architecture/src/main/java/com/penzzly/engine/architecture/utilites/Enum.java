package com.penzzly.engine.architecture.utilites;

import org.jetbrains.annotations.NotNull;

public interface Enum<Type extends java.lang.Enum<Type>> {
	int flag();
	
	//--And--
	@NotNull
	default Enum<Type> and(int values) {
		final int flag = flag() | values;
		return () -> flag;
	}
	
	@NotNull
	default Enum<Type> and(@NotNull Enum value) {
		return and(value.flag());
	}
	
	//--Is In--
	default boolean isIn(@NotNull Enum value) {
		return isIn(value.flag());
	}
	
	default boolean isIn(int value) {
		return (flag() & value) != 0;
	}
	
	//--Not---
	@NotNull
	default Enum<Type> not(@NotNull Enum value) {
		return not(value.flag());
	}
	
	@NotNull
	default Enum<Type> not(int values) {
		final int flag = flag() & ~values;
		return () -> flag;
	}
}
