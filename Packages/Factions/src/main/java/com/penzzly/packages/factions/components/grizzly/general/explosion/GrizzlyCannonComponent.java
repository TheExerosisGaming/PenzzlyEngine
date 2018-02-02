package com.penzzly.packages.factions.components.grizzly.general.explosion;

import com.penzzly.engine.architecture.base.Component;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockPistonExtendEvent;

import java.util.stream.IntStream;

import static com.penzzly.engine.core.base.Events.listen;

public final class GrizzlyCannonComponent extends Component {
	
	public GrizzlyCannonComponent() {
		addChild(listen((BlockPistonExtendEvent event) -> {
			World world = event.getBlock().getWorld();
			
			for (Block block : event.getBlocks()) {
				boolean cancel = IntStream.range(block.getY(), world.getMaxHeight())
						.mapToObj(y -> world.getBlockAt(block.getX(), y, block.getZ()))
						.map(Block::getType)
						.filter(Material.SAND::equals)
						.count() >= 25;
				
				if (cancel) {
					event.setCancelled(true);
					return;
				}
			}
		}));
	}
	
}
