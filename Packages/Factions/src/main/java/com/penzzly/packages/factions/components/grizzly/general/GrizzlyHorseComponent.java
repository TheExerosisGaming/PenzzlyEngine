package com.penzzly.packages.factions.components.grizzly.general;

import com.google.gson.reflect.TypeToken;
import com.penzzly.engine.core.base.Configurations.GsonConfigurationComponent;
import com.penzzly.engine.core.components.unorganized.HorseOwnershipComponent;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.spigotmc.event.entity.EntityDismountEvent;
import org.spigotmc.event.entity.EntityMountEvent;

import java.util.HashMap;
import java.util.Map;

import static com.penzzly.engine.core.base.Configurations.jsonConfig;
import static com.penzzly.engine.core.utilites.functions.Functions.entityEventType;
import static com.penzzly.engine.core.utilites.functions.Functions.isCancelled;
import static java.lang.Integer.MAX_VALUE;
import static java.util.concurrent.ThreadLocalRandom.current;
import static org.bukkit.entity.EntityType.HORSE;
import static org.bukkit.entity.EntityType.PLAYER;
import static org.bukkit.potion.PotionEffectType.INCREASE_DAMAGE;
import static org.bukkit.potion.PotionEffectType.SPEED;

public class GrizzlyHorseComponent extends HorseOwnershipComponent {
	
	public GrizzlyHorseComponent() {
		GsonConfigurationComponent<Map<Horse, OfflinePlayer>> config = addChild(jsonConfig(new HashMap<>(),
				new TypeToken<HashMap<Horse, OfflinePlayer>>() {
				}.getType()));
		
		addChild(listen(EntityMountEvent.class))
				.filter(isCancelled(false))
				.filter(entityEventType(PLAYER))
				.subscribe(event -> {
					if (event.getMount().getType() == HORSE) {
						((Player) event.getEntity()).addPotionEffect(new PotionEffect(INCREASE_DAMAGE, MAX_VALUE, 0));
					}
				});
		
		addChild(listen(EntityDismountEvent.class))
				.filter(entityEventType(PLAYER))
				.subscribe(event -> {
					if (event.getDismounted().getType() == HORSE) {
						((Player) event.getEntity()).removePotionEffect(INCREASE_DAMAGE);
						if (current().nextBoolean() && current().nextBoolean()) {
							((Player) event.getEntity()).addPotionEffect(new PotionEffect(SPEED, 5 * 20, 1));
						}
					}
				});
		
		getEnableListeners().clear();
		config.onEnable(() -> config.ifPresent(getHorses()::putAll));
		config.onDisable(() -> config.accept(getHorses()));
	}
}