package com.penzzly.engine.core.mini.partial;

import com.comphenix.protocol.events.PacketContainer;
import com.penzzly.engine.architecture.base.Component;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import static com.comphenix.protocol.PacketType.Play.Client.USE_ENTITY;
import static com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;
import static com.penzzly.engine.core.utilites.bukkit.PacketUtil.intercept;

public class TestComponent extends Component {
	
	public TestComponent() {
		addChild(intercept(USE_ENTITY, event -> {
			PacketContainer packet = event.getPacket();
			EntityUseAction action = packet.getEntityUseActions().read(0);
			if (action == EntityUseAction.INTERACT) {
				Vector vector = packet.getPositionModifier().read(0).toVector();
				Block block = vector.toLocation(event.getPlayer().getWorld()).getBlock();
				if (block.getTypeId() == 1335)
					event.setCancelled(true);
			}
		}));
		
	}
}
