package com.penzzly.engine.core.mini;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.Arrays;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Arrays.fill;
import static java.util.Arrays.stream;
import static org.bukkit.Bukkit.*;
import static org.bukkit.craftbukkit.v1_12_R1.util.CraftMagicNumbers.getItem;
import static org.bukkit.event.EventPriority.LOWEST;

public class StackSizes implements Listener {
	public static final int MAX_VALUE = 127;
	public static final int MIN_VALUE = 1;
	private final Plugin plugin;
	
	public StackSizes(Plugin plugin) {
		this.plugin = plugin;
		//Make sure all current players get updated inventories.
		getOnlinePlayers().stream()
				.map(Player::getInventory)
				.forEach(StackSizes::setupInventory);
		getPluginManager().registerEvents(this, plugin);
	}
	
	public int reset(Material material) {
		return set(material, material.getMaxStackSize());
	}
	
	public int get(Material material) {
		try {
			return (int) ITEM_MAX_STACK.get(getItem(material));
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return material.getMaxStackSize();
	}
	
	public int set(Material material, Number size) {
		int stackSize = max(MIN_VALUE, min(MAX_VALUE, size.intValue()));
		try {
			ITEM_MAX_STACK.set(getItem(material), stackSize);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		return stackSize;
	}
	
	
	//Helpers
	private static ItemStack[] split(ItemStack stack) {
		if (stack != null) {
			int amount = stack.getAmount();
			int max = stack.getMaxStackSize();
			if (amount > max) {
				int remaining = amount % max;
				ItemStack item = stack.clone();
				item.setAmount(max);
				stack.setAmount(remaining);
				ItemStack[] stacks = new ItemStack[(amount - remaining) / max];
				fill(stacks, item);
				return stacks;
			}
		}
		return new ItemStack[0];
	}
	
	private static void setupInventory(Inventory inventory) {
		//Support stack sizes greater than 64.
		if (inventory.getMaxStackSize() == MAX_VALUE) {
			return;
		}
		inventory.setMaxStackSize(MAX_VALUE);
		stream(inventory.getContents())
				.map(StackSizes::split)
				.flatMap(Arrays::stream)
				.forEach(item -> {
					if (inventory.firstEmpty() != -1) {
						inventory.addItem(item);
					} else if (inventory.getHolder() instanceof Entity) {
						inventory.getLocation().getWorld()
								.dropItemNaturally(inventory.getLocation(), item);
					}
				});
	}
	
	private void updateInventory(Entity entity) {
		if (entity instanceof Player) {
			getScheduler().runTask(plugin, ((Player) entity)::updateInventory);
		}
	}
	
	
	//Make sure the client sees the changes to stack size properly.
	@EventHandler(priority = LOWEST)
	void onClick(InventoryClickEvent event) {
		updateInventory(event.getWhoClicked());
	}
	
	@EventHandler(priority = LOWEST)
	void onDrag(InventoryDragEvent event) {
		updateInventory(event.getWhoClicked());
	}
	
	@EventHandler(priority = LOWEST)
	void onPickup(EntityPickupItemEvent event) {
		updateInventory(event.getEntity());
	}
	
	
	//To make sure a large stack doesn't end up getting dropped on the ground.(creative mode drop)
	@EventHandler
	void onDrop(PlayerDropItemEvent event) {
		Item item = event.getItemDrop();
		for (ItemStack stack : split(item.getItemStack()))
			item.getWorld().dropItemNaturally(item.getLocation(), stack)
					.setVelocity(item.getVelocity());
	}
	
	
	//To support sizes larger than 64.
	@EventHandler
	void onJoin(PlayerJoinEvent event) {
		setupInventory(event.getPlayer().getInventory());
	}
	
	@EventHandler
	void onOpen(InventoryOpenEvent event) {
		setupInventory(event.getInventory());
	}
	
	@EventHandler
	void onMove(InventoryMoveItemEvent event) {
		setupInventory(event.getDestination());
	}
	
	
	//Set this field in whatever way works in your system.
	private static final Field ITEM_MAX_STACK;
	
	static {
		try {
			ITEM_MAX_STACK = net.minecraft.server.v1_12_R1.Item.class
					.getDeclaredField("maxStackSize");
			ITEM_MAX_STACK.setAccessible(true);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}
}
