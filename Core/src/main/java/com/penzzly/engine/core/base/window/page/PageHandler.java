package com.penzzly.engine.core.base.window.page;

import com.penzzly.engine.core.base.window.TransactionHandler;
import io.reactivex.disposables.Disposable;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.penzzly.engine.core.base.Events.listen;
import static com.penzzly.engine.core.utilites.bukkit.PacketUtil.openInventory;
import static org.bukkit.Bukkit.createInventory;

//TODO Update this to not use bukkit inventories at all :D
public class PageHandler extends TransactionHandler<Page> {
	public PageHandler(Player player) {
		super(player);
	}
	
	@NotNull
	@Override
	protected RxPage create() {
		return new RxPage() {
			private final List<Disposable> disposables = new ArrayList<>();
			private Inventory inventory = createInventory(null, 9);
			
			@Override
			protected void init() {
				addInternalChild(listen(InventoryClickEvent.class, event -> {
					if (event.getInventory().equals(inventory)) {
						clickListeners.get(event.getSlot()).forEach(listener ->
								listener.accept(event.getClick()));
						event.setCancelled(true);
					}
				}));
				addInternalChild(listen(InventoryCloseEvent.class, event -> {
					if (event.getInventory().equals(inventory)) {
						closeListeners.forEach(Runnable::run);
						complete();
					}
				}));
			}
			
			@Override
			protected void reveal() {
				if (!isRevealed()) {
					disposables.add(titleSubject.subscribe(title -> {
						if (inventory == null || inventory.getSize() < size()) {
							inventory = createInventory(null, size(), title.toString());
						} else if (player.getOpenInventory().equals(inventory)) {
							openInventory(player, title.toString(), 18);
						}
					}));
					disposables.add(items.subscribe(pair -> inventory.setItem(pair.getValue0(), pair.getValue1())));
				}
				super.reveal();
			}
			
			@Override
			protected void conceal() {
				if (isRevealed()) {
					disposables.forEach(Disposable::dispose);
					disposables.clear();
				}
				super.conceal();
			}
			
			@NotNull
			@Override
			public Page show() {
				if (!isShown()) {
					if (!player.getOpenInventory().equals(inventory)) {
//						player.openInventory(inventory);
						openInventory(player, titleSubject.getValue().toString(), size());
					}
				}
				return super.show();
			}
			
			@NotNull
			@Override
			public Page hide() {
				if (isShown() && player.getOpenInventory().equals(inventory)) {
					player.closeInventory();
				}
				return super.hide();
			}
		};
	}
	
}