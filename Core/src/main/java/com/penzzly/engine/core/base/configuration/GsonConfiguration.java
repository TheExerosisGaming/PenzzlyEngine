package com.penzzly.engine.core.base.configuration;

import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.io.File;

import static com.penzzly.engine.core.base.configuration.serialization.Serializers.GSON;
import static com.penzzly.engine.core.utilites.io.StreamUtil.readString;
import static com.penzzly.engine.core.utilites.io.StreamUtil.write;

public interface GsonConfiguration<Type> extends Configuration<Type> {
	
	@NotNull File getFile();
	
	@Override
	default void load() {
		set(readString(getFile())
				.map(json -> GSON.fromJson(json, new TypeToken<Type>() {
				}.getType()))
				.<Type>cast()
				.get());
	}
	
	@Override
	default void save() {
		if (isPresent()) {
			write(GSON.toJson(get()).getBytes(), getFile());
		}
	}
	
	default void set(Type value, boolean save) {
		set(value);
		if (save) {
			save();
		}
	}
}