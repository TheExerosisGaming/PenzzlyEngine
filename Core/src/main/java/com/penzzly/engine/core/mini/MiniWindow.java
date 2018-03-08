package com.penzzly.engine.core.mini;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;
import static java.util.Collections.max;
import static org.bukkit.Bukkit.createInventory;
import static org.bukkit.Bukkit.getPluginManager;
import static org.bukkit.Material.STONE;

//https://gist.github.com/Exerosis/8dd53a0ca476ed0f1fe705102c3f2111
public class MiniWindow implements Listener {
	private final Map<Inventory, Page> inventories = new HashMap<>();
	
	public MiniWindow(Plugin plugin) {
		getPluginManager().registerEvents(this, plugin);
		
	/*	//Make constants.
		final String closeMessage = "Awww, looks like {0} stopped looking at the inventory.";
		final String openMessage = "Looks like {0} popped open your inventory!";
		final String clickMessage = "Sweet! Looks like {0} {1} clicked on an {3}";
		
		//Setup page using closure to get Inventory directly.
		Inventory inventory = page(page -> {
			page.onClose(player ->
					System.out.println(format(closeMessage, player.getName()))
			);
			page.onOpen(player ->
					System.out.println(format(openMessage, player.getName()))
			);
			page.element()
					.title("Test Item")
					.icon(Material.APPLE)
					.amount(3)
					.text("Lore,", "Lore,", "Lore!")
					.onClick((player, type) ->
							System.out.println(format(clickMessage,
									player.getName(),
									type.toString().replace('_', ' ').toLowerCase(),
									"apple")
							)
					);
		});
		
		//Show to players as they join.
		getPluginManager().registerEvents(new Listener() {
			@EventHandler
			void onJoin(PlayerJoinEvent event) {
				event.getPlayer().openInventory(inventory);
			}
		}, plugin);*/
	}
	
	//--Page--
	public Inventory page(Consumer<Page> like) {
		return page(null, like);
	}
	
	public Inventory page(InventoryHolder holder, Consumer<Page> like) {
		Page page = page(holder);
		like.accept(page);
		return page.toInventory();
	}
	
	public Page page() {
		return page((InventoryHolder) null);
	}
	
	public Page page(InventoryHolder holder) {
		return new Page() {
			private final List<Consumer<Player>> closeListeners = new ArrayList<>();
			private final List<Consumer<Player>> openListeners = new ArrayList<>();
			private final Map<Integer, Item> elements = new HashMap<>();
			
			@Override
			public Map<Integer, Item> elements() {
				return elements;
			}
			
			@Override
			public Inventory toInventory() {
				Inventory inventory = createInventory(holder, size());
				elements.forEach((index, item) -> inventory.setItem(index, item.itemStack()));
				inventories.put(inventory, this);
				return inventory;
			}
			
			@Override
			public List<Consumer<Player>> getCloseListeners() {
				return closeListeners;
			}
			
			@Override
			public List<Consumer<Player>> getOpenListeners() {
				return openListeners;
			}
		};
	}
	
	
	//--Events--
	@EventHandler
	void onClick(InventoryClickEvent event) {
		Page page = inventories.get(event.getInventory());
		if (page != null) {
			Item item = page.elements().get(event.getRawSlot());
			if (item != null && event.getWhoClicked() instanceof Player) {
				item.getClickListeners().forEach(listener -> listener.accept((Player) event.getWhoClicked(), event.getClick()));
			}
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	void onClose(InventoryCloseEvent event) {
		Page page = inventories.remove(event.getInventory());
		if (page != null && event.getPlayer() instanceof Player) {
			page.getCloseListeners().forEach(listener -> listener.accept((Player) event.getPlayer()));
		}
	}
	
	@EventHandler
	void onOpen(InventoryOpenEvent event) {
		Page page = inventories.get(event.getInventory());
		if (page != null && event.getPlayer() instanceof Player) {
			page.getOpenListeners().forEach(listener -> listener.accept((Player) event.getPlayer()));
		}
	}
	
	
	//--Classes--
	interface Page {
		int MAX_INDEX = 45;
		
		Map<Integer, Item> elements();
		
		Inventory toInventory();
		
		default int size() {
			return max(elements().keySet()) + 1;
		}
		
		default Item element() {
			for (int i = 0; i < MAX_INDEX; i++)
				if (!elements().containsKey(i)) {
					return element(i);
				}
			return elements().get(MAX_INDEX - 1);
		}
		
		default Item element(Number row, Number column) {
			return element((column.intValue() * 9) + row.intValue());
		}
		
		default Item element(Number index) {
			if (index.intValue() < MAX_INDEX) {
				return elements().computeIfAbsent(index.intValue(), $ -> new Item() {
					private final List<BiConsumer<Player, ClickType>> clickListeners = new ArrayList<>();
					private final ItemStack stack = new ItemStack(STONE);
					
					@Override
					public ItemStack itemStack() {
						return stack;
					}
					
					@Override
					public List<BiConsumer<Player, ClickType>> getClickListeners() {
						return clickListeners;
					}
				});
			}
			throw new IndexOutOfBoundsException("There is no room left in this page!");
		}
		
		//--Events--
		default Consumer<Player> onClose(Runnable listener) {
			return onClose(player -> listener.run());
		}
		
		default Consumer<Player> onClose(Consumer<Player> listener) {
			getCloseListeners().add(listener);
			return listener;
		}
		
		default Consumer<Player> onOpen(Runnable listener) {
			return onOpen(player -> listener.run());
		}
		
		default Consumer<Player> onOpen(Consumer<Player> listener) {
			getOpenListeners().add(listener);
			return listener;
		}
		
		List<Consumer<Player>> getCloseListeners();
		
		List<Consumer<Player>> getOpenListeners();
	}
	
	interface Item {
		ItemStack itemStack();
		
		List<BiConsumer<Player, ClickType>> getClickListeners();
		
		default Item text(String... lines) {
			ItemMeta meta = itemStack().getItemMeta();
			meta.setLore(asList(lines));
			itemStack().setItemMeta(meta);
			return this;
		}
		
		default Item text(Iterable<String> lines) {
			ItemMeta meta = itemStack().getItemMeta();
			meta.setLore(newArrayList(lines));
			itemStack().setItemMeta(meta);
			return this;
		}
		
		default Item amount(Number amount) {
			itemStack().setAmount(amount.intValue());
			return this;
		}
		
		default Item title(Object title) {
			ItemMeta meta = itemStack().getItemMeta();
			meta.setDisplayName(title.toString());
			itemStack().setItemMeta(meta);
			return this;
		}
		
		default Item icon(Material material) {
			itemStack().setType(material);
			return this;
		}
		
		default Item data(Number data) {
			itemStack().setData(new MaterialData(itemStack().getType(), data.byteValue()));
			return this;
		}
		
		default BiConsumer<Player, ClickType> onClick(Runnable listener) {
			return onClick((player, type) -> listener.run());
		}
		
		default BiConsumer<Player, ClickType> onClick(Consumer<Player> listener) {
			return onClick((player, type) -> listener.accept(player));
		}
		
		default BiConsumer<Player, ClickType> onClick(BiConsumer<Player, ClickType> listener) {
			getClickListeners().add(listener);
			return listener;
		}
	}
}