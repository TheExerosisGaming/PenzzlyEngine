package com.penzzly.engine.core.components.messaging;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.io.ByteArrayDataOutput;
import com.penzzly.engine.architecture.base.Component;
import io.reactivex.Observable;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.DataInput;
import java.io.DataOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.google.common.io.ByteStreams.newDataInput;
import static com.google.common.io.ByteStreams.newDataOutput;
import static com.penzzly.engine.core.utilites.bukkit.ServerUtil.*;
import static io.reactivex.Observable.fromIterable;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.bukkit.Bukkit.getMessenger;
import static org.bukkit.Bukkit.getOnlinePlayers;

public class PluginMessagingComponent extends Component implements PluginMessageListener {
	private final List<BiConsumer<Player, DataInput>> messageListeners = new ArrayList<>();
	private final String channel;
	
	public PluginMessagingComponent(String channel) {
		this.channel = channel;
		onEnable(() -> {
			registerChannel(channel, this);
			registerChannel(channel);
		});
		onDisable(() -> {
			unregisterChannel(channel, this);
			unregisterChannel(channel);
		});
	}
	
	@Override
	public void onPluginMessageReceived(@NotNull String channel, Player player, @NotNull byte[] message) {
		if (channel.equals(this.channel)) {
			messageListeners.forEach(listener -> listener.accept(player, newDataInput(message)));
		}
	}
	
	
	public Observable<List<String>> getAllPlayers() {
		return fromIterable(getOnlinePlayers())
				.take(1)
				.flatMap(someone -> messageForResponse(someone, "Bungee", "PlayerList", "All"))
				.scanWith(ArrayList::new, (list, data) -> {
					list.add(data.readLine());
					return list;
				});
	}
	
	public Observable<DataInput> messageForResponse(Player player, String channel, String... message) {
		ByteArrayDataOutput dataOutput = newDataOutput();
		for (String line : message)
			dataOutput.writeUTF(line);
		return messageForResponse(player, channel, dataOutput.toByteArray());
	}
	
	public Observable<DataInput> messageForResponse(Player player, String channel, byte[] message) {
		return Observable.create(observer -> {
			onMessage(new BiConsumer<Player, DataInput>() {
				@Override
				public void accept(Player target, DataInput data) {
					if (target.equals(player)) {
						observer.onNext(data);
						observer.onComplete();
						getMessageListeners().remove(this);
					}
				}
			});
			sendPluginMessage(player, channel, message);
		});
	}
	
	static class BungeeChannel implements Closeable, PluginMessageListener {
		private static final String BUNGEE = "Bungee";
		private final Plugin plugin;
		private final List<BungeeCommand<?, ?>> commands = new ArrayList<>();
		
		public BungeeChannel(Plugin plugin) {
			this.plugin = plugin;
		}
		
		public <Params, Result> PlayerCommand<Params, Result> addPlayerCommand(Function<DataInput, Result> to, Function<Params[], byte[]> from) {
			Player target = getOnlinePlayers().stream().findAny().orElseThrow(() -> new IllegalStateException("No olnine players!"));
			BasePlayerCommand<Params, Result> command = new BasePlayerCommand<Params, Result>() {
				
				@Override
				void incoming(Player player, byte[] data) {
					if (player.equals(target)) {
						getListeners(target).forEach(listener -> listener.accept(to.apply(newDataInput(data))));
					}
				}
				
				@Override
				void launch(Player player, Params... params) {
					target.sendPluginMessage(plugin, BUNGEE, from.apply(params));
				}
			};
			commands.add(command);
			return command;
		}
		
		public <Params, Result> Command<Params, Result> addCommand(Function<DataInput, Result> to, Function<Params[], byte[]> from) {
			BaseCommand<Params, Result> command = new BaseCommand<Params, Result>() {
				@Override
				void incoming(Player player, byte[] data) {
					getListeners(player).forEach(listener -> listener.accept(to.apply(newDataInput(data))));
				}
				
				@SafeVarargs
				@Override
				final void launch(Player player, Params... params) {
					player.sendPluginMessage(plugin, BUNGEE, from.apply(params));
				}
			};
			commands.add(command);
			return command;
		}
		
		public void register() {
			getMessenger().registerOutgoingPluginChannel(plugin, BUNGEE);
			getMessenger().registerIncomingPluginChannel(plugin, BUNGEE, this);
		}
		
		public void unregister() {
			getMessenger().unregisterOutgoingPluginChannel(plugin, BUNGEE);
			getMessenger().unregisterIncomingPluginChannel(plugin, BUNGEE, this);
		}
		
		@Override
		public void close() {
			unregister();
		}
		
		@Override
		public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
			commands.forEach(command -> {
				if (channel.equals(BUNGEE)) {
					command.incoming(player, bytes);
				}
			});
		}
		
		private abstract class BaseCommand<Params, Result> extends BungeeCommand<Params, Result> implements Command<Params, Result> {
			private final List<Consumer<Result>> listeners = new ArrayList<>();
			
			@Override
			List<Consumer<Result>> getListeners(Player player) {
				return listeners;
			}
		}
		
		private abstract class BasePlayerCommand<Params, Result> extends BungeeCommand<Params, Result> {
			private final ListMultimap<Player, Consumer<Result>> listeners = ArrayListMultimap.create();
			
			@Override
			List<Consumer<Result>> getListeners(Player player) {
				return listeners.get(player);
			}
		}
		
		private abstract class BungeeCommand<Params, Result> implements PlayerCommand<Params, Result> {
			abstract List<Consumer<Result>> getListeners(Player player);
			
			abstract void incoming(Player player, byte[] data);
			
			abstract void launch(Player player, Params... params);
			
			@Override
			public void execute(Player player, Consumer<Result> callback, Params... params) {
				getListeners(player).add(callback);
				if (getListeners(player).size() <= 1) {
					launch(player, params);
				}
			}
			
			private Result currentResult = null;
			
			@Override
			public Optional<Result> execute(Player player, Params... params) {
				CountDownLatch latch = new CountDownLatch(1);
				execute(player, result -> {
					this.currentResult = result;
					latch.countDown();
				}, params);
				try {
					latch.await(3, SECONDS);
					return ofNullable(currentResult);
				} catch (InterruptedException ignored) {
					return empty();
				} finally {
					currentResult = null;
				}
			}
		}
		
		public interface Command<Params, Result> extends PlayerCommand<Params, Result> {
			default void execute(Consumer<Result> callback, Params... params) {
				execute(null, callback, params);
			}
			
			default Optional<Result> execute(Params... params) {
				return execute((Player) null, params);
			}
		}
		
		public interface PlayerCommand<Params, Result> {
			void execute(Player player, Consumer<Result> callback, Params... params);
			
			Optional<Result> execute(Player player, Params... params);
		}
	}
	
	
	public BiConsumer<Player, DataInput> onMessage(BiConsumer<Player, DataInput> listener) {
		messageListeners.add(listener);
		return listener;
	}
	
	public void sendMessage(@NotNull Player player, String... messages) {
		sendPluginMessage(player, channel, messages);
	}
	
	public void sendMessage(@NotNull Player player, Consumer<DataOutput> output) {
		ByteArrayDataOutput data = newDataOutput();
		output.accept(data);
		
		sendPluginMessage(player, channel, data.toByteArray());
	}
	
	public void broadcastMessage(Consumer<DataOutput> output) {
		ByteArrayDataOutput data = newDataOutput();
		output.accept(data);
		
		broadcastPluginMessage(channel, data.toByteArray());
	}
	
	public void broadcastMessage(String... message) {
		broadcastPluginMessage(channel, message);
	}
	
	@NotNull
	public List<BiConsumer<Player, DataInput>> getMessageListeners() {
		return messageListeners;
	}
}
