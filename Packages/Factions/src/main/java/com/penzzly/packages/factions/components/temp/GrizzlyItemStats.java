package com.penzzly.packages.factions.components.temp;

import com.penzzly.engine.architecture.base.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.comphenix.protocol.utility.BukkitUtil.getOnlinePlayers;
import static com.penzzly.engine.core.base.Events.listen;
import static java.util.Arrays.asList;
import static net.minecraft.util.com.google.common.collect.Sets.newHashSet;
import static org.bukkit.event.EventPriority.MONITOR;

public class GrizzlyItemStats extends Component {
	
	private static final Set<Material> ITEMS_WITH_STATS = newHashSet(
			Material.WOOD_SWORD, Material.STONE_SWORD, Material.IRON_SWORD,
			Material.GOLD_SWORD, Material.DIAMOND_SWORD,
			
			Material.BOW
	);
	
	public GrizzlyItemStats() {
		/*
		 * Add lore to the item being held if it doesn't
		 * already have.
		 */
		onEnable(() -> getOnlinePlayers().stream()
				.map(Player::getItemInHand)
				.filter(item -> ITEMS_WITH_STATS.contains(item.getType()))
				.findAny()
				.ifPresent(this::addLoreToItem)
		);
		
		addChild(listen(MONITOR, (PlayerItemHeldEvent event) -> {
			ItemStack item = event.getPlayer().getInventory().getItem(event.getNewSlot());
			
			if (item == null) {
				return;
			}
			
			Optional.of(item)
					.filter(i -> ITEMS_WITH_STATS.contains(i.getType()))
					.ifPresent(this::addLoreToItem);
		}));
		
		addChild(listen(MONITOR, (PlayerJoinEvent event) -> {
			Optional.of(event.getPlayer().getItemInHand())
					.filter(item -> ITEMS_WITH_STATS.contains(item.getType()))
					.ifPresent(this::addLoreToItem);
		}));
		
		/*
		 * Increment kills when needed.
		 */
		addChild(listen((PlayerDeathEvent event) -> {
			Player killer = event.getEntity().getKiller();
			
			if (killer == null) {
				return;
			}
			
			Optional.of(killer)
					.map(Player::getItemInHand)
					.filter(item -> ITEMS_WITH_STATS.contains(item.getType()))
					.ifPresent(this::incrementKills);
		}));
	}
	
	private void addLoreToItem(@NotNull ItemStack item) {
		ItemMeta itemMeta = item.getItemMeta();
		
		/*
		 * If an item already has the custom
		 * lore, do nothing.
		 */
		if (itemMeta.hasLore() && itemMeta.getLore().stream().anyMatch(s -> s.startsWith("Kills"))) {
			return;
		}
		
		if (!itemMeta.hasLore()) {
			itemMeta.setLore(asList("Kills: 0"));
		} else {
			itemMeta.getLore().add("Kills: 0");
		}
		
		item.setItemMeta(itemMeta);
	}
	
	private void incrementKills(@NotNull ItemStack item) {
		if (!item.getItemMeta().hasLore()) {
			addLoreToItem(item);
		}
		
		List<String> newLore = new ArrayList<>();
		
		ItemMeta itemMeta = item.getItemMeta();
		
		for (String lore : itemMeta.getLore()) {
			if (lore.startsWith("Kills")) {
				lore = "Kills: " + (Integer.parseInt(lore.split(" ")[1]) + 1);
			}
			
			newLore.add(lore);
		}
		
		itemMeta.setLore(newLore);
		
		item.setItemMeta(itemMeta);
	}
	
}
