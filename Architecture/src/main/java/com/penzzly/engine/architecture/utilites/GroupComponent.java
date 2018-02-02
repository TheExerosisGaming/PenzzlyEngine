package com.penzzly.engine.architecture.utilites;

import com.penzzly.engine.architecture.base.Component;
import com.penzzly.engine.architecture.base.Phase;
import org.jetbrains.annotations.NotNull;

//FIXME IDK what to do with this... I guess overhaul?
public class GroupComponent extends Phase {
	private Scope enableScope = Scope.NONE;
	private Scope disableScope = Scope.NONE;
	private Scope completeScope = Scope.NONE;
	private int enabledCount = 0;
	
	public static GroupComponent of(Component... components) {
		return new GroupComponent(components);
	}
	
	public GroupComponent(@NotNull Component... components) {
		Runnable onEnable = () -> {
			if (enableScope != Scope.NONE && ++enabledCount >= (enableScope == Scope.FIRST ? 1 : components.length)) {
				enable();
			}
		};
		Runnable onDisable = () -> {
			if (disableScope != Scope.NONE && --enabledCount <= (disableScope == Scope.LAST ? 0 : components.length)) {
				disable();
			}
		};
		Runnable onComplete = () -> {
			if (completeScope == Scope.NONE) {
				return;
			}
			for (Component component : components) {
				if (component instanceof Phase && (((Phase) component).isComplete() == (completeScope == Scope.FIRST))) {
					if (completeScope == Scope.FIRST) {
						complete();
						return;
					} else {
						return;
					}
				}
			}
			complete();
		};
		for (Component component : components) {
			component.onEnable(onEnable).onDisable(onDisable);
			if (component instanceof Phase) {
				((Phase) component).onComplete(onComplete);
			}
		}
		
		onEnable(() -> {
			for (Component component : components)
				component.enable();
		});
		onDisable(() -> {
			for (Component component : components)
				component.disable();
		});
		onComplete(() -> {
			for (Component component : components)
				if (component instanceof Phase) {
					((Phase) component).complete();
				}
		});
	}
	
	@NotNull
	public GroupComponent enableWith(Scope scope) {
		enableScope = scope;
		return this;
	}
	
	@NotNull
	public GroupComponent disableWith(Scope scope) {
		disableScope = scope;
		return this;
	}
	
	@NotNull
	public GroupComponent completeWith(Scope scope) {
		completeScope = scope;
		return this;
	}
	
	@NotNull
	public GroupComponent enable(Scope scope) {
		completeScope = scope;
		return this;
	}
	
	public enum Scope {
		FIRST, LAST, NONE
	}
}