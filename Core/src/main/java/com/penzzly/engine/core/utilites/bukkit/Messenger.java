package com.penzzly.engine.core.utilites.bukkit;

import org.bukkit.entity.Player;

import static com.penzzly.engine.core.utilites.bukkit.ServerUtil.registerChannel;

public class Messenger {
	private static final String BUNGEE = "Bungee";
	private final Player player;
	event
	
	public Messenger(Player player) {
		this.player = player;
		registerChannel(BUNGEE);
		registerChannel(BUNGEE, (channel, player, data) -> {
		
		});
	}
	
	private Observable<DataInput> messageForResponse(String message) {
		return Observable.create(observer -> {
			sendPluginMessage(player, BUNGEE, message);
			
		});
	}
	
}
