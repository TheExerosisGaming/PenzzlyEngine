package com.penzzly.engine.core.base.configuration.serialization.alpha;

import com.penzzly.engine.architecture.base.Component;
import org.jetbrains.annotations.NotNull;

import static com.penzzly.engine.architecture.utilites.Components.create;

public class Serializers {
	public static TempFormat DEFAULT_FORMAT;
	
	public static FormatBuilder persist(Object data) {
		return new FormatBuilder();
	}
	
	public static <Data> Data persisted(Component component, Data data) {
		return data;
	}
	
	public static class FormatBuilder {
		public TempFormat with(TempFormat format) {
			return format;
		}
	}
	
	public <Data> Data insanelyPersisted(@NotNull Component component, Data data) {
		component.addChild(create(() -> {
		
		}, () -> {
		
		}));
		return data;
	}
	
	public <Data> Data unsafelyPersisted(@NotNull Component component, Data data) {
		component.addChild(create(() -> {
		
		}, () -> {
		
		}));
		return data;
	}
}
