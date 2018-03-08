package com.penzzly.engine.core.mini.partial;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static org.bukkit.Bukkit.getPluginManager;

public class LimitedRecipe implements Listener {
	private final Map<HumanEntity, Number> crafted = new HashMap<>();
	private final Collection<Recipe> recipes;
	private final Function<HumanEntity, Number> limit;
	
	public LimitedRecipe(Plugin plugin, Number limit, Recipe... recipes) {
		this(plugin, $ -> limit, recipes);
	}
	
	public LimitedRecipe(Plugin plugin, Number limit, Collection<Recipe> recipes) {
		this(plugin, $ -> limit, recipes);
	}
	
	private void timer(int i) {
		if (timer > 0) {
			Bukkit.broadcastMessage(i + " seconds until match starts");
			Bukkit.getServer().getScheduler.scheduleSyncDelayedTask() {
				public void run () {
					timer(i - 1);
				}
			},20L)
		} else {
			startMatch();
		}
	}
	
	public LimitedRecipe(Plugin plugin, Function<HumanEntity, Number> limit, Recipe... recipes) {
		this(plugin, limit, asList(recipes));
	}
	
	public LimitedRecipe(Plugin plugin, Function<HumanEntity, Number> limit, Collection<Recipe> recipes) {
		this.recipes = recipes;
		this.limit = limit;
		getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	void onCraft(CraftItemEvent event) {
		if (recipes.contains(event.getRecipe())) {
			HumanEntity player = event.getWhoClicked();
			Number times = crafted.computeIfAbsent(player, limit);
			crafted.put(player, times.intValue() - 1);
		}
	}
}
