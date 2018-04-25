package com.penzzly.packages.factions.components.tutorial;

import com.penzzly.engine.architecture.functions.compat.Function;
import com.penzzly.engine.architecture.functions.compat.Predicate;
import com.penzzly.engine.architecture.holder.mutable.MutableHolder;
import com.penzzly.engine.core.components.EnginePlugin;
import com.penzzly.engine.core.components.player.VoidLevelComponent;
import com.penzzly.engine.core.utilites.holder.OnlinePlayerHolder;
import io.reactivex.Observable;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

import static com.comphenix.protocol.utility.BukkitUtil.getOnlinePlayers;
import static com.penzzly.engine.core.base.Events.listen;
import static com.penzzly.engine.core.utilites.functions.Functions.PLAYER;
import static io.reactivex.Observable.just;

/*
 * __cut__
 * it will serve to show of the primary functions of the engine and how to use them.
 * The biggest thing that it does not teach, is knowing when to component
 * and when not to. That simply means when to take game specific functions, and move
 * them into a more general component that can be used in other games. That means there is an
 * unlabeled difference between types of components. They are either:
 * General:
 *    A general component can have a number of levels of generalness to it. Or may simply
 *    not be in a final state yet. For example, a HeadGodAppleComponent that adds the player
 *    head god apple recipe to the game could also have simply been a crafting recipe component.
 *    however I decided it was too simple to be worth it.
 * Specific:
 *    A specific component is one that contains direct primitives/data that are set for a specific server.
 *    For example, I made GrizzlyHorseComponent, at first it contained keeping unique horses and applying
 *    effects when players were on them. I decided that HorseOwnership was general, so I split that off
 *    now GrizzlyHorseComponent applies effects for durations specified by Mist himself.
 *
 * Whenever possible specific components should be split into a general specific pair.
 * That is the most complex and difficult thing about the engine, and not something that can easily be
 * explained with examples.
 */
public class TutorialGame extends EnginePlugin {
	
	/**
	 * Basically everything happens in the constructor, that's why callbacks are important.
	 * General components are more likely to have fields for getters and setters.
	 */
	public TutorialGame(@NotNull Predicate<String> playersInGame) {
		//Each component has 2 basic functions, enable and task. In the case of EnginePlugin
		//calling enable or task manually enables or disables the bukkit plugin.
		//Each component also has the ability to listen to when it's enabled and disabled.
		onEnable(() -> {
			System.out.println("Normally things getOrSet setup in here.");
		});
		onDisable(() -> {
			System.out.println("Normally cleanup happens here.");
		});
		
		//This is a very basic system and should be quite simple to understand, in an EnginePlugin
		//these callbacks function just as overriding onEnable and onDisable do.
		
		
		//The next basic method is the addChild method it takes another component and adds it
		//to a list of children.
		//Children getOrSet enabled and disabled whenever their parent does.
		//This is most commonly used to enable and task events or schedulers.
		//Forgetting to add a component as a child will likely cause memory leaks or unexpected
		//things to happen, avoid it!
		
		//One of the most common children is the EventComponent it registers as a listener and
		//allows you to receive bukkit events.
		//There are three ways to use the Events class.
		//1. Type specific callbacks.
		//3. Type inferred anonymous inner callbacks.
		//2. RxJava Observables.
		
		//This is an example of a type specific callback on an event. This requires the class
		//type as an argument. It's the more safe of the two callback options and will never fail.
		addChild(listen(PlayerJoinEvent.class, event -> {
			event.getPlayer().sendMessage("This is simply a tutorial!");
		}));
		
		//This is the inferred callback. It's easier on the eyes and will always work as long as
		//the event listener is an anonymous inner class(lambdas included obviously).
		//It has been disabled for a long time which is why it's not the current go to.
		//If it fails it will simply throw an exception and you will then know to switch.
		//It's best to try this before falling back to specific type args.
		addChild(listen((PlayerJoinEvent event) -> {
			event.getPlayer().sendMessage("This is simply a tutorial!");
		}));
		
		//This is the RxJava option, instead of returning an EventComponent it returns an
		//ObservableComponent, which is an Observable of the specified type. There is far more
		//power here however it's more complex and not very good for simple cases like this.
		addChild(listen(PlayerJoinEvent.class))
				.map(PLAYER)
				.subscribe(player -> player.sendMessage("This is simply a tutorial!"));
		
		
		//The addChild method is also how you piece together components you have created.
		Collection<Player> players = getOnlinePlayers();
		addChild(new VoidLevelComponent(players, 10));
		
		//Components take in parameters that configure their scope and actions via the constructor.
		//in this case we told the void level component to work on all online players, and
		//to kill players who are below level 10. The effect will remain until the parent disables.
		
		//Next we have Holders, they are a special datatype that can be used to define a scope without
		//any memory footprint. Each holder is both an Iterable<T> and a Predicate<T> the predicate test
		//method returns true if the Holder contains the specified item.
		//There are two primary types of Holders.
		//1. Holder: A holder is simply iterated over or tested for contains.
		//2. MutableHolder: This type of holder has methods add, and remove. As well as
		//   onAdd(item -> {}) and onRemove(item -> {}). These methods are used to
		//   track changes to the holders and apply actions when things are added or removed.
		
		
		//This holder is an OnlinePlayerHolder, which automatically keeps track of OnlinePlayers
		//and updates itself.
		MutableHolder<Player> allPlayers = new OnlinePlayerHolder();
		
		//Holders can have operations applied to them in two modes.
		//1. Live: Live holders have no memory footprint, but take longer to iterate beacuse
		//   they call back to the last cached holder.
		//2. Cached: Cached holders keep a list of players for each operation, but can take a large
		//   amount of memory and a long time to perform array copies.
		
		MutableHolder<Player> gamePlayers = allPlayers
				.live()
				.partition(player -> playersInGame.apply(player.getName()));
		
		//This operation split our holder into two holders one only contains players in the game.
		//The other contains every player on the server.
		
		//In this case a cached holder would be faster, because it's likely we will iterate over
		//this holder a lot, and it won't change that frequently.
		gamePlayers = allPlayers
				.cached()
				.partition(player -> playersInGame.apply(player.getName()));
		
		//Because OnlinePlayerHolder is a MutableHolder we can track changes to our game players easily.
		//There are two modes for using this, callback and rx java(same with events).
		gamePlayers.onAdd(player -> {
			player.sendMessage("Welcome to the game!");
		});
		
		gamePlayers.onRemove(player -> {
			player.sendMessage("Hope you had fun!");
		});
		
		//Ask me more about holders if you need to know, they have some intricate features.
		//As an example for those who know rxjava we will mock up an example playerdata call.
		Function<Player, Observable<String>> getPlayerData = player ->
				just("PlayerData for: " + player.getName());
		
		gamePlayers.onAdd().flatMap(getPlayerData).subscribe(data -> {
			System.out.println("Loaded data: " + data);
		});
		
		//This is our example phase, however phases can be chained together by
		//enabling one when the first completes and so on. Just don't forget to task them all
		//when the parent disables. (Ask me for a trick to make this simpler)
		addChild(new TutorialPhase(allPlayers.size()));
		
		//This is the very basics of the engine... I may be missing some things but it's enough to
		//understand the general concept.
	}
}
