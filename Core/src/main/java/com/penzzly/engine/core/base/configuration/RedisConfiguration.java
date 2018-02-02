package com.penzzly.engine.core.base.configuration;

import org.apache.commons.lang.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RFuture;
import org.redisson.api.RMap;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;

import static java.util.stream.Collectors.toList;

@SuppressWarnings("unchecked")
public interface RedisConfiguration extends MapConfiguration {
	
	@Override
	default void save() {
		throw new NotImplementedException("Redisson maps save on put, save does not effect this configuration.");
	}
	
	@Override
	default void load() {
		throw new NotImplementedException("Redisson maps load values on getOrSet, load does not effect this configuration.");
	}
	
	@NotNull
	default <T> RFuture<T> getAsync(@NotNull Object key) {
		return (RFuture<T>) get().getAsync(key.toString());
	}
	
	default CompletionStage<MapConfiguration> getSectionAsync(@NotNull Object key) {
		return this.<RMap<String, Object>>getAsync(key).thenApply(result -> MapConfiguration.section(this, result));
	}
	
	default CompletionStage<List<MapConfiguration>> getSectionsAsync(@NotNull Object key) {
		return this.<List<RMap<String, Object>>>getAsync(key).thenApply(result -> result.stream().map(section -> MapConfiguration.section(this, section)).collect(toList()));
	}
	
	@Override
	default void remove(@NotNull Object key) {
		get().fastRemove(key.toString());
	}
	
	default void removeAsync(@NotNull Object key) {
		get().fastRemoveAsync(key.toString());
	}
	
	@Override
	default void set(@NotNull Object key, Object value) {
		get().fastPut(key.toString(), value);
	}
	
	default void setAsync(@NotNull Object key, Object value) {
		get().fastPutAsync(key.toString(), value);
	}
	
	@NotNull
	@Override
	RMap<String, Object> get();
	
	default void setAsync(Map<String, Object> map) {
		get().clear();
		get().putAllAsync(map);
	}
}