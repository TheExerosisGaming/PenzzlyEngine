package com.penzzly.packages.factions.components.grizzly.general;

import com.penzzly.engine.architecture.base.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.function.Predicate;

import static com.penzzly.engine.core.base.Events.listen;
import static com.penzzly.engine.core.utilites.bukkit.ServerUtil.getPlugin;
import static org.bukkit.potion.Potion.fromItemStack;

public class GrizzlyPotionsComponent extends Component {
	
	public GrizzlyPotionsComponent(Predicate<PotionEffectType> disabledPotions) {
		addChild(listen((PlayerItemConsumeEvent event) -> {
			ItemStack item = event.getItem();
			
			if (item.getType() != Material.POTION) {
				return;
			}
			
			fromItemStack(item).getEffects()
					.stream()
					.map(PotionEffect::getType)
					.filter(disabledPotions)
					.findAny()
					.ifPresent($ -> event.setCancelled(true));
		}));
		
		addChild(listen((PotionSplashEvent event) -> {
			ThrownPotion potion = event.getPotion();
			
			potion.getEffects()
					.stream()
					.map(PotionEffect::getType)
					.filter(disabledPotions)
					.findAny()
					.ifPresent(effect -> {
						Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), () -> {
							event.getAffectedEntities().forEach(entity -> entity.removePotionEffect(effect));
						}, 1);
						
						event.setCancelled(true);
					});
		}));
	}
	
}
