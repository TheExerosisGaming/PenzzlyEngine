package com.penzzly.engine.core.utilites.time;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class Duration implements Comparable<Duration> {
	private final Number time;
	private final TimeUnit unit;
	
	public static Duration For(Number time, TimeUnit unit) {
		return new Duration(time, unit);
	}
	
	public static Supplier<Number> toMillis(@NotNull Number number, @NotNull TimeUnit unit) {
		return () -> unit.toMillis(number.longValue());
	}
	
	public static Supplier<Number> toMillis(@NotNull Supplier<Number> number, @NotNull TimeUnit unit) {
		return () -> unit.toMillis(number.get().longValue());
	}
	
	public static Supplier<Number> toMillis(@NotNull Number number, @NotNull Supplier<TimeUnit> unit) {
		return () -> unit.get().toMillis(number.longValue());
	}
	
	public static Supplier<Number> toMillis(@NotNull Supplier<Number> number, @NotNull Supplier<TimeUnit> unit) {
		return () -> unit.get().toMillis(number.get().longValue());
	}
	
	
	public Duration(Number time, TimeUnit unit) {
		this.time = time;
		this.unit = unit;
	}
	
	public boolean longerThan(@Nullable Duration duration) {
		return duration != null && duration.compareTo(this) > 0;
	}
	
	public boolean shorterThan(@Nullable Duration duration) {
		return duration != null && duration.compareTo(this) < 0;
	}
	
	public long toMillis() {
		return unit.toMillis(time.longValue());
	}
	
	@Override
	public boolean equals(@Nullable Object obj) {
		return obj != null && obj instanceof Duration && ((Duration) obj).compareTo(this) == 0;
	}
	
	public long time() {
		return time.longValue();
	}
	
	public TimeUnit unit() {
		return unit;
	}
	
	@Override
	public int compareTo(Duration duration) {
		return Long.compare(unit.toNanos(time()), duration.unit.toNanos(duration.time()));
	}
	
	@NotNull
	@Override
	public String toString() {
		String units = unit.toString().toLowerCase();
		return time + " " + units.substring(0, 1).toUpperCase() + units.substring(1);
	}
}