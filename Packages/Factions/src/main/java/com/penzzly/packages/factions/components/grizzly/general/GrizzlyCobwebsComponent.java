package com.penzzly.packages.factions.components.grizzly.general;

import com.penzzly.engine.architecture.base.Component;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.stream.IntStream;

import static com.penzzly.engine.core.base.Events.listen;
import static org.bukkit.Material.WEB;

public final class GrizzlyCobwebsComponent extends Component {
	
	public GrizzlyCobwebsComponent(int maxWebs) {
//		addChild(listen((BlockPlaceEvent event) -> {
//			Block block = event.getBlockPlaced();
//			if (block.getType() == WEB) {
//				long webs = dualDirectStream(block, UP, DOWN)
//						.map(Block::getType)
//						.filter(WEB::equals)
//						.limit(maxWebs)
//						.count();
//
//				if (webs >= maxWebs) {
//					event.setCancelled(true);
//				}
//			}
//		}));
		addChild(listen((BlockPlaceEvent event) -> {
			Block block = event.getBlockPlaced();
			
			World world = block.getWorld();
			
			if (block.getType() == WEB) {
				long webs = IntStream.range(0, world.getMaxHeight())
						.mapToObj(i -> world.getBlockAt(block.getX(), i, block.getZ()))
						.map(Block::getType)
						.filter(WEB::equals)
						.count();
				
				if (webs > maxWebs) {
					event.setCancelled(true);
				}
			}
		}));
	}
	
}