package com.penzzly.packages.factions.components.grizzly.general;

import com.penzzly.engine.architecture.base.Component;
import com.penzzly.engine.core.base.configuration.MapConfiguration;
import com.penzzly.engine.core.base.configuration.YamlConfiguration;
import com.penzzly.engine.core.components.unorganized.HeadGodAppleComponent;
import com.penzzly.engine.core.utilites.EnumUtil;
import com.penzzly.packages.factions.components.grizzly.chest.VoidChestComponent;
import com.penzzly.packages.factions.components.grizzly.general.explosion.GrizzlyExplosionComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.penzzly.engine.core.base.Configurations.ymlConfig;
import static com.penzzly.engine.core.base.Events.listen;
import static java.util.stream.Collectors.toList;
import static net.minecraft.util.com.google.common.collect.Sets.newHashSet;
import static org.bukkit.Bukkit.createInventory;
import static org.bukkit.Material.ENDER_CHEST;
import static org.bukkit.enchantments.Enchantment.LOOT_BONUS_MOBS;

public class GrizzlyComponent extends Component {
	
	@NotNull
	private static Set<Material> ARMOR = newHashSet(
			Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS,
			Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS,
			Material.GOLD_HELMET, Material.GOLD_CHESTPLATE, Material.GOLD_LEGGINGS, Material.GOLD_BOOTS,
			Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS,
			Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS
	);
	
	public GrizzlyComponent() {
		YamlConfiguration config = addChild(ymlConfig().enable());
		
		addChild(new GrizzlySpongeComponent(config.getOrSet("Sponge Radius", 6)));
		
		//Add bonus exp for kills with looting items.
		double bonusExp = config.getOrSet("Looting Exp Bonus", 0.10);
		addChild(new GrizzlyMobDropComponent(player -> {
			if (player.getItemInHand() != null) {
				return player.getItemInHand().getEnchantmentLevel(LOOT_BONUS_MOBS) * bonusExp;
			}
			return 0;
		}));
		
		//Add a limit to the number of cobwebs at each x, z.
		addChild(new GrizzlyCobwebsComponent(config.getOrSet("Max Cobwebs", 3)));
		
		//Setup patches for blocks that break sand.
		List<Material> materials = config.<List<String>>getOrSet("Falling Sand Patches", new ArrayList<>())
				.stream()
				.map(type -> EnumUtil.closest(type, Material.class))
				.collect(toList());
		addChild(new GrizzlyFallingSandComponent(materials::contains));
		
		//Disable potions
		List<PotionEffectType> types = config.<List<String>>getOrSet("Disabled Potions", new ArrayList<>())
				.stream()
				.map(PotionEffectType::getByName)
				.collect(toList());
		addChild(new GrizzlyPotionsComponent(types::contains));
		
		//Send messages when armour is broken.
		addChild(listen((PlayerItemBreakEvent event) -> {
			if (!ARMOR.contains(event.getBrokenItem().getType())) {
				return;
			}
			
			event.getPlayer().sendMessage(ChatColor.RED + "Your " +
					event.getBrokenItem().getItemMeta().getDisplayName().toLowerCase().replace("_", " ") + " has broken!");
		}));
		
		//Setup Void Chests
		MapConfiguration chests = config.getSection("Void Chests");
		String title = chests.getOrSet("Title", "Void Chest");
		int size = chests.getOrSet("Size", 0);
		
		//TODO populate defaults here:
		ItemStack item = getItem(chests.getSection("Item"), new ItemStack(ENDER_CHEST));
		Inventory inventory = createInventory(null, size, title);
		addChild(new VoidChestComponent(item, inventory));
		
		addChild(listen((PortalCreateEvent event) -> event.setCancelled(true)));
		
		//Tweak explosions.
		addChild(new GrizzlyExplosionComponent(config.getOrSet("Explosion Radius", 5)));
		
		//Add effects on death, make sure end portals return you correctly,
		//task undesirables, and allow god apples to be crafted with heads.
		addChild(new GrizzlyDeathComponent(), new GrizzlyEndComponent(),
				new GrizzlyDisablersComponent(), new HeadGodAppleComponent());
	}
}
