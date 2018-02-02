package com.penzzly.packages.factions.components.grizzly.unconfigurable.classes;

import com.penzzly.engine.architecture.base.Component;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static java.lang.Integer.MAX_VALUE;
import static java.util.Arrays.asList;
import static org.bukkit.Material.*;
import static org.bukkit.potion.PotionEffectType.*;

public class GrizzlyMiner extends Component {
	
	@NotNull
	private static final List<PotionEffect> POTION_EFFECTS;
	
	@NotNull
	private static final Set<Material> MINER_ARMOR;
	
	static {
		MINER_ARMOR = newHashSet(
				IRON_HELMET, IRON_CHESTPLATE,
				IRON_LEGGINGS, IRON_BOOTS
		);
		
		POTION_EFFECTS = asList(
				new PotionEffect(FAST_DIGGING, MAX_VALUE, 2, true),
				new PotionEffect(NIGHT_VISION, MAX_VALUE, 2, true),
				new PotionEffect(SPEED, MAX_VALUE, 1, true)
		);
	}
	
	public GrizzlyMiner() {
		addChild(new ArmorKit(MINER_ARMOR)).onAdd(player -> {
			POTION_EFFECTS.forEach(player::addPotionEffect);
			player.sendMessage("You are now a miner!");
		}).onRemove(player -> {
			POTION_EFFECTS.stream()
					.map(PotionEffect::getType)
					.forEach(player::removePotionEffect);
			player.sendMessage("You are no longer a miner!");
		});
	}
	
}
