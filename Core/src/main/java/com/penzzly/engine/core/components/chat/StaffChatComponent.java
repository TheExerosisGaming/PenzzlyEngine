package com.penzzly.engine.core.components.chat;

import com.penzzly.engine.architecture.base.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public class StaffChatComponent extends Component implements Listener, CommandExecutor {
	public static final String COMMAND = "sc";
	private final Set<Player> staffChat = new HashSet<>();
	private final JavaPlugin plugin;
	
	public StaffChatComponent(JavaPlugin plugin) {
		this.plugin = plugin;
	}
	
	public boolean isInStaffChat(Player player) {
		return staffChat.contains(player);
	}
	
	@EventHandler
	void onChat(AsyncPlayerChatEvent event) {
		if (staffChat.contains(event.getPlayer()))
			event.getRecipients().retainAll(staffChat);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String name, String[] args) {
		if (sender instanceof Player)
			if (!staffChat.remove(sender))
				staffChat.add((Player) sender);
		return true;
	}
	
	@Override
	public Component enable() {
		Bukkit.getPluginManager().registerEvents(this, plugin);
		plugin.getCommand(COMMAND).setExecutor(this);
		return super.enable();
	}
	
	@Override
	public Component disable() {
		HandlerList.unregisterAll(this);
		plugin.getCommand("sc").setExecutor(null);
		return super.disable();
	}
}
