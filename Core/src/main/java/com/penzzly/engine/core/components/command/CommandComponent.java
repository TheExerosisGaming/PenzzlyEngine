package com.penzzly.engine.core.components.command;

import com.penzzly.engine.architecture.base.Component;
import com.penzzly.engine.architecture.base.Toggleable;
import com.penzzly.engine.architecture.functions.Optional;
import com.penzzly.engine.architecture.utilites.Components;
import com.penzzly.engine.core.utilites.EnumUtil;
import com.penzzly.engine.core.utilites.time.Duration;
import net.jodah.typetools.TypeResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.*;

import static com.comphenix.protocol.utility.BukkitUtil.getOnlinePlayers;
import static com.penzzly.engine.architecture.functions.Optional.of;
import static com.penzzly.engine.core.base.Events.listen;
import static com.penzzly.engine.core.utilites.StringUtil.closest;
import static com.penzzly.engine.core.utilites.bukkit.ServerUtil.getOfflinePlayer;
import static com.penzzly.engine.core.utilites.bukkit.ServerUtil.getPlugin;
import static org.bukkit.Bukkit.getScheduler;

//TODO addChild arg listeners
@SuppressWarnings("unchecked")
public class CommandComponent extends Component {
	
	private final Map<Predicate<String>, BiConsumer<CommandSender, Arguments>> commandListeners = new HashMap<>();
	
	private final Map<Class<?>, TypeAdapter<?, ?>> adapters = new HashMap<>();
	
	public CommandComponent() {
		addChild(listen((PlayerCommandPreprocessEvent event) -> {
			String message = event.getMessage();
			
			if (message.startsWith("/")) {
				commandListeners.forEach((key, value) -> {
					String[] split = message.substring(1).split(" ", 2);
					
					if (key.test(split[0])) {
						String[] args = split.length == 2 ? split[1].split(" ") : new String[0];
						
						getScheduler().runTask(getPlugin(), () -> {
							value.accept(event.getPlayer(), new Arguments(args));
							event.setCancelled(true);
						});
					}
				});
			}
		}));
		
		addChild(listen((ServerCommandEvent event) -> {
			commandListeners.forEach((key, value) -> {
				String[] split = event.getCommand().split(" ", 2);
				
				if (key.test(split[0])) {
					String[] args = split.length == 2 ? split[1].split(" ") : new String[0];
					
					getScheduler().runTask(getPlugin(), () -> {
						value.accept(event.getSender(), new Arguments(args));
					});
				}
			});
		}));
		
		//--Primitives--
		addTypeAdapter((String arg) -> Integer.parseInt(arg));
		addTypeAdapter((String arg) -> Double.parseDouble(arg));
		addTypeAdapter((String arg) -> Float.parseFloat(arg));
		addTypeAdapter((String arg) -> Byte.parseByte(arg));
		addTypeAdapter((String arg) -> Short.parseShort(arg));
		addTypeAdapter((String arg) -> Long.parseLong(arg));
		
		//--Players--
		addTypeAdapter((String arg) -> closest(arg, getOnlinePlayers(), Player::getName));
		addTypeAdapter((String arg) -> getOfflinePlayer(arg));
		
		//--Times--
		addTypeAdapter((String arg) -> EnumUtil.closest(arg, TimeUnit.class));
		addTypeAdapter(Duration::new);
	}
	
	@NotNull
	public Arguments make(String[] args) {
		return new Arguments(args);
	}
	
	@NotNull
	public <From, To> CommandComponent addTypeAdapter(@NotNull Function<From, To> adapter) {
		Class<?>[] arguments = TypeResolver.resolveRawArguments(Function.class, adapter.getClass());
		return addTypeAdapter((Class<From>) arguments[0], (Class<To>) arguments[1], adapter);
	}
	
	@NotNull
	public <From, To> CommandComponent addTypeAdapter(Class<From> from, Class<To> to, @NotNull Function<From, To> adapter) {
		adapters.put(to, new TypeAdapter<To, From>(from) {
			@Override
			public To apply(From from) {
				return adapter.apply(from);
			}
		});
		return this;
	}
	
	@NotNull
	public <First, Second, To> CommandComponent addTypeAdapter(@NotNull BiFunction<First, Second, To> adapter) {
		Class<?>[] arguments = TypeResolver.resolveRawArguments(BiFunction.class, adapter.getClass());
		return addTypeAdapter((Class<First>) arguments[0], (Class<Second>) arguments[1], (Class<To>) arguments[2], adapter);
	}
	
	@NotNull
	public <First, Second, To> CommandComponent addTypeAdapter(@NotNull Class<First> first, @NotNull Class<Second> second, Class<To> to, @NotNull BiFunction<First, Second, To> adapter) {
		TypeAdapter<First, String> firstAdapter = (TypeAdapter<First, String>) getAdapter(first);
		TypeAdapter<Second, String> secondAdapter = (TypeAdapter<Second, String>) getAdapter(second);
		
		return addTypeAdapter(DoubleString.class, to, args ->
				adapter.apply(firstAdapter.apply(args.first), secondAdapter.apply(args.second)));
	}
	
	@NotNull
	private <To> TypeAdapter<To, ?> getAdapter(@NotNull Class<To> to) {
		TypeAdapter<?, ?> target = adapters.get(to);
		if (target == null) {
			target = adapters.entrySet()
					.stream()
					.filter(entry -> to.isAssignableFrom(entry.getKey()))
					.map(Map.Entry::getValue)
					.findFirst()
					.orElseThrow(() ->
							new IllegalStateException("Could not find an getAdapter to: " + to.getSimpleName()));
		}
		if (target.getFrom() == String.class || target.getFrom() == DoubleString.class) {
			return (TypeAdapter<To, ?>) target;
		}
		return (TypeAdapter<To, ?>) target.compose((TypeAdapter<?, Object>) getAdapter(target.getFrom()));
	}
	
	public Toggleable onPlayerCommand(Predicate<String> filter, BiConsumer<Player, Arguments> listener) {
		return onCommand(filter, (sender, args) -> {
			if (sender instanceof Player)
				listener.accept((Player) sender, args);
		});
	}
	
	public Toggleable onCommand(Predicate<String> filter, BiConsumer<CommandSender, Arguments> listener) {
		return Components.create(
				() -> commandListeners.put(filter, listener),
				() -> commandListeners.remove(filter, listener)
		);
	}
	
	@NotNull
	public Map<Predicate<String>, BiConsumer<CommandSender, Arguments>> getCommandListeners() {
		return commandListeners;
	}
	
	@NotNull
	public Map<Class<?>, TypeAdapter<?, ?>> getAdapters() {
		return adapters;
	}
	
	public abstract class TypeAdapter<To, From> implements Function<From, To> {
		private Class<From> from;
		
		public TypeAdapter(Class<From> from) {
			this.from = from;
		}
		
		private TypeAdapter() {
		}
		
		public Class<From> getFrom() {
			return from;
		}
		
		@NotNull
		public TypeAdapter<To, Object> compose(@NotNull TypeAdapter<?, Object> adapter) {
			return new TypeAdapter<To, Object>() {
				@Override
				public To apply(Object type) {
					return TypeAdapter.this.apply((From) adapter.apply(type));
				}
			};
		}
	}
	
	public class Arguments {
		private final String[] args;
		private int index = 0;
		
		public Arguments(String[] args) {
			this.args = args;
		}
		
		@Override
		public String toString() {
			return String.join(" ", args);
		}
		
		public String nextUnsafe() {
			return next().get();
		}
		
		@NotNull
		public Optional<String> next() {
			if (index < args.length - 1) {
				return Optional.of(args[index++]);
			} else {
				return Optional.empty();
			}
		}
		
		@NotNull
		public Arguments next(@NotNull BiConsumer<String, String> arg) {
			if (index < args.length - 2) {
				arg.accept(args[index++], args[index++]);
			}
			return this;
		}
		
		@NotNull
		public Arguments next(@NotNull Consumer<String> arg) {
			if (index < args.length - 1) {
				arg.accept(args[index++]);
			}
			return this;
		}
		
		public <Type> Type asOr(@NotNull Type alternative) {
			return or(alternative);
		}
		
		public <Type> Type asOr(@NotNull Class<Type> type, @NotNull Type alternative) {
			return as(type).or(alternative);
		}
		
		public <Type> Type or(@NotNull Type alternative) {
			return as((Class<Type>) alternative.getClass()).or(alternative);
		}
		
		public <Type> Type asUnsafe(@NotNull Class<Type> type) {
			return as(type).get();
		}
		
		@NotNull
		public <Type> Optional<Type> as(@NotNull Class<Type> type) {
			CommandComponent.TypeAdapter<Type, ?> adapter = getAdapter(type);
			Type result = null;
			if (adapter.getFrom() == String.class) {
				if (index < args.length) {
					result = ((CommandComponent.TypeAdapter<Type, String>) adapter).apply(args[index++]);
				}
			} else {
				if (index < args.length - 1) {
					result = ((CommandComponent.TypeAdapter<Type, CommandComponent.DoubleString>) adapter).apply(new CommandComponent.DoubleString(args[index++], args[index++]));
				}
			}
			return of(result);
		}
		
		@NotNull
		public <Type> Arguments as(@NotNull Consumer<Type> arg) {
			Class<Type> type = (Class<Type>) TypeResolver.resolveRawArgument(Consumer.class, arg.getClass());
			return as(type, arg);
		}
		
		@NotNull
		public <Type> Arguments as(@NotNull Class<Type> type, @NotNull Consumer<Type> arg) {
			as(type).ifPresent(arg);
			return this;
		}
	}
	
	private class DoubleString {
		private final String first;
		private final String second;
		
		public DoubleString(String first, String second) {
			this.first = first;
			this.second = second;
		}
	}
}
