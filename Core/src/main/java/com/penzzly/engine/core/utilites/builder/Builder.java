package com.penzzly.engine.core.utilites.builder;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class Builder<Product> implements Buildable {
	@NotNull
	private List<Consumer<Product>> listeners = new ArrayList<>();
	
	@NotNull
	public Builder<Product> onBuild(Consumer<Product> listener) {
		listeners.add(listener);
		return this;
	}
	
	@NotNull
	public List<Consumer<Product>> getBuildListeners() {
		return listeners;
	}
	
	@NotNull
	protected abstract Product getProduct();
	
	@Override
	public void build() {
		listeners.forEach(listener -> listener.accept(getProduct()));
	}
}
