package com.penzzly.packages.factions.components.temp;

import com.penzzly.engine.architecture.base.Component;
import com.penzzly.engine.architecture.functions.compat.Predicate;
import com.penzzly.engine.core.utilites.time.Duration;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static java.lang.System.currentTimeMillis;

public class PvPComponent extends Component implements Predicate<Player> {
	private final Map<Player, Long> inPvp = new HashMap<>();
	
	//TODO replace with function of player for dur.
	public PvPComponent(@NotNull Duration duration) {
		addChild(listen(EntityDamageByEntityEvent.class, event -> {
			if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
				inPvp.put((Player) event.getEntity(), currentTimeMillis() + duration.toMillis());
				inPvp.put((Player) event.getDamager(), currentTimeMillis() + duration.toMillis());
			}
		}));
	}
	
	public boolean isInPvp(Player player) {
		return inPvp.compute(player, ($, remaining) -> {
			if (remaining == null || remaining <= currentTimeMillis()) {
				return null;
			}
			return remaining;
		}) != null;
	}
	
	@Override
	public boolean test(Player player) {
		return isInPvp(player);
	}
}