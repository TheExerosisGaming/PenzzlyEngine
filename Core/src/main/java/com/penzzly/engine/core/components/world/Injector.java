package com.penzzly.engine.core.components.world;

import net.minecraft.server.v1_7_R4.NetworkManager;
import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayOutAbilities;
import net.minecraft.util.com.google.common.collect.ArrayListMultimap;
import net.minecraft.util.com.google.common.collect.Multimap;
import net.minecraft.util.io.netty.channel.Channel;
import net.minecraft.util.io.netty.channel.ChannelDuplexHandler;
import net.minecraft.util.io.netty.channel.ChannelHandlerContext;
import net.minecraft.util.io.netty.channel.ChannelPromise;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.penzzly.engine.core.base.Events.listen;
import static net.jodah.typetools.TypeResolver.resolveRawArgument;
import static org.bukkit.Bukkit.getPluginManager;

@SuppressWarnings("unchecked")
public class Injector {
	private static final String HANDLER = "packet_handler";
	private static final String INJECTOR = "packet_handler";
	private static final Field CHANNEL;
	private final Multimap<Class<?>, BiPredicate<Player, Packet>> packetListeners = ArrayListMultimap.create();
	
	static {
		try {
			CHANNEL = NetworkManager.class.getDeclaredField("m");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@NotNull
	public Injection inject(@NotNull Player player) {
		ChannelDuplexHandler handler = new ChannelDuplexHandler() {
			@Override
			public void channelRead(ChannelHandlerContext context, Object packet) throws Exception {
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
		
		final Channel channel;
		try {
			channel = (Channel) CHANNEL.get(((CraftPlayer) player).getHandle().playerConnection.networkManager);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		channel.pipeline().addAfter(HANDLER, INJECTOR, handler);
		return () -> channel.pipeline().remove(handler);
	}
	
	//Example use.
	public static void main(String[] args) {
		Injector injector = new Injector();
		injector.onPacket((Player player, PacketPlayOutAbilities packet) -> {
			//handle your packet as you see fit...
			//doesn't support ping packets... but that's another process entirely...
			//if you want to support them just add an injectServer method to do so...
		});
		
		listen((PlayerJoinEvent joinEvent) -> {
			Injection injection = injector.inject(joinEvent.getPlayer());
			listen((PlayerQuitEvent quitEvent) -> injection.close());
		});
	}
	
	private boolean handle(Player player, @NotNull Packet packet) {
		for (BiPredicate<Player, Packet> listener : packetListeners.get(packet.getClass()))
			if (!listener.test(player, packet)) {
				return false;
			}
		return true;
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
	
	class Spawner implements Listener {
		private final Map<Predicate<Player>, Consumer<Player>> entities = new HashMap<>();
		
		public Spawner(Plugin plugin) {
			getPluginManager().registerEvents(this, plugin);
		}
		
		@EventHandler
		void onJoin(PlayerJoinEvent event) {
			entities.forEach((players, entity) -> {
				if (players.test(event.getPlayer())) {
					entity.accept(event.getPlayer());
				}
			});
		}
		
		public Predicate<Player> spawn(Consumer<Player> entity) {
			return spawn(player -> true, entity);
		}
		
		public Predicate<Player> spawn(Predicate<Player> players, Consumer<Player> entity) {
			entities.put(players, entity);
//			getServer().getOnlinePlayers().stream().filter(players).forEach(entity);
			return players;
		}
		
		public Map<Predicate<Player>, Consumer<Player>> getEntities() {
			return entities;
		}
	}
}
