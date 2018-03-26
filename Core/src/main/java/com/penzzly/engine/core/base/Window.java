package com.penzzly.engine.core.base;


import com.penzzly.engine.core.base.window.Display;
import com.penzzly.engine.core.base.window.Screen;
import com.penzzly.engine.core.base.window.Transaction;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import static com.google.common.collect.Lists.partition;
import static com.penzzly.engine.core.base.Events.listen;
import static org.bukkit.Material.*;

public interface Window {
	Map<Player, Screen> screens = new HashMap<>();
	
	static Display like(@NotNull BiConsumer<Screen, Player> description) {
		return new Display(player ->
				description.accept(screens.computeIfAbsent(player, Screen::new)
						.enable(), player), player -> screens.get(player).disable());
	}
	
	static void main(String[] args) {
		List<String> items = new ArrayList<>();
		int pageSize = 7;
		Window.like((screen, player) ->
				partition(items, pageSize).forEach(chunk -> screen.page(page -> {
					//Add a back arrow which pops the backstack once.
					page.element()
							.icon(ARROW)
							.title("Back")
							.onClick(() -> screen.page().popBack());
					
					//Add that batch of items to the middle of the page
					chunk.forEach(item -> page.element().title(item));
					
					//Add a next arrow which pops the fore stack
					//ie. whatever page is next in the queue.
					page.element()
							.icon(ARROW)
							.title("Next")
							.onClick(() -> screen.page().popFore());
					
					//Commit all the pages, which opens the first and queues all the rest.
				}).commit()).var
		);
	}
	
	static void main(String[] args) {
		Display testDisplay = like((screen, player) -> {
			screen.page(page -> {
				page.element()
						.title("Go Forward!")
						.icon(APPLE)
						.amount(3)
						.text("Lore", "Lore", "Lore")
						.onClick(screen.page()::popFore);
				
				page.element()
						.title("Close!")
						.icon(BARRIER)
						.onClick(player::closeInventory);
			}).commitClearing();
			
			Transaction secondPage = screen.page(page -> page.element()
					.title("Go Back!")
					.icon(BARRIER)
					.onClick(screen.page()::popBack)
			).commit();
		});
		
		listen((PlayerJoinEvent event) -> testDisplay.showTo(event.getPlayer()));
	}
}