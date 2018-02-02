package com.penzzly.engine.core.utilites.functions;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class Spawner implements Consumer<Player> {
	private final List<Vector> spawns;
	private int index;
	
	public Spawner(List<Vector> spawns) {
		this.spawns = spawns;
	}
	
	@Override
	public void accept(@NotNull Player player) {
		if (spawns.size() < 1) {
			player.kickPlayer("Couldn't find a place to spawn!");
		} else {
			player.teleport(spawns.get(index > spawns.size() - 1 ? index = 0 : index++).toLocation(player.getWorld()));
		}
	}
}
