package com.penzzly.engine.core.mini;

import com.penzzly.engine.architecture.base.Component;
import com.penzzly.engine.architecture.functions.compat.Function;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.subjects.BehaviorSubject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.redisson.Redisson;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.penzzly.engine.core.base.Events.listen;
import static io.reactivex.Single.fromFuture;
import static java.util.stream.Collectors.joining;

public class Main extends Component {
	//Lets just say that level is the square root of exp.
	public static final Function<Long, Integer> LEVELING_FUNCTION = exp -> (int) Math.sqrt(exp);
	public static final Data DEFAULT_DATA = new Data() {
		@Override
		public Flowable<Integer> coins() {
			return BehaviorSubject.createDefault(0)
					.toFlowable(BackpressureStrategy.LATEST);
		}
		
		@Override
		public Flowable<Long> exp() {
			return BehaviorSubject.createDefault(0L)
					.toFlowable(BackpressureStrategy.LATEST);
		}
		
		@Override
		public Flowable<Set<UUID>> friends() {
			return BehaviorSubject.<Set<UUID>>createDefault(new HashSet<>())
					.toFlowable(BackpressureStrategy.LATEST);
		}
	};
	
	interface Data {
		Flowable<Integer> coins();
		
		Flowable<Long> exp();
		
		Flowable<Set<UUID>> friends();
		
		default Flowable<Integer> level() {
			return exp().map(LEVELING_FUNCTION);
		}
	}
	
	static class Database {
		final RedissonClient redis = Redisson.create();
		final RMap<UUID, Data> data = redis.getMap("Data");
		
		public Single<Data> getPlayerData(Player player) {
			return fromFuture(data.putIfAbsentAsync(player.getUniqueId(), DEFAULT_DATA));
		}
	}
	
	public static Function<Object, String> format(String format) {
		return value -> String.format(format, value);
	}
	
	
	public Main() {
		final Database database = new Database();
		addChild(listen(PlayerJoinEvent.class))
				.map(PlayerEvent::getPlayer)
				.subscribe(player -> {
					final Flowable<Data> data = database.getPlayerData(player)
							.toFlowable()
							.replay(1)
							.autoConnect();
					
					//Add Coins Exp and Level to the scoreboard
					//those values should always show the most recent count.
					RxScoreboard board = new RxTeamsScoreboard();
					board.line(
							data.flatMap(Data::coins)
									.map(format("Coins: %s"))
					);
					board.line(
							data.flatMap(Data::exp)
									.map(format("Exp: %s"))
					);
					board.line(
							data.flatMap(Data::level)
									.map(format("Level: %s"))
					);
					player.setScoreboard(board.getScoreboard());
					
					//Add a friends command that displays a list(seperated by commas)
					//of the senders current friends.
					addChild(command("friends"::equals, player))
							.switchMapSingle(args -> data
									.flatMap(Data::friends)
									.singleOrError()
							)
							.map(friends -> friends.stream()
									.map(Bukkit::getPlayer)
									.map(Player::getName)
									.collect(joining(", "))
							).forEach(player::sendMessage);
				});
		
	}
}
