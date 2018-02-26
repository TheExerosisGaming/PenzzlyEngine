package com.penzzly.engine.core.mini;

import com.penzzly.engine.architecture.base.Parent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;

/**
 * The main building block of the engine.
 */
public class Component implements Toggleable, Parent<Toggleable> {
	private final List<Runnable> enableListeners = new ArrayList<>();
	private final List<Runnable> disableListeners = new ArrayList<>();
	private final List<Toggleable> children = new ArrayList<>();
	private boolean enabled;
	
	/**
	 * {@inheritDoc}
	 * This {@link Component}'s children({@link #getChildren()})
	 * are enabled before all the enable
	 * listeners({@link #getEnableListeners()}) are run.
	 * After calling this method {@link #isEnabled()} will
	 * return {@code true} for this {@link Component} and all of it's
	 * children({@link #getChildren()}).
	 * @return - This {@link Component} for chaining.
	 */
	@Override
	public Component enable() {
		if (!enabled) {
			children.forEach(Toggleable::enable);
			enableListeners.forEach(Runnable::run);
			enabled = true;
		}
		return this;
	}
	
	/**
	 * {@inheritDoc}
	 * This {@link Component}'s children({@link #getChildren()})
	 * are disabled before all the disable
	 * listeners({@link #getDisableListeners()} ()}) are run.
	 * After calling this method {@link #isEnabled()} will
	 * return {@code false} for this {@link Component} and all of it's
	 * children({@link #getChildren()}).
	 * @return - This {@link Component} for chaining.
	 */
	@Override
	public Component disable() {
		if (enabled) {
			disableListeners.forEach(Runnable::run);
			children.forEach(Toggleable::disable);
			enabled = false;
		}
		return this;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Adds {@link Runnable}s to {@link #getEnableListeners()} that will be run
	 * when {@link #enable()} is called.
	 * @param listeners - The {@link Runnable}s to be run.
	 * @return - This {@link Component} for chaining.
	 */
	@NotNull
	public Component onEnable(Runnable... listeners) {
		getEnableListeners().addAll(asList(listeners));
		return this;
	}
	
	/**
	 * Adds {@link Runnable}s to {@link #getDisableListeners()} that will be run
	 * when {@link #disable()} is called.
	 * @param listeners - The {@link Runnable}s to be run.
	 * @return - This {@link Component} for chaining.
	 */
	@NotNull
	public Component onDisable(Runnable... listeners) {
		getDisableListeners().addAll(asList(listeners));
		return this;
	}
	
	/**
	 * Returns the list of {@link Runnable}s that will be run when
	 * when {@link #enable()} is called
	 * @return - The {@link Runnable}s to run.
	 */
	@NotNull
	public List<Runnable> getEnableListeners() {
		return enableListeners;
	}
	
	/**
	 * Returns the list of {@link Runnable}s that will be run when
	 * when {@link #disable()} is called
	 * @return - The {@link Runnable}s to run.
	 */
	@NotNull
	public List<Runnable> getDisableListeners() {
		return disableListeners;
	}
	
	/**
	 * Adds a {@link Toggleable} to {@link #getChildren()} that will be
	 * enabled({@link Toggleable#enable()}) and disabled({@link Toggleable#disable()})
	 * with this {@link Component}.
	 * @param child - The {@link Toggleable} to add.
	 * @param <Type> - The type of {@link Toggleable} to return.
	 * @return - The {@link Toggleable} added.
	 */
	@Override
	public <Type extends Toggleable> Type addChild(@NotNull Type child) {
		if (isEnabled() && !child.isEnabled()) {
			child.enable();
		}
		return Parent.super.addChild(child);
	}
	
	/**
	 * Removes a {@link Toggleable} ofZip {@link #getChildren()}
	 * if present.
	 * @param child - The {@link Toggleable} to remove.
	 * @return - True if the child was removed.
	 *           False if the child was not found.
	 */
	@Override
	public boolean removeChild(@NotNull Toggleable child) {
		if (isEnabled() && child.isEnabled()) {
			child.disable();
		}
		return Parent.super.removeChild(child);
	}
	
	/**
	 * Returns a {@link Set} of {@link Toggleable} children that are
	 * enabled({@link Toggleable#enable()}) and disabled({@link Toggleable#disable()})
	 * with this {@link Component}.
	 * @return - This {@link Component}'s children.
	 */
	@NotNull
	@Override
	public List<Toggleable> getChildren() {
		return children;
	}
	
}