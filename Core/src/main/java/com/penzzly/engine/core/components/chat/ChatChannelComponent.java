package com.penzzly.engine.core.components.chat;

import com.penzzly.engine.architecture.base.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

import static com.penzzly.engine.core.base.Events.listen;

public class ChatChannelComponent extends Component {
	private final Map<Player, Channel> channelMap = new HashMap<>();
	
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
		
		addChild(listen((AsyncPlayerChatEvent event) -> {
			Player sender = event.getPlayer();
			if (getChannel(sender) == channel) {
				channel.broadcast(format.apply(sender, event.getMessage()));
				event.setCancelled(true);
			}
		}));
		return channel;
	}
	
	//Simple enough, each channel has some players and a nice util method :)
	interface Channel {
		Set<Player> players();
		
		default Channel broadcast(String message) {
			for (Player player : players())
				player.sendMessage(message);
			return this;
		}
	}
}
