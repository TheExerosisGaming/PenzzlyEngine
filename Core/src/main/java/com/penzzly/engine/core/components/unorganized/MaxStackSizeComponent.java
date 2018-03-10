package com.penzzly.engine.core.components.unorganized;

import com.penzzly.engine.architecture.base.Component;
import net.minecraft.server.v1_12_R1.Item;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;

import java.lang.reflect.Field;
import java.util.function.BiFunction;

import static com.penzzly.engine.core.utilites.MathUtil.bound;

public class MaxStackSizeComponent extends Component {
	private static final Field SET_MAX_STACK;
	public static final int MAX_STACK = 127;
	
	static {
		try {
			SET_MAX_STACK = Item.class.getDeclaredField("maxStackSize");
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}
	
	private final BiFunction<Inventory, Material, Number> stackSize;
	
	public MaxStackSizeComponent(BiFunction<Inventory, Material, Number> stackSize) {
		this.stackSize = stackSize.andThen(size -> bound(size.intValue(), MAX_STACK, 1));
	}
	
	/**
	 * Sets the max stack size of the {@linkplain Inventory} to the max Minecraft allows.
	 *
	 * @param inventory {@linkplain Inventory} - Sets the {@linkplain Inventory}'s stack size to the max Minecraft allows.
	 */
	public static void setInventoryStackSize(Inventory inventory) {
		inventory.setMaxStackSize(Stackables.MAX_STACK);
	}
	
	/**
	 * If the entity is a player it will update the players inventory after waiting one tick.
	 *
	 * @param entity {@linkplain Entity}
	 */
	public static void updateInventoryLater(Entity entity) {
		if (!(entity instanceof Player)) {
			return;
		}
		Player player = (Player) entity;
		Bukkit.getScheduler().runTaskLater(Stackables.getPlugin(), player::updateInventory, 1);
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent event) {
		updateInventoryLater(event.getWhoClicked());
	}
	
	@EventHandler
	public void onDrag(InventoryDragEvent event) {
		updateInventoryLater(event.getWhoClicked());
	}
	
	@EventHandler
	public void onPickup(PlayerPickupItemEvent event) {
		updateInventoryLater(event.getPlayer());
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		setInventoryStackSize(event.getPlayer().getInventory());
	}
	
	@EventHandler
	public void onOpenInventory(InventoryOpenEvent event) {
		setInventoryStackSize(event.getInventory());
	}
	
	@EventHandler
	public void onHopper(InventoryMoveItemEvent event) {
		setInventoryStackSize(event.getDestination());
	}
	
	
	private static void setMaxStackSize(Material material, int size) {
		try {
			SET_MAX_STACK.set(CraftMagicNumbers.getItem(material), size);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	
}
