package com.penzzly.engine.core.components.world;

import com.penzzly.engine.architecture.base.Component;
import com.penzzly.engine.core.utilites.io.FileUtil;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;
import org.bukkit.event.world.WorldLoadEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.penzzly.engine.core.base.Events.listen;
import static com.penzzly.engine.core.utilites.bukkit.ServerUtil.getPlugin;
import static java.io.File.separator;
import static java.util.Arrays.asList;

public class WorldComponent extends Component implements Supplier<World> {
	private final List<Consumer<World>> loadListeners = new ArrayList<>();
	private final List<Consumer<World>> unloadListeners = new ArrayList<>();
	@Nullable
	private World world;
	
	public WorldComponent(@NotNull File worldsDirectory, @NotNull String name) {
		this(worldsDirectory, name, null);
	}
	
	public WorldComponent(@NotNull File worldsDirectory, @NotNull String name, @Nullable URL backupURL) {
		onEnable(() -> {
			System.out.println("Attempting to load world.");
			if (worldsDirectory.isFile()) {
				throw new RuntimeException("Cannot load worlds from a file, please specify a directory instead.");
			}
			if (!worldsDirectory.exists() && !worldsDirectory.mkdirs()) {
				throw new RuntimeException("Cannot locate or create a worlds directory at the given location.");
			}
			File worldDirectory = new File(worldsDirectory.getPath() + separator + name);
			if (!worldDirectory.exists()) {
				if (backupURL == null) {
					throw new RuntimeException("Cannot locate or download a world directory.");
				}
				System.err.println("Failed to find world directory, attempting download.");
				try {
					FileUtil.downloadFile(backupURL, worldsDirectory);
				} catch (IOException e) {
					throw new RuntimeException("Failed to download world zip.");
				}
				if (!worldDirectory.exists()) {
					throw new RuntimeException("Failed to download world zip.");
				}
			}
			System.out.println("Copying world directory.");
			try {
				FileUtils.copyDirectory(worldDirectory, Bukkit.getWorldContainer(), false);
			} catch (IOException e) {
				throw new RuntimeException("Failed to copy world directory.");
			}
			
			WorldCreator worldCreator = new WorldCreator(name);
			worldCreator.type(WorldType.FLAT);
			world = Bukkit.createWorld(worldCreator);
			System.out.println("Loading world.");
		});
		
		onDisable(() -> {
			System.out.println("Attempting to unload world.");
			
			//Remove players
			for (World backupWorld : Bukkit.getWorlds())
				if (backupWorld != world) {
					for (Player player : world.getPlayers())
						player.teleport(backupWorld.getSpawnLocation().add(0, 4, 0));
					return;
				}
			//Wait 1 second before unloading the world.
			Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), () -> {
				if (!Bukkit.unloadWorld(world, false)) {
					System.err.println("Failed to unload world, memory leak warning.");
				} else {
					Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), () -> {
						try {
							System.out.println("Attempting to remove world folder.");
							FileUtils.deleteDirectory(world.getWorldFolder());
						} catch (IOException e) {
							System.err.println("Failed to remove world folder, this is likely to cause world load failures.");
						}
						System.out.println("World folder removed.");
						world = null;
						unloadListeners.forEach(listener -> listener.accept(world));
					}, 4 * 20);
				}
			}, 20);
		});
		
		addChild(listen(WorldLoadEvent.class, event -> loadListeners.forEach(listener -> listener.accept(world))));
	}
	
	@NotNull
	public WorldComponent onLoad(@NotNull Consumer<World>... listeners) {
		getLoadListeners().addAll(asList(listeners));
		if (world != null) {
			for (Consumer<World> listener : listeners)
				listener.accept(world);
		}
		return this;
	}
	
	@NotNull
	public WorldComponent onUnload(@NotNull Consumer<World>... listeners) {
		if (listeners.length > 1) {
			getUnloadListeners().addAll(asList(listeners));
		} else {
			getUnloadListeners().add(listeners[0]);
		}
		return this;
	}
	
	@Nullable
	@Override
	public World get() {
		return getWorld();
	}
	
	@Nullable
	public World getWorld() {
		return world;
	}
	
	@NotNull
	public List<Consumer<World>> getLoadListeners() {
		return loadListeners;
	}
	
	@NotNull
	public List<Consumer<World>> getUnloadListeners() {
		return unloadListeners;
	}
}
