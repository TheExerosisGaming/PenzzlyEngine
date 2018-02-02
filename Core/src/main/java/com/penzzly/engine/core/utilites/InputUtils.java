package com.penzzly.engine.core.utilites;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public interface InputUtils {
	ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
	
	static Closeable scanLines(@NotNull InputStream stream, @NotNull Consumer<String> lines) {
		final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		final Future<?> task = EXECUTOR.submit(() -> {
			for (; ; )
				lines.accept(reader.readLine());
		});
		return () -> {
			task.cancel(true);
			reader.close();
		};
	}
	
	static void main(String[] args) {
		scanLines(System.in, line -> {
			System.out.println(line);
		});
	}
}
