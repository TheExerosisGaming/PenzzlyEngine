package com.penzzly.engine.core.base.configuration.serialization.alpha;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.OutputStream;

public interface Serializer {
	void write(OutputStream out, Object target);
	
	@NotNull Object read(InputStream in);
	
	default void readUnsafe(InputStream in, Object caller, Object target) {
		Object loaded = read(in);
//		getUnsafe().ifPresent(unsafe -> {
//			if (caller == null) {
//				Class<?> type = target.getClass();
//				concat(stream(type.getDeclaredFields()),
//						stream(type.getFields())).forEach(field -> {
//					long offset = unsafe.objectFieldOffset(field);
//					unsafe.putObject(target, offset, unsafe.getObject(loaded, offset));
//				});
//			} else {
//				for (Field field : caller.getClass().getDeclaredFields()) {
//					if (unsafe.getObject(caller, unsafe.objectFieldOffset(field)).equals(target)) {
//						unsafe.putObject(caller, unsafe.objectFieldOffset(field), loaded);
//					}
//				}
//			}
//		});
	}
}
