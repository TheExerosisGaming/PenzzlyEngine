package com.penzzly.engine.core.utilites.bukkit;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public final class LocationUtil {
	private LocationUtil() {
	}
	
	public static double distance(@NotNull Entity one, @NotNull Entity two) {
		return distance(one.getLocation(), two.getLocation());
	}
	
	public static double distance(@NotNull Location one, Location two) {
		return one.distance(two);
	}
	
	public static double distance(@NotNull Vector one, @NotNull Vector two) {
		return one.distance(two);
	}
}
