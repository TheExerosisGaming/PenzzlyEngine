package com.penzzly.packages.factions.components;

import com.google.gson.reflect.TypeToken;
import com.penzzly.engine.architecture.base.Component;
import com.penzzly.engine.core.base.configuration.GsonConfiguration;
import com.penzzly.engine.core.components.command.CommandComponent;
import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.User;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.javatuples.Pair;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.*;

import static com.penzzly.engine.core.base.Configurations.jsonConfig;
import static com.penzzly.engine.core.base.Events.listen;
import static java.time.Duration.between;
import static org.bukkit.event.EventPriority.MONITOR;
import static org.bukkit.event.player.PlayerLoginEvent.Result.KICK_BANNED;

public class GrizzlyDeathBanComponent extends Component {
	
	private final GsonConfiguration<Map<UUID, Pair<Integer, Instant>>> BANNED_PLAYERS = addChild(
			jsonConfig(new HashMap<>(), new TypeToken<Map<UUID, Pair<Integer, Instant>>>() {
			}.getType())
	);
	
	private enum Rank {
		CUB(2, 15),
		BRAWN(5, 15),
		BRUTE(8, 15),
		ELITE(10, 15),
		URSA(12, 15),
		GRIZZLY(15, 10);
		
		private int numLives;
		
		private int numMinutes;
		
		private static final Map<String, Rank> RANKS = new HashMap<>();
		
		static {
			for (Rank rank : values()) {
				RANKS.put(rank.name().toLowerCase(), rank);
			}
		}
		
		Rank(int numLives, int numMinutes) {
			this.numLives = numLives;
			this.numMinutes = numMinutes;
		}
		
		public static int getLives(@NotNull String name) {
			Rank rank = RANKS.get(name.toLowerCase());
			
			if (rank == null) {
				return 0;
			}
			
			return rank.numLives;
		}
		
		public static int getMinutes(@NotNull String name) {
			Rank rank = RANKS.get(name.toLowerCase());
			
			if (rank == null) {
				return 30;
			}
			
			return rank.numMinutes;
		}
	}
	
	public GrizzlyDeathBanComponent(@NotNull CommandComponent commands) {
		// Command to give lives
		// Command to revive (2-hour cooldown) w/ permission
		
		/*
		 * Ex. /revive av0
		 *
		 * Revives a player (removes their death-ban).
		 */
		commands.onCommand("revive"::equals, (sender, args) -> {
			//TODO Add 2-hour cooldown.
			
			if (!(sender instanceof ConsoleCommandSender) && !sender.hasPermission("core.revive")) {
				sender.sendMessage("You do not have the permission: core.revive");
				return;
			}
			
			args.as(OfflinePlayer.class).ifPresent(player -> {
				Map<UUID, Pair<Integer, Instant>> players = BANNED_PLAYERS.get();
				
				players.computeIfPresent(player.getUniqueId(), (uuid, pair) -> {
					sender.sendMessage(player.getName() + " is now revived!");
					
					return new Pair<>(pair.getValue0(), Instant.now());
				});
			});
		});
		
		/*
		 * Ex. /givelives 10 av0
		 *
		 * Gives a specific amount of lives to a player.
		 */
		commands.onCommand("givelives"::equals, (sender, args) -> {
			if (!(sender instanceof ConsoleCommandSender) && !sender.hasPermission("core.givelives")) {
				sender.sendMessage("You do not have the permission: core.givelives");
				return;
			}
			
			args.as(Integer.class).ifPresent(lives -> {
				Map<UUID, Pair<Integer, Instant>> players = BANNED_PLAYERS.get();
				
				args.as(OfflinePlayer.class).ifPresent(player -> {
					players.computeIfPresent(player.getUniqueId(), (uuid, pair) -> {
						sender.sendMessage(String.format(player.getName() + " now has %d lives!",
								pair.getValue0() + lives));
						
						return new Pair<>(pair.getValue0() + lives, pair.getValue1());
					});
				});
			});
		});
		
		/*
		 * When a player logs in, we want to add their amount
		 * of lives to a database depending on their rank if
		 * they aren't already added.
		 */
		addChild(listen((PlayerLoginEvent event) -> {
			String rank = LuckPerms.getApi()
					.getUserSafe(event.getPlayer().getUniqueId())
					.map(User::getPrimaryGroup)
					.orElse("");
			
			BANNED_PLAYERS.get().putIfAbsent(event.getPlayer().getUniqueId(), new Pair<>(Rank.getLives(rank), Instant.now()));
			
			BANNED_PLAYERS.get().computeIfPresent(event.getPlayer().getUniqueId(), (uuid, pair) -> {
				if (pair.getValue1().isBefore(Instant.now())) {
					return pair;
				}
				
				event.disallow(KICK_BANNED, ChatColor.RED + "You are death-banned for " + between(Instant.now(), pair.getValue1()).getSeconds() + " second(s)!");
				
				return pair;
			});
		}));
		
		/*
		 * When a player dies, we want to check if they
		 * have any more lives.  If they do, decrement
		 * the value.  Otherwise, ban them for a specific
		 * amount of time, depending on their rank.
		 */
		addChild(listen(MONITOR, (PlayerDeathEvent event) -> {
			Player player = event.getEntity();
			
			Arrays.stream(player.getInventory().getContents())
					.filter(Objects::nonNull)
					.filter(item -> item.getType() != Material.AIR)
					.forEach(player.getInventory()::remove);
			
			player.getInventory().setHelmet(null);
			player.getInventory().setChestplate(null);
			player.getInventory().setLeggings(null);
			player.getInventory().setBoots(null);
			
			BANNED_PLAYERS.get().compute(player.getUniqueId(), (uuid, pair) -> {
				return new Pair<>(Math.max(0, pair.getValue0() - 1), pair.getValue0() == 0 ? banPlayer(player) : Instant.now());
			});
		}));
	}
	
	private Instant banPlayer(@NotNull Player player) {
		int minutes = Rank.getMinutes(LuckPerms.getApi()
				.getUserSafe(player.getUniqueId())
				.map(User::getPrimaryGroup)
				.orElse(""));
		
		player.kickPlayer(ChatColor.RED + "You have been death-banned! Come back in " + minutes + " minutes!");
		
		return Instant.now().plusSeconds(minutes * 60);
	}
	
}