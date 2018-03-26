package com.penzzly.engine.core.mini.partial;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class ZoneManager implements Listener {
	
	public ZoneManager(Plugin plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
}
