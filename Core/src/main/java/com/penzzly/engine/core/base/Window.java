package com.penzzly.engine.core.base;


import com.penzzly.engine.core.base.window.Display;
import com.penzzly.engine.core.base.window.Screen;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public interface Window {
	Map<Player, Screen> screens = new HashMap<>();
	
	static Display like(@NotNull BiConsumer<Screen, Player> description) {
		return new Display(player ->
				description.accept(screens.computeIfAbsent(player, Screen::new)
						.enable(), player), player -> screens.get(player).disable());
	}
	
/*	public static void main(String[] args) {
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

			firstPage.disableEagerly().addToBackstack().commitNow();
		});

		addChild(listen((PlayerJoinEvent event) -> testDisplay.showTo(event.getPlayer())));
	}*/
}