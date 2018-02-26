package com.penzzly.packages.factions;

import com.penzzly.engine.core.components.EnginePlugin;
import com.penzzly.engine.core.components.command.CommandComponent;
import com.penzzly.engine.core.components.player.BanComponent;
import com.penzzly.engine.core.components.player.BanComponent.BanInfo;
import com.penzzly.engine.core.utilites.bukkit.ServerUtil;
import com.penzzly.packages.factions.components.grizzly.commands.GrizzlyCommandsComponent;
import com.penzzly.packages.factions.components.grizzly.cooldowns.GrizzlyGoldenAppleComponent;
import com.penzzly.packages.factions.components.grizzly.general.GrizzlyComponent;
import com.penzzly.packages.factions.components.grizzly.unconfigurable.GrizzlyUnconfigurableComponent;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GrizzlyCore extends EnginePlugin {
	
	public GrizzlyCore() {
		ServerUtil.init(this);
		final Map<UUID, BanInfo> bans = new HashMap<>();
		final Map<Player, Integer> lives = new HashMap<>();
		
		BanComponent banComponent = addChild(new BanComponent(bans));
		CommandComponent commands = addChild(new CommandComponent());
		addChild(new GrizzlyCommandsComponent(commands, banComponent));
		addChild(new GrizzlyComponent());
		addChild(new GrizzlyUnconfigurableComponent());
		//addChild(new GrizzlyDeathbans(banComponent, "Death", player -> For(30, SECONDS), lives));
//		addChild(new GrizzlyDeathBanComponent(commands));
		addChild(new GrizzlyGoldenAppleComponent());
	}
	
}