package com.penzzly.engine.core.utilites.bukkit;

import com.google.common.base.Optional;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.LazyMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.Callable;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static org.bukkit.Bukkit.*;
import static org.bukkit.metadata.LazyMetadataValue.CacheStrategy;

/**
 * Created by BinaryBench on 4/23/2017.
 */
public class ServerUtil {
	
	private static Plugin plugin;
	
	public static void init(Plugin plugin) {
		ServerUtil.plugin = plugin;
	}
	
	public static MetadataValue createLazyMeta(@NotNull Callable<Object> value, @NotNull CacheStrategy strategy) {
		return new LazyMetadataValue(plugin, strategy, value);
	}
	
	public static MetadataValue createLazyMeta(@NotNull Callable<Object> value) {
		return new LazyMetadataValue(plugin, value);
	}
	
	public static MetadataValue createFixedMeta(Object value) {
		return new FixedMetadataValue(plugin, value);
	}
	
	public static void shutdown(String shutdownMessage) {
		shutdown(shutdownMessage, "Bye bye");
	}
	
	public static void shutdown(String shutdownMessage, String kickMessage) {
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			player.kickPlayer(kickMessage);
		}
		
		System.err.println(" ");
		System.err.println(shutdownMessage);
		System.err.println(" ");
		
		Bukkit.shutdown();
	}
	
	public static PluginMessageListener registerChannels(String name, PluginMessageListener listener) {
		registerChannel(name);
		return registerChannel(name, listener);
	}
	
	public static PluginMessageListener registerChannel(String name, PluginMessageListener listener) {
		getMessenger().registerIncomingPluginChannel(getPlugin(), name, listener);
		return listener;
	}
	
	public static void unregisterChannel(String name, PluginMessageListener listener) {
		getMessenger().unregisterIncomingPluginChannel(getPlugin(), name, listener);
	}
	
	public static void registerChannel(String name) {
		getMessenger().unregisterOutgoingPluginChannel(getPlugin(), name);
	}
	
	public static void unregisterChannel(String name) {
		getMessenger().unregisterIncomingPluginChannel(getPlugin(), name);
	}
	
	public static void sendPluginMessage(@NotNull Player player, String channel, byte[] bytes) {
		player.sendPluginMessage(getPlugin(), channel, bytes);
	}
	
	public static void sendPluginMessage(@NotNull Player player, String channel, @NotNull String... messages) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		stream(messages).forEach(out::writeUTF);
		player.sendPluginMessage(getPlugin(), channel, out.toByteArray());
	}
	
	public static void broadcastPluginMessage(String channel, String... messages) {
		Bukkit.getOnlinePlayers().forEach(player -> sendPluginMessage(player, channel, messages));
	}
	
	public static void broadcastPluginMessage(String channel, byte[] bytes) {
		Bukkit.getOnlinePlayers().forEach(player -> sendPluginMessage(player, channel, bytes));
	}
	
	interface DataSerializer<Type> {
		Collection<Type> possibleValues(); //Or something like that?
		
		String serialize(Type value);
		
		Optional<Type> deserialize(String value);
		
		default Class<Type> type() {
			return (Class<Type>) possibleValues().iterator().next().getClass();
		}
	}
	
	static class DataMasker<Type extends Comparable<Type>> {
		private final IBlockState<Type> state;
		
		
		public DataMasker(String name, DataSerializer<Type> serializer) {
			this.state = new IBlockState<Type>() {
				@Override
				public String a() {
					return name;
				}
				
				@Override
				public Collection<Type> c() {
					return serializer.possibleValues();
				}
				
				@Override
				public Class<Type> b() {
					return serializer.type();
				}
				
				@Override
				public Optional<Type> b(String value) {
					return serializer.deserialize(value);
				}
				
				@Override
				public String a(Type value) {
					return serializer.serialize(value);
				}
			};
		}
		
		public void setMaskedData(Block block, Type data) {
			World world = ((CraftWorld) block.getWorld()).getHandle();
			int x = block.getX(), y = block.getY(), z = block.getZ();
			
			
			ChunkSection section = world.getChunkAt(x >> 4, z >> 4).getSections()[y >> 4];
			int rX = x & 15, rY = y & 15, rZ = z & 15;
			IBlockData customData = section.getType(rX, rY, rZ);
			customData.set(state, data);
			section.setType(rX, rY, rZ, customData);
		}
		
		public Type getMaskedData(Block block) {
			World world = ((CraftWorld) block.getWorld()).getHandle();
			int x = block.getX(), y = block.getY(), z = block.getZ();
			
			ChunkSection section = world.getChunkAt(x >> 4, z >> 4).getSections()[y >> 4];
			IBlockData customData = section.getType(x & 15, y & 15, z & 15);
			return customData.get(state);
		}
	}
	
	public static TileEntity getTitleEntity(Block block) {
		World world = ((CraftWorld) block.getWorld()).getHandle();
		int x = block.getX(), z = block.getZ();
		BlockPosition position = new BlockPosition(x, block.getY(), z);
		return world.getChunkAt(x >> 4, z >> 4).tileEntities.get(position);
	}
	
	public static void setTileEntity(Block block, TileEntity entity) {
		World world = ((CraftWorld) block.getWorld()).getHandle();
		int x = block.getX(), z = block.getZ();
		BlockPosition position = new BlockPosition(x, block.getY(), z);
		world.getChunkAt(x >> 4, z >> 4).tileEntities.put(position, entity);
	}
	
	
	public static void broadcast(String message, @NotNull Iterable<Player> players) {
		class CustomData implements Comparable<CustomData> {
			final String first;
			final int second;
			
			CustomData(String first, int second) {
				this.first = first;
				this.second = second;
			}
			
			@Override
			public int compareTo(@NotNull CustomData o) {
				return Integer.compare(second, o.second);
			}
		}
		
		DataSerializer<CustomData> serializer = new DataSerializer<CustomData>() {
			@Override
			public Collection<CustomData> possibleValues() {
				return asList(new CustomData("typeA", 1), new CustomData("typeB", 2));
			}
			
			@Override
			public String serialize(CustomData value) {
				return value.first + ":" + value.second;
			}
			
			@Override
			public Optional<CustomData> deserialize(String value) {
				String[] split = value.split(":");
				if (split.length < 2)
					return Optional.absent();
				return Optional.of(new CustomData(split[0], Integer.parseInt(split[1])));
			}
		};
		
		DataMasker<CustomData> masker = new DataMasker<>("customData", serializer);
		Block block = Bukkit.getWorlds().get(0).getBlockAt(0, 0, 0);
		masker.setMaskedData(block, new CustomData("Whatever", 10));
		CustomData data = masker.getMaskedData(block);
		for (Player player : players)
			player.sendMessage(message);
	}
	
	public static void broadcast(String message) {
		broadcastMessage(message);
	}
	
	public static Player getPlayer(String name) {
		return Bukkit.getPlayer(name);
	}
	
	public static Player getPlayer(UUID uuid) {
		return Bukkit.getPlayer(uuid);
	}
	
	public static boolean isOnline(UUID uuid) {
		return getPlayer(uuid) != null;
	}
	
	public static UUID getPlayersUUID(String name) {
		return getOfflinePlayer(name).getUniqueId();
	}
	
	@SuppressWarnings("deprecation")
	public static OfflinePlayer getOfflinePlayer(String name) {
		return Bukkit.getOfflinePlayer(name);
	}
	
	public static OfflinePlayer getOfflinePlayer(UUID uuid) {
		return Bukkit.getOfflinePlayer(uuid);
	}

//	public static List<String> getOnlinePlayerNames() {
//		return getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList());
//	}

//	public static Collection<? extends Player> getOnlinePlayers() {
//		return Bukkit.getOnlinePlayers();
//	}
	
	public static Plugin getPlugin() {
		if (plugin == null) {
			plugin = getPluginManager().getPlugins()[0];
		}
		return plugin;
	}
	
	public static void registerListener(Listener listener) {
		getPluginManager().registerEvents(listener, getPlugin());
	}
	
	public static void unregisterListener(Listener listener) {
		HandlerList.unregisterAll(listener);
	}
	
	public static void callEvent(Event event) {
		getPluginManager().callEvent(event);
	}
}
