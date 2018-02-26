package com.penzzly.engine.core.components.unorganized;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.observables.ConnectableObservable;

import java.util.concurrent.locks.ReentrantLock;

class EventManager {
	private static final ReentrantLock mutex = new ReentrantLock();
	//Oh the abuse!!!
	private static Object currentEvent = null;
	
	//If this is called from within the subscription to an event... than no matter how much mapping you have done...
	//This should still be the event you are listening to... which you can promptly cancel 
	//or get more data from for more complex maps.
	public static <Event> Event event() {
		return (Event) currentEvent;
	}
	
	//Best to subclass event and throw UnsupportedOpperationExcepetion of someone tries to
	//change the scheduler the event is being run on. I'm not 100% sure if that's required... but I'm also not sure
	//what would happen if you changed the working thread.
	public static <Event> ObservableTransformer<Event, Event> fire() {
		return upstream -> {
			//Multicast upstream b/c we don't really want to propagate subscriptions past this point.
			ConnectableObservable<Event> events = upstream.publish();
			events.connect(); //Normally we would handle this disposeable along with our sub to events.
			
			return Observable.create(observer -> events.subscribe(event -> {
				//Note: It's obviously best to multicast `upstream` before subscribing.
				//If another event is firing we are gonna need to just wait.
				//If not we can take the lock to prevent other events from being fired until we are done.
				mutex.lockInterruptibly();
				
				//Once that event is done, we can 
				currentEvent = event;
				
				//Now we can fire the event to any listeners.
				observer.onNext(event);
				
				//At this point it should be safe to assume any calls to the static 'event()' have been made.
				//So we allow the next event to take the lock. (I'm fairly sure it's not possible for this to go screwy...)
				mutex.unlock();
			}));
		};
	}
}