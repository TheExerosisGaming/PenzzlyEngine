package com.penzzly.engine.core.mini;

import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;
import static java.nio.file.Files.*;

//https://gist.github.com/Exerosis/a2fbfd160340f1126f89cf6d056ed648
@SuppressWarnings("unchecked")
public class MiniConfigurations {
	public static Yaml YAML = new Yaml(); //Feel free to register type adapters and sutff.
	
	//--Configuration--
	public interface Configuration {
		/**
		 * Gets the underlying map this {@link Configuration}
		 * delegates to.
		 *
		 * @return The underlying map.
		 */
		Map<String, Object> getMap();
		
		//--Getters-
		default int size() {
			return getMap().size();
		}
		
		default boolean isEmpty() {
			return getMap().isEmpty();
		}
		
		
		//--Get--
		
		/**
		 * Gets and casts the value associated with a given key.
		 *
		 * @param key
		 * 		The key associated with the target value.
		 * 		(After calling {@link Object#toString()})
		 * @param <Type>
		 * 		The type to attempt to cast the value to.
		 * @return The value associated with the key as {@link Type}.
		 */
		default <Type> Type get(Object key) {
			//FIXME catch cast exceptions.
			return (Type) getMap().get(key.toString());
		}
		
		/**
		 * Gets and casts the value associated with a given key or returns a default
		 * which optionally becomes the new associated value.
		 *
		 * @param key
		 * 		The key associated with the target value.
		 * 		(After calling {@link Object#toString()})
		 * @param or
		 * 		The optional default value to associate if the key has no association.
		 * @param saveDefault
		 * 		Whether to use the default value as a new association.
		 * @param <Type>
		 * 		The type to attempt to cast the value to.
		 * @return The final value associated with the key as {@link Type}.
		 */
		default <Type> Type get(Object key, Type or, boolean saveDefault) {
			Class<Type> type = (Class<Type>) or.getClass();
			Object value = get(key);
			if (value == null) {
				if (saveDefault) {
					set(key, or);
				}
				return or;
			}
			try {
				return type.cast(value);
			} catch (ClassCastException e) {
				throw new TypeMismatchException(key, value, type);
			}
		}
		
		/**
		 * Gets and casts the value associated with a given key or returns a default.
		 *
		 * @param key
		 * 		The key associated with the target value.
		 * 		(After calling {@link Object#toString()})
		 * @param or
		 * 		The default value to return if the key has no association.
		 * @param <Type>
		 * 		The type to attempt to cast the value to.
		 * @return The value associated with the key or the default as {@link Type}.
		 */
		default <Type> Type get(Object key, Type or) {
			return get(key, or, false);
		}
		
		/**
		 * Gets and casts the value associated with a given key or returns a default
		 * which becomes the new associated value.
		 *
		 * @param key
		 * 		The key associated with the target value.
		 * 		(After calling {@link Object#toString()})
		 * @param or
		 * 		The default value to associate if the key has no association.
		 * @param <Type>
		 * 		The type to attempt to cast the value to.
		 * @return The final value associated with the key as {@link Type}.
		 */
		default <Type> Type getDefault(Object key, Type or) {
			return get(key, or, true);
		}
		
		
		//--Set--
		
		/**
		 * Sets the value at a given key.
		 *
		 * @param key
		 * 		The key to associate the value with.
		 * 		(After calling {@link Object#toString()})
		 * @param value
		 * 		The value to associate with the key.
		 * @param <Type>
		 * 		The type to attempt to cast the previously
		 * 		associated value to.
		 * @return The previously associated value as {@link Type} or {@code null}.
		 */
		default <Type> Type set(Object key, Object value) {
			return (Type) getMap().put(key.toString(), value);
		}
		
		//--Remove--
		
		/**
		 * Removes the value at a given key.
		 *
		 * @param key
		 * 		The key to unassociate.
		 * @param <Type>
		 * 		The type to attempt to cast the previously
		 * 		associated value to.
		 * @return The previously associated value as {@link Type} or {@code null}.
		 */
		default <Type> Type remove(Object key) {
			return (Type) getMap().remove(key.toString());
		}
		
		/**
		 * Removes the value at a given key.
		 *
		 * @param key
		 * 		The key to unassociate.
		 * @param or
		 * 		The default value to return if the key had no previous
		 * 		association.
		 * @param <Type>
		 * 		The type to attempt to cast the previously
		 * 		associated value to.
		 * @return The previously associated value or the default as {@link Type}.
		 */
		default <Type> Type remove(Object key, Type or) {
			Class<Type> type = (Class<Type>) or.getClass();
			Object value = remove(key);
			if (value == null) {
				return or;
			}
			try {
				return type.cast(value);
			} catch (ClassCastException e) {
				throw new TypeMismatchException(key, value, type);
			}
		}
		
		
		//--Section--
		
		/**
		 * Gets the {@link Configuration} representation of a {@link Map}
		 * value associated with a given key.
		 *
		 * @param key
		 * 		The key associated with the target value.
		 * 		(After calling {@link Object#toString()})
		 * @return The {@link Configuration} representation of the
		 * value or an empty {@link Map}.
		 */
		default Configuration section(Object key) {
			return () -> get(key, new HashMap<>());
		}
		
		
		//--Contains--
		
		/**
		 * Checks if there is a key by the given name.
		 *
		 * @param key
		 * 		The key to check for
		 * 		(After calling {@link Object#toString()})
		 * @return true if the key exists.
		 */
		default boolean containsKey(Object key) {
			return getMap().containsKey(key.toString());
		}
		
		/**
		 * Checks if there is a key associated with the given value
		 *
		 * @param value
		 * 		The value to check for.
		 * @return true if there is a key associated with the value.
		 */
		default boolean containsValue(Object value) {
			return getMap().containsValue(value);
		}
	}
	
	public static Configuration of(final Map<String, Object> map) {
		return () -> map;
	}
	
	public static Configuration empty() {
		return of(new HashMap<>());
	}
	
	
	//--Yaml Configuration--
	public interface YamlConfiguration extends Configuration {
		//FIXME decide if we are going to load/save overwrite or merge.
		YamlConfiguration load();
		
		YamlConfiguration save();
		
		@Override
		default YamlConfiguration section(Object key) {
			return new YamlConfiguration() {
				@Override
				public YamlConfiguration load() {
					return YamlConfiguration.this.load();
				}
				
				@Override
				public YamlConfiguration save() {
					return YamlConfiguration.this.save();
				}
				
				@Override
				public Map<String, Object> getMap() {
					return YamlConfiguration.this.getDefault(key, new HashMap<>());
				}
			};
		}
		
		default String toYaml() {
			return YAML.dump(getMap());
		}
	}
	
	public static YamlConfiguration from(Mutable<String> yaml) {
		return new YamlConfiguration() {
			private Map<String, Object> map = new HashMap<>();
			
			@Override
			public YamlConfiguration load() {
				Object loaded = YAML.load(yaml.getValue());
				if (loaded != null)
					map = (Map<String, Object>) loaded;
				return this;
			}
			
			@Override
			public YamlConfiguration save() {
				yaml.setValue(toYaml());
				return this;
			}
			
			@Override
			public Map<String, Object> getMap() {
				return map;
			}
		};
	}
	
	public static YamlConfiguration from(Object string) {
		return from(new MutableObject<>(string.toString()));
	}
	
	public static YamlConfiguration from(URI uri) {
		return from(Paths.get(uri));
	}
	
	public static YamlConfiguration from(String path) {
		return from(Paths.get(path));
	}
	
	public static YamlConfiguration from(File file) {
		return from(file.toPath());
	}
	
	public static YamlConfiguration from(Path path) {
		return from(new Mutable<String>() {
			@Override
			public String getValue() {
				try {
					if (!exists(path)) {
						createDirectories(path.getParent());
						createFile(path);
					}
					return new String(readAllBytes(path));
				} catch (Exception e) {
					throw new YamlIOException(e, path, false);
				}
			}
			
			@Override
			public void setValue(String yaml) {
				try {
					createDirectories(path.getParent());
					write(path, yaml.getBytes());
				} catch (Exception e) {
					throw new YamlIOException(e, path, true);
				}
			}
		});
		
	}
	
	
	//--Exceptions--
	public static class TypeMismatchException extends RuntimeException {
		private static final String MESSAGE = "\tFailed to find the correct type!\n\tKey: %s\n\tValue: %s\n\tValue Type: %s\n\tExpected Type: %s\n";
		
		public TypeMismatchException(Object key, Object value) {
			this(key, value, null);
		}
		
		public TypeMismatchException(Object key, Object value, Class<?> type) {
			super(format(MESSAGE,
					key,
					value,
					value.getClass().getSimpleName(),
					type == null ? " Unknown(Caused by generic erasure)" : type.getSimpleName()
			));
		}
	}
	
	public static class YamlIOException extends RuntimeException {
		private static final String MESSAGE = "\tFailed to %s a yaml configuration!\n\tPath: %s";
		
		public YamlIOException(Throwable reason, Path path, boolean save) {
			super(format(MESSAGE, save ? "save" : "load", path), reason);
		}
	}
}