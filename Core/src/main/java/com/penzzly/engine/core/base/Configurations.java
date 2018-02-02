package com.penzzly.engine.core.base;

import com.penzzly.engine.architecture.base.Component;
import com.penzzly.engine.core.base.configuration.GsonConfiguration;
import com.penzzly.engine.core.base.configuration.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import static com.penzzly.engine.core.base.configuration.serialization.Serializers.GSON;
import static com.penzzly.engine.core.utilites.bukkit.ServerUtil.getPlugin;
import static com.penzzly.engine.core.utilites.io.StreamUtil.readString;
import static java.lang.Class.forName;
import static java.lang.Thread.currentThread;

//FIXME Gson configs are shit atm.
public class Configurations {
	@NotNull
	public static YamlConfigurationComponent ymlConfig() {
		return ymlConfig(new HashMap<>());
	}
	
	@NotNull
	public static YamlConfigurationComponent ymlConfig(@NotNull Map<String, Object> defaultValue) {
		try {
			return ymlConfig(forName(currentThread().getStackTrace()[3].getClassName()).getSimpleName(), defaultValue);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static YamlConfigurationComponent ymlConfig(Object name, @NotNull Map<String, Object> defaultValue) {
		return new YamlConfigurationComponent() {
			private final File file = new File(getPlugin().getDataFolder(), name + ".yml");
			private Map<String, Object> value;
			
			@NotNull
			@Override
			public YamlConfigurationComponent enable() {
				if (!isEnabled()) {
					load();
					if (!isPresent()) {
						set(defaultValue);
						save();
					}
					super.enable();
				}
				return this;
			}
			
			
			@Override
			public void set(Map<String, Object> value) {
				this.value = value;
			}
			
			@NotNull
			@Override
			public File getFile() {
				return file;
			}
			
			@Override
			public Map<String, Object> get() {
				return value;
			}
		};
	}
	
	public static <Data> GsonConfigurationComponent<Data> jsonConfig(Type type) {
		try {
			return jsonConfig(forName(currentThread().getStackTrace()[2].getClassName()).getSimpleName(), null, type);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static <Data> GsonConfigurationComponent<Data> jsonConfig(Data defaultValue, Type type) {
		try {
			return jsonConfig(forName(currentThread().getStackTrace()[2].getClassName()).getSimpleName(), defaultValue, type);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static <Data> GsonConfigurationComponent<Data> jsonConfig(Object name, Data defaultValue, Type type) {
		return new GsonConfigurationComponent<Data>() {
			private Data value;
			private final File file = new File(getPlugin().getDataFolder(), name + ".json");
			
			@Override
			public void load() {
				set(readString(getFile())
						.map(json -> GSON.fromJson(json, type))
						.<Data>cast()
						.get());
			}
			
			@NotNull
			@Override
			public GsonConfigurationComponent<Data> enable() {
				if (!isEnabled()) {
					load();
					if (!isPresent()) {
						set(defaultValue, true);
					}
				}
				super.enable();
				return this;
			}
			
			@NotNull
			@Override
			public GsonConfigurationComponent<Data> disable() {
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
			public void set(Data value) {
				this.value = value;
			}
			
			@Override
			public Data get() {
				return value;
			}
		};
	}
	
	/**
	 * This class must remain abstract to correctly resolve the Data for deserialization.
	 * @param <Type>
	 */
	public abstract static class GsonConfigurationComponent<Type> extends Component implements GsonConfiguration<Type> {
	
	}
	
	public abstract static class YamlConfigurationComponent extends Component implements YamlConfiguration {
		
		@NotNull
		@Override
		public YamlConfigurationComponent disable() {
			if (isEnabled()) {
				super.disable();
				save();
			}
			return this;
		}
		
		@NotNull
		@Override
		public YamlConfigurationComponent enable() {
			super.enable();
			return this;
		}
	}
}