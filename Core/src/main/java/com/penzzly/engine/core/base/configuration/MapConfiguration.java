package com.penzzly.engine.core.base.configuration;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 * Created by Exerosis.
 */
public interface MapConfiguration extends Configuration<Map<String, Object>> {
	
	static MapConfiguration section(@NotNull MapConfiguration parent, @NotNull Map<String, Object> section) {
		return new MapConfiguration() {
			@NotNull
			@Override
			public Map<String, Object> get() {
				return section;
			}
			
			@Override
			public void save() {
				parent.save();
			}
			
			@Override
			public void load() {
				parent.load();
			}
		};
	}
	
	@Override
	default void set(@Nullable Map<String, Object> value) {
		if (value != null) {
			get().clear();
			get().putAll(value);
		}
	}
	
	@Nullable
	@SuppressWarnings("unchecked")
	default <T> T get(@NotNull Object key) {
		return (T) get().get(key.toString());
	}
	
	@Nullable
	default <T> T get(@NotNull Object key, T defaultValue) {
		T result = get(key);
		if (result != null) {
			return result;
		}
		return defaultValue;
	}
	
	@Nullable
	default <T> T getOrSet(@NotNull Object key, T defaultValue) {
		T result = get(key);
		if (result != null) {
			return result;
		}
		set(key, defaultValue);
		return defaultValue;
	}
	
	default void set(@NotNull Object key, Object value) {
		get().put(key.toString(), value);
	}
	
	default void remove(@NotNull Object key) {
		get().remove(key.toString());
	}
	
	default boolean contains(@NotNull Object key) {
		return get().containsKey(key.toString());
	}
	
	@NotNull
	default MapConfiguration getSection(@NotNull Object key) {
		return section(this, getOrSet(key, new HashMap<>()));
	}
	
	default List<MapConfiguration> getSections(@NotNull Object key) {
		return this.<List<Map<String, Object>>>get(key).stream().map(section -> section(this, section)).collect(toList());
	}
}