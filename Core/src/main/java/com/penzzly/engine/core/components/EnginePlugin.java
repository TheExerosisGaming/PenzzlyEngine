package com.penzzly.engine.core.components;

import com.penzzly.engine.architecture.base.Parent;
import com.penzzly.engine.architecture.base.Toggleable;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.bukkit.Bukkit.getPluginManager;

public class EnginePlugin extends JavaPlugin implements Toggleable, Parent<Toggleable> {
	
	private final List<Toggleable> children = new ArrayList<>();
	
	private final List<Runnable> enableListenable = new ArrayList<>();
	
	private final List<Runnable> disableListenable = new ArrayList<>();
	
	public EnginePlugin() {
		onEnable(() -> {
			//TODO, make this somewhat automatic... so I don't have to use it to warn myself and others
			//TODO of my stupidity.
			/*getConsoleSender().sendMessage(RED +
					"====================================\n" + RED +
					"=====================================================\n" + RED +
					"        WARNING: ENGINE INSTABILITY DETECTED!        \n" + RED +
					"=====================================================\n" + RED +
					"=====================================================\n" + RED +
					"  WARNING: ENGINE COMPONENT PRE-ENABLE IO DETECTED!  \n" + RED +
					"=====================================================\n" + RED +
					"=====================================================\n" + RED);*/
		});
	}
	
	@NotNull
	public EnginePlugin onEnable(@NotNull Runnable... listeners) {
		if (listeners.length > 1) {
			getEnableListenable().addAll(Arrays.asList(listeners));
		} else {
			getEnableListenable().add(listeners[0]);
		}
		return this;
	}
	
	@NotNull
	public EnginePlugin onDisable(@NotNull Runnable... listeners) {
		if (listeners.length > 1) {
			getDisableListenable().addAll(Arrays.asList(listeners));
		} else {
			getDisableListenable().add(listeners[0]);
		}
		return this;
	}
	
	@NotNull
	public EnginePlugin unregisterEnable(@NotNull Runnable... listeners) {
		if (listeners.length > 1) {
			getEnableListenable().removeAll(Arrays.asList(listeners));
		} else {
			getEnableListenable().remove(listeners[0]);
		}
		return this;
	}
	
	@NotNull
	public EnginePlugin unregisterDisable(@NotNull Runnable... listeners) {
		if (listeners.length > 1) {
			getDisableListenable().removeAll(Arrays.asList(listeners));
		} else {
			getDisableListenable().remove(listeners[0]);
		}
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
	
	@Override
	public void onEnable() {
		children.forEach(Toggleable::enable);
		enableListenable.forEach(Runnable::run);
	}
	
	@Override
	public void onDisable() {
		disableListenable.forEach(Runnable::run);
		children.forEach(Toggleable::disable);
	}
	
	@NotNull
	@Override
	public EnginePlugin enable() {
		if (!isEnabled()) {
			getPluginManager().enablePlugin(this);
		}
		return this;
	}
	
	@NotNull
	@Override
	public EnginePlugin disable() {
		if (isEnabled()) {
			getPluginManager().disablePlugin(this);
		}
		return this;
	}
	
	@NotNull
	@Override
	public List<Toggleable> getChildren() {
		return children;
	}
	
}