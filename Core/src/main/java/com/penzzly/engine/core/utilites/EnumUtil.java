package com.penzzly.engine.core.utilites;

import org.jetbrains.annotations.NotNull;

import static java.util.EnumSet.allOf;

public class EnumUtil {
	public static <T extends Enum<T>> T closest(@NotNull String query, Class<T> type) {
		return StringUtil.closest(query.toUpperCase().replace(' ', '_'), allOf(type), Enum::name);
	}
}
