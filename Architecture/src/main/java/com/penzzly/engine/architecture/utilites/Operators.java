package com.penzzly.engine.architecture.utilites;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class Operators {
	
	public static <T> void Switch(@NotNull T input, @NotNull Consumer<Swi.Ca<T>> closure) {
		Swi.tch(input, closure);
	}
	
	public static Tr.Catch Try(@NotNull Tr.Try trial) {
		return Tr.y(trial);
	}
}
