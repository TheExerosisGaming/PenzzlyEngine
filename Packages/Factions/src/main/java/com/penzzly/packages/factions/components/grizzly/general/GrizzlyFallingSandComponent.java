package com.penzzly.packages.factions.components.grizzly.general;

import com.penzzly.engine.architecture.base.Component;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

import static org.bukkit.block.BlockFace.UP;

public class GrizzlyFallingSandComponent extends Component {
	public GrizzlyFallingSandComponent(@NotNull Predicate<Material> patch) {
		addChild(listen(ItemSpawnEvent.class, event -> {
			final Block block = event.getLocation().getBlock();
			if (patch.test(block.getType())) {
				event.setCancelled(true);
				Block blockUp = block.getRelative(UP);
				blockUp.setType(event.getEntity().getItemStack().getType());
				blockUp.setData((byte) event.getEntity().getItemStack().getDurability(), true);
			}
		}));
	}
}
