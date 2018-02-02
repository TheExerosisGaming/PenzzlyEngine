package com.penzzly.packages.factions.components.grizzly.unconfigurable.classes;

import com.penzzly.engine.architecture.base.Component;
import com.penzzly.engine.architecture.holder.Holder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static com.penzzly.engine.core.base.Events.listen;
import static com.penzzly.engine.core.utilites.TempItemUtil.addEnchantment;
import static com.penzzly.engine.core.utilites.TempItemUtil.removeEnchantment;
import static java.lang.Integer.MAX_VALUE;
import static java.util.Arrays.asList;
import static org.bukkit.Material.*;
import static org.bukkit.enchantments.Enchantment.*;
import static org.bukkit.entity.EntityType.PLAYER;
import static org.bukkit.event.EventPriority.MONITOR;
import static org.bukkit.potion.PotionEffectType.NIGHT_VISION;
import static org.bukkit.potion.PotionEffectType.SPEED;

public class GrizzlyArcher extends Component {
	
	@NotNull
	private static final List<PotionEffect> POTION_EFFECTS;
	
	@NotNull
	private static final Set<Material> ARCHER_ARMOR;
	
	static {
		ARCHER_ARMOR = newHashSet(
				LEATHER_HELMET, LEATHER_CHESTPLATE,
				LEATHER_LEGGINGS, LEATHER_BOOTS
		);
		
		POTION_EFFECTS = asList(
				new PotionEffect(NIGHT_VISION, MAX_VALUE, 1, true),
				new PotionEffect(SPEED, MAX_VALUE, 2, true)
		);
	}
	
	public GrizzlyArcher() {
		Holder<Player> archers = addChild(new ArmorKit(ARCHER_ARMOR)).onAdd(player -> {
			POTION_EFFECTS.forEach(player::addPotionEffect);
			player.sendMessage("You are now an archer!");
		}).onRemove(player -> {
			POTION_EFFECTS.stream()
					.map(PotionEffect::getType)
					.forEach(player::removePotionEffect);
			player.sendMessage("You are no longer an archer!");
		});
		
		addChild(listen((EntityShootBowEvent event) -> {
			if (event.getEntity().getType() == PLAYER && archers.contains(event.getEntity())) {
				addEnchantment(event.getBow(), ARROW_DAMAGE, 2);
				addEnchantment(event.getBow(), ARROW_INFINITE, 1);
				addEnchantment(event.getBow(), ARROW_FIRE, 1);
			}
		}));
		
		addChild(listen(MONITOR, (EntityShootBowEvent event) -> {
			if (event.getEntity().getType() == PLAYER && archers.contains(event.getEntity())) {
				removeEnchantment(event.getBow(), ARROW_DAMAGE, 2);
				removeEnchantment(event.getBow(), ARROW_INFINITE, 1);
				removeEnchantment(event.getBow(), ARROW_FIRE, 1);
			}
		}));
	}
	
}
