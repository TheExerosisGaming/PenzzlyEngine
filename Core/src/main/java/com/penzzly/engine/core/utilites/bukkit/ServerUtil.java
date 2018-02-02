package com.penzzly.engine.core.utilites.bukkit;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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

import java.util.UUID;
import java.util.concurrent.Callable;

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
	
	public static void broadcast(String message, @NotNull Iterable<Player> players) {
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
