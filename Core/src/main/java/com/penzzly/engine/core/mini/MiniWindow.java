package com.penzzly.engine.core.mini;

import com.google.common.collect.ListMultimap;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
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
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Multimaps.newListMultimap;
import static java.util.Arrays.asList;
import static java.util.Collections.max;
import static org.bukkit.Bukkit.createInventory;
import static org.bukkit.Bukkit.getPluginManager;
import static org.bukkit.Material.STONE;

//https://gist.github.com/Exerosis/ba02f24882a703808bdd3277a62bef3a
public class MiniWindow implements Listener {
	public MiniWindow(Plugin plugin) {
		getPluginManager().registerEvents(this, plugin);
	}
	
	//--Page--
	public Inventory page(Consumer<Page> like) {
		Page page = page();
		like.accept(page);
		return page.toInventory();
	}
	
	public Page page() {
		return new PageImpl();
	}
	
	//--Events--
	@EventHandler
	void onClick(InventoryClickEvent event) {
		InventoryHolder holder = event.getInventory().getHolder();
		if (holder instanceof PageImpl && event.getCurrentItem() != null)
			if (((PageImpl) holder).clickListeners.get(event.getCurrentItem()).stream()
					.anyMatch(listener ->
							listener.test((Player) event.getWhoClicked(), event.getClick())
					))
				event.setCancelled(true);
	}
	
	@EventHandler
	void onClose(InventoryCloseEvent event) {
		fireOpenClose(event.getInventory(), event.getPlayer());
	}
	
	@EventHandler
	void onOpen(InventoryOpenEvent event) {
		fireOpenClose(event.getInventory(), event.getPlayer());
	}
	
	private void fireOpenClose(Inventory inventory, HumanEntity player) {
		InventoryHolder holder = inventory.getHolder();
		if (holder instanceof PageImpl && player instanceof Player)
			((PageImpl) holder).openListeners.forEach(listener ->
					listener.accept((Player) player)
			);
	}
	
	
	//--Classes--
	interface EventListener extends AutoCloseable {
		default void unregister() {
			try {
				close();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	interface Page {
		int MAX_INDEX = 45;
		
		Page title(Object title);
		
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
			return element((row.intValue() * 9) + column.intValue());
		}
		
		Item element(Number index);
		
		
		//--Events--
		default EventListener onClose(Runnable listener) {
			return onClose(player -> listener.run());
		}
		
		default EventListener onOpen(Runnable listener) {
			return onOpen(player -> listener.run());
		}
		
		EventListener onClose(Consumer<Player> listener);
		
		EventListener onOpen(Consumer<Player> listener);
	}
	
	private class PageImpl implements Page, InventoryHolder {
		final ListMultimap<ItemStack, BiPredicate<Player, ClickType>> clickListeners =
				newListMultimap(new IdentityHashMap<>(), ArrayList::new);
		final List<Consumer<Player>> closeListeners = new ArrayList<>();
		final List<Consumer<Player>> openListeners = new ArrayList<>();
		final Map<Integer, Item> elements = new IdentityHashMap<>();
		String title = "Chest Mk.2";
		
		@Override
		public Page title(Object title) {
			this.title = title.toString();
			return this;
		}
		
		@Override
		public Map<Integer, Item> elements() {
			return elements;
		}
		
		@Override
		public Inventory toInventory() {
			Inventory inventory = createInventory(this, size(), title);
			elements.forEach((index, item) -> inventory.setItem(index, item.itemStack()));
			return inventory;
		}
		
		@Override
		public Item element(Number index) {
			if (index.intValue() < MAX_INDEX)
				return elements().computeIfAbsent(index.intValue(), $ -> new Item() {
					final ItemStack stack = new ItemStack(STONE);
					final List<BiPredicate<Player, ClickType>> listeners = clickListeners.get(stack);
					
					@Override
					public ItemStack itemStack() {
						return stack;
					}
					
					@Override
					public EventListener onClick(BiPredicate<Player, ClickType> listener) {
						listeners.add(listener);
						return () -> listeners.remove(listener);
					}
					
				});
			throw new IndexOutOfBoundsException("There is no room left in this page!");
		}
		
		@Override
		public EventListener onClose(Consumer<Player> listener) {
			closeListeners.add(listener);
			return () -> closeListeners.remove(listener);
		}
		
		@Override
		public EventListener onOpen(Consumer<Player> listener) {
			openListeners.add(listener);
			return () -> openListeners.remove(listener);
		}
		
		@Override
		public Inventory getInventory() {
			throw new UnsupportedOperationException("Use toInventory() instead.");
		}
	}
	
	interface Item {
		ItemStack itemStack();
		
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
		
		@SuppressWarnings("unchecked")
		default <Data extends MaterialData> Data data() {
			return (Data) itemStack().getData();
		}
		
		default Item data(MaterialData data) {
			return data(data.getData());
		}
		
		default Item data(Number data) {
			itemStack().getData().setData(data.byteValue());
			return durability(data);
		}
		
		default Item durability(Number durability) {
			itemStack().setDurability(durability.shortValue());
			return this;
		}
		
		default EventListener onClick(Runnable runnable) {
			return onClick($ -> runnable.run());
		}
		
		
		default EventListener onClick(Consumer<Player> listener) {
			return onClick((player, type) -> {
				listener.accept(player);
			});
		}
		
		default EventListener onClick(BiConsumer<Player, ClickType> listener) {
			return onClick((player, type) -> {
				listener.accept(player, type);
				return true;
			});
		}
		
		EventListener onClick(BiPredicate<Player, ClickType> listener);
	}
}

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