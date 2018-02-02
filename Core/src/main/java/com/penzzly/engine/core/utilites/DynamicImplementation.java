package com.penzzly.engine.core.utilites;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

public class DynamicImplementation {
	
	private DynamicImplementation() {
	
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T become(@NotNull Class<T> type, @NotNull Object target, @NotNull Object... implementations) {
		Class<?>[] interfaces;
		if (type.isInterface()) {
			interfaces = new Class<?>[]{type};
		} else {
			interfaces = type.getInterfaces();
		}
		
		return (T) Proxy.newProxyInstance(type.getClassLoader(), interfaces, (proxy, method, args) -> {
			method.setAccessible(true);
			for (Object implementation : implementations) {
				for (Method declaredMethod : implementation.getClass().getDeclaredMethods())
					if (method.getName().equals(declaredMethod.getName()) && Arrays.equals(method.getParameterTypes(), declaredMethod.getParameterTypes())) {
						return method.invoke(implementation, args);
					}
			}
			if (method.getName().equals("equals")) {
				return target.getClass().getMethod("hashCode").invoke(target).equals(args[0].getClass().getMethod("hashCode").invoke(args[0]));
			}
			return method.invoke(target, args);
		});
	}
	
}
