package com.penzzly.packages.factions.components.grizzly.commands;

import com.penzzly.engine.architecture.base.Component;
import com.penzzly.engine.core.components.command.CommandComponent;
import com.penzzly.engine.core.components.player.BanComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ReviveCommand extends Component {
	
	public ReviveCommand(@NotNull CommandComponent commands, @NotNull BanComponent banComponent) {
		//TODO add reason check again.
		commands.onCommand("revive"::equals, (sender, args) ->
				banComponent.pardon(args.asUnsafe(Player.class))
		);
	}
	
}
