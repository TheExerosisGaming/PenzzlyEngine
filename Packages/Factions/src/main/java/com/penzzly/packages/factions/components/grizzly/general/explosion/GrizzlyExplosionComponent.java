package com.penzzly.packages.factions.components.grizzly.general.explosion;

import com.penzzly.engine.architecture.base.Component;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityCombustByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import static com.penzzly.engine.core.base.Events.listen;

public class GrizzlyExplosionComponent extends Component {
	public GrizzlyExplosionComponent(int radius) {
		addChild(new GrizzlyTNTComponent(radius));
		addChild(new GrizzlyCannonComponent());
		
		addChild(listen((EntityCombustByBlockEvent event) -> {
			event.setCancelled(event.getEntityType() == EntityType.DROPPED_ITEM);
		}));
		
		addChild(listen((EntityDamageByEntityEvent event) -> {
			EntityType type = event.getDamager().getType();
			
			if (type != EntityType.PRIMED_TNT && type != EntityType.CREEPER) {
				return;
			}
			
			event.setCancelled(event.getEntity().getType() == EntityType.DROPPED_ITEM);
		}));
	}
}
