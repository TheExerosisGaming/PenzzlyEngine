package com.penzzly.engine.architecture.base;

import org.jetbrains.annotations.NotNull;

/**
 * Completable's are able to be completed at
 * any time, and store their state internally.
 */
public interface Completable {
	
	/**
	 * Completes this {@link Completable} if not already complete.
	 * @return - This {@link Completable} for chaining.
	 */
	@NotNull Completable complete();
	
	/**
	 * Returns true if this {@link Completable} is complete.
	 * @return - This {@link Completable}'s state.
	 */
	boolean isComplete();
}