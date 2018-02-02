package com.penzzly.engine.core.components.messaging;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.penzzly.engine.architecture.base.Component;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;

import java.util.function.Consumer;
import java.util.function.Predicate;

@SuppressWarnings("unchecked")
public class MessagingComponent extends Component {
	private final Multimap<Class<?>, Consumer<?>> listeners = ArrayListMultimap.create();
	private final RTopic<Object> topic;
	@NotNull
	private final Predicate<Object> packets;
	private int listener;
	
	public MessagingComponent(@NotNull RedissonClient redisson, String channel) {
		this(redisson, packet -> true, channel);
	}
	
	public MessagingComponent(@NotNull RedissonClient redisson, @NotNull Predicate<Object> packets, String channel) {
		this.packets = packets;
		topic = redisson.getTopic(channel);
		
		onEnable(() -> listener = topic.addListener((c, packet) -> {
			if (packets.test(packet)) {
				listeners.get(packet.getClass()).forEach(listener -> ((Consumer<Object>) listener).accept(packet));
			}
		}));
		
		onDisable(() -> {
			topic.removeListener(listener);
		});
	}
	
	public void _sendTest(@NotNull Object packet) {
		if (packets.test(packet)) {
			listeners.get(packet.getClass()).forEach(listener -> ((Consumer<Object>) listener).accept(packet));
		}
	}
	
	public <T> void addListener(Consumer<T> listener, Class<T> type) {
		listeners.put(type, listener);
	}
	
	public void removeListener(Consumer<?> listener) {
		listeners.asMap().forEach((type, list) -> {
			if (list.contains(listener)) {
				list.remove(listener);
			}
		});
	}
	
	public void clear() {
		listeners.clear();
	}
	
	public void send(Object packet) {
		topic.publish(packet);
	}
	
}