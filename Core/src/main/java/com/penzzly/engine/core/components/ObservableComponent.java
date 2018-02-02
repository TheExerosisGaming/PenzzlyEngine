package com.penzzly.engine.core.components;

import com.penzzly.engine.architecture.base.Parent;
import com.penzzly.engine.architecture.base.Toggleable;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.functions.Consumer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ObservableComponent<Type> extends Observable<Type> implements Toggleable, Parent<Toggleable> {
	private final List<Toggleable> children = new ArrayList<>();
	private final List<Runnable> enableListenable = new ArrayList<>();
	private final List<Runnable> disableListenable = new ArrayList<>();
	private boolean enabled = false;
	private Consumer<Observer<? super Type>> onSubscribe;
	
	public ObservableComponent(Consumer<Observer<? super Type>> onSubscribe) {
		this.onSubscribe = onSubscribe;
	}
	
	@Override
	protected void subscribeActual(Observer<? super Type> observer) {
		try {
			onSubscribe.accept(observer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@NotNull
	public ObservableComponent onEnable(Runnable... listeners) {
		getEnableListenable().addAll(Arrays.asList(listeners));
		return this;
	}
	
	@NotNull
	public ObservableComponent onDisable(Runnable... listeners) {
		getDisableListenable().addAll(Arrays.asList(listeners));
		return this;
	}
	
	@NotNull
	public List<Runnable> getEnableListenable() {
		return enableListenable;
	}
	
	@NotNull
	public List<Runnable> getDisableListenable() {
		return disableListenable;
	}
	
	@NotNull
	@Override
	public ObservableComponent enable() {
		if (!enabled) {
			children.forEach(Toggleable::enable);
			enableListenable.forEach(Runnable::run);
			enabled = true;
		}
		return this;
	}
	
	@NotNull
	@Override
	public ObservableComponent disable() {
		if (enabled) {
			disableListenable.forEach(Runnable::run);
			children.forEach(Toggleable::disable);
			enabled = false;
			return this;
		}
		return this;
	}
	
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	@NotNull
	@Override
	public List<Toggleable> getChildren() {
		return children;
	}
}
