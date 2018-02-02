package com.penzzly.packages.factions.components.grizzly.commands;

import com.penzzly.engine.architecture.base.Component;
import com.penzzly.engine.core.base.configuration.MapConfiguration;
import com.penzzly.engine.core.base.configuration.YamlConfiguration;
import com.penzzly.engine.core.components.command.CommandComponent;
import com.penzzly.engine.core.components.player.BanComponent;
import com.penzzly.engine.core.components.player.FreezeComponent;
import com.penzzly.engine.core.utilites.time.Duration;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

import static com.penzzly.engine.core.base.Configurations.ymlConfig;
import static com.penzzly.engine.core.utilites.EnumUtil.closest;
import static com.penzzly.engine.core.utilites.time.Duration.For;
import static java.lang.Integer.parseInt;
import static java.util.concurrent.TimeUnit.SECONDS;

public class GrizzlyCommandsComponent extends Component {
	
	public GrizzlyCommandsComponent(@NotNull CommandComponent commands, BanComponent banComponent) {
		YamlConfiguration config = addChild(ymlConfig().enable());
		
		//Setup start world command.
		String node = config.getOrSet("World Node", "grizzly.world");
		
		addChild(new WorldCommand(commands, node));
		
		//Setup chat control commands.
		
		Duration chatSpeed = For(2, SECONDS);
		
		String[] split = config.get("Default Chat Speed", "").split(" ");
		
		if (split.length > 1) {
			chatSpeed = For(parseInt(split[0]), closest(split[1], TimeUnit.class));
		} else {
			config.set("Default Chat Speed", chatSpeed.toString());
		}
		
		addChild(new ChatControlCommand(commands, chatSpeed));
		
		//Setup freeze and thaw commands.
		MapConfiguration messages = config.getSection("Freeze Messages");
		
		FreezeCommand freezeCommand = addChild(new FreezeCommand(commands, new FreezeComponent()));
		
		freezeCommand.onFreeze((target, sender) -> {
			target.sendMessage(format(messages.getOrSet("Frozen", ""), sender));
			sender.sendMessage(format(messages.getOrSet("Freeze", ""), target));
		});
		
		freezeCommand.onThaw((target, sender) -> {
			target.sendMessage(format(messages.getOrSet("Thawed", ""), sender));
			sender.sendMessage(format(messages.getOrSet("Thaw", ""), target));
		});
		
		//Delay /fhome command.
		addChild(new FHomeCommand(config.getOrSet("FHome Delay", 60)));
		
		//Add revive command to pardon deathbans.
		//addChild(new ReviveCommand(commands, banComponent));
	}
}
