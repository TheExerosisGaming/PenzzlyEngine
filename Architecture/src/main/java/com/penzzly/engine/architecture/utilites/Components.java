package com.penzzly.engine.architecture.utilites;

import com.penzzly.engine.architecture.base.Component;
import com.penzzly.engine.architecture.base.Parent;
import com.penzzly.engine.architecture.base.Phase;
import com.penzzly.engine.architecture.base.Toggleable;
import com.penzzly.engine.architecture.holder.mutable.MutableHolder;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class Components {
	
	private Components() {
	}
	
	public static <Type> Component subTo(@NotNull Observable<Type> observable, @NotNull io.reactivex.functions.Consumer<Type> listener) {
		return new Component() {
			private Disposable disposable;
			
			@Override
			public Component enable() {
				if (!isEnabled()) {
					disposable = observable.subscribe(listener);
				}
				return super.enable();
			}
			
			@Override
			public Component disable() {
				if (isEnabled()) {
					disposable.dispose();
				}
				return super.disable();
			}
		};
	}
	
	@NotNull
	public static <Type> Component addTo(@NotNull MutableHolder<Type> holder, Type element) {
		return create(() -> holder.add(element), () -> holder.remove(element));
	}
	
	@NotNull
	public static <Type> Component addTo(@NotNull Collection<Type> list, Type element) {
		return create(() -> list.add(element), () -> list.remove(element));
	}
	
	@NotNull
	public static <Type> Component addToList(@NotNull List<Type> list, Type element, int index) {
		return create(() -> list.add(index, element), () -> list.remove(element));
	}
	
	@NotNull
	public static List<Toggleable> getChildrenRecurse(@NotNull Parent<Toggleable> component) {
		return getChildrenRecurse(component.getChildren());
	}
	
	@NotNull
	private static List<Toggleable> getChildrenRecurse(@NotNull List<Toggleable> children) {
		final List<Toggleable> deepChildren = new ArrayList<>();
		for (Toggleable child : children) {
			if (child instanceof Component) {
				deepChildren.addAll(getChildrenRecurse(((Component) child).getChildren()));
			}
		}
		children.addAll(deepChildren);
		return children;
	}
	
	@NotNull
	public static Phase create(@Nullable Runnable enable, @Nullable Runnable disable, @Nullable Runnable complete) {
		Phase phase = new Phase();
		if (enable != null) {
			phase.onEnable(enable);
		}
		if (disable != null) {
			phase.onDisable(disable);
		}
		if (complete != null) {
			phase.onComplete(complete);
		}
		return phase;
	}
	
	@NotNull
	public static Component create(Runnable enable) {
		return new Component().onEnable(enable);
	}
	
	@NotNull
	public static Component create(Runnable enable, Runnable disable) {
		return new Component().onEnable(enable).onDisable(disable);
	}
	
}