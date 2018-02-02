package com.penzzly.engine.core.base.window.footer;

import com.penzzly.engine.architecture.utilites.Clarifiers;
import com.penzzly.engine.core.base.window.BaseElement;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import org.jetbrains.annotations.NotNull;

import static io.reactivex.subjects.BehaviorSubject.createDefault;

public abstract class RxFooter extends BaseElement<Footer> implements Footer {
	protected final BehaviorSubject<Object> textSubject = createDefault("Text");
	protected final BehaviorSubject<Object> subtextSubject = createDefault("Text");
	
	//--Text--
	@NotNull
	@Override
	public Footer text(@Clarifiers.Text Object text) {
		textSubject.onNext(text);
		return this;
	}
	
	@NotNull
	@Override
	public Footer text(@NotNull @Clarifiers.Text Observable<Object> text) {
		text.subscribe(textSubject);
		return this;
	}
	
	//--Subtext--
	@NotNull
	@Override
	public Footer subtext(@Clarifiers.Text Object text) {
		subtextSubject.onNext(text);
		return this;
	}
	
	@NotNull
	@Override
	public Footer subtext(@NotNull @Clarifiers.Text Observable<Object> text) {
		text.subscribe(subtextSubject);
		return this;
	}
}