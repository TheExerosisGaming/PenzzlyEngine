package com.penzzly.engine.core.utilites.functions;

import com.penzzly.engine.architecture.functions.compat.Consumer;
import com.penzzly.engine.architecture.functions.compat.Function;
import com.penzzly.engine.architecture.functions.compat.Predicate;
import com.penzzly.engine.architecture.functions.compat.Supplier;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiPredicate;


@SuppressWarnings("unchecked")
public interface Functions {
	Function<EntityEvent, Entity> ENTITY = EntityEvent::getEntity;
	Function<PlayerEvent, Player> PLAYER = PlayerEvent::getPlayer;
	Supplier<Boolean> FALSE = () -> false;
	Supplier<Boolean> TRUE = () -> true;
	
	@NotNull
	static <Type extends PlayerEvent> Function<Type, Player> player() {
		return (Function<Type, Player>) PLAYER;
	}
	
	static Predicate<Entity> entityType(EntityType type) {
		return entity -> entity.getType() == type;
	}
	
	static Predicate<EntityEvent> entityEventType(EntityType type) {
		return event -> event.getEntityType() == type;
	}
	
	static Predicate<Cancellable> isCancelled(boolean whenTrue) {
		return cancellable -> cancellable.isCancelled() == whenTrue;
	}
	
	static <To> Function<Object, To> cast() {
		return from -> (To) from;
	}
	
	static <Type extends Cancellable> Consumer<Type> setCancelled(boolean canceled) {
		return cancellable -> cancellable.setCancelled(canceled);
	}
	
	static <Type> Supplier<Type> roundRobin(@NotNull Type... instances) {
		return new Supplier<Type>() {
			private int index = 0;
			
			@Override
			public Type get() {
				if (index++ > instances.length) {
					index = 0;
				}
				return instances[index];
			}
		};
	}
	
	static <T> ObservableTransformer<T, T> scanAndTakeWhile(@NotNull BiPredicate<T, T> filter) {
		return new ObservableTransformer<T, T>() {
			private T lastElement;
			
			@Override
			public ObservableSource<T> apply(@NotNull Observable<T> upstream) {
				return upstream.takeWhile(value -> {
					if (lastElement == null) {
						lastElement = value;
						return true;
					}
					return filter.test(lastElement, value);
				});
			}
		};
	}
}
