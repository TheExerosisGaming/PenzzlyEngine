package com.penzzly.engine.architecture.base;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Phases are the second building block of the engine.
 * They are both {@link Completable} and {@link Toggleable}.
 */
public class Phase extends Component implements Completable {
	private final List<Runnable> completeListeners = new ArrayList<>();
	private boolean complete = false;
	private boolean autoDisable = false;
	
	/**
	 * {@inheritDoc}
	 */
	@NotNull
	@Override
	public Phase disable() {
		return this;
	}
	
	/**
	 * {@inheritDoc}
	 * After calling this method {@link #isComplete()}
	 * will return {@code false}.
	 */
	@NotNull
	@Override
	public Phase enable() {
		if (!isEnabled()) {
			complete = false;
		}
		super.enable();
		return this;
	}
	
	/**
	 * {@inheritDoc}
	 * This {@link Phase}'s listeners({@link #getCompleteListeners()})
	 * are run. After calling this method {@link #isComplete()} will
	 * return {@code true}.
	 * @return - This {@link Phase} for chaining.
	 */
	@NotNull
	@Override
	public Phase complete() {
		if (!complete && isEnabled()) {
			complete = true;
			getCompleteListeners().forEach(Runnable::run);
		}
		return this;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isComplete() {
		return complete;
	}
	
	/**
	 * Toggles this {@link Phase}'s auto-disable
	 * state.
	 * Auto-disable calls {@link #disable()}
	 * when {@link #complete()} is called.
	 * If this {@link Phase} is complete
	 * {@link #disable()} is called immediately.
	 */
	@NotNull
	public Phase autoDisable() {
		autoDisable ^= true;
		if (complete && autoDisable) {
			disable();
		}
		return this;
	}
	
	/**
	 * Adds {@link Runnable}s to {@link #getCompleteListeners()} that will be run
	 * when {@link #complete()} is called.
	 * @param listeners - The {@link Runnable}s to be run.
	 * @return - This {@link Phase} for chaining.
	 */
	@NotNull
	public Phase onComplete(Runnable... listeners) {
		getCompleteListeners().addAll(asList(listeners));
		return this;
	}
	
	/**
	 * Returns the list of {@link Runnable}s that will be run when
	 * when {@link #complete()} is called
	 * @return - The {@link Runnable}s to run.
	 */
	@NotNull
	public List<Runnable> getCompleteListeners() {
		return completeListeners;
	}
}