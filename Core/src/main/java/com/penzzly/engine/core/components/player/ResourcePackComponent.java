/*package com.penzzly.engine.core.components.player;

import com.penzzly.engine.architecture.base.Component;
import com.penzzly.engine.core.base.Events;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;

public class ResourcePackComponent extends Component {
    private final List<BiConsumer<Player, Status>> statusListeners = Collections.synchronizedList(new ArrayList<>());
    private final String pack;
	
	public ResourcePackComponent(String pack, @NotNull Predicate<Player> players) {
		this.pack = pack;
        onEnable(() -> {
            Bukkit.getOnlinePlayers().stream().filter(players).forEach(player -> player.setResourcePack(pack));
        });
        addChild(Events.listen(PlayerJoinEvent.class, event -> {
            if (players.test(event.getPlayer()))
                event.getPlayer().setResourcePack(pack);
        }));
        addChild(Events.listen(PlayerResourcePackStatusEvent.class, event ->
                statusListeners.forEach(listener -> listener.accept(event.getPlayer(), event.getStatus()))));
    }

    public BiConsumer<Player, Status> onStatus(BiConsumer<Player, Status> listener) {
        statusListeners.add(listener);
        return listener;
    }
	
	@NotNull
	public List<BiConsumer<Player, Status>> getStatusListeners() {
		return statusListeners;
	}
	
	public BiConsumer<Player, Status> givePack(@NotNull Player target, @NotNull Consumer<Status> callback) {
		BiConsumer<Player, Status> listener = onStatus(new BiConsumer<Player, Status>() {
            @Override
            public void accept(@NotNull Player player, Status status) {
                if (!player.equals(target))
                    return;
                callback.accept(status);
                ResourcePackComponent.this.getStatusListeners().remove(this);
            }
        });
        target.setResourcePack(pack);
        return listener;
    }
}*/
