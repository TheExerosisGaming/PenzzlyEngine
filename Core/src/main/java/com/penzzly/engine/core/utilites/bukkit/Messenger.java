package com.penzzly.engine.core.utilites.bukkit;

import org.bukkit.entity.Player;

import static com.penzzly.engine.core.utilites.bukkit.ServerUtil.registerChannel;

public class Messenger {
	private static final String BUNGEE = "Bungee";
	private final Player player;
	
	public Messenger(Player player) {
		this.player = player;
		registerChannel(BUNGEE);
	}
	
}
