package com.penzzly.engine.core.mini;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

//https://gist.github.com/Exerosis/3ca8398185664f0726b4e2bef85c1822
public class MiniCommandHandler {
	private JavaPlugin plugin;
	
	public MiniCommandHandler(JavaPlugin plugin) {
		this.plugin = plugin;
	}
	
	public MiniCommandHandler registerCommand(String name, BiConsumer<Player, Arguments> command) {
		return registerCommand(name, (player, args) -> {
			command.accept(player, args);
			return true;
		});
	}
	
	public MiniCommandHandler registerCommand(String name, BiPredicate<Player, Arguments> command) {
		plugin.getCommand(name).setExecutor((sender, $, name1, args) ->
				!(sender instanceof Player) || command.test((Player) sender, new Arguments(args)));
		return this;
	}
	
	public class Arguments {
		final String[] args;
		int index = 0;
		
		Arguments(String[] args) {
			this.args = args;
		}
		
		
		//TODO add very simple type adapters.
		
		
		public String nextUnsafe() {
			return nextOr(null);
		}
		
		public Optional<String> next() {
			if (index < args.length) {
				return Optional.of(args[index++]);
			}
			return Optional.empty();
		}
		
		public Arguments next(Consumer<String> arg) {
			if (index < args.length) {
				arg.accept(args[index++]);
			}
			return this;
		}
		
		public String nextOr(String alternative) {
			if (index < args.length) {
				return args[index++];
			}
			return alternative;
		}
		
		public String[] args() {
			return args;
		}
	}
	
}