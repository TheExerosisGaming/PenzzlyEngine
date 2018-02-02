package com.penzzly.packages.factions.components.grizzly.general.explosion;

import com.penzzly.engine.architecture.base.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.List;

import static com.penzzly.engine.core.base.Events.listen;
import static java.util.Arrays.asList;
import static org.bukkit.Material.*;
import static org.bukkit.entity.EntityType.CREEPER;
import static org.bukkit.entity.EntityType.PRIMED_TNT;

public final class GrizzlyTNTComponent extends Component {
	private static final List<EntityType> TYPES = asList(CREEPER, PRIMED_TNT);
	private static final List<Material> FORCE_EXPLODE = asList(CHEST, TRAPPED_CHEST, MOB_SPAWNER, BEACON);
	
	public GrizzlyTNTComponent(int radius) {
		addChild(listen((EntityExplodeEvent event) -> {
			if (!TYPES.contains(event.getEntityType())) {
				return;
			}
			
			Location location = event.getLocation();
			
			World world = location.getWorld();
			
			for (int x = -radius; x <= radius; x++) {
				for (int y = -radius; y <= radius; y++) {
					for (int z = -radius; z <= radius; z++) {
						Block block = world.getBlockAt(location.getBlockX() + x, location.getBlockY() + y, location.getBlockZ() + z);
						
						if (!FORCE_EXPLODE.contains(block.getType())) {
							continue;
						}
						
						block.breakNaturally();
					}
				}
			}
		}));
	}
	
}
