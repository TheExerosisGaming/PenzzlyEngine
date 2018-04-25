package com.penzzly.packages.factions.components.grizzly.unconfigurable.classes;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.penzzly.engine.architecture.base.Component;
import com.penzzly.engine.architecture.holder.Holder;
import com.penzzly.engine.core.utilites.bukkit.PacketUtil;
import com.penzzly.engine.core.utilites.time.Duration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.google.common.collect.Sets.newHashSet;
import static com.penzzly.engine.core.base.Events.listen;
import static com.penzzly.engine.core.base.Scheduler.every;
import static com.penzzly.engine.core.base.Scheduler.schedule;
import static com.penzzly.engine.core.utilites.bukkit.PacketUtil.Particle.create;
import static com.penzzly.engine.core.utilites.time.Duration.For;
import static com.penzzly.packages.factions.components.grizzly.unconfigurable.classes.GrizzlyBard.BardAbility.*;
import static java.lang.Math.max;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.bukkit.Material.*;
import static org.bukkit.Sound.FIREWORK_LAUNCH;
import static org.bukkit.potion.PotionEffectType.*;

public class GrizzlyBard extends Component {
	@NotNull
	private static final Set<Material> MINER_ARMOR;
	private static final List<BardAbility> ABILITIES = new ArrayList<>();
	private static final PacketUtil.Particle HIT = create("largeexplode");
	private static final Vector DOWN = new Vector(0, -0.5, 0);
	private static final int DESNITY = 4;
	private static final int TRAVEL_TIME = 750;
	private static final int CHASE_TIME = 500;
	private static final int FLAT_SPEED = 15;
	private static final float MAX_MANA = 100;
	
	static {
		MINER_ARMOR = newHashSet(
				GOLD_HELMET, GOLD_CHESTPLATE,
				GOLD_LEGGINGS, GOLD_BOOTS
		);
		
		ABILITIES.add(STRENGTH);
		ABILITIES.add(MOVE_SPEED);
		ABILITIES.add(FIRE_RESIST);
		ABILITIES.add(DAMAGE_RESIST);
		ABILITIES.add(JUMP_BOOST);
		ABILITIES.add(REGEN);
	}
	
	enum BardAbility {
		STRENGTH(chains -> 50D,
				hit -> hit.addPotionEffect(new PotionEffect(INCREASE_DAMAGE, 100, 1)),
				For(5, SECONDS),
				10,
				BLAZE_POWDER,
				create("crit")),
		
		MOVE_SPEED(chains -> 50D,
				hit -> hit.addPotionEffect(new PotionEffect(SPEED, 100, 1)),
				For(3, SECONDS),
				10,
				SUGAR,
				create("fireworksSpark")),
		
		FIRE_RESIST(chains -> 50D,
				hit -> hit.addPotionEffect(new PotionEffect(FIRE_RESISTANCE, 100, 1)),
				For(3, SECONDS),
				10,
				MAGMA_CREAM,
				create("flame")),
		
		DAMAGE_RESIST(chains -> 50D,
				hit -> hit.addPotionEffect(new PotionEffect(DAMAGE_RESISTANCE, 100, 1)),
				For(3, SECONDS),
				10,
				IRON_INGOT,
				create("smoke")),
		
		JUMP_BOOST(chains -> 50D,
				hit -> hit.addPotionEffect(new PotionEffect(JUMP, 100, 1)),
				For(3, SECONDS),
				10,
				FEATHER,
				create("magicCrit")),
		
		REGEN(chains -> 50D,
				hit -> hit.addPotionEffect(new PotionEffect(REGENERATION, 100, 1)),
				For(3, SECONDS),
				10,
				GHAST_TEAR,
				create("reddust"));
		
		private final Function<Integer, Double> range;
		private final Consumer<Player> applier;
		private final Duration cooldown;
		private Float manaCost;
		private Material type;
		private PacketUtil.Particle particle;
		
		BardAbility(Function<Integer, Double> range,
		            Consumer<Player> applier,
		            Duration cooldown,
		            @NotNull Number manaCost,
		            Material type,
		            PacketUtil.Particle particle) {
			this.range = range;
			this.applier = applier;
			this.cooldown = cooldown;
			this.manaCost = manaCost.floatValue();
			this.type = type;
			this.particle = particle;
		}
		
		public Material type() {
			return type;
		}
		
		public Duration cooldown() {
			return cooldown;
		}
		
		@NotNull
		public BardAbility apply(Player target) {
			applier.accept(target);
			return this;
		}
		
		public double range(int chains) {
			return range.apply(chains);
		}
	}
	
	private final Multimap<Player, BardAbility> cooldowns = ArrayListMultimap.create();
	private final Map<Player, Float> mana = new HashMap<>();
	private final Map<Player, Float> experience = new HashMap<>();
	
	public GrizzlyBard() {
		Holder<Player> bards = addChild(new ArmorKit(MINER_ARMOR)).onAdd(player -> {
			experience.put(player, player.getExp());
			addMana(player, 0);
			player.sendMessage("You are now a bard!");
		}).onRemove(player -> {
			player.setExp(experience.get(player));
			player.sendMessage("You are no longer a bard!");
		});
		
		//Add mana every 30 seconds.
		addChild(every(30)
				.seconds()
				.forever()
				.run(() -> {
					Iterator<Player> players = mana.keySet().iterator();
					while (players.hasNext()) {
						addMana(players.next(), 30);
					}
				}).synchronously()
		);
		
		addChild(listen((PlayerInteractEvent event) -> {
			Player player = event.getPlayer();
			
			if (!bards.contains(player)) {
				return;
			}
			
			ItemStack item = player.getItemInHand();
			if (item.getType() == Material.AIR) {
				ABILITIES.forEach(ability -> player.getInventory().addItem(new ItemStack(ability.type)));
			}
			
			int slot = player.getInventory().getHeldItemSlot();
			
			if (slot >= ABILITIES.size()) {
				return;
			}
			
			ABILITIES.forEach(ability -> {
				if (ability.type == item.getType()) {
					if (ability.manaCost > mana.computeIfAbsent(player, $ -> MAX_MANA)) {
						player.sendMessage("Not enough mana!");
					} else if (cooldowns.get(player).contains(ability)) {
						player.sendMessage("Cooling down!");
					} else {
						item.setAmount(item.getAmount() - 1);
//						player.setItemInHand(item);
						addMana(player, -ability.manaCost);
						ability.apply(player);
						chain(player, newArrayList(player), ability);
						cooldowns.put(player, ability);
						schedule(ability.cooldown).run(() -> {
							cooldowns.remove(player, ability);
						}).enable();
//						schedule(ability.cooldown).task(addTo(cooldowns.get(player), ability)).enable();
					}
				}
			});
		}));
	}
	
	private void addMana(@NotNull Player player, float amount) {
		if (!mana.containsKey(player)) {
			return;
		}
		if ((amount += mana.get(player)) > MAX_MANA) {
			mana.remove(player);
		} else {
			mana.replace(player, max(0, amount));
		}
		player.setExp(amount / MAX_MANA);
	}
	
	
	private void chain(@NotNull Player target, @NotNull List<Player> targets, @NotNull BardAbility ability) {
		double range = ability.range.apply(targets.size());
		if (range > 0) {
			closePlayers(target, range)
					.filter(t -> !targets.contains(t))
					.findFirst()
					.ifPresent(closest -> {
						if (!targets.contains(closest)) {
							final Vector current = target.getEyeLocation().toVector().add(DOWN);
							final Supplier<Vector> dest = () -> closest.getEyeLocation().toVector().add(DOWN);
							every(new Supplier<Number>() {
								private int timeLeft = TRAVEL_TIME - CHASE_TIME;
								private int timeTraveled = 0;
								
								@Override
								public Number get() {
									if (current.distance(dest.get()) <= 0.2) {
										return 0;
									}
									if (timeTraveled < CHASE_TIME) {
										timeTraveled += FLAT_SPEED;
										return FLAT_SPEED;
									}
									double nextDelay = timeLeft / (current.distance(dest.get()) * DESNITY);
									timeLeft -= nextDelay;
									return nextDelay;
								}
							})
									.milliseconds()
									.run((ticks, task) -> {
										current.add(dest.get()
												.clone()
												.subtract(current)
												.normalize()
												.divide(new Vector(DESNITY, DESNITY, DESNITY))
										);
										spawnParticle(ability.particle, current);
									})
									.forever()
									.synchronously()
									.onComplete(() -> {
												targets.add(closest);
												ability.apply(closest);
												chain(closest, targets, ability);
												spawnParticle(HIT, closest.getLocation());
												closest.getWorld().playSound(closest.getLocation(), FIREWORK_LAUNCH, 1, 1);
											}
									).enable();
						}
					});
		}
	}
	
	private static Stream<Player> closePlayers(@NotNull Player player, double radius) {
//		return player.getNearbyEntities(radius, radius, radius).stream()
//				.filter(entity -> entity.getType() == PLAYER)
//				.sorted(comparingDouble(entity -> distance(entity, player)))
//				.map(Functions.<Player>cast());
		return null;
	}
}