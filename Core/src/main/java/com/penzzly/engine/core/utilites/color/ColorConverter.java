package com.penzzly.engine.core.utilites.color;

import com.google.common.collect.BiMap;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

import static com.google.common.collect.HashBiMap.create;
import static org.bukkit.DyeColor.*;

public class ColorConverter {
	private final static BiMap<DyeColor, ChatColor> colors = create();
	
	static {
		colors.put(BLACK, ChatColor.DARK_GRAY);
		colors.put(BLUE, ChatColor.DARK_BLUE);
		colors.put(BROWN, ChatColor.GOLD);
		colors.put(CYAN, ChatColor.AQUA);
		colors.put(GRAY, ChatColor.GRAY);
		colors.put(GREEN, ChatColor.DARK_GREEN);
		colors.put(LIGHT_BLUE, ChatColor.BLUE);
		colors.put(LIME, ChatColor.GREEN);
		colors.put(MAGENTA, ChatColor.LIGHT_PURPLE);
		colors.put(ORANGE, ChatColor.GOLD);
		colors.put(PINK, ChatColor.LIGHT_PURPLE);
		colors.put(PURPLE, ChatColor.DARK_PURPLE);
		colors.put(RED, ChatColor.DARK_RED);
		colors.put(SILVER, ChatColor.GRAY);
		colors.put(WHITE, ChatColor.WHITE);
		colors.put(YELLOW, ChatColor.YELLOW);
	}
	
	
	public ChatColor fromDyeColor(DyeColor color) {
		return colors.get(color);
	}
	
	public DyeColor fromChatColor(ChatColor color) {
		return colors.inverse().get(color);
	}
}
