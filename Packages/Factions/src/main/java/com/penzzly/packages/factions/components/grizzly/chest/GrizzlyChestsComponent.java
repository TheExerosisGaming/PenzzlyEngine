package com.penzzly.packages.factions.components.grizzly.chest;

import com.penzzly.engine.architecture.base.Component;
import com.penzzly.engine.core.base.configuration.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static com.penzzly.engine.core.base.Configurations.ymlConfig;
import static org.bukkit.Bukkit.createInventory;
import static org.bukkit.Material.ENDER_CHEST;

public class GrizzlyChestsComponent extends Component {
	
	public GrizzlyChestsComponent() {
		//Setup Void Chests
		YamlConfiguration config = addChild(ymlConfig());
		String title = config.getOrSet("Title", "Void Chest");
		int size = config.getOrSet("Size", 0);
		
		ItemStack item = getItem(config.get("Item"), new ItemStack(ENDER_CHEST));
		Inventory inventory = createInventory(null, size, title);
		addChild(new VoidChestComponent(item, inventory));
		
		
	}
}
