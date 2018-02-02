package com.penzzly.packages.factions.components.temp;

import com.comphenix.protocol.events.PacketContainer;
import com.penzzly.engine.architecture.base.Component;
import com.penzzly.engine.architecture.functions.compat.Function;
import com.penzzly.engine.core.utilites.time.Duration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.comphenix.protocol.PacketType.Play.Server.ENTITY_DESTROY;
import static com.penzzly.engine.core.base.Scheduler.schedule;
import static com.penzzly.engine.core.utilites.bukkit.PacketUtil.broadcastSilently;
import static com.penzzly.engine.core.utilites.bukkit.PacketUtil.intercept;

/**
 * Created by Home on 11/26/2017.
 */
public class CombatLoggerComponent extends Component {
	private final List<Consumer<Player>> combatLogListeners = new ArrayList<>();
	
	//TODO store in map, listen for dmg... record health... ensure player death.
	public CombatLoggerComponent(@NotNull PvPComponent pvpComponent, @NotNull Function<Player, Duration> loggerTime) {
		addChild(intercept(ENTITY_DESTROY, event -> {
			PacketContainer packet = event.getPacket();
			
			//FIXME lol
//			Player player = getPlayer(packet.getOrSet().read(0));
			Player player = null;
			if (player != null && pvpComponent.isInPvp(player)) {
				event.setCancelled(true);
				addChild(schedule(loggerTime.apply(player)).run(() -> broadcastSilently(packet)));
			}
		}));
	}
	
	public Consumer<Player> onCombatLog(Consumer<Player> listener) {
		combatLogListeners.add(listener);
		return listener;
	}
	
	@NotNull
	public List<Consumer<Player>> getCombatLogListeners() {
		return combatLogListeners;
	}
}
