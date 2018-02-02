package com.penzzly.engine.core.components.player;

import com.google.common.collect.ImmutableList;
import com.penzzly.engine.architecture.base.Component;
import com.penzzly.engine.core.utilites.bukkit.ServerUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Created by BinaryBench on 5/23/2017.
 */
public class VoidLevelComponent extends Component {
	private int id = -1;
	
	public VoidLevelComponent(@NotNull Iterable<Player> players) {
		this(players, null, null);
	}
	
	public VoidLevelComponent(@NotNull Iterable<Player> players, int height) {
		this(players, null, () -> height);
	}
	
	public VoidLevelComponent(@NotNull Iterable<Player> players, Predicate<World> worldPredicate) {
		this(players, worldPredicate, null);
	}
	
	public VoidLevelComponent(@NotNull Iterable<Player> players, Supplier<Integer> heightSupplier) {
		this(players, null, heightSupplier);
	}
	
	//TODO Update this!!!
	public VoidLevelComponent(@NotNull Iterable<Player> players, @Nullable Predicate<World> worldPredicate, Supplier<Integer> heightSupplier) {
		
		final Predicate<World> finalWorldPredicate = worldPredicate == null ? world -> true : worldPredicate;
		final Supplier<Integer> finalHeightSupplier = worldPredicate == null ? () -> 0 : heightSupplier;
		
		
		onEnable(() -> {
			this.id = Bukkit.getScheduler().runTaskTimer(ServerUtil.getPlugin(), () -> {
				ImmutableList.copyOf(players).forEach(player -> {
					
					if (finalWorldPredicate.test(player.getWorld()) && player.getLocation().getBlockY() < finalHeightSupplier.get()) {
						player.damage(player.getHealth());
					}
					
				});
			}, 4L, 4L).getTaskId();
		});
		
		onDisable(() -> {
			if (this.id != -1) {
				Bukkit.getScheduler().cancelTask(this.id);
				this.id = -1;
			}
		});
	}
}