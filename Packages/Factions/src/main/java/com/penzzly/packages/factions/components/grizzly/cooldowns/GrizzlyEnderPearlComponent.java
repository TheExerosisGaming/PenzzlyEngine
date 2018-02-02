package com.penzzly.packages.factions.components.grizzly.cooldowns;

import com.penzzly.engine.architecture.base.Component;
import com.penzzly.engine.core.utilites.time.Duration;
import com.penzzly.packages.factions.components.temp.Debouncer;
import com.penzzly.packages.factions.components.temp.PvPComponent;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.jetbrains.annotations.NotNull;

import static com.penzzly.engine.core.utilites.functions.Functions.setCancelled;


public class GrizzlyEnderPearlComponent extends Component {
	
	//TODO add action bar for cooldown
	public GrizzlyEnderPearlComponent(@NotNull PvPComponent pvpComponent, @NotNull Duration duration) {
		addChild(listen(ProjectileLaunchEvent.class))
				.filter(event -> event.getEntity() instanceof EnderPearl &&
						event.getEntity().getShooter() instanceof Player &&
						pvpComponent.test((Player) event.getEntity().getShooter()))
				.compose(new Debouncer<>(event -> event.getEntity().getShooter(), setCancelled(true), duration))
				.subscribe();
	}
}