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

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;
import static org.bukkit.Bukkit.createInventory;
import static org.bukkit.Bukkit.getPluginManager;
import static org.bukkit.Material.STONE;

//https://gist.github.com/Exerosis/0d330decbb96f5695ac0bcf19e33d2e6
public class MiniWindow implements Listener {
	public MiniWindow(Plugin plugin) {
		getPluginManager().registerEvents(this, plugin);
	}
	
	//--Page--
	public Inventory page(final int size, final String title, final Consumer<Page> like) {
		Page page = page(size, title);
		like.accept(page);
		return page.getInventory();
	}
	
	public Page page(final int size, final String title) {
		return new PageImpl() {
			final Inventory inventory = createInventory(this, size, title);
			final Item[] items = new Item[size];
			
			@Override
			public Inventory getInventory() {
				return inventory;
			}
			
			@Override
			public int size() {
				return size;
			}
			
			@Override
			public String title() {
				return title;
			}
			
			@Override
			public Item[] items() {
				return items;
			}
		};
	}
	
	//--Events--
	@EventHandler
	void onClick(InventoryClickEvent event) {
		final ItemStack item = event.getCurrentItem();
		if (item instanceof ItemImpl && ((ItemImpl) item).listener.
				test((Player) event.getWhoClicked(), event.getClick()))
			event.setCancelled(true);
	}
	
	@EventHandler
	void onClose(InventoryCloseEvent event) {
		final InventoryHolder holder = event.getInventory().getHolder();
		if (holder instanceof PageImpl)
			((PageImpl) holder).openListener.accept((Player) event.getPlayer());
	}
	
	@EventHandler
	void onOpen(InventoryOpenEvent event) {
		final InventoryHolder holder = event.getInventory().getHolder();
		if (holder instanceof PageImpl)
			((PageImpl) holder).closeListener.accept((Player) event.getPlayer());
	}
	
	//--Classes--
	private static abstract class PageImpl implements Page {
		Consumer<Player> openListener, closeListener;
		
		@Override
		public void onClose(Consumer<Player> listener) {
			closeListener = listener;
		}
		
		@Override
		public void onOpen(Consumer<Player> listener) {
			openListener = listener;
		}
	}
	
	private static class ItemImpl extends ItemStack implements Item {
		BiPredicate<Player, ClickType> listener = (p, t) -> true;
		
		@Override
		public void onClick(BiPredicate<Player, ClickType> listener) {
			this.listener = listener;
		}
		
		@Override
		public ItemStack itemStack() {
			return this;
		}
	}
	
	interface Page extends InventoryHolder {
		Item[] items();
		
		int size();
		
		String title();
		
		default Item item() {
			return item(STONE);
		}
		
		//TODO consider making this a little smarter.
		default Item item(Material type) {
			for (int i = 0; i < size(); i++)
				if (items()[i] == null)
					return item(i, type);
			throw new IndexOutOfBoundsException("There is no room left in this page!");
		}
		
		default Item item(Number row, Number column) {
			return item(row, column, STONE);
		}
		
		default Item item(Number row, Number column, Material type) {
			return item((row.intValue() * 9) + column.intValue(), type);
		}
		
		default Item item(Number index) {
			return item(index, STONE);
		}
		
		default Item item(Number index, Material type) {
			return items()[index.intValue()] = new ItemImpl().type(type);
		}
		
		//--Events--
		default void onClose(Runnable listener) {
			onClose(player -> listener.run());
		}
		
		default void onOpen(Runnable listener) {
			onOpen(player -> listener.run());
		}
		
		void onClose(Consumer<Player> listener);
		
		void onOpen(Consumer<Player> listener);
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
		
		default Item type(Material material) {
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
		
		default void onClick(Runnable runnable) {
			onClick($ -> runnable.run());
		}
		
		default void onClick(Consumer<Player> listener) {
			onClick((player, type) -> {
				listener.accept(player);
			});
		}
		
		default void onClick(BiConsumer<Player, ClickType> listener) {
			onClick((player, type) -> {
				listener.accept(player, type);
				return true;
			});
		}
		
		void onClick(BiPredicate<Player, ClickType> listener);
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
			page.item()
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