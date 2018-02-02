package com.penzzly.engine.core.utilites;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.function.*;

import static java.lang.ClassLoader.getSystemClassLoader;
import static net.jodah.typetools.TypeResolver.resolveRawArgument;

@SuppressWarnings("unchecked")
public interface ProxyUtil {
	
	static <Type> void create(@NotNull Consumer<Type> result) {
		new Fake() {
		}.create(result);
	}
	
	static <Type> void create(Object backer, @NotNull Consumer<Type> result) {
		new Fake() {
		}.create(backer, result);
	}
	
	@NotNull
	static <Type> Type create(Object backer, @NotNull Class<Type> type) {
		return new Fake() {
		}.create(type, backer);
	}
	
	@NotNull
	static <Type> Type create(Class<Type> type) {
		return new Fake() {
		}.create(type);
	}
	
	@NotNull
	static <First> Fake method(@NotNull Method method, @NotNull Function<First, Object> implementation) {
		return new Fake() {
		}.method(method, implementation);
	}
	
	@NotNull
	static <First> Fake method(String name, @NotNull Function<First, Object> implementation) {
		return new Fake() {
		}.method(name, implementation);
	}
	
	@NotNull
	static Fake bigMethod(String name, @NotNull Function<Object[], Object> implementation) {
		return new Fake() {
		}.method(name, implementation);
	}
	
	//--Consumer--
	@NotNull
	static <First> Fake method(@NotNull Method method, @NotNull Consumer<First> implementation) {
		return new Fake() {
		}.method(method, implementation);
	}
	
	@NotNull
	static <First> Fake method(String name, @NotNull Consumer<First> implementation) {
		return new Fake() {
		}.method(name, implementation);
	}
	
	@NotNull
	static Fake bigMethod(String name, @NotNull Consumer<Object[]> implementation) {
		return new Fake() {
		}.method(name, implementation);
	}
	
	//--BiFunction
	@NotNull
	static <First, Second> Fake method(@NotNull Method method, @NotNull BiFunction<First, Second, Object> implementation) {
		return new Fake() {
		}.method(method, implementation);
	}
	
	@NotNull
	static <First, Second> Fake method(String name, @NotNull BiFunction<First, Second, Object> implementation) {
		return new Fake() {
		}.method(name, implementation);
	}
	
	//--BiConsumer--
	@NotNull
	static <First, Second> Fake method(@NotNull Method method, @NotNull BiConsumer<First, Second> implementation) {
		return new Fake() {
		}.method(method, implementation);
	}
	
	@NotNull
	static <First, Second> Fake method(String name, @NotNull BiConsumer<First, Second> implementation) {
		return new Fake() {
		}.method(name, implementation);
	}
	
	//--Supplier--
	@NotNull
	static <First> Fake method(@NotNull Method method, @NotNull Supplier<First> implementation) {
		return new Fake() {
		}.method(method, implementation);
	}
	
	@NotNull
	static <First> Fake method(String name, @NotNull Supplier<First> implementation) {
		return new Fake() {
		}.method(name, implementation);
	}
	
	//--Runnable--
	@NotNull
	static Fake method(@NotNull Method method, @NotNull Runnable implementation) {
		return new Fake() {
		}.method(method, implementation);
	}
	
	abstract class Fake implements InvocationHandler {
		private final Map<Predicate<Method>, Function<Object[], Object>> methods = new HashMap<>();
		
		@NotNull
		public <Type> Type create(@NotNull Class<Type> type, @Nullable Object backer) {
			return (Type) Proxy.newProxyInstance(getSystemClassLoader(), new Class[]{type}, (proxy, method, args) -> {
				Object result = this.invoke(proxy, method, args);
				if (result != null) {
					return result;
				} else if (backer == null) {
					return null;
				} else if (type.isAssignableFrom(backer.getClass())) {
					return method.invoke(backer, args);
				}
				return backer.getClass().getDeclaredMethod(method.getName(), method.getParameterTypes()).invoke(backer, args);
			});
		}
		
		public <Type> void create(Object backer, @NotNull Consumer<Type> result) {
			result.accept(create((Class<Type>) resolveRawArgument(Consumer.class, result.getClass()), backer));
		}
		
		public <Type> void create(@NotNull Consumer<Type> result) {
			result.accept(create((Class<Type>) resolveRawArgument(Consumer.class, result.getClass())));
		}
		
		@NotNull
		public <Type> Type create(Class<Type> type) {
			return (Type) Proxy.newProxyInstance(getSystemClassLoader(), new Class[]{type}, this);
		}
		
		//--Function--
		@NotNull
		public <First> Fake method(@NotNull Method method, @NotNull Function<First, Object> implementation) {
			methods.put(method::equals, args -> implementation.apply((First) args[0]));
			return this;
		}
		
		@NotNull
		public <First> Fake method(String name, @NotNull Function<First, Object> implementation) {
			methods.put(method -> method.getName().equals(name), args -> implementation.apply((First) args[0]));
			return this;
		}
		
		@NotNull
		public Fake bigMethod(String name, Function<Object[], Object> implementation) {
			methods.put(method -> method.getName().equals(name), implementation);
			return this;
		}
		
		//--Consumer--
		@NotNull
		public <First> Fake method(@NotNull Method method, @NotNull Consumer<First> implementation) {
			methods.put(method::equals, args -> {
				implementation.accept((First) args[0]);
				return null;
			});
			return this;
		}
		
		@NotNull
		public <First> Fake method(String name, @NotNull Consumer<First> implementation) {
			methods.put(method -> method.getName().equals(name), args -> {
				implementation.accept((First) args[0]);
				return null;
			});
			return this;
		}
		
		@NotNull
		public Fake bigMethod(String name, @NotNull Consumer<Object[]> implementation) {
			methods.put(method -> method.getName().equals(name), args -> {
				implementation.accept(args);
				return null;
			});
			return this;
		}
		
		//--BiFunction
		@NotNull
		public <First, Second> Fake method(@NotNull Method method, @NotNull BiFunction<First, Second, Object> implementation) {
			methods.put(method::equals, args -> implementation.apply((First) args[0], (Second) args[1]));
			return this;
		}
		
		@NotNull
		public <First, Second> Fake method(String name, @NotNull BiFunction<First, Second, Object> implementation) {
			methods.put(method -> method.getName().equals(name), args -> implementation.apply((First) args[0], (Second) args[1]));
			return this;
		}
		
		//--BiConsumer--
		@NotNull
		public <First, Second> Fake method(@NotNull Method method, @NotNull BiConsumer<First, Second> implementation) {
			methods.put(method::equals, args -> {
				implementation.accept((First) args[0], (Second) args[1]);
				return null;
			});
			return this;
		}
		
		@NotNull
		public <First, Second> Fake method(String name, @NotNull BiConsumer<First, Second> implementation) {
			methods.put(method -> method.getName().equals(name), args -> {
				implementation.accept((First) args[0], (Second) args[1]);
				return null;
			});
			return this;
		}
		
		//--Supplier--
		@NotNull
		public <First> Fake method(@NotNull Method method, @NotNull Supplier<First> implementation) {
			methods.put(method::equals, args -> implementation.get());
			return this;
		}
		
		@NotNull
		public <First> Fake method(String name, @NotNull Supplier<First> implementation) {
			methods.put(method -> method.getName().equals(name), args -> implementation.get());
			return this;
		}
		
		//--Runnable--
		@NotNull
		public Fake method(@NotNull Method method, @NotNull Runnable implementation) {
			methods.put(method::equals, args -> {
				implementation.run();
				return null;
			});
			return this;
		}
		
		@Nullable
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			for (Map.Entry<Predicate<Method>, Function<Object[], Object>> entry : methods.entrySet()) {
				if (entry.getKey().test(method)) {
					return entry.getValue().apply(args);
				}
			}
			return null;
		}
	}
}
