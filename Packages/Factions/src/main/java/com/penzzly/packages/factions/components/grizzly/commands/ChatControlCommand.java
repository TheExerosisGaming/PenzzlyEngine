package com.penzzly.packages.factions.components.grizzly.commands;

import com.penzzly.engine.architecture.base.Component;
import com.penzzly.engine.core.components.command.CommandComponent;
import com.penzzly.engine.core.utilites.time.Duration;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.Subject;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

import static com.penzzly.engine.core.utilites.time.Duration.For;
import static io.reactivex.subjects.BehaviorSubject.createDefault;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.bukkit.ChatColor.RED;

public class ChatControlCommand extends Component {
	@NotNull
	private final Subject<Duration> durations;
	private boolean muted = false;
	
	public ChatControlCommand(@NotNull CommandComponent commands, @NotNull Duration defaultChatSpeed) {
		durations = createDefault(For(1, SECONDS));
		commands.onCommand("chat"::equals, (player, args) ->
				args.next().ifPresentOr(arg -> {
					if (arg.contains("slow")) {
						durations.onNext(args.or(defaultChatSpeed));
					} else if (arg.contains("mute")) {
						muted ^= true;
					} else {
						durations.onNext(defaultChatSpeed);
						muted = false;
					}
				}, () -> {
					//TODO pop up chat utils menu.
				}));
		
		Consumer<AsyncPlayerChatEvent> limited = event -> {
			event.setCancelled(true);
			event.getPlayer().sendMessage(RED + "You are being rate limited!");
		};

//		addChild(listen(AsyncPlayerChatEvent.class, HIGHEST))
//				.compose(new Debouncer<>(player(), limited, durations))
//				.filter(ignored -> muted)
//				.subscribe(limited);
	}
}
