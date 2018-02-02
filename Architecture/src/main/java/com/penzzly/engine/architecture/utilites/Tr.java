package com.penzzly.engine.architecture.utilites;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class Tr {
	
	public static void main(String[] args) {
		Tr.y(() -> {
			throw new RuntimeException("test");
		}).Catch(Throwable::printStackTrace);
	}
	
	private Tr() {
	
	}
	
	@SuppressWarnings("unchecked")
	public static Catch y(@NotNull Try trial) {
		final List<Exception> exceptions = new ArrayList<>();
		try {
			trial.Try();
		} catch (Exception exception) {
			exceptions.add(exception);
		}
		return new Catch() {
			@Override
			public <T extends Exception> void Catch(@NotNull Consumer<T> consumer) {
				exceptions.forEach(exception -> {
					try {
						consumer.accept((T) exception);
					} catch (Exception ignored) {
					
					}
				});
			}
		};
	}
	
	public interface Try {
		void Try() throws Exception;
	}
	
	public interface Catch {
		<T extends Exception> void Catch(Consumer<T> consumer);
		
		default <T extends Exception> void runtime() {
			Catch(throwable -> {
				throw new RuntimeException(throwable);
			});
		}
	}
}
