package com.penzzly.engine.core.base.window;

import com.penzzly.engine.architecture.base.Component;
import com.penzzly.engine.architecture.holder.mutable.MutableHolder;
import io.reactivex.Observable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static java.util.Arrays.asList;

public class Display extends Component implements com.penzzly.engine.architecture.functions.compat.Consumer<Player> {
	private final List<Player> viewers = new ArrayList<>();
	private final List<Consumer<Player>> showListeners = new ArrayList<>();
	private final List<Consumer<Player>> hideListeners = new ArrayList<>();
	
	public Display() {
	}
	
	public Display(Consumer<Player> onShow, Consumer<Player> onHide) {
		showListeners.add(onShow);
		hideListeners.add(onHide);
	}
	
	@NotNull
	@SafeVarargs
	public final Display onShow(Consumer<Player>... listeners) {
		showListeners.addAll(asList(listeners));
		return this;
	}
	
	@NotNull
	@SafeVarargs
	public final Display onHide(Consumer<Player>... listeners) {
		hideListeners.addAll(asList(listeners));
		return this;
	}
	
	@Override
	public void accept(Player player) {
		if (viewers.contains(player)) {
			hideTo(player);
		} else {
			showTo(player);
		}
	}
	
	public void showTo(Player player) {
		showListeners.forEach(listener -> listener.accept(player));
	}
	
	public void hideTo(Player player) {
		hideListeners.forEach(listener -> listener.accept(player));
	}
	
	public void showTo(@NotNull Observable<Player> players) {
		players.subscribe(this::showTo);
	}
	
	public void hideTo(@NotNull Observable<Player> players) {
		players.subscribe(this::hideTo);
	}
	
	public void showFor(@NotNull MutableHolder<Player> players) {
		players.forEach(this::showTo);
		players.onAdd(this::showTo);
		players.onRemove(this::hideTo);
	}
	
	@NotNull
	public List<Player> getViewers() {
		return viewers;
	}
}
