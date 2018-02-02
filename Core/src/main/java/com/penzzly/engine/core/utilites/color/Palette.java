package com.penzzly.engine.core.utilites.color;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

public class Palette {
	private final ChatColor primary;
	private final ChatColor secondary;
	private final ChatColor accent;
	@NotNull
	private final ChatColor darkShader;
	@NotNull
	private final ChatColor lightShader;
	
	public Palette(ChatColor primary, ChatColor secondary, ChatColor accent, boolean dark) {
		this.primary = primary;
		this.secondary = secondary;
		this.accent = accent;
		darkShader = dark ? ChatColor.DARK_GRAY : ChatColor.GRAY;
		lightShader = dark ? ChatColor.GRAY : ChatColor.WHITE;
	}
	
	public ChatColor getPrimary() {
		return primary;
	}
	
	public ChatColor getAccent() {
		return accent;
	}
	
	public ChatColor getSecondary() {
		return secondary;
	}
	
	@NotNull
	public ChatColor getDarkShader() {
		return darkShader;
	}
	
	@NotNull
	public ChatColor getLightShader() {
		return lightShader;
	}
}