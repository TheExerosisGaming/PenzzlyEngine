package com.penzzly.packages.factions.components.grizzly.chest;

import com.penzzly.engine.architecture.base.Component;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import static com.penzzly.engine.core.base.Events.listen;
import static com.penzzly.engine.core.utilites.bukkit.ServerUtil.createFixedMeta;
import static org.bukkit.Material.AIR;
import static org.bukkit.event.EventPriority.LOW;
import static org.bukkit.event.EventPriority.MONITOR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

/**
 * Created by Home on 12/6/2017.
 */
public class VoidChestComponent extends Component {
	public static final String META_VOID_CHEST = "VOID_CHEST";
	private final List<BiConsumer<Inventory, ItemStack>> voidListeners = new ArrayList<>();
	private final ItemStack item;
	private final Inventory inventory;
	
	public VoidChestComponent(ItemStack item, Inventory inventory) {
		this.item = item;
		this.inventory = inventory;
		addChild(listen(MONITOR, (BlockPlaceEvent event) -> {
			if (isVoidChest(event.getItemInHand())) {
				event.getBlock().setMetadata(META_VOID_CHEST, createFixedMeta("VoidChest"));
			}
		}));
		
		addChild(listen(LOW, (BlockBreakEvent event) -> {
			Block block = event.getBlock();
			if (isVoidChest(block)) {
				block.getDrops().clear();
				block.getDrops().add(getVoidChest());
			}
		}));
		
		addChild(listen(LOW, (PlayerInteractEvent event) -> {
			if (event.getAction() == RIGHT_CLICK_BLOCK && isVoidChest(event.getClickedBlock())) {
				event.setCancelled(true);
				event.getPlayer().openInventory(this.inventory);
			}
		}));
		
		addChild(listen((InventoryMoveItemEvent event) -> {
			if (event.getDestination().equals(this.inventory)) {
				voidListeners.forEach(listener -> listener.accept(event.getInitiator(), event.getItem()));
				event.setItem(new ItemStack(AIR));
			}
		}));
	}
	
	public boolean isVoidChest(ItemStack stack) {
		return item.equals(stack);
	}
	
	public boolean isVoidChest(@NotNull Block block) {
		return block.hasMetadata(META_VOID_CHEST);
	}
	
	public ItemStack getVoidChest() {
		return item;
	}
	
	public BiConsumer<Inventory, ItemStack> onVoid(BiConsumer<Inventory, ItemStack> listener) {
		voidListeners.add(listener);
		return listener;
	}
	
	@NotNull
	public List<BiConsumer<Inventory, ItemStack>> getVoidListeners() {
		return voidListeners;
	}
}
