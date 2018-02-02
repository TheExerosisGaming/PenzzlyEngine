package com.penzzly.engine.core.components.unorganized;

import com.penzzly.engine.architecture.base.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.jetbrains.annotations.NotNull;
import org.spigotmc.event.entity.EntityMountEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;

import static com.penzzly.engine.core.base.Events.listen;
import static com.penzzly.engine.core.utilites.functions.Functions.*;
import static org.bukkit.entity.EntityType.HORSE;
import static org.bukkit.entity.EntityType.PLAYER;

public class HorseOwnershipComponent extends Component implements BiPredicate<Horse, OfflinePlayer> {
	private final Map<Horse, OfflinePlayer> horses = new HashMap<>();
	
	public HorseOwnershipComponent() {
		addChild(listen(EntityDeathEvent.class))
				.filter(entityEventType(HORSE))
				.map(EntityEvent::getEntity)
				.subscribe(horses::remove);
		
		addChild(listen(EntityTameEvent.class))
				.filter(entityEventType(HORSE))
				.subscribe(event -> {
					if (event.getOwner() instanceof Player) {
						horses.put((Horse) event.getEntity(), (Player) event.getOwner());
					}
				});
		
		addChild(listen(EntityMountEvent.class))
				.filter(isCancelled(false))
				.filter(entityEventType(PLAYER))
				.filter(event -> entityType(HORSE).test(event.getMount()) &&
						horses.containsKey(event.getMount()) &&
						!event.getEntity().equals(horses.get(event.getMount())))
				.subscribe(setCancelled(true));
		
	}
	
	public boolean isOwner(Horse horse, @NotNull OfflinePlayer player) {
		return test(horse, player);
	}
	
	@Override
	public boolean test(Horse horse, @NotNull OfflinePlayer player) {
		return player.equals(horses.get(horse));
	}
	
	@NotNull
	public Map<Horse, OfflinePlayer> getHorses() {
		return horses;
	}
}