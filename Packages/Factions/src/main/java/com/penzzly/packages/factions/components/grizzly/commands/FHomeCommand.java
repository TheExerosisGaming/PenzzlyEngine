package com.penzzly.packages.factions.components.grizzly.commands;

import com.google.gson.reflect.TypeToken;
import com.penzzly.engine.architecture.base.Component;
import com.penzzly.engine.core.base.configuration.GsonConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.penzzly.engine.core.base.Configurations.jsonConfig;
import static com.penzzly.engine.core.base.Events.listen;
import static java.time.Duration.between;
import static org.bukkit.event.EventPriority.LOWEST;

public class FHomeCommand extends Component {
	
	//FIXME support duration properly.
	public FHomeCommand(int delay) {
		GsonConfiguration<Map<UUID, Instant>> config =
				addChild(jsonConfig(new HashMap<>(), new TypeToken<Map<UUID, Instant>>() {
				}.getType()));
		
		addChild(listen((PlayerJoinEvent event) -> {
			config.get().put(event.getPlayer().getUniqueId(), Instant.now());
		}));
		
		addChild(listen(LOWEST, (PlayerCommandPreprocessEvent event) -> {
			if (!event.getMessage().equals("/fhome")) {
				return;
			}
			
			Player player = event.getPlayer();
			
			long numSeconds = between(Instant.now(), config.get().get(player.getUniqueId())).getSeconds();
			
			if (numSeconds > 0) {
				player.sendMessage(String.format("You can use this command in %d seconds!", numSeconds));
				event.setCancelled(true);
			} else {
				config.get().put(player.getUniqueId(), Instant.now().plusSeconds(delay));
			}
		}));
	}
	
}
