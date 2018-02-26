package com.penzzly.engine.core.base;

import com.penzzly.engine.architecture.base.Component;
import com.penzzly.engine.architecture.utilites.Components;
import com.penzzly.engine.core.components.ObservableComponent;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.penzzly.engine.core.utilites.bukkit.ServerUtil.getPlugin;
import static net.jodah.typetools.TypeResolver.resolveRawArgument;
import static org.bukkit.Bukkit.getPluginManager;
import static org.bukkit.event.EventPriority.NORMAL;

@SuppressWarnings("unchecked")
public interface Events {
	Map<Class<? extends Event>, ConsumerListener> listeners = new WeakHashMap<>();
	
	static <T extends Event> ObservableComponent<T> listen(Class<T> type) {
		return listen(item -> true, type);
	}
	
	static <T extends Event> ObservableComponent<T> listen(Predicate<T> filter, Class<T> type) {
		return listen(filter, type, NORMAL);
	}
	
	static <T extends Event> ObservableComponent<T> listen(Class<T> type, EventPriority priority) {
		return listen(item -> true, type, priority);
	}
	
	static <T extends Event> ObservableComponent<T> listen(Predicate<T> filter, Class<T> type, EventPriority priority) {
		Subject<T> events = PublishSubject.create();
		Consumer<T> listener = events::onNext;
		return new ObservableComponent<T>(events::subscribe)
				.onEnable(() -> registerListener(type, priority, filter, listener))
				.onDisable(() -> unregisterListener(type, listener));
	}
	
	
	@NotNull
	static <T extends Event> Component listen(@NotNull Class<T> type, Consumer<T> listener) {
		return listen(type, event -> true, listener);
	}
	
	@NotNull
	static <T extends Event> Component listen(@NotNull Consumer<T> listener) {
		return listen(event -> true, listener);
	}
	
	@NotNull
	static <T extends Event> Component listen(EventPriority priority, @NotNull Consumer<T> listener) {
		return listen(priority, event -> true, listener);
	}
	
	@NotNull
	static <T extends Event> Component listen(Predicate<T> filter, @NotNull Consumer<T> listener) {
		return listen(NORMAL, filter, listener);
	}
	
	@NotNull
	static <T extends Event> Component listen(EventPriority priority, Predicate<T> filter, @NotNull Consumer<T> listener) {
		Class<T> type = (Class<T>) resolveRawArgument(Consumer.class, listener.getClass());
		if (!Event.class.isAssignableFrom(type)) {
			throw new IllegalArgumentException("Could not resolve event type ofZip lambda. Try using: listen(Class<T> type, event -> {}) instead.");
		}
		return listen(type, filter, priority, listener);
	}
	
	@NotNull
	static <T extends Event> Component listen(@NotNull Class<T> type, Predicate<T> filter, Consumer<T> listener) {
		return listen(type, filter, NORMAL, listener);
	}
	
	@NotNull
	static <T extends Event> Component listen(@NotNull Class<T> type, Predicate<T> filter, EventPriority priority, Consumer<T> listener) {
		if (!Event.class.isAssignableFrom(type)) {
			throw new IllegalArgumentException("Sorry, couldn't resolve Event through method that returns functional interfaces.\n" +
					"Please either use listen(Consumer<T> listener, Predicate<T> filter, Class<T> type) or use direct method references or lambdas");
		}
		return Components.create(
				() -> registerListener(type, priority, filter, listener),
				() -> unregisterListener(type, listener));
	}
	
	static <T extends Event> void registerListener(Class<T> type, EventPriority priority, Predicate<T> filter, Consumer<T> listener) {
		listeners.computeIfAbsent(type, t -> {
			ConsumerListener bukkitListener = new ConsumerListener();
			getPluginManager().registerEvent(type, bukkitListener, priority, bukkitListener, getPlugin());
			return bukkitListener;
		}).consumers.add(listener);
	}
	
	static void unregisterListener(Class<? extends Event> type, Consumer<? extends Event> listener) {
		final ConsumerListener bukkitListener = listeners.get(type);
		bukkitListener.consumers.remove(listener);
		if (bukkitListener.consumers.size() < 1) {
			listeners.remove(type);
		}
	}
	
	class ConsumerListener<Type extends Event> implements Listener, EventExecutor {
		final List<Consumer<Type>> consumers = new ArrayList<>();
		
		@Override
		public void execute(Listener listener, Event event) throws EventException {
			for (int i = 0; i < consumers.size(); i++)
				consumers.get(i).accept((Type) event);
		}
	}
}