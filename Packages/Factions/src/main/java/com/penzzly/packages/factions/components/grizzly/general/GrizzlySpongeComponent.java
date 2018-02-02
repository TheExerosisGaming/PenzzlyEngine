package com.penzzly.packages.factions.components.grizzly.general;

import com.penzzly.engine.architecture.base.Component;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockPlaceEvent;

import static com.penzzly.engine.core.base.Events.listen;
import static org.bukkit.Material.AIR;
import static org.bukkit.Material.SPONGE;

public class GrizzlySpongeComponent extends Component {
	
	public GrizzlySpongeComponent(int radius) {
		addChild(listen((BlockPlaceEvent event) -> {
			if (event.getBlock().getType() == SPONGE) {
				sphere(event.getBlock(), radius, false)
						.stream()
						.map(Location::getBlock)
						.filter(Block::isLiquid)
						.forEach(block -> block.setType(AIR));
			}
		}));
	}
	
}
