package com.penzzly.engine.core.mini;


/**
 * Toggleable objects can be toggled from enabled
 * to disabled at any time and store their state
 * internally.
 */
public interface Toggleable {
	
	/**
	 * Enables this {@link Toggleable} if not already enabled.
	 * @return - This {@link Toggleable} for chaining.
	 */
	Toggleable enable();
	
	/**
	 * Disables this {@link Toggleable} if not already disabled.
	 * @return - This {@link Toggleable} for chaining.
	 */
	Toggleable disable();
	
	/**
	 * Returns true if this {@link Toggleable} is enabled.
	 * @return - This {@link Toggleable}'s state.
	 */
	boolean isEnabled();
}