package com.penzzly.engine.core.components.messaging;

import com.penzzly.engine.architecture.base.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BungeeComponent extends Component {
	private final PluginMessagingComponent channel;
	
	public BungeeComponent() {
		channel = new PluginMessagingComponent("Bungee");
	}
	
	public void connect(@NotNull Player player, String server) {
		channel.sendMessage(player, "Connect", server);
	}
	
	
}
