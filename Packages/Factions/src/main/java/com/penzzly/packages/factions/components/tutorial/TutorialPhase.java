package com.penzzly.packages.factions.components.tutorial;

import com.penzzly.engine.architecture.base.Phase;
import org.bukkit.event.entity.PlayerDeathEvent;

import static com.penzzly.engine.core.base.Events.listen;
import static com.penzzly.engine.core.base.Scheduler.in;

//Next up we have phases. A phase is like a component however
//unlike a component it can task itself. To avoid confusion by practice it cannot actually
//task itself, instead it calls onComplete. When a Phase calls onComplete you should always follow
//that call with phase.task().
//If you don't need to do processing onComplete you can instead call autoDisable() to task the phase
//when it completes automatically.
public class TutorialPhase extends Phase {
	private int players;
	
	public TutorialPhase(int players) {
		this.players = players;
		//when creating a phase you need to decide when the phase is over and then call complete();
		//here is a basic example:
		
		//This listener will listen to the players dying. Once nobody is left the phase completes itself.
		addChild(listen((PlayerDeathEvent event) -> {
			if (--this.players <= 0) {
				complete();
			}
		}));
		
		//The Scheduler is another basic component. However the Scheduler itself is a Phase.
		//Unlike ALL other phases it auto disables by default, and has a run method that is run onComplete.
		
		//As an alternative the phase will end when the time runs out.
		addChild(in(30).minutes().run(this::complete));
	}
}
