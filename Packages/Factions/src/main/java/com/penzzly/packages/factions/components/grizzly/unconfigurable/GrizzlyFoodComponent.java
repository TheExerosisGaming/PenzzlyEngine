package com.penzzly.packages.factions.components.grizzly.unconfigurable;

import com.penzzly.engine.architecture.base.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffect;

import static com.penzzly.engine.core.base.Events.listen;
import static com.penzzly.engine.core.utilites.bukkit.ServerUtil.getPlugin;
import static org.bukkit.potion.PotionEffectType.*;

public class GrizzlyFoodComponent extends Component {
	public GrizzlyFoodComponent() {
		addChild(listen((PlayerItemConsumeEvent event) -> {
			Material material = event.getItem().getType();
			Player player = event.getPlayer();
			switch (material) {
				case BREAD:
					player.addPotionEffect(new PotionEffect(DAMAGE_RESISTANCE, 100, 0));
					break;
				case BAKED_POTATO:
					player.addPotionEffect(new PotionEffect(SPEED, 300, 0));
					break;
				case CARROT:
					player.addPotionEffect(new PotionEffect(NIGHT_VISION, 600, 0));
					break;
				case MUSHROOM_SOUP:
					player.setHealth(player.getHealth() + 2D);
					break;
				case COOKED_CHICKEN:
					player.addPotionEffect(new PotionEffect(REGENERATION, 60, 0));
					break;
				case GOLDEN_CARROT:
					player.addPotionEffect(new PotionEffect(WATER_BREATHING, 200, 0));
					player.addPotionEffect(new PotionEffect(NIGHT_VISION, 300, 0));
					break;
				case MELON:
					player.setHealth(player.getHealth() + 1D);
					break;
				case COOKED_FISH:
					player.addPotionEffect(new PotionEffect(WATER_BREATHING, 200, 0));
					break;
				case RAW_FISH:
					player.addPotionEffect(new PotionEffect(CONFUSION, 200, 0));
					player.addPotionEffect(new PotionEffect(WEAKNESS, 200, 0));
					break;
				case COOKIE:
					player.addPotionEffect(new PotionEffect(SPEED, 100, 1));
					
					Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), () -> {
						player.addPotionEffect(new PotionEffect(SLOW, 50, 1));
					}, 20 * 30);
					break;
				default:
					break;
			}
		}));
	}
}
