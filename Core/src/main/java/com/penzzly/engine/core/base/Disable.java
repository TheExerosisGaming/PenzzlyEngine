package com.penzzly.engine.core.base;

import com.penzzly.engine.architecture.base.Component;
import com.penzzly.engine.architecture.base.Toggleable;
import com.penzzly.engine.architecture.holder.mutable.MutableHolder;
import com.penzzly.engine.core.utilites.bukkit.BlockUtil;
import org.bukkit.Effect;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.penzzly.engine.architecture.utilites.Components.subTo;

/**
 * Created by BinaryBench on 4/20/2017.
 */
public interface Disable {
	
	// ---=== COMPONENTS ===---
	//Explosions
	@NotNull
	static Component explosions() {
		return explosions(entity -> true);
	}
	
	@NotNull
	static Component explosions(@NotNull Predicate<Entity> entities) {
		return Events.listen(EntityExplodeEvent.class, event -> {
			if (!entities.test(event.getEntity())) {
				event.setCancelled(true);
			}
		});
	}
	
	
	//Block Break
	@NotNull
	static Component blockBreak() {
		return blockBreakBlockFilter(player -> true, block -> true);
	}
	
	@NotNull
	static Component blockBreak(@NotNull Predicate<Player> players) {
		return blockBreakBlockFilter(players, block -> true);
	}
	
	@NotNull
	static Component blockBreak(@NotNull Predicate<Player> players, @NotNull Predicate<MaterialData> filter) {
		return blockBreakBlockFilter(players, block -> filter.test(BlockUtil.toMaterialData(block)));
	}
	
	@NotNull
	static Component blockBreakBlockFilter(@NotNull Predicate<Player> players, @NotNull Predicate<Block> filter) {
		return Events.listen(BlockBreakEvent.class, event -> {
			if (players.test(event.getPlayer()) && filter.test(event.getBlock())) {
				event.setCancelled(true);
			}
		});
	}
	
	//Block Place
	@NotNull
	static Component blockPlace() {
		return blockPlaceBlockFilter(player -> true, block -> true);
	}
	
	@NotNull
	static Component blockPlace(@NotNull Predicate<Player> players) {
		return blockPlaceBlockFilter(players, block -> true);
	}
	
	@NotNull
	static Component blockPlace(@NotNull Predicate<Player> players, @NotNull Predicate<MaterialData> filter) {
		return blockPlaceBlockFilter(players, block -> filter.test(BlockUtil.toMaterialData(block)));
	}
	
	@NotNull
	static Component blockPlaceBlockFilter(@NotNull Predicate<Player> players, @NotNull Predicate<Block> filter) {
		return Events.listen(BlockPlaceEvent.class, event -> {
			if (players.test(event.getPlayer()) && filter.test(event.getBlock())) {
				event.setCancelled(true);
			}
		});
	}
	
	//Pick Up Item
	@NotNull
	static Component itemPickup(@NotNull Predicate<Player> players) {
		return itemPickupItemFilter(players, item -> true);
	}
	
	@NotNull
	static Component itemPickup(@NotNull Predicate<Player> players, @NotNull Predicate<MaterialData> filter) {
		return itemPickupItemStackFilter(players, itemStack -> filter.test(itemStack.getData()));
	}
	
	@NotNull
	static Component itemPickupItemStackFilter(@NotNull Predicate<Player> players, @NotNull Predicate<ItemStack> filter) {
		return itemPickupItemFilter(players, item -> filter.test(item.getItemStack()));
	}
	
	@NotNull
	static Component itemPickupItemFilter(@NotNull Predicate<Player> players, @NotNull Predicate<Item> filter) {
		return Events.listen(PlayerPickupItemEvent.class, event -> {
			if (players.test(event.getPlayer()) && filter.test(event.getItem())) {
				event.setCancelled(true);
			}
		});
	}
	
	//Drop Item
	@NotNull
	static Component dropItem(@NotNull Predicate<Player> players) {
		return dropItemItemFilter(players, item -> true);
	}
	
	@NotNull
	static Component dropItem(@NotNull Predicate<Player> players, @NotNull Predicate<MaterialData> filter) {
		return dropItemItemStackFilter(players, itemStack -> filter.test(itemStack.getData()));
	}
	
	@NotNull
	static Component dropItemItemStackFilter(@NotNull Predicate<Player> players, @NotNull Predicate<ItemStack> filter) {
		return dropItemItemFilter(players, item -> filter.test(item.getItemStack()));
	}
	
	@NotNull
	static Component dropItemItemFilter(@NotNull Predicate<Player> players, @NotNull Predicate<Item> filter) {
		return Events.listen(PlayerDropItemEvent.class, event -> {
			if (players.test(event.getPlayer()) && filter.test(event.getItemDrop())) {
				event.setCancelled(true);
			}
		});
	}
	
	//Damage
	@NotNull
	static Component damage() {
		return damage(player -> true, event -> true);
	}
	
	@NotNull
	static Component damage(@NotNull Predicate<Player> players) {
		return damage(players, event -> true);
	}
	
	@NotNull
	static Component damage(@NotNull Predicate<Player> players, EntityDamageEvent.DamageCause cause) {
		return damage(players, event -> event.getCause().equals(cause));
	}
	
	@NotNull
	static Component damage(@NotNull Predicate<Player> players, @NotNull Predicate<EntityDamageEvent> filter) {
		return Events.listen(EntityDamageEvent.class, event -> {
			if (filter.test(event) &&
					event.getEntity() instanceof Player &&
					players.test((Player) event.getEntity())) {
				event.setCancelled(true);
			}
		});
	}
	
	//PvP
	@NotNull
	static Component pvp() {
		return pvp(player -> true);
	}
	
	@NotNull
	static Component pvp(@NotNull Predicate<Player> players) {
		return pvp(players, player -> true);
	}
	
	@NotNull
	static Component pvp(@NotNull Predicate<Player> players, @NotNull Predicate<Player> attackers) {
		return damage(players, event ->
				event instanceof EntityDamageByEntityEvent &&
						((EntityDamageByEntityEvent) event).getDamager() instanceof Player &&
						attackers.test((Player) ((EntityDamageByEntityEvent) event).getDamager())
		);
	}
	
	//Fall
	@NotNull
	static Component fallDamage(@NotNull Predicate<Player> players) {
		return damage(players, EntityDamageEvent.DamageCause.FALL);
	}
	
	// Hunger
	@NotNull
	static Component hunger(@NotNull MutableHolder<Player> players) {
		return hunger(players, 20);
	}
	
	@NotNull
	static Component hunger(@NotNull MutableHolder<Player> players, int foodLevel) {
		Component Component = new Component();
		Component.addChild(hungerChange(players));
		Component.addChild(subTo(players.onAdd(), player -> player.setFoodLevel(foodLevel)));
		Component.onEnable(() -> players.forEach(player -> player.setFoodLevel(foodLevel)));
		return Component;
	}
	
	//Hunger Change
	@NotNull
	static Component hungerChange() {
		return hungerChange(player -> true);
	}
	
	@NotNull
	static Component hungerChange(@NotNull Predicate<Player> players) {
		return Events.listen(FoodLevelChangeEvent.class, event -> {
			if (event.getEntity() instanceof Player && players.test((Player) event.getEntity())) {
				event.setCancelled(true);
			}
		});
	}
	
	//Bow
	@NotNull
	static Component bowShoot(@NotNull Predicate<Player> players) {
		return Events.listen(EntityShootBowEvent.class, event -> {
			if (event.getEntity() instanceof Player && players.test((Player) event.getEntity())) {
				event.setCancelled(true);
			}
		});
	}
	
	// Falling Blocks
	@NotNull
	static Component fallingBlocks(@NotNull Predicate<World> worldPredicate) {
		return Events.listen(EntityChangeBlockEvent.class, event -> {
			if (event.getEntityType().equals(EntityType.FALLING_BLOCK)) {
				
				if (!worldPredicate.test(event.getBlock().getWorld())) {
					return;
				}
				
				if (event.getEntity() instanceof FallingBlock) {
					FallingBlock block = (FallingBlock) event.getEntity();
					
					block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getMaterial());
				}
				event.getEntity().remove();
				event.setCancelled(true);
			}
		});
	}
	
	// ---=== LISTENERS ===---
	
	//     GameRule
	static Consumer<World> gameRule(String gameRule, String value) {
		return world -> world.setGameRuleValue(gameRule, value);
	}
	
	static Consumer<World> gameRule(String gameRule) {
		return gameRule(gameRule, "false");
	}
	
	static Consumer<World> time() {
		return gameRule("doDaylightCycle");
	}
	
	static Consumer<World> time(long time) {
		Consumer<World> stopTime = time();
		return world -> {
			world.setTime(time);
			stopTime.accept(world);
		};
	}
	
	static Consumer<World> currentStorm() {
		return world -> world.setStorm(false);
	}
	
	static Consumer<World> entityDrops() {
		return gameRule("doEntityDrops");
	}
	
	static Consumer<World> fireSpread() {
		return gameRule("doFireTick");
	}
	
	static Consumer<World> mobLoot() {
		return gameRule("doMobLoot");
	}
	
	static Consumer<World> mobSpawning() {
		return gameRule("doMobSpawning");
	}
	
	static Consumer<World> mobGriefing() {
		return gameRule("mobGriefing");
	}
	
	static Consumer<World> naturalRegeneration() {
		return gameRule("naturalRegeneration");
	}
	
	static Consumer<World> randomTickSpeed() {
		return gameRule("randomTickSpeed", "0");
	}
	
	static Consumer<World> deathMessages() {
		return gameRule("showDeathMessages");
	}
	
	@NotNull
	static Toggleable movement(MutableHolder<Player> players) {
		throw new UnsupportedOperationException("Method not yet implemented.");
	}
}
