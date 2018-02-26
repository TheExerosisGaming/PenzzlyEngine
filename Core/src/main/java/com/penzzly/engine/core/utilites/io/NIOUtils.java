package com.penzzly.engine.core.utilites.io;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.Files.copy;

public interface NIOUtils {
	
	default FileVisitor<Path> copyVistor(Path source, Path dest) {
		return new SimpleFileVisitor<>() {
			
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				copy(file, dest.resolve(source.relativize(file)));
				return CONTINUE;
			}
			
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				return CONTINUE;
			}
		};
	}
	
}
