package com.penzzly.packages.factions.components.grizzly.commands;

import com.penzzly.engine.architecture.base.Component;
import com.penzzly.engine.architecture.base.Toggleable;
import com.penzzly.engine.core.components.command.CommandComponent;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import static com.penzzly.engine.core.base.Disable.*;
import static com.penzzly.engine.core.utilites.bukkit.ServerUtil.getPlugin;
import static com.penzzly.engine.core.utilites.functions.Functions.entityType;
import static com.penzzly.engine.core.utilites.time.Duration.For;
import static java.util.concurrent.TimeUnit.MINUTES;
import static org.bukkit.entity.EntityType.PRIMED_TNT;

public class WorldCommand extends Component {
	
	public WorldCommand(@NotNull CommandComponent commands, String node) {
		commands.onCommand("world"::equals, (player, args) -> {
			args.next(subCommand -> {
				if (player.hasPermission(node + '.' + subCommand)) {
					if ("start".equals(subCommand)) {
						pvp().disable();
						hungerChange().disable();
						explosions(entityType(PRIMED_TNT)).disable();
						
						Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), () -> {
							pvp().enable();
							hungerChange().enable();
							explosions(entityType(PRIMED_TNT)).enable();
						}, args.asOr(For(20, MINUTES)).toMillis() / 50);
					} else if ("cancel".equals(subCommand)) {
						getChildren().forEach(Toggleable::disable);
					} else {
						//TODO add end For the world phase.
					}
				}
			});
		});
	}
	
}
