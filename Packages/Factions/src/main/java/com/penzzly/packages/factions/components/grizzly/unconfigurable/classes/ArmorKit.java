package com.penzzly.packages.factions.components.grizzly.unconfigurable.classes;

import com.penzzly.engine.architecture.base.Component;
import com.penzzly.engine.architecture.holder.mutable.MutableHolder;
import io.reactivex.Observable;
import io.reactivex.subjects.Subject;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static com.comphenix.protocol.utility.BukkitUtil.getOnlinePlayers;
import static com.penzzly.engine.core.base.Events.listen;
import static com.penzzly.engine.core.utilites.bukkit.ServerUtil.getPlugin;
import static io.reactivex.subjects.PublishSubject.create;
import static org.bukkit.event.EventPriority.MONITOR;
import static org.bukkit.event.inventory.InventoryType.SlotType.ARMOR;

public class ArmorKit extends Component implements MutableHolder<Player> {
	
	private final Subject<Player> addSubject = create();
	
	private final Subject<Player> removeSubject = create();
	
	private final Set<Player> players = new HashSet<>();
	
	@NotNull
	private final Collection<Material> armor;
	
	public ArmorKit(@NotNull Collection<Material> armor) {
		this.armor = armor;
		
		/*
		 * Checks whether a player equipped armor by
		 * right-clicking the item.
		 */
		addChild(listen(MONITOR, (PlayerInteractEvent event) -> {
			ItemStack item = event.getItem();
			
			if (item == null || !armor.contains(item.getType())) {
				return;
			}
			
			checkWearing(event.getPlayer());
		}));
		
		/*
		 * Checks whether a player equipped armor manually.
		 */
		addChild(listen(MONITOR, (InventoryClickEvent event) -> {
			if (!(event.getWhoClicked() instanceof Player)) {
				return;
			}
			
			if (event.getSlotType() != ARMOR && !event.isShiftClick()) {
				return;
			}
			
			Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), () ->
					checkWearing((Player) event.getWhoClicked()), 1);
		}));
		
		addChild(listen((PlayerJoinEvent event) -> checkWearing(event.getPlayer())));
		addChild(listen((PlayerQuitEvent event) -> remove(event.getPlayer())));
		
		onEnable(() -> getOnlinePlayers().forEach(this::checkWearing));
	}
	
	private void checkWearing(@NotNull Player player) {
		for (ItemStack item : player.getInventory().getArmorContents()) {
			if (item == null || !armor.contains(item.getType())) {
				setWearing(player, false);
				return;
			}
		}
		
		setWearing(player, true);
	}
	
	private void setWearing(Player player, boolean wearing) {
		if (wearing && !test(player)) {
			add(player);
		} else if (test(player) && !wearing) {
			remove(player);
		}
	}
	
	@NotNull
	@Override
	public Observable<Player> onAdd() {
		return addSubject;
	}
	
	@NotNull
	@Override
	public Observable<Player> onRemove() {
		return removeSubject;
	}
	
	@Override
	public boolean test(Player player) {
		return players.contains(player);
	}
	
	@Override
	public boolean add(Player element) {
		addSubject.onNext(element);
		
		return players.add(element);
	}
	
	@Override
	public boolean remove(Object element) {
		if (element instanceof Player) {
			removeSubject.onNext((Player) element);
		}
		
		return players.remove(element);
	}
	
	@NotNull
	@Override
	public Iterator<Player> iterator() {
		return players.iterator();
	}
	
}