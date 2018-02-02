package com.penzzly.engine.core.components.player;

import com.penzzly.engine.architecture.base.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.util.Vector;
import org.javatuples.Triplet;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.penzzly.engine.core.base.Events.listen;
import static java.lang.Math.floor;
import static org.javatuples.Triplet.with;

public class FreezeComponent extends Component {
	private final Map<Player, Triplet<Boolean, Boolean, Float>> players = new HashMap<>();
	
	public FreezeComponent() {
		onDisable(() -> players.keySet().forEach(this::thaw));
		addChild(listen(PlayerToggleFlightEvent.class, event -> {
			if (isFrozen(event.getPlayer())) {
				event.setCancelled(true);
				event.getPlayer().setVelocity(new Vector(0, 0, 0));
			}
		}));
		addChild(listen(PlayerMoveEvent.class, event -> {
			if (!isFrozen(event.getPlayer())) {
				return;
			}
			Location from = event.getFrom();
			Location to = event.getTo();
			double x = floor(from.getX());
			double z = floor(from.getZ());
			
			if (floor(to.getZ()) != z || floor(to.getX()) != x) {
				x += .5;
				z += .5;
				event.getPlayer().teleport(new Location(from.getWorld(), x, from.getY(), z, from.getYaw(), from.getPitch()));
			}
		}));
	}
	
	public boolean isFrozen(Player player) {
		return players.containsKey(player);
	}
	
	public void freeze(@NotNull Player player) {
		if (!isFrozen(player)) {
			players.put(player, with(player.getAllowFlight(), player.isFlying(), player.getFlySpeed()));
			player.setFlySpeed(0);
			player.setAllowFlight(true);
			player.setFlying(true);
			player.setVelocity(new Vector(0, 0, 0));
		}
	}
	
	public void thaw(@NotNull Player player) {
		if (isFrozen(player)) {
			Triplet<Boolean, Boolean, Float> result = players.remove(player);
			player.setFlySpeed(result.getValue2());
			player.setFlying(result.getValue1());
			player.setAllowFlight(result.getValue0());
		}
	}
}
