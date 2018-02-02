package com.penzzly.packages.factions.components;

import com.penzzly.engine.architecture.base.Component;
import com.penzzly.engine.core.components.player.BanComponent;
import com.penzzly.engine.core.utilites.time.Duration;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.penzzly.engine.core.base.Events.listen;
import static java.lang.System.currentTimeMillis;
import static java.time.Instant.ofEpochMilli;
import static java.util.Date.from;

public class GrizzlyDeathbans extends Component {
	
	private final List<Consumer<Player>> joinListeners = new ArrayList<>();
	
	public Consumer<Player> onJoin(Consumer<Player> listener) {
		joinListeners.add(listener);
		return listener;
	}
	
	@NotNull
	public List<Consumer<Player>> getJoinListeners() {
		return joinListeners;
	}
	
	public GrizzlyDeathbans(@NotNull BanComponent banComponent, String reason, @NotNull Function<Player, Duration> banTime, @NotNull Map<Player, Integer> lives) {
		addChild(listen((PlayerDeathEvent event) -> {
			Player player = event.getEntity();
			player.setHealth(20);
			
			lives.compute(player, ($, remaining) -> {
				if (remaining == null || remaining == 0) {
					banComponent.ban(player, reason, from(ofEpochMilli(currentTimeMillis() + banTime.apply(player).toMillis())));
					return null;
				}
				respawnPlayer(player, remaining--);
				if (remaining <= 0) {
					lives.remove(player);
				}
				return remaining;
			});
		}));
	}
	
	private void respawnPlayer(Player player, int livesRemaining) {
	
	}
}