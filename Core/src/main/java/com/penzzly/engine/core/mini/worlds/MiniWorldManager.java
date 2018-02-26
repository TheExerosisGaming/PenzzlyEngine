package com.penzzly.engine.core.mini.worlds;

import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static java.io.File.separator;
import static java.lang.String.format;
import static java.nio.file.Files.*;
import static java.nio.file.Paths.get;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.Optional.ofNullable;
import static org.apache.commons.io.FileUtils.openInputStream;
import static org.apache.commons.io.IOUtils.buffer;
import static org.bukkit.Bukkit.*;
import static org.bukkit.WorldType.FLAT;

//I was so close to using only nio stuff... but I just can't help these look better.
//Feel free to replace with FileInputStream and BufferedInputStream.
//https://gist.github.com/Exerosis/e198e1681172573b51e4ff5627baefc7
public class MiniWorldManager {
	private final Plugin plugin;
	
	public MiniWorldManager(Plugin plugin) {
		this.plugin = plugin;
	}
	
	public interface WorldContainer {
		WorldContainer delete();
		
		WorldContainer load();
		
		WorldContainer unload();
		
		Path getPath();
		
		Optional<World> getWorld();
		
		List<Consumer<World>> getLoadListeners();
		
		List<Consumer<World>> getUnloadListeners();
		
		default String getName() {
			return getPath().getFileName().toString();
		}
		
		default Consumer<World> onUnload(Consumer<World> listener) {
			getUnloadListeners().add(listener);
			return listener;
		}
		
		default Consumer<World> onLoad(Consumer<World> listener) {
			getLoadListeners().add(listener);
			return listener;
		}
	}
	
	//--Zip File or Directory--
	public WorldContainer from(String backup) {
		return from(get(backup));
	}
	
	public WorldContainer from(Path backup) {
		return from(backup, backup.getFileName().toString().replace(".zip", ""));
	}
	
	public WorldContainer from(String backup, String name) {
		return from(get(backup), name);
	}
	
	public WorldContainer from(Path backup, String name) {
		return from(backup, get(name));
	}
	
	public WorldContainer from(Path backup, Path path) {
		if ((backup.endsWith(".zip") && exists(backup))) {
			return ofZip(container -> {
				try {
					return buffer(openInputStream(backup.toFile()));
				} catch (IOException e) {
					throw new LoadException(container.getPath(), backup, true, e);
				}
			}, path);
		}
		if (isDirectory(backup)) {
			return of(container -> {
				try {
					container.delete();
					walk(backup).forEach(file -> {
						try {
							copy(file, container.getPath().resolve(file));
						} catch (IOException reason) {
							throw new LoadException(container.getPath(), backup, file, reason);
						}
					});
				} catch (IOException reason) {
					throw new LoadException(container.getPath(), backup, false, reason);
				}
			}, path);
		}
		throw new CreateException(path);
	}
	
	
	//--URL--
	public WorldContainer from(URL url, String name) {
		return from(url, get(name));
	}
	
	public WorldContainer from(URL url, Path path) {
		return ofZip(container -> {
			try {
				return buffer(url.openStream());
			} catch (IOException e) {
				throw new LoadException(container.getPath(), url, e);
			}
		}, path);
	}
	
	
	//--Raw Zip Input--
	public WorldContainer ofZip(Function<WorldContainer, InputStream> zip, String name) {
		return ofZip(zip, get(name));
	}
	
	public WorldContainer ofZip(Function<WorldContainer, InputStream> zip, Path path) {
		return of(container -> {
			try {
				container.delete();
				unzip(zip.apply(container), container.getPath());
			} catch (Exception e) {
				throw new LoadException(path, e);
			}
		}, path);
	}
	
	
	//--Randomly Generated--
	public WorldContainer of(String name) {
		return of(get(name));
	}
	
	public WorldContainer of(Path path) {
		return of(WorldContainer::delete, path);
	}
	
	
	//--Raw--
	public WorldContainer of(Consumer<WorldContainer> preLoad, String name) {
		return of(preLoad, get(name));
	}
	
	public WorldContainer of(Consumer<WorldContainer> preLoad, Path path) {
		if (plugin == null) {
			throw new CreateException();
		}
		Path resolvedPath = getWorldContainer().toPath().resolve(path);
		WorldContainer container = new WorldContainer() {
			private final List<Consumer<World>> loadListeners = new ArrayList<>();
			private final List<Consumer<World>> unloadListeners = new ArrayList<>();
			private World world = null;
			
			@Override
			public Path getPath() {
				return resolvedPath;
			}
			
			@Override
			public WorldContainer delete() {
				if (world != null) {
					unload();
					//TODO issues here
					getScheduler().runTaskLaterAsynchronously(plugin, this::delete, 100);
				} else {
					try {
						walk(getPath()).forEach(file -> {
							try {
								Files.deleteIfExists(file);
							} catch (IOException e) {
								throw new RemoveException(getPath(), file, e);
							}
						});
					} catch (IOException e) {
						throw new RemoveException(getPath(), e);
					}
				}
				return this;
			}
			
			@Override
			public WorldContainer load() {
				if (world == null) {
					preLoad.accept(this);
					WorldCreator worldCreator = new WorldCreator(getName());
					worldCreator.type(FLAT);
					world = createWorld(worldCreator);
				}
				return this;
			}
			
			@Override
			public WorldContainer unload() {
				if (world != null) {
					unloadListeners.forEach(listener -> listener.accept(world));
					for (Player player : world.getPlayers())
						player.kickPlayer("Your world is unloading!");
					
					//TODO id like to avoid a task, maybe play around with stuff.
					getScheduler().runTaskLater(plugin, () -> {
						if (!unloadWorld(world, false)) {
							throw new UnloadException(world);
						}
						world = null;
					}, 20);
				}
				return this;
			}
			
			@Override
			public List<Consumer<World>> getLoadListeners() {
				return loadListeners;
			}
			
			@Override
			public List<Consumer<World>> getUnloadListeners() {
				return unloadListeners;
			}
			
			@Override
			public Optional<World> getWorld() {
				return ofNullable(world);
			}
		};
		getPluginManager().registerEvents(new Listener() {
			@EventHandler
			void onEvent(WorldLoadEvent event) {
				container.getWorld()
						.filter(world -> event.getWorld().equals(world))
						.ifPresent(world ->
								container.getLoadListeners().forEach(listener ->
										listener.accept(world)
								)
						);
			}
		}, plugin);
		return container;
	}
	
	
	//--Exceptions--
	public static class RemoveException extends RuntimeException {
		static final String ACCESS_MESSAGE = "\tFailed to remove a directory.\n\tWorld: %s";
		static final String PATH_MESSAGE = "\tFailed to remove a file or directory.\n\tPath: %s\n\tWorld: %s";
		
		RemoveException(Path world, Path path, Throwable cause) {
			super(format(PATH_MESSAGE, path, world), cause);
		}
		
		RemoveException(Path world, Throwable cause) {
			super(format(ACCESS_MESSAGE, world), cause);
		}
	}
	
	public static class UnloadException extends RuntimeException {
		static final String MESSAGE = "\tFailed to unload a world.\n\tWorld: %s";
		
		UnloadException(World world) {
			super(format(MESSAGE, world.getName()));
		}
	}
	
	public static class CreateException extends RuntimeException {
		static final String BACKUP_MESSAGE = "\tFailed to find a backup.\n\tType %s\n\tPath: %s";
		static final String PLUGIN_MESSAGE = "\tNot initialized! Call MiniWorldManager.init(<plugin>) before fetching a world container.";
		
		CreateException(Path path) {
			super(format(BACKUP_MESSAGE, path.endsWith(".zip") ? "zip" : "directory", path));
		}
		
		CreateException() {
			super(PLUGIN_MESSAGE);
		}
	}
	
	public static class LoadException extends RuntimeException {
		static final String ZIP_MESSAGE = "\tFailed to unzip a stream.\n\tWorld: %s";
		static final String URL_MESSAGE = "\tFailed to open a url connection.\n\tURL: %s\n\tWorld: %s";
		static final String BACKUP_MESSAGE = "\tFailed to load a backup.\n\tType %s\n\tPath: %s\n\tWorld: %s";
		static final String COPY_MESSAGE = "\tFailed to copy a backup file.\n\tType directory\n\tPath: %s\n\tFile: %s\n\tWorld: %s";
		
		LoadException(Path world, Path path, Path file, Throwable reason) {
			super(format(COPY_MESSAGE, path, file, world), reason);
		}
		
		LoadException(Path world, Path path, boolean zip, Throwable reason) {
			super(format(BACKUP_MESSAGE, zip ? "zip" : "directory", path, world), reason);
		}
		
		LoadException(Path world, URL url, Throwable reason) {
			super(format(URL_MESSAGE, url, world), reason);
		}
		
		LoadException(Path world, Throwable reason) {
			super(format(ZIP_MESSAGE, world), reason);
		}
	}
	
	
	//--Util--
	private static void unzip(InputStream zip, Path destination) throws IOException {
		try (ZipInputStream in = new ZipInputStream(zip)) {
			ZipEntry entry;
			while ((entry = in.getNextEntry()) != null) {
				Path path = destination.resolve(entry.getName());
				if (path.endsWith(separator)) {
					createDirectories(path);
				} else {
					createDirectories(path.getParent());
					copy(in, path, REPLACE_EXISTING);
				}
				in.closeEntry();
			}
		}
	}
}