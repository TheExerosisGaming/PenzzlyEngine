package com.penzzly.packages.factions.components.grizzly.commands;

import com.penzzly.engine.architecture.base.Component;
import com.penzzly.engine.core.components.command.CommandComponent;
import com.penzzly.engine.core.components.player.FreezeComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class FreezeCommand extends Component {
	
	private final List<BiConsumer<Player, CommandSender>> freezeListeners = new ArrayList<>();
	
	private final List<BiConsumer<Player, CommandSender>> thawListeners = new ArrayList<>();
	
	public FreezeCommand(@NotNull CommandComponent commands, @NotNull FreezeComponent freezeComponent) {
		commands.onCommand(command -> command.equals("freeze") || command.equals("thaw"), (sender, args) ->
				args.as(Player.class, target -> {
					if (!freezeComponent.isFrozen(target)) {
						freezeComponent.freeze(target);
						freezeListeners.forEach(listener -> listener.accept(target, sender));
					} else {
						freezeComponent.thaw(target);
						thawListeners.forEach(listener -> listener.accept(target, sender));
					}
				})
		);
	}
	
	public BiConsumer<Player, CommandSender> onFreeze(BiConsumer<Player, CommandSender> listener) {
		freezeListeners.add(listener);
		return listener;
	}
	
	@NotNull
	public List<BiConsumer<Player, CommandSender>> getFreezeListeners() {
		return freezeListeners;
	}
	
	public BiConsumer<Player, CommandSender> onThaw(BiConsumer<Player, CommandSender> listener) {
		thawListeners.add(listener);
		return listener;
	}
	
	@NotNull
	public List<BiConsumer<Player, CommandSender>> getThawListeners() {
		return thawListeners;
	}
	
}
