package com.penzzly.engine.core.mini.dsm;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static java.io.File.separator;
import static java.nio.file.Files.copy;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public interface ByteUtils {
	
	static void putString(ByteBuffer buffer, Object value) {
		putString(buffer, value, length -> buffer.put(length.byteValue()));
	}
	
	static void putString(ByteBuffer buffer, Object value, Consumer<Integer> length) {
		byte[] bytes = value.toString().getBytes();
		length.accept(bytes.length);
		buffer.put(bytes);
	}
	
	static String getString(ByteBuffer buffer) {
		return getString(buffer, ByteBuffer::get);
	}
	
	static String getString(ByteBuffer buffer, Function<ByteBuffer, Number> length) {
		byte[] bytes = new byte[length.apply(buffer).intValue()];
		buffer.get(bytes);
		return new String(bytes);
	}
	
	static void unzip(InputStream zip, Path destination) throws IOException {
		try (ZipInputStream in = new ZipInputStream(zip)) {
			ZipEntry entry;
			while ((entry = in.getNextEntry()) != null) {
				Path path = destination.resolve(entry.getName());
				if (path.endsWith(separator)) {
					createDirectories(path);
				} else {
					createDirectories(path.getParent());
					copy(in, path, REPLACE_EXISTING);
				}
				in.closeEntry();
			}
		}
	}
}
