package com.penzzly.packages.factions.components.grizzly.cooldowns;

import com.google.gson.reflect.TypeToken;
import com.penzzly.engine.architecture.base.Component;
import com.penzzly.engine.core.base.configuration.GsonConfiguration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.javatuples.Pair;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.penzzly.engine.core.base.Configurations.jsonConfig;
import static com.penzzly.engine.core.base.Events.listen;
import static java.time.Duration.between;

public class GrizzlyGoldenAppleComponent extends Component {
	
	public GrizzlyGoldenAppleComponent() {
		GsonConfiguration<Map<UUID, Pair<Instant, Instant>>> config =
				addChild(jsonConfig(new HashMap<>(), new TypeToken<Map<UUID, Pair<Instant, Instant>>>() {
				}.getType()));
		
		addChild(listen((PlayerJoinEvent event) -> {
			config.get().putIfAbsent(event.getPlayer().getUniqueId(), new Pair<>(Instant.now(), Instant.now()));
		}));
		
		addChild(listen((PlayerItemConsumeEvent event) -> {
			ItemStack item = event.getItem();
			
			if (item.getType() != Material.GOLDEN_APPLE) {
				return;
			}
			
			Player player = event.getPlayer();
			
			Pair<Instant, Instant> pair = config.get().get(player.getUniqueId());
			
			long numSeconds = between(Instant.now(), item.getDurability() == 1 ? pair.getValue0()
					: pair.getValue1()).getSeconds();
			
			if (numSeconds > 0) {
				if (item.getDurability() == 1) {
					player.sendMessage(String.format("You can eat this Notch Apple in %d minutes!", (numSeconds / 60) + 1));
				} else {
					player.sendMessage(String.format("You can eat this Golden Apple in %d seconds!", numSeconds));
				}
				
				event.setCancelled(true);
			} else {
				if (item.getDurability() == 1) {
					config.get().put(player.getUniqueId(), new Pair<>(Instant.now().plusSeconds(60 * 60 * 2), pair.getValue1()));
				} else {
					config.get().put(player.getUniqueId(), new Pair<>(pair.getValue0(), Instant.now().plusSeconds(15)));
				}
			}
		}));
	}
	
}
