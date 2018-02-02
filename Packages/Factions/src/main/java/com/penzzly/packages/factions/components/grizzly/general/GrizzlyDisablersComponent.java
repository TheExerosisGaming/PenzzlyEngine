package com.penzzly.packages.factions.components.grizzly.general;

import com.penzzly.engine.architecture.base.Component;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import static com.penzzly.engine.core.base.Events.listen;
import static org.bukkit.Material.*;
import static org.bukkit.entity.EntityType.ARROW;
import static org.bukkit.entity.EntityType.WITHER;

public class GrizzlyDisablersComponent extends Component {
	public GrizzlyDisablersComponent() {
		//Prevent bow boosting.
		addChild(listen((EntityDamageByEntityEvent event) -> {
			Entity damager = event.getDamager();
			if (damager.getType() == ARROW && ((Arrow) damager).getShooter().equals(event.getEntity())) {
				event.setCancelled(true);
			}
		}));
		
		//Disable the wither.
		addChild(listen((EntitySpawnEvent event) -> {
			if (event.getEntityType() == WITHER) {
				event.setCancelled(true);
			}
		}));
		
		//Disable enderchests.
		addChild(listen((PlayerInteractEvent event) -> {
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if (event.getClickedBlock().getType() == Material.ENDER_CHEST) {
					event.setCancelled(true);
				}
			}
		}));
		
		//Disable the use of boats on anything other than water.
		addChild(listen((PlayerInteractEvent event) -> {
			if (event.getItem() != null && event.getItem().getType() == BOAT) {
				Material type = event.getClickedBlock().getType();
				if (type != WATER && type != STATIONARY_WATER) {
					event.setCancelled(true);
				}
			}
		}));
	}
}
