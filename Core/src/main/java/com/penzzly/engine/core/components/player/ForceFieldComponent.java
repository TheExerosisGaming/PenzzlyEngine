package com.penzzly.engine.core.components.player;

import com.penzzly.engine.architecture.base.Component;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

import static com.penzzly.engine.core.base.Scheduler.every;

public class ForceFieldComponent extends Component {
	
	public ForceFieldComponent(@NotNull Iterable<Player> players, @NotNull Predicate<Entity> entities, Number range) {
		this(players, entities, new Vector(range.intValue(), range.intValue(), range.intValue()));
	}
	
	public static void main(String[] args) {
		new ForceFieldComponent(null, entity -> {
			return
		})
	}
	
	public ForceFieldComponent(@NotNull Iterable<Player> players, @NotNull Predicate<Entity> entities, Vector range) {
		addChild(every().second().run(() ->
				players.forEach(player ->
						player.getNearbyEntities(range.getX(), range.getY(), range.getZ())
								.stream()
								.filter(entities)
								.forEach(entity ->
										entity.setVelocity(getLaunchVector(player, entity))
								)
				)
		).forever().synchronously());
	}
	
	private static Vector getLaunchVector(@NotNull Player player, @NotNull Entity entity) {
		return entity.getLocation()
				.toVector()
				.subtract(player.getLocation().toVector())
				.normalize()
				.multiply(0.7)
				.setY(0.1);
		
	}
}
