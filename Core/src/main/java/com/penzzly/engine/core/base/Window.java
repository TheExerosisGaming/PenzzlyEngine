package com.penzzly.engine.core.base;


import com.penzzly.engine.core.base.window.Display;
import com.penzzly.engine.core.base.window.Screen;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import static com.google.common.collect.Lists.partition;
import static org.bukkit.Material.ARROW;

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
	
	/*public static void main(String[] args) {
		final String closeMessage = "Awww, looks like {0} stopped looking at the inventory.";
		final String openMessage = "Looks like {0} popped open your inventory!";
		final String clickMessage = "Sweet! Looks like {0} {1} clicked on an {3}";

		Display testDisplay = like((screen, player) -> {
			Transaction firstPage = screen.page(page -> {
				page.onEnable(() ->
						System.out.println(format(closeMessage, player.getName()))
				);
				page.onClose(() ->
						System.out.println(format(openMessage, player.getName()))
				);
				page.element()
						.title("Test Item")
						.icon(Material.APPLE)
						.amount(3)
						.text("Lore,", "Lore,", "Lore!")
						.onClick(type ->
								System.out.println(format(clickMessage,
										player.getName(),
										type.toString().replace('_', ' ').toLowerCase(),
										"apple")
								)
						);
			});

			
		});

		addChild(listen((PlayerJoinEvent event) -> testDisplay.showTo(event.getPlayer())));
	}*/
}