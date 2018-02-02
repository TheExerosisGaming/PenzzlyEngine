package com.penzzly.engine.core.base.window.header;

import com.penzzly.engine.core.base.window.BaseElement;
import io.reactivex.Observable;
import io.reactivex.subjects.Subject;
import org.jetbrains.annotations.NotNull;

import static com.penzzly.engine.architecture.utilites.Clarifiers.Integer;
import static com.penzzly.engine.architecture.utilites.Clarifiers.*;
import static io.reactivex.subjects.BehaviorSubject.createDefault;

public abstract class RxHeader extends BaseElement<Header> implements Header {
	protected int fadeIn = 250;
	protected int stay = 1000;
	protected int fadeOut = 250;
	protected final Subject<Object> textSubject = createDefault("Text");
	protected final Subject<Object> subtextSubject = createDefault("Subtext");
	
	//--Text--
	@NotNull
	@Override
	public Header text(@Text Object text) {
		textSubject.onNext(text);
		return this;
	}
	
	@NotNull
	@Override
	public Header text(@NotNull @Text Observable<Object> text) {
		text.subscribe(textSubject);
		return this;
	}
	
	//--Subtext--
	@NotNull
	@Override
	public Header subtext(@Text Object text) {
		subtextSubject.onNext(text);
		return this;
	}
	
	@NotNull
	@Override
	public Header subtext(@NotNull @Text Observable<Object> text) {
		text.subscribe(subtextSubject);
		return this;
	}
	
	//--In Out Stay--
	@NotNull
	@Override
	public Header fade(@NotNull @Millis @Integer Number in, @NotNull @Millis @Integer Number stay, @NotNull @Millis @Integer Number out) {
		fadeIn = in.intValue();
		this.stay = stay.intValue();
		fadeOut = out.intValue();
		return this;
	}
	
	//--In Out--
	@NotNull
	@Override
	public Header fade(@NotNull @Millis @Integer Number in, @NotNull @Millis @Integer Number out) {
		fadeIn = in.intValue();
		fadeOut = out.intValue();
		return this;
	}
	
	//--In--
	@NotNull
	@Override
	public Header fadeIn(@NotNull @Millis @Integer Number duration) {
		fadeIn = duration.intValue();
		return this;
	}
	
	//--Out--
	@NotNull
	@Override
	public Header fadeOut(@NotNull @Millis @Integer Number duration) {
		fadeOut = duration.intValue();
		return this;
	}
	
	@NotNull
	@Override
	public Header stay(@NotNull @Millis @Integer Number duration) {
		stay = duration.intValue();
		return this;
	}
}
