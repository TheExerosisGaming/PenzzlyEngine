package com.penzzly.engine.core.components.player;

import com.penzzly.engine.architecture.base.Component;
import com.penzzly.engine.core.base.Scheduler;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class ForceFieldComponent extends Component {
	
	public ForceFieldComponent(@NotNull Iterable<Player> players, @NotNull Predicate<Entity> entities, int range) {
		addChild(Scheduler.every().second().run(() ->
				players.forEach(player ->
						player.getNearbyEntities(range, range, range).forEach(entity -> {
							if (entities.test(entity)) {
								entity.setVelocity(getLaunchVector(player, entity));
							}
						}))).forever().synchronously());
	}
	
	private Vector getLaunchVector(@NotNull Player player, @NotNull Entity entity) {
		return entity.getLocation().toVector().subtract(player.getLocation().toVector()).normalize().multiply(0.7).setY(0.1);
	}
}
