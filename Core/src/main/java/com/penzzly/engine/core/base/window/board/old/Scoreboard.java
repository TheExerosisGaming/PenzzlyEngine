package com.penzzly.engine.core.base.window.board.old;

public interface Scoreboard {
	
	/**
	 * @param line  - The text to display on the new line, 64 characters max.
	 * @param score - The score value to give the new line.
	 */
	void set(Object line, int score);
	
	/**
	 * Removes the line with the given score.
	 *
	 * @param score - The score value associated with the target line.
	 */
	void remove(int score);
	
	/**
	 * Sets both {@link org.bukkit.scoreboard.Objective}s display names.
	 *
	 * @param title - The desired title for the {@link org.bukkit.scoreboard.Scoreboard}.
	 */
	void setTitle(Object title);
	
	/**
	 * @param score - The score value to give the new blank line.
	 */
	default void blank(int score) {
		set("", score);
	}
	
	/**
	 * Returns the underlying {@link org.bukkit.scoreboard.Scoreboard}.
	 *
	 * @return - The underlying {@link org.bukkit.scoreboard.Scoreboard}.
	 */
	org.bukkit.scoreboard.Scoreboard getScoreboard();
}