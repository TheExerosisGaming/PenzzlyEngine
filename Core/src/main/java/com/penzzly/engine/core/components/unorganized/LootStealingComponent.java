package com.penzzly.engine.core.components.unorganized;

import com.penzzly.engine.architecture.base.Component;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;

import java.util.HashMap;
import java.util.Map;

import static com.penzzly.engine.core.base.Events.listen;

public class LootStealingComponent extends Component {
	
	public LootStealingComponent() {
		final Map<Item, Player> loot = new HashMap<>();
		
		addChild(listen((PlayerDeathEvent event) -> {
			Player player = event.getEntity();
			event.getDrops().forEach(item ->
					loot.put(player.getWorld().dropItemNaturally(player.getLocation(), item), player.getKiller()));
		}));
		
		addChild(listen((InventoryPickupItemEvent event) -> {
			Player player = loot.get(event.getItem());
			if (player == null) {
				return;
			}
			if (event.getInventory().getHolder().equals(player)) {
				loot.remove(event.getItem());
			} else {
				event.setCancelled(true);
			}
		}));
		
		addChild(listen((EntityDeathEvent event) -> loot.remove(event.getEntity())));
		addChild(listen((ItemDespawnEvent event) -> loot.remove(event.getEntity())));
	}
}
