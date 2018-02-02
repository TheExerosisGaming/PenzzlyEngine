package com.penzzly.packages.factions.components.temp;

import com.penzzly.engine.core.utilites.time.Duration;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Consumer;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static io.reactivex.Observable.just;
import static io.reactivex.internal.functions.Functions.emptyConsumer;
import static java.lang.System.currentTimeMillis;

public class Debouncer<T> implements ObservableTransformer<T, T> {
	private final Map<Object, Long> cooling = new HashMap<>();
	private Function<T, ?> equal;
	private final Consumer<T> debounced;
	private final Observable<Long> duration;
	
	public Debouncer(@NotNull Duration duration) {
		this(just(duration));
	}
	
	public Debouncer(Consumer<T> debounced, @NotNull Duration duration) {
		this(t -> t, debounced, duration);
	}
	
	public Debouncer(Function<T, ?> equal, Consumer<T> debounced, @NotNull Duration duration) {
		this(equal, debounced, just(duration));
	}
	
	public Debouncer(int time, @NotNull TimeUnit unit) {
		this(just(time), just(unit));
	}
	
	public Debouncer(Consumer<T> debounced, int time, @NotNull TimeUnit unit) {
		this(debounced, just(time), just(unit));
	}
	
	public Debouncer(@NotNull Observable<Number> times, @NotNull Observable<TimeUnit> units) {
		this(emptyConsumer(), times, units);
	}
	
	public Debouncer(@NotNull Observable<Duration> durations) {
		this(emptyConsumer(), durations);
	}
	
	public Debouncer(Consumer<T> debounced, @NotNull Observable<Duration> durations) {
		this(t -> t, debounced, durations);
	}
	
	public Debouncer(Function<T, ?> equal, Consumer<T> debounced, @NotNull Observable<Duration> durations) {
		this(equal, debounced, durations.map(Duration::time), durations.map(Duration::unit));
	}
	
	public Debouncer(Consumer<T> debounced, @NotNull Observable<Number> times, @NotNull Observable<TimeUnit> units) {
		this(t -> t, debounced, times, units);
	}
	
	public Debouncer(Function<T, ?> equal, Consumer<T> debounced, @NotNull Observable<Number> times, @NotNull Observable<TimeUnit> units) {
		this.equal = equal;
		this.debounced = debounced;
		duration = times
				.zipWith(units, (time, unit) -> unit.toMillis(time.longValue()))
				.replay(1)
				.autoConnect();
	}
	
	@Override
	public ObservableSource<T> apply(@NotNull Observable<T> observable) {
		return observable.filter(item -> {
			Object key = equal.apply(item);
			if (!cooling.containsKey(key)) {
				cooling.put(key, currentTimeMillis() + duration.blockingFirst());
			} else if (cooling.get(key) < currentTimeMillis()) {
				cooling.remove(key);
			} else {
				debounced.accept(item);
				return false;
			}
			return true;
		});
	}
}
