package com.penzzly.engine.core.utilites.color;

import com.penzzly.engine.core.utilites.tags.Colored;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

public enum Palettes {
	BLUE, GREEN, PURPLE, YELLOW, RED, AQUA;
	
	
	public Palette accent(ChatColor accent, boolean dark) {
		switch (this) {
			case BLUE:
				return new Palette(ChatColor.DARK_BLUE, ChatColor.BLUE, accent, dark);
			case GREEN:
				return new Palette(ChatColor.DARK_GREEN, ChatColor.GREEN, accent, dark);
			case PURPLE:
				return new Palette(ChatColor.DARK_PURPLE, ChatColor.LIGHT_PURPLE, accent, dark);
			case YELLOW:
				return new Palette(ChatColor.GOLD, ChatColor.YELLOW, accent, dark);
			case RED:
				return new Palette(ChatColor.DARK_RED, ChatColor.RED, accent, dark);
			case AQUA:
				return new Palette(ChatColor.DARK_AQUA, ChatColor.AQUA, accent, dark);
			default:
				return new Palette(ChatColor.DARK_AQUA, ChatColor.AQUA, accent, dark);
		}
	}
	
	
	public static String primary(@NotNull Colored palette) {
		return palette.getColor().getPrimary().toString();
	}
	
	public static String ta(@NotNull Colored palette) {
		return palette.getColor().getAccent().toString();
	}
	
	public static String secondary(@NotNull Colored palette) {
		return palette.getColor().getSecondary().toString();
	}
	
	public static String darkShade(@NotNull Colored palette) {
		return palette.getColor().getDarkShader().toString();
	}
	
	public static String lightShade(@NotNull Colored palette) {
		return palette.getColor().getLightShader().toString();
	}
	
	
	public static String primary(@NotNull Palette palette) {
		return palette.getPrimary().toString();
	}
	
	public static String accent(@NotNull Palette palette) {
		return palette.getAccent().toString();
	}
	
	public static String secondary(@NotNull Palette palette) {
		return palette.getSecondary().toString();
	}
	
	public static String darkShade(@NotNull Palette palette) {
		return palette.getDarkShader().toString();
	}
	
	public static String lightShade(@NotNull Palette palette) {
		return palette.getLightShader().toString();
	}
	
	public static String bold() {
		return ChatColor.BOLD.toString();
	}
	
	public static String reset() {
		return ChatColor.RESET.toString();
	}
}
