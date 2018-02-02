package com.penzzly.engine.core.base.window;

import com.penzzly.engine.core.base.window.elements.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static java.lang.Double.compare;
import static java.util.UUID.randomUUID;

public abstract class Transaction<Type extends Element> implements Comparable<Transaction> {
	public static final Boolean CLEAR = false;
	public static final Boolean QUEUE = true;
	@Nullable
	public static final Boolean NOW = null;
	private final UUID id = randomUUID();
	private final BaseElement<Type> element;
	private boolean disableEagerly = false;
	private boolean addToBackstack = false;
	private double priority = 0;
	
	Transaction(BaseElement<Type> element) {
		this.element = element;
	}
	
	@NotNull
	BaseElement<Type> getElement() {
		return element;
	}
	
	@NotNull
	public UUID commitNow() {
		return commit(NOW);
	}
	
	@NotNull
	public UUID commitClearing() {
		return commit(CLEAR);
	}
	
	@NotNull
	public UUID commit() {
		return commit(QUEUE);
	}
	
	@NotNull
	public abstract UUID commit(Boolean mode);
	
	@NotNull
	public Transaction priority(double priority) {
		this.priority = priority;
		return this;
	}
	
	@NotNull
	public Transaction disableEagerly() {
		disableEagerly ^= true;
		return this;
	}
	
	@NotNull
	public Transaction addToBackstack() {
		addToBackstack ^= true;
		return this;
	}
	
	@NotNull
	public UUID getId() {
		return id;
	}
	
	public double getPriority() {
		return priority;
	}
	
	public boolean willDisableEagerly() {
		return disableEagerly;
	}
	
	public boolean willAddToBackstack() {
		return addToBackstack;
	}
	
	@Override
	public int compareTo(@NotNull Transaction holder) {
		return compare(priority, holder.priority);
	}
}