package com.penzzly.packages.factions.components.grizzly.commands;

import com.penzzly.engine.architecture.base.Component;
import com.penzzly.engine.core.components.command.CommandComponent;
import com.penzzly.packages.factions.components.grizzly.chest.VoidChestComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class VoidChestCommand extends Component {
	public VoidChestCommand(@NotNull CommandComponent commands, @NotNull VoidChestComponent voidChestComponent) {
		commands.onCommand("voidchest"::equals, (player, args) -> {
			Player target = args.asOr(Player.class, (Player) player);
			target.getInventory().addItem(voidChestComponent.getVoidChest());
		});
	}
}
