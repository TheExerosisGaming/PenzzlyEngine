package com.penzzly.engine.core.mini.partial;

import org.apache.commons.lang3.text.StrLookup;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class StringReplacer<Type> implements Function<Type, StrLookup> {
	private final Map<String, Function<Type, Object>> replacements = new HashMap<>();
	
	public static <Type> StringReplacer<Type> replacements() {
		return new StringReplacer<>();
	}
	
	@Override
	public StrLookup apply(Type type) {
		return new StrLookup() {
			@Override
			public String lookup(String name) {
				return replacements.get(name).apply(type).toString();
			}
		};
	}
	
	public StringReplacer<Type> field(Object name, Object value) {
		return field(name, value::toString);
	}
	
	public StringReplacer<Type> field(Object name, Supplier<Object> value) {
		return field(name, $ -> value.get());
	}
	
	public StringReplacer<Type> field(Object name, Function<Type, Object> value) {
		replacements.put(name.toString(), value);
		return this;
	}
}