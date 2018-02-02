package com.penzzly.engine.core.components.command;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class CommandGroup {
	
	private final List<String> commands = new ArrayList<>();
	
	private CommandComponent commandComponent;
	
	public CommandGroup(CommandComponent commandComponent) {
		this.commandComponent = commandComponent;
	}
	
	@NotNull
	public CommandGroup onCommand(String name, BiConsumer<CommandSender, CommandComponent.Arguments> command) {
		commands.add(name);
		commandComponent.onCommand(query -> query.equals(name), command);
		return this;
	}
	
}
