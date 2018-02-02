package com.penzzly.engine.architecture.functions.compat;

import com.penzzly.engine.architecture.functions.Optional;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import org.jetbrains.annotations.NotNull;

import static com.penzzly.engine.architecture.functions.Optional.of;
import static io.reactivex.Observable.unsafeCreate;

public interface Supplier<Type> extends java.util.function.Supplier<Type>, ObservableSource<Type> {
	@NotNull
	default Optional<Type> getOptional() {
		return of(get());
	}
	
	@Override
	default void subscribe(@NotNull Observer<? super Type> observer) {
		try {
			observer.onNext(get());
		} catch (Exception exception) {
			observer.onError(exception);
		} finally {
			observer.onComplete();
		}
	}
	
	default Observable<Type> observe(@NotNull Scheduler scheduler) {
		return unsafeCreate(this).subscribeOn(scheduler);
	}
	
	default Observable<Type> observe() {
		return unsafeCreate(this);
	}
}
