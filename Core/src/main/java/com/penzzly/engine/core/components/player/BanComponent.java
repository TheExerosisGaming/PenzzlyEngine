package com.penzzly.engine.core.components.player;

import com.penzzly.engine.architecture.base.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import static com.penzzly.engine.architecture.utilites.Clarifiers.Millis;
import static com.penzzly.engine.core.base.Events.listen;
import static java.lang.System.currentTimeMillis;
import static org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result.KICK_BANNED;

//TODO update this to handle a ban history and all that crap at somepoint.
public class BanComponent extends Component {
	private final List<BiConsumer<UUID, BanInfo>> kickListeners = new ArrayList<>();
	@NotNull
	private final Map<UUID, BanInfo> bans;
	
	public BanComponent(@NotNull Map<UUID, BanInfo> bans) {
		this.bans = bans;
		addChild(listen((AsyncPlayerPreLoginEvent event) ->
				bans.computeIfPresent(event.getUniqueId(), (uuid, info) -> {
					if (info.end < currentTimeMillis()) {
						return null;
					}
					kickListeners.forEach(listener -> listener.accept(uuid, info));
					event.disallow(KICK_BANNED, info.reason);
					return info;
				}))
		);
	}
	
	@NotNull
	public BanComponent pardonIf(@NotNull OfflinePlayer player, @NotNull Predicate<BanInfo> filter) {
		return pardonIf(player.getUniqueId(), filter);
	}
	
	@NotNull
	public BanComponent pardonIf(UUID player, @NotNull Predicate<BanInfo> filter) {
		BanInfo banInfo = bans.get(player);
		if (banInfo != null && filter.test(banInfo)) {
			pardon(player);
		}
		return this;
	}
	
	@NotNull
	public BanComponent pardon(@NotNull OfflinePlayer player) {
		return pardon(player.getUniqueId());
	}
	
	@NotNull
	public BanComponent pardon(UUID player) {
		bans.remove(player);
		return this;
	}
	
	@NotNull
	public BanComponent ban(UUID player, String reason, @NotNull Date end) {
		bans.put(player, new BanInfo(reason, end));
		return this;
	}
	
	@NotNull
	public BanComponent ban(@NotNull OfflinePlayer player, String reason, @NotNull Date end) {
		bans.put(player.getUniqueId(), new BanInfo(reason, end));
		if (player.isOnline()) {
			player.getPlayer().kickPlayer(reason);
		}
		return this;
	}
	
	public BiConsumer<UUID, BanInfo> onKick(BiConsumer<UUID, BanInfo> listener) {
		kickListeners.add(listener);
		return listener;
	}
	
	@NotNull
	public List<BiConsumer<UUID, BanInfo>> getKickListeners() {
		return kickListeners;
	}
	
	public static class BanInfo {
		@Nullable
		private final String reason;
		@Millis
		private final Long start;
		@NotNull
		@Millis
		private final Long end;
		
		BanInfo(@Nullable String reason, @NotNull Date end) {
			this(reason, end.getTime());
		}
		
		BanInfo(@Nullable String reason, @NotNull @Millis Long end) {
			this.reason = reason;
			this.start = currentTimeMillis();
			this.end = end;
		}
		
		@Nullable
		public String getReason() {
			return reason;
		}
		
		@NotNull
		@Millis
		public Long getStart() {
			return start;
		}
		
		@NotNull
		@Millis
		public Long getEnd() {
			return end;
		}
	}
}