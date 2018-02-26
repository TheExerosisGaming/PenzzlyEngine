package com.penzzly.engine.core.mini;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

import static org.bukkit.Bukkit.getPluginManager;

public class ChannelManager implements Listener {
	//What channel does each player have selected.
	private final Map<Player, Channel> channelMap = new HashMap<>();
	private final Plugin plugin;
	
	public ChannelManager(Plugin plugin) {
		this.plugin = plugin;
	}
	
	public Channel getChannel(Player player) {
		return channelMap.get(player);
	}
	
	public void select(Player player, Channel channel) {
		channelMap.compute(player, ($, current) -> {
			if (!channel.players().add(player)) {
				current.players().remove(player);
			}
			return channel;
		});
	}
	
	public Channel create(BiFunction<Player, String, String> format) {
		Channel channel = new Channel() {
			final Set<Player> players = new HashSet<>();
			
			@Override
			public Set<Player> players() {
				return players;
			}
		};
		
		getPluginManager().registerEvents(new Listener() {
			@EventHandler
			void onChat(AsyncPlayerChatEvent event) {
				Player sender = event.getPlayer();
				if (getChannel(sender) == channel) {
					channel.broadcast(format.apply(sender, event.getMessage()));
					event.setCancelled(true);
				}
			}
		}, plugin);
		return channel;
	}
	
	interface Channel {
		Set<Player> players();
		
		default Channel broadcast(String message) {
			for (Player player : players())
				player.sendMessage(message);
			return this;
		}
	}
}
