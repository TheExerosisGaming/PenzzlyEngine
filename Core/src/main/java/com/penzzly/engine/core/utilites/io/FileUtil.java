package com.penzzly.engine.core.utilites.io;


import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static com.penzzly.engine.core.utilites.io.StreamUtil.closeQuietly;
import static com.penzzly.engine.core.utilites.io.StreamUtil.write;
import static java.io.File.separator;

/**
 * Created by Exerosis.
 */
public class FileUtil {
	
	public static void downloadFile(@NotNull URL url, @NotNull File directory) throws IOException {
		downloadFile(url, directory, true);
	}
	
	public static void downloadFile(@NotNull URL url, @NotNull File directory, boolean overwrite) throws IOException {
		if (directory.isFile()) {
			throw new FileNotFoundException("Cannot download file into a file, please specify a directory instead!");
		}
		if (!directory.exists() && !directory.mkdirs()) {
			throw new FileNotFoundException("Cannot locate or create a directory at the given location!");
		}
		String urlPath = url.getPath();
		String fileName = urlPath.substring(urlPath.lastIndexOf('/') + 1);
		
		String[] fileComponents = fileName.split("\\.");
		String name = fileComponents[0].replace("%20", " ");
		String extension = "";
		if (fileComponents.length >= 2) {
			extension = fileComponents[1];
		}
		
		StringBuilder path = new StringBuilder(directory.getPath()).append('/').append(name);
		
		if (!extension.equals("zip")) {
			Files.copy(url.openStream(), Paths.get(path.append(extension).toString()), StandardCopyOption.REPLACE_EXISTING);
		} else {
			unzip(url.openStream(), new File(path.toString()), overwrite);
		}
	}
	
	public static void copyFile(@NotNull File from, @NotNull File into, boolean overwrite) {
		if (!from.exists() || from.getPath().equals(into.getPath())) {
			return;
		}
		if (from.exists() && overwrite && from.delete()) {
			System.err.println("Deleted file while unzipping: " + from.getPath());
		}
		try {
			write(into, new BufferedInputStream(new FileInputStream(from)), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	@NotNull
	public static Map<String, File> mapDirectory(@NotNull File directory) throws IOException {
		if (!directory.exists()) {
			throw new IOException("File does not exist.");
		}
		Map<String, File> contents = new HashMap<>();
		for (File file : directory.listFiles()) {
			if (file.isFile()) {
				contents.put(file.getPath().replace(directory.getPath(), ""), file);
			} else {
				contents.putAll(mapDirectory(file));
			}
		}
		return contents;
	}
	
	public static void unzip(@NotNull File file, @NotNull File directory, boolean overwrite) throws IOException {
		unzip(new BufferedInputStream(new FileInputStream(file)), directory, overwrite);
	}
	
	public static void unzip(InputStream inputStream, @NotNull File directory, boolean overwrite) throws IOException {
		if (directory.mkdirs()) {
			System.err.println("Could not find destination directory, created it.");
		}
		
		try {
			inputStream = new ZipInputStream(inputStream);
			ZipEntry entry = ((ZipInputStream) inputStream).getNextEntry();
			while (entry != null) if (!entry.isDirectory()) {
				write(directory.getPath() + separator + entry.getName(), inputStream, overwrite);
			}
		} finally {
			closeQuietly(inputStream);
		}
	}
	
	public static void zip(@NotNull File directory, @NotNull File zip) throws IOException {
		zip(mapDirectory(directory), zip);
	}
	
	public static void zip(@NotNull Map<String, File> contents, @NotNull File zip) throws IOException {
		OutputStream outputStream = null;
		
		try {
			outputStream = new FileOutputStream(zip);
			outputStream = new BufferedOutputStream(outputStream);
			outputStream = new ZipOutputStream(outputStream);
			
			for (Map.Entry<String, File> entry : contents.entrySet()) {
				if (!entry.getValue().isFile()) {
					throw new IOException("Cannot zip directory.");
				}
				
				InputStream inputStream = null;
				try {
					ZipEntry zipEntry = new ZipEntry(entry.getKey());
					((ZipOutputStream) outputStream).putNextEntry(zipEntry);
					
					inputStream = new FileInputStream(entry.getValue());
					inputStream = new BufferedInputStream(inputStream);
					
					IOUtils.copy(inputStream, outputStream);
					((ZipOutputStream) outputStream).closeEntry();
				} finally {
					closeQuietly(inputStream);
				}
			}
		} finally {
			closeQuietly(outputStream);
		}
	}
	
}