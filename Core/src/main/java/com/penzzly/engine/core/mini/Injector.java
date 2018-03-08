package com.penzzly.engine.core.mini;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.server.v1_12_R1.Packet;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

import static net.jodah.typetools.TypeResolver.resolveRawArgument;

@SuppressWarnings("unchecked")
public class Injector {
	private static final String HANDLER = "packet_handler";
	private static final String INJECTOR = "custom_packet_handler";
	private final Multimap<Class<?>, BiPredicate<Player, Packet>> packetListeners = ArrayListMultimap.create();
	
	/*Example use.
	public static void main(String[] args) {
		Injector injector = new Injector();
		injector.onPacket((Player player, PacketPlayOutAbilities packet) -> {
			//handle your packet as you see fit...
			//doesn't support ping packets... but that's another process entirely...
			//if you want to support them just add an injectServer method to do so...
		});
	}*/
	
	@NotNull
	public Injection inject(@NotNull Player player) {
		ChannelDuplexHandler handler = new ChannelDuplexHandler() {
			@Override
			public void channelRead(ChannelHandlerContext context, Object packet) throws Exception {
				//FIXME not getting called.
				if (handle(player, (Packet) packet)) {
					super.channelRead(context, packet);
				}
			}
			
			@Override
			public void write(ChannelHandlerContext context, Object packet, ChannelPromise promise) throws Exception {
				if (handle(player, (Packet) packet)) {
					super.write(context, packet, promise);
				}
			}
		};
		
		Channel channel = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel;
		channel.pipeline().addAfter(HANDLER, INJECTOR, handler);
		return () -> channel.pipeline().remove(handler);
	}
	
	private boolean handle(Player player, @NotNull Packet packet) {
		for (BiPredicate<Player, Packet> listener : packetListeners.get(packet.getClass()))
			if (!listener.test(player, packet)) {
				return false;
			}
		return true;
	}
	
	public void send(Player player, Packet packet) {
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}
	
	//--Listeners--
	//Receive
	public <Type extends Packet> BiPredicate<Player, Type> onPacket(@NotNull BiPredicate<Player, Type> listener) {
		return onPacket((Class<Type>) resolveRawArgument(BiPredicate.class, listener.getClass()),
				listener);
	}
	
	public <Type extends Packet> BiPredicate<Player, Type> onPacket(Class<Type> type, BiPredicate<Player, Type> listener) {
		packetListeners.put(type, (BiPredicate<Player, Packet>) listener);
		return listener;
	}
	
	@NotNull
	public <Type extends Packet> BiConsumer<Player, Type> onPacket(@NotNull BiConsumer<Player, Type> listener) {
		return onPacket((Class<Type>) resolveRawArgument(BiConsumer.class, listener.getClass()),
				listener);
	}
	
	@NotNull
	public <Type extends Packet> BiConsumer<Player, Type> onPacket(Class<Type> type, @NotNull BiConsumer<Player, Type> listener) {
		packetListeners.put(type, (player, packet) -> {
			listener.accept(player, (Type) packet);
			return true;
		});
		return listener;
	}
	
	//--Getters--
	//Receive
	@NotNull
	public <Type extends Packet> List<BiPredicate<Player, Packet>> getPacketListeners(Class<Type> type) {
		return (List<BiPredicate<Player, Packet>>) packetListeners.get(type);
	}
	
	@NotNull
	public Multimap<Class<?>, BiPredicate<Player, Packet>> getPacketListeners() {
		return packetListeners;
	}
	
	public interface Injection extends Closeable {
		@Override
		void close();
	}
}
