package com.penzzly.engine.core.utilites.holder;

import com.penzzly.engine.architecture.base.Toggleable;
import com.penzzly.engine.architecture.holder.mutable.CachedHolder;
import com.penzzly.engine.core.utilites.bukkit.ServerUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class OnlinePlayerHolder extends CachedHolder<Player> implements Listener, Toggleable {
	private boolean enabled = false;
	
	@EventHandler
	private void onJoin(@NotNull PlayerJoinEvent event) {
		add(event.getPlayer());
	}
	
	@EventHandler
	private void onQuit(@NotNull PlayerQuitEvent event) {
		remove(event.getPlayer());
	}
	
	@NotNull
	@Override
	public OnlinePlayerHolder enable() {
		if (!enabled) {
			ServerUtil.registerListener(this);
		}
		enabled = true;
		return this;
	}
	
	@NotNull
	@Override
	public OnlinePlayerHolder disable() {
		if (enabled) {
			ServerUtil.unregisterListener(this);
		}
		enabled = false;
		return this;
	}
	
	@Override
	public boolean isEnabled() {
		return enabled;
	}
}