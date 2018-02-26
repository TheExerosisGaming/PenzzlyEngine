package com.penzzly.engine.core.utilites.io;


import com.google.common.io.Files;
import com.penzzly.engine.architecture.functions.Optional;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;

import static com.google.common.io.Files.createParentDirs;
import static com.google.common.io.Files.touch;
import static com.penzzly.engine.architecture.functions.Optional.empty;
import static com.penzzly.engine.architecture.functions.Optional.of;
import static java.nio.charset.Charset.defaultCharset;

public final class StreamUtil {
	
	private StreamUtil() {
	}
	
	@NotNull
	public static File createFileWithDirs(@NotNull String path) throws IOException {
		return createFileWithDirs(new File(path));
	}
	
	@NotNull
	public static File createFileWithDirs(@NotNull Path path) throws IOException {
		return createFileWithDirs(path.toFile());
	}
	
	@NotNull
	public static File createFileWithDirs(@NotNull Path path, @NotNull String more) throws IOException {
		return createFileWithDirs(path.toFile(), more);
	}
	
	@NotNull
	public static File createFileWithDirs(File file, @NotNull String more) throws IOException {
		return createFileWithDirs(new File(file, more));
	}
	
	@NotNull
	public static File createFileWithDirs(@NotNull File file) throws IOException {
		if (file.isFile()) {
			return file;
		}
		createParentDirs(file);
		touch(file);
		return file;
	}
	
	public static Reader readFromURL(@NotNull URL url) throws IOException {
		return new BufferedReader(new InputStreamReader(url.openStream()));
	}
	
	public static boolean write(@NotNull byte[] from, File to, @NotNull String more) {
		return write(from, new File(to, more));
	}
	
	public static boolean write(@NotNull byte[] from, @NotNull File to) {
		try {
			Files.write(from, createFileWithDirs(to));
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static void write(@NotNull CharSequence path, @NotNull InputStream inputStream, boolean overwrite) {
		write(new File(path.toString()), inputStream, overwrite);
	}
	
	//FIXME sorta old
	public static void write(@NotNull File file, @NotNull InputStream inputStream, boolean overwrite) {
		if (file.getParentFile().mkdirs()) {
			System.out.println("Created directory while writing file: " + file.getParent());
		}
		
		if (file.exists() && overwrite && file.delete()) {
			System.err.println("Deleted file while writing: " + file.getPath());
		}
		
		OutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(file);
			outputStream = new BufferedOutputStream(outputStream);
			IOUtils.copy(inputStream, outputStream);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeQuietly(outputStream);
		}
	}
	
	@NotNull
	public static Optional<String> readString(File file, @NotNull String more) {
		return readString(new File(file, more));
	}
	
	@NotNull
	public static Optional<String> readString(@NotNull File file) {
		try {
			createFileWithDirs(file);
			String result = Files.toString(file, defaultCharset());
			if (result.isEmpty()) {
				return empty();
			}
			return of(result);
		} catch (IOException e) {
			return empty();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <Type> Optional<Type> read(@NotNull File file) {
		return () -> {
			Type result = null;
			InputStream stream = null;
			try {
				stream = new FileInputStream(file);
				stream = new BufferedInputStream(stream);
				stream = new ObjectInputStream(stream);
				result = (Type) ((ObjectInputStream) stream).readObject();
			} catch (Exception exception) {
				exception.printStackTrace();
			} finally {
				closeQuietly(stream);
			}
			return result;
		};
	}
	
	public static void closeQuietly(@Nullable Closeable closeable) {
		if (closeable != null) {
			try {
				if (closeable.getClass().isAssignableFrom(OutputStream.class)) {
					((OutputStream) closeable).flush();
				}
				closeable.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Gets bytes ofZip InputStream
	 *
	 * @param stream The InputStream
	 * @return Returns a byte[] representation For given stream
	 */
	
	//TODO Update
	public static byte[] getBytesFromIS(@NotNull InputStream stream) {
		
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try {
			int nRead;
			byte[] data = new byte[16384];
			
			while ((nRead = stream.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, nRead);
			}
			
			buffer.flush();
		} catch (Exception e) {
			System.err.println("Failed to predicate IS to byte[]!");
			e.printStackTrace();
		}
		
		return buffer.toByteArray();
	}
	
	/**
	 * Gets bytes ofZip class
	 *
	 * @param clazz The class
	 * @return Returns a byte[] representation For given class
	 */
	
	public static byte[] getBytesFromClass(@NotNull Class<?> clazz) {
		return getBytesFromIS(clazz.getClassLoader().getResourceAsStream(clazz.getName().replace('.', '/') + ".class"));
	}
}