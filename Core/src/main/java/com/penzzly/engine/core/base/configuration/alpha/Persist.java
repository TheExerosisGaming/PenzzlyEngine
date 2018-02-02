package com.penzzly.engine.core.base.configuration.alpha;

import com.penzzly.engine.architecture.base.Component;
import com.penzzly.engine.core.base.Configurations;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.penzzly.engine.core.utilites.bukkit.ServerUtil.getPlugin;
import static java.lang.Class.forName;
import static java.lang.Thread.currentThread;
import static net.jodah.typetools.TypeResolver.resolveRawArgument;

public class Persist {
	
	@NotNull
	@SuppressWarnings("unchecked")
	public static <T> Class<T> typeOf(@NotNull Consumer<T> typeResolver) {
		return (Class<T>) resolveRawArgument(Consumer.class, typeResolver.getClass());
	}
	
	public static void main(String[] args) {
		System.out.println(typeOf((String t) -> {
		}));
	}
	
	public static <Type> Component persist(@NotNull List<Type> value) {
		try {
			String name = forName(currentThread().getStackTrace()[2].getClassName()).getSimpleName() + ".json";
			return new Configurations.GsonConfigurationComponent<List<Type>>() {
				private final File file = new File(getPlugin().getDataFolder(), name);
				
				@NotNull
				@Override
				public Configurations.GsonConfigurationComponent<List<Type>> enable() {
					if (!isEnabled()) {
						load();
						super.enable();
					}
					return this;
				}
				
				@NotNull
				@Override
				public Configurations.GsonConfigurationComponent<List<Type>> disable() {
					if (isEnabled()) {
						super.disable();
						save();
					}
					return this;
				}
				
				@NotNull
				@Override
				public File getFile() {
					return file;
				}
				
				@Override
				public void set(@Nullable List<Type> newValue) {
					if (newValue == null || newValue.isEmpty()) {
						save();
					} else {
						value.clear();
						value.addAll(newValue);
					}
				}
				
				@NotNull
				@Override
				public List<Type> get() {
					return value;
				}
			};
		} catch (ClassNotFoundException e) {
			throw new RuntimeException();
		}
	}
	
	@NotNull
	public static <Key, Value> Component persist(@NotNull Map<Key, Value> value) {
		try {
			String name = forName(currentThread().getStackTrace()[2].getClassName()).getSimpleName() + ".json";
			return new Configurations.GsonConfigurationComponent<Map<Key, Value>>() {
				private final File file = new File(getPlugin().getDataFolder(), name);
				
				@NotNull
				@Override
				public Configurations.GsonConfigurationComponent<Map<Key, Value>> enable() {
					if (!isEnabled()) {
						load();
					}
					super.enable();
					return this;
				}
				
				@NotNull
				@Override
				public Configurations.GsonConfigurationComponent<Map<Key, Value>> disable() {
					super.disable();
					save();
					return this;
				}
				
				@NotNull
				@Override
				public File getFile() {
					return file;
				}
				
				@Override
				public void set(@Nullable Map<Key, Value> newValue) {
					if (newValue == null) {
						save();
					} else {
						value.clear();
						value.putAll(newValue);
					}
				}
				
				@NotNull
				@Override
				public Map<Key, Value> get() {
					return value;
				}
			};
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}
