package com.penzzly.engine.core.base.window;

import com.penzzly.engine.architecture.base.Toggleable;
import com.penzzly.engine.core.base.window.elements.Element;
import org.jetbrains.annotations.NotNull;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class BaseElement<Type extends Element> implements Element<Type> {
	private final List<Toggleable> internalChildren = new ArrayList<>();
	private final List<Toggleable> children = new ArrayList<>();
	private final List<Runnable> completeListeners = new ArrayList<>();
	private final List<Runnable> enableListeners = new ArrayList<>();
	private final List<Runnable> disableListeners = new ArrayList<>();
	private boolean revealed = false;
	private boolean shown = false;
	private boolean enabled = false;
	private boolean complete = false;
	
	public BaseElement() {
		init();
	}
	
	protected void init() {
	}
	
	@OverridingMethodsMustInvokeSuper
	protected void enable() {
		if (!enabled) {
			revealed = false;
			enabled = true;
			getEnableListeners().forEach(Runnable::run);
		}
	}
	
	@OverridingMethodsMustInvokeSuper
	protected void disable() {
		if (enabled) {
			conceal();
			enabled = false;
			getDisableListeners().forEach(Runnable::run);
		}
	}
	
	@OverridingMethodsMustInvokeSuper
	protected void complete() {
		if (!complete) {
			complete = true;
			getCompleteListeners().forEach(Runnable::run);
		}
	}
	
	@OverridingMethodsMustInvokeSuper
	protected void reveal() {
		if (!revealed) {
			complete = false;
			revealed = true;
			getInternalChildren().forEach(Toggleable::enable);
		}
	}
	
	@OverridingMethodsMustInvokeSuper
	protected void conceal() {
		if (revealed) {
			hide();
			getInternalChildren().forEach(Toggleable::disable);
			revealed = false;
		}
	}
	
	@NotNull
	@Override
	@OverridingMethodsMustInvokeSuper
	public Type show() {
		shown = true;
		return (Type) this;
	}
	
	@NotNull
	@Override
	@OverridingMethodsMustInvokeSuper
	public Type hide() {
		shown = false;
		return (Type) this;
	}
	
	@NotNull
	@Override
	public List<Toggleable> getInternalChildren() {
		return internalChildren;
	}
	
	@NotNull
	@Override
	public List<Toggleable> getChildren() {
		return children;
	}
	
	@NotNull
	@Override
	public List<Runnable> getCompleteListeners() {
		return completeListeners;
	}
	
	@NotNull
	@Override
	public List<Runnable> getEnableListeners() {
		return enableListeners;
	}
	
	@NotNull
	@Override
	public List<Runnable> getDisableListeners() {
		return disableListeners;
	}
	
	@Override
	public boolean isShown() {
		return shown;
	}
	
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	@Override
	public boolean isComplete() {
		return complete;
	}
	
	public boolean isRevealed() {
		return revealed;
	}
}
