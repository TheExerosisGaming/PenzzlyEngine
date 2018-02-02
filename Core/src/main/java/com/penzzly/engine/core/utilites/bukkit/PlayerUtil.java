package com.penzzly.engine.core.utilites.bukkit;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.stream.Stream;

import static com.penzzly.engine.core.utilites.bukkit.LocationUtil.distance;

/**
 * Created by BinaryBench on 4/23/2017.
 */
public class PlayerUtil {
	private PlayerUtil() {
	}
	
	public static Stream<Entity> entitiesInSphere(@NotNull Entity target, double radius) {
		return target.getNearbyEntities(radius, radius, radius).stream()
				.filter(entity -> distance(entity, target) <= radius);
	}
	
	public static Optional<Integer> getOpenInventoryId(@NotNull Player player) {
		if (player.getOpenInventory() != null) {
			try {
				Object craftPlayer = player.getClass().getMethod("getHandle").invoke(player);
				Object container = craftPlayer.getClass().getField("activeContainer").get(craftPlayer);
				return Optional.of(container.getClass().getField("windowId").getInt(container));
			} catch (Exception ignored) {
			}
		}
		return Optional.empty();
	}
	
	/**
	 * Resets the {@code Player} who's {@code UUID} is {@code playersUUID}
	 * Health, Hunger, Walk speed, Fall Distance, Fire Ticks, PotionEffects
	 * and clears the player's inventory.
	 *
	 * @param player The player
	 */
	public static void resetPlayer(@NotNull Player player) {
		clearInventory(player);
		clearPotionEffects(player);
		player.resetMaxHealth();
		resetHealth(player);
		resetMaxHunger(player);
		resetWalkSpeed(player);
		player.setFallDistance(0);
		player.setFireTicks(0);
	}
	
	/**
	 * Sets the {@code Player} who's {@code UUID} is {@code playersUUID}
	 * Health to their maxHealth.
	 *
	 * @param player The player
	 */
	public static void resetHealth(@NotNull Player player) {
		player.setHealth(player.getMaxHealth());
	}
	
	/**
	 * Sets the {@code Player} who's {@code UUID} is {@code playersUUID}
	 * Hunger and Saturation to 20.
	 *
	 * @param player The player
	 */
	public static void resetMaxHunger(@NotNull Player player) {
		player.setFoodLevel(20);
		player.setSaturation(20);
	}
	
	/**
	 * Clears the {@code Player} who's {@code UUID} is {@code playersUUID}
	 * Inventory.
	 *
	 * @param player The player
	 */
	public static void clearInventory(@NotNull Player player) {
		player.getInventory().setHelmet(null);
		player.getInventory().setChestplate(null);
		player.getInventory().setLeggings(null);
		player.getInventory().setBoots(null);
		player.getInventory().clear();
	}
	
	/**
	 * Removes all potion effects from the {@code Player} who's {@code UUID}
	 * is {@code playersUUID}.
	 *
	 * @param player The player
	 */
	public static void clearPotionEffects(@NotNull Player player) {
		for (PotionEffect effect : player.getActivePotionEffects())
			player.removePotionEffect(effect.getType());
	}
	
	/**
	 * Sets the {@code Player} who's {@code UUID} is {@code playersUUID}
	 * WalkSpeed to {@code 0.2f}.
	 *
	 * @param player The player
	 */
	public static void resetWalkSpeed(@NotNull Player player) {
		player.setWalkSpeed(0.2f);
	}
}