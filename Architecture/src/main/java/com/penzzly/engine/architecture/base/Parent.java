package com.penzzly.engine.architecture.base;


import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("ALL")
public interface Parent<A> {
	/**
	 * @return - This {@link Parent}'s children.
	 */
	@NotNull List<A> getChildren();
	
	/**
	 * Adds the specified child to {@link #getChildren()}
	 * if not present.
	 * @param child - The child to add.
	 * @return - The child added.
	 */
	default <B extends A> B addChild(B child) {
		if (!getChildren().contains(child)) {
			getChildren().add(child);
		}
		return child;
	}
	
	/**
	 * Adds the specified children to {@link #getChildren()}
	 * if not present.
	 * @param children - The children to add.
	 * @return - The children added.
	 */
	@NotNull
	default <B extends A> B[] addChild(@NotNull B... children) {
		for (B child : children)
			addChild(child);
		return children;
	}
	
	
	/**
	 * Removes the specified child from {@link #getChildren()}
	 * if present.
	 * @param child The child to remove.
	 * @return {@code true} if the child was removed.
	 */
	default boolean removeChild(A child) {
		return getChildren().remove(child);
	}
	
	/**
	 * Removes the specified children from {@link #getChildren()}
	 * if present.
	 * @param child The child to remove.
	 * @return {@code true} if any of the children were removed.
	 */
	default boolean removeChild(@NotNull A... children) {
		boolean result = false;
		for (A child : children)
			result = result || removeChild(child);
		return result;
	}
}