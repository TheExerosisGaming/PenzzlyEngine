package com.penzzly.packages.factions.components.grizzly.general;

import com.google.gson.reflect.TypeToken;
import com.penzzly.engine.architecture.base.Component;
import com.penzzly.engine.core.base.configuration.GsonConfiguration;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.player.PlayerPortalEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.penzzly.engine.core.base.Configurations.jsonConfig;
import static com.penzzly.engine.core.base.Events.listen;

public final class GrizzlyEndComponent extends Component {
	
	public GrizzlyEndComponent() {
		GsonConfiguration<Map<UUID, Location>> config = addChild(jsonConfig(
				new HashMap<>(), new TypeToken<Map<UUID, Location>>() {
				}.getType()
		));
		
		addChild(listen((PlayerPortalEvent event) -> {
			if (event.getTo().getWorld().getEnvironment() == World.Environment.THE_END) {
				config.get().put(event.getPlayer().getUniqueId(), event.getFrom().add(2, 0, 0));
			} else if (event.getFrom().getWorld().getEnvironment() == World.Environment.THE_END) {
				config.get().computeIfPresent(event.getPlayer().getUniqueId(), (uuid, location) -> {
					event.setTo(location);
					
					return location;
				});
			}
		}));
	}
	
}
