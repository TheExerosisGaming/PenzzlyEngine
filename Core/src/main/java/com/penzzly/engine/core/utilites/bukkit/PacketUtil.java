package com.penzzly.engine.core.utilites.bukkit;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.penzzly.engine.architecture.base.Component;
import com.penzzly.engine.architecture.functions.compat.Consumer;
import com.penzzly.engine.architecture.utilites.Clarifiers;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import static com.comphenix.protocol.PacketType.Play.Server.*;
import static com.comphenix.protocol.ProtocolLibrary.getProtocolManager;
import static com.penzzly.engine.architecture.utilites.Components.create;
import static com.penzzly.engine.core.utilites.bukkit.PlayerUtil.getOpenInventoryId;
import static com.penzzly.engine.core.utilites.bukkit.ServerUtil.getPlugin;

@SuppressWarnings("unchecked")
public final class PacketUtil {
	private PacketUtil() {
	
	}
	
	//https://gist.github.com/anonymous/b058e06e5a0ff97aa1f3e53b4dfb1e7e
	public static class Particle {
		private final String id;
		private int count = 0;
		private float data, x, y, z = 0;
		
		public Particle(String id) {
			this.id = id;
		}
		
		public static Particle create(String id) {
			return new Particle(id);
		}
		
		@NotNull
		public Particle color(@NotNull Color color) {
			return color(color.getRed(), color.getGreen(), color.getBlue());
		}
		
		@NotNull
		public Particle color(@NotNull Number red, @NotNull Number green, @NotNull Number blue) {
			x = red.intValue() / 255f;
			y = green.intValue() / 255f;
			z = blue.intValue() / 255f;
			return this;
		}
		
		@NotNull
		public Particle offset(@NotNull Vector vector) {
			x = (float) vector.getX();
			y = (float) vector.getY();
			z = (float) vector.getZ();
			return this;
		}
		
		@NotNull
		public Particle offset(@NotNull Number randSeed) {
			return offset(randSeed, randSeed, randSeed);
		}
		
		@NotNull
		public Particle offset(@NotNull Number x, @NotNull Number y, @NotNull Number z) {
			this.x = x.floatValue();
			this.y = y.floatValue();
			this.z = z.floatValue();
			return this;
		}
		
		@NotNull
		public Particle data(float data) {
			this.data = data;
			return this;
		}
		
		@NotNull
		public Particle count(@NotNull Number count) {
			this.count = count.intValue();
			return this;
		}
	}
	
	public static void spawnParticle(@NotNull Particle particle, @NotNull Location location) {
		spawnParticle(particle, location.toVector());
	}
	
	public static void spawnParticle(@NotNull Particle particle, @NotNull Vector location) {
		spawnParticle(null, particle, location);
	}
	
	public static void spawnParticle(Player player, @NotNull Particle particle, @NotNull Location location) {
		spawnParticle(player, particle, location.toVector());
	}
	
	public static void spawnParticle(@Nullable Player player, @NotNull Particle particle, @NotNull Vector location) {
		PacketContainer packet = getProtocolManager().createPacket(WORLD_PARTICLES);
		
		packet.getStrings().write(0, particle.id);
		packet.getFloat().write(0, (float) location.getX());
		packet.getFloat().write(1, (float) location.getY());
		packet.getFloat().write(2, (float) location.getZ());
		packet.getFloat().write(3, particle.x);
		packet.getFloat().write(4, particle.y);
		packet.getFloat().write(5, particle.z);
		packet.getFloat().write(3, 6f);
		packet.getFloat().write(4, 236f);
		packet.getFloat().write(5, 233f);
		packet.getFloat().write(6, particle.data);
		packet.getIntegers().write(0, particle.count);
		if (player != null) {
			sendSilently(player, packet);
		} else {
			broadcastSilently(packet);
		}
	}
	
	public static Component intercept(PacketType type, @NotNull Consumer<PacketEvent> packet) {
		PacketListener listener = new PacketAdapter(getPlugin(), type) {
			@Override
			public void onPacketReceiving(PacketEvent event) {
				packet.accept(event);
			}
			
			@Override
			public void onPacketSending(PacketEvent event) {
				packet.accept(event);
			}
		};
		return create(
				() -> getProtocolManager().addPacketListener(listener),
				() -> getProtocolManager().removePacketListener(listener)
		);
	}
	
	static class MultipleNameTags {
		private static final Field VEHICLE;
		private static final Field PASSENGERS;
		
		static {
			try {
				VEHICLE = PacketPlayOutMount.class.getDeclaredField("a");
				VEHICLE.setAccessible(true);
				PASSENGERS = PacketPlayOutMount.class.getDeclaredField("b");
				PASSENGERS.setAccessible(true);
			} catch (NoSuchFieldException e) {
				throw new RuntimeException(e);
			}
		}
		
		public static Packet getMountPacket(int vehicle, int... passengers) {
			try {
				PacketPlayOutMount packet = new PacketPlayOutMount();
				VEHICLE.set(packet, vehicle);
				PASSENGERS.set(packet, passengers);
				return packet;
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		
		public static List<Packet> setNameTag(Entity entity, String... lines) {
			net.minecraft.server.v1_12_R1.World world = ((CraftWorld) entity.getWorld()).getHandle();
			int[] passengers = new int[lines.length];
			List<Packet> packets = new LinkedList<>();
			
			for (int i = 0; i < lines.length; i++) {
				int size = (i - 1) * -1;
				EntitySlime holder = new EntitySlime(world);
				holder.setInvulnerable(true);
				Location location = entity.getLocation();
				holder.setPosition(location.getX(), location.getY(), location.getZ());
				holder.setInvisible(true);
				holder.setSize(size, false);
				
				EntityArmorStand line = new EntityArmorStand(world);
				line.setInvulnerable(true);
				line.setPosition(location.getX(), location.getY(), location.getZ());
				
				line.setInvisible(true);
				line.setSmall(true);
				line.setCustomName(lines[i]);
				line.setCustomNameVisible(true);
				
				passengers[i] = holder.getId();
				
				packets.add(new PacketPlayOutSpawnEntityLiving(holder));
				packets.add(new PacketPlayOutSpawnEntityLiving(line));
				packets.add(getMountPacket(holder.getId(), line.getId()));
			}
			
			packets.add(getMountPacket(entity.getEntityId(), passengers));
			
			return packets;
		}
	}
	
	public static void sendTooltip(@NotNull Player player, @NotNull Object message) {
		ItemStack stack = player.getItemInHand().clone();
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(message.toString());
		stack.setItemMeta(meta);
		PacketContainer packet = getProtocolManager().createPacket(SET_SLOT);
		packet.getBytes().write(0, (byte) 0);
		packet.getShorts().write(0, (short) player.getInventory().getHeldItemSlot());
		packet.getItemModifier().write(0, stack);
		sendSilently(player, packet);
		packet.getItemModifier().write(0, player.getItemInHand());
		sendSilently(player, packet);
	}
	
	public static void sendActionBar(Player player, @NotNull Object message) {
//		PacketContainer packet = getProtocolManager().createPacket(CHAT);
//		packet.getChatComponents().write(0, fromText(message.toString()));
//		packet.getChatTypes().write(0, GAME_INFO);
//		sendSilently(player, packet);
	}
	
	
	public static void openInventory(@NotNull Player player, @NotNull String name, int size) {
		PacketContainer packet = getProtocolManager().createPacket(OPEN_WINDOW);
		getOpenInventoryId(player).ifPresent(id ->
				packet.getIntegers().write(0, id));
		packet.getStrings().write(0, "minecraft:chest");
		packet.getChatComponents().write(0, WrappedChatComponent.fromText(name));
		packet.getIntegers().write(1, size);
		sendSilently(player, packet);
	}
	
	public static void titleTimes(Player player, @NotNull @Clarifiers.Millis @Clarifiers.Long Number in, @NotNull @Clarifiers.Millis @Clarifiers.Long Number stay, @NotNull @Clarifiers.Millis @Clarifiers.Long Number out) {
//		PacketContainer packet = getProtocolManager().createPacket(Server.TITLE);
//		packet.getTitleActions().write(0, TIMES);
//		packet.getIntegers().write(0, in.intValue() * 20_000);
//		packet.getIntegers().write(1, stay.intValue() * 20_000);
//		packet.getIntegers().write(2, out.intValue() * 20_000);
//		sendSilently(player, packet);
	}
	
	public static void titleText(Player player, @NotNull @Clarifiers.Text Object title, Object action) {
//		PacketContainer packet = getProtocolManager().createPacket(Server.TITLE);
//		packet.getTitleActions().write(0, action);
//		packet.getChatComponents().write(0, fromText(title.toString()));
//		sendSilently(player, packet);
	}
	
	public static void titleReset(Player player, boolean hide) {
//		PacketContainer packet = getProtocolManager().createPacket(Server.TITLE);
//		packet.getTitleActions().write(0, hide ? CLEAR : RESET);
//		sendSilently(player, packet);
	}
	
	public static void receiveSilently(Player player, PacketContainer packet, boolean filters) {
		try {
			getProtocolManager().recieveClientPacket(player, packet, filters);
		} catch (Exception ignored) {
		}
	}
	
	public static void broadcastSilently(PacketContainer packet) {
		getProtocolManager().broadcastServerPacket(packet);
	}
	
	public static void sendSilently(Player player, PacketContainer packet) {
		try {
			getProtocolManager().sendServerPacket(player, packet);
		} catch (InvocationTargetException ignored) {
		}
	}
}