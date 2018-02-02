package com.penzzly.engine.core.base.window.elements;

import com.penzzly.engine.architecture.base.Parent;
import com.penzzly.engine.architecture.base.Toggleable;
import org.jetbrains.annotations.NotNull;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.List;

import static java.util.Arrays.asList;


@SuppressWarnings("unchecked")
public interface Element<Type extends Element> extends Parent<Toggleable> {
	//--Show--
	@OverridingMethodsMustInvokeSuper
	@NotNull Type show();
	
	//--Hide--
	@OverridingMethodsMustInvokeSuper
	@NotNull Type hide();
	
	
	//--Internal Children--
	default <Child extends Toggleable> Child addInternalChild(Child toggleable) {
		getInternalChildren().add(toggleable);
		return toggleable;
	}
	
	@NotNull
	default <Child extends Toggleable> Child[] addChild(@NotNull Child... children) {
		for (Child child : children)
			addChild(child);
		return children;
	}
	
	@NotNull List<Toggleable> getInternalChildren();
	
	
	//--Complete--
	@NotNull
	default Type onComplete(Runnable... listeners) {
		getCompleteListeners().addAll(asList(listeners));
		return (Type) this;
	}
	
	@NotNull List<Runnable> getCompleteListeners();
	
	
	//--Enable--
	@NotNull
	default Type onEnable(Runnable... listeners) {
		getEnableListeners().addAll(asList(listeners));
		return (Type) this;
	}
	
	@NotNull List<Runnable> getEnableListeners();
	
	
	//--Disable--
	@NotNull
	default Type onDisable(Runnable... listeners) {
		getDisableListeners().addAll(asList(listeners));
		return (Type) this;
	}
	
	@NotNull List<Runnable> getDisableListeners();
	
	
	//--Getters--
	boolean isShown();
	
	boolean isEnabled();
	
	boolean isComplete();
}