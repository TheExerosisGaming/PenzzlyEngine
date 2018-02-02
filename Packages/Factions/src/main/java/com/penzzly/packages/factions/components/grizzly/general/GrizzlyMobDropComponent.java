package com.penzzly.packages.factions.components.grizzly.general;

import com.penzzly.engine.architecture.base.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static com.penzzly.engine.core.base.Events.listen;
import static java.util.concurrent.ThreadLocalRandom.current;
import static org.bukkit.Material.*;
import static org.bukkit.enchantments.Enchantment.LOOT_BONUS_MOBS;

public final class GrizzlyMobDropComponent extends Component {
	
	public GrizzlyMobDropComponent(@NotNull Function<Player, Number> expBouns) {
		addChild(listen((EntityDeathEvent event) -> {
			ItemStack item = null;
			
			Player killer = event.getEntity().getKiller();
			
			if (killer != null) {
				item = killer.getItemInHand();
			}
			
			if (item != null && item.getEnchantments().containsKey(LOOT_BONUS_MOBS)) {
				event.setDroppedExp((int) (event.getDroppedExp() * expBouns.apply(killer).doubleValue()));
			}
			
			List<ItemStack> drops = new ArrayList<>(event.getDrops());
			
			switch (event.getEntityType()) {
				case IRON_GOLEM:
					drops.removeIf(drop -> drop.getType() == Material.RED_ROSE);
					break;
				case CHICKEN:
					drops.clear();
					drops.add(new ItemStack(FEATHER));
					break;
				case SPIDER:
					drops.clear();
					drops.add(new ItemStack(STRING));
					break;
				case SHEEP:
					if (event.getEntity().getFireTicks() > 0) {
						drops.add(new ItemStack(GRILLED_PORK));
					} else {
						drops.add(new ItemStack(PORK));
					}
					break;
				case BLAZE:
					drops.clear();
					drops.add(new ItemStack(BLAZE_ROD));
					break;
				case GHAST:
					drops.clear();
					drops.add(new ItemStack(NETHER_WARTS));
					drops.add(new ItemStack(ARROW, 5));
					break;
				case SKELETON:
					if (Math.random() <= 0.15) {
						drops.add(new ItemStack(BOW));
					}
					break;
				case SILVERFISH:
					drops.clear();
					drops.add(new ItemStack(SLIME_BALL, current().nextInt(3)));
					break;
				case WITCH:
					drops.clear();
					//TODO figure out what potion this is?
					if (Math.random() <= 0.03) {
						drops.add(new ItemStack(POTION, 1, (short) 16421));
					}
					break;
			}
			
			event.getDrops().clear();
			event.getDrops().addAll(drops);
		}));
	}
}
