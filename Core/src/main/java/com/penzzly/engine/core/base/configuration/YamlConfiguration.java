package com.penzzly.engine.core.base.configuration;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Map;

import static com.penzzly.engine.core.base.configuration.serialization.Serializers.YAML;
import static com.penzzly.engine.core.utilites.io.StreamUtil.readString;
import static com.penzzly.engine.core.utilites.io.StreamUtil.write;

public interface YamlConfiguration extends MapConfiguration {
	
	@NotNull File getFile();
	
	@Override
	default void save() {
		write(YAML.dump(get()).getBytes(), getFile());
	}
	
	@Override
	default void load() {
		set(readString(getFile())
				.map(YAML::load)
				.<Map<String, Object>>cast()
				.get());
	}
}