package com.penzzly.engine.architecture.functions;


import com.penzzly.engine.architecture.functions.compat.Supplier;
import io.reactivex.Single;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

public interface Optional<Type> extends Supplier<Type> {
	
	static <Type> Optional<Type> from(@NotNull com.google.common.base.Optional<Type> value) {
		return new Optional<Type>() {
			@Override
			public boolean isPresent() {
				return value.isPresent();
			}
			
			@Nullable
			@Override
			public Type get() {
				return isPresent() ? value.get() : null;
			}
		};
	}
	
	static <Type> Optional<Type> from(@NotNull java.util.Optional<Type> value) {
		return new Optional<Type>() {
			@Override
			public boolean isPresent() {
				return value.isPresent();
			}
			
			@Nullable
			@Override
			public Type get() {
				return isPresent() ? value.get() : null;
			}
		};
	}
	
	static <Type> Optional<Type> from(@NotNull Single<Type> value) {
		return of(value.blockingGet());
	}
	
	@Deprecated
	static <Type> Optional<Type> ofNullable(Type value) {
		return of(value);
	}
	
	static <Type> Optional<Type> of(@Nullable Type value) {
		boolean present = value != null;
		return new Optional<Type>() {
			@Override
			public boolean isPresent() {
				return present;
			}
			
			@Nullable
			@Override
			public Type get() {
				return value;
			}
		};
	}
	
	static <Type> Optional<Type> empty() {
		return new Optional<Type>() {
			@Override
			public boolean isPresent() {
				return false;
			}
			
			@Nullable
			@Override
			public Type get() {
				return null;
			}
		};
	}
	
	@NotNull
	default <Return> Optional<Return> map(@NotNull Function<Type, Return> mapper) {
		if (isPresent()) {
			return () -> mapper.apply(get());
		}
		return empty();
	}
	
	default boolean isPresent() {
		return get() != null;
	}
	
	default Type orElseThrow(@NotNull Supplier<RuntimeException> throwable) {
		return getOrThrow(throwable);
	}
	
	default Type orElseThrow(RuntimeException throwable) {
		return getOrThrow(throwable);
	}
	
	default Type getOrThrow(@NotNull Supplier<RuntimeException> throwable) {
		return getOrThrow(throwable.get());
	}
	
	default Type getOrThrow(RuntimeException throwable) {
		if (isPresent()) {
			return get();
		} else {
			throw throwable;
		}
	}
	
	default Type orElse(@NotNull Supplier<Type> alternative) {
		return getOr(alternative);
	}
	
	default Type orElse(Type alternative) {
		return getOr(alternative);
	}
	
	default Type getOr(@NotNull Supplier<Type> alternative) {
		return getOr(alternative.get());
	}
	
	default Type getOr(Type alternative) {
		return isPresent() ? get() : alternative;
	}
	
	default Type or(Type alternative) {
		return getOr(alternative);
	}
	
	@NotNull
	@SuppressWarnings("unchecked")
	default <Cast> Optional<Cast> cast() {
		return (Optional<Cast>) this;
	}
	
	@NotNull
	default Optional<Type> or(@NotNull Optional<Type> optional) {
		return new Optional<Type>() {
			@Override
			public boolean isPresent() {
				return Optional.this.isPresent() || optional.isPresent();
			}
			
			@Override
			public Type get() {
				return Optional.this.isPresent() ? Optional.this.get() : optional.get();
			}
		};
	}
	
	@NotNull
	default Optional<Type> ifPresentOrElse(@NotNull Consumer<Type> consumer, @NotNull Runnable runnable) {
		return ifPresentOr(consumer, runnable);
	}
	
	@NotNull
	default Optional<Type> ifPresentOr(@NotNull Consumer<Type> consumer, @NotNull Runnable runnable) {
		if (isPresent()) {
			consumer.accept(get());
		} else {
			runnable.run();
		}
		return this;
	}
	
	@NotNull
	default Optional<Type> ifPresent(@NotNull Consumer<Type> consumer) {
		if (isPresent()) {
			consumer.accept(get());
		}
		return this;
	}
	
	@NotNull
	default Optional<Type> ifAbsent(@NotNull Runnable runnable) {
		if (!isPresent()) {
			runnable.run();
		}
		return this;
	}
	
	default com.google.common.base.Optional<Type> toGuava() {
		return com.google.common.base.Optional.fromNullable(get());
	}
	
	default java.util.Optional<Type> toJava() {
		return java.util.Optional.ofNullable(get());
	}
}
