package com.penzzly.packages.factions.components.grizzly.general;

import com.penzzly.engine.architecture.base.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import static org.bukkit.SkullType.PLAYER;
import static org.bukkit.event.EventPriority.LOWEST;

public class GrizzlyDeathComponent extends Component {
	
	public GrizzlyDeathComponent() {
		addChild(listen(LOWEST, (PlayerDeathEvent event) -> {
			Player player = event.getEntity();
			
			player.getWorld().strikeLightningEffect(player.getLocation());
			event.setKeepLevel(false);
			
			ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) PLAYER.ordinal());
			SkullMeta meta = (SkullMeta) item.getItemMeta();
			meta.setOwner(player.getName());
			item.setItemMeta(meta);
			event.getDrops().add(item);
		}));
	}
}
