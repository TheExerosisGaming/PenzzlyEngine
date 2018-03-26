package com.penzzly.engine.core.mini;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;

import static java.lang.ClassLoader.getSystemResourceAsStream;
import static java.nio.file.Files.*;
import static java.nio.file.Paths.get;
import static org.bukkit.configuration.file.YamlConfiguration.loadConfiguration;

public class TinyConfig {
	
	interface Resource<Type> extends Closeable {
		default Optional<Type> open() {
			try {
				return Optional.ofNullable(openUnsafe());
			} catch (Exception e) {
				return Optional.empty();
			}
		}
		
		default void closeUnsafe() {
			try {
				close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		
		Type openUnsafe() throws Exception;
	}
	
	public static Resource<Properties> properties(String name) {
		return properties(Paths.get("/", name));
	}
	
	public static Resource<Properties> properties(Path file) {
		return properties(file.getFileName(), file);
	}
	
	public static Resource<Properties> properties(Path resource, Path file) {
		return new Resource<Properties>() {
			final Properties properties = new Properties();
			
			@Override
			public Properties openUnsafe() throws Exception {
				createDirectories(file.getParent());
				if (!exists(file)) {
					if (resource != null) {
						InputStream stream = getSystemResourceAsStream(resource.toString());
						if (stream == null)
							throw new IllegalStateException("Could not find a resource at: " + resource);
						copy(stream, file);
					} else
						createFile(file);
				}
				
				properties.load(newBufferedReader(file));
				return properties;
			}
			
			@Override
			public void close() throws IOException {
				properties.store(newBufferedWriter(file), "");
			}
		};
	}
	
	public static class Resources {
		interface Resource<Type> extends Closeable {
			default Optional<Type> open() {
				try {
					return Optional.ofNullable(openUnsafe());
				} catch (Exception e) {
					return Optional.empty();
				}
			}
			
			default void closeUnsafe() {
				try {
					close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
			
			Type openUnsafe() throws Exception;
		}
		
		public static Resource<Properties> properties(String name) {
			return properties(Paths.get("/", name));
		}
		
		public static Resource<Properties> properties(Path file) {
			return properties(file.getFileName(), file);
		}
		
		public static Resource<Properties> properties(Path resource, Path file) {
			return new Resource<Properties>() {
				final Properties properties = new Properties();
				
				@Override
				public Properties openUnsafe() throws Exception {
					createDirectories(file.getParent());
					if (!exists(file)) {
						if (resource != null) {
							InputStream stream = getSystemResourceAsStream(resource.toString());
							if (stream == null)
								throw new IllegalStateException("Could not find a resource at: " + resource);
							copy(stream, file);
						} else
							createFile(file);
					}
					
					properties.load(newBufferedReader(file));
					return properties;
				}
				
				@Override
				public void close() throws IOException {
					properties.store(newBufferedWriter(file), "");
				}
			};
		}
	}
	
	public static class YamlConfig implements Resource<FileConfiguration> {
		private final Path path;
		private final Path resource;
		private YamlConfiguration config;
		
		public YamlConfig(Path path, Path resource) {
			this.path = path;
			this.resource = resource;
		}
		
		@Override
		public FileConfiguration openUnsafe() throws Exception {
			createDirectories(path.getParent());
			if (!exists(path)) {
				if (resource != null) {
					InputStream stream = getSystemResourceAsStream(this.resource.toString());
					if (stream == null)
						throw new IllegalStateException("Could not find a resource at: " + resource);
					copy(stream, path);
				} else
					createFile(path);
			}
			config = loadConfiguration(newBufferedReader(path));
			return config;
		}
		
		@Override
		public void close() throws IOException {
			write(path, config.saveToString().getBytes());
		}
	}
	
	public static YamlConfig config(Plugin plugin, String name) {
		return config(plugin, get(name));
	}
	
	public static YamlConfig config(Plugin plugin, Path path) {
		return config(plugin.getDataFolder().toPath().resolve(path));
	}
	
	public static YamlConfig config(Path path) {
		return new YamlConfig(path, path.getFileName());
	}
}
