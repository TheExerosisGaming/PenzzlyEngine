package com.penzzly.engine.core.base.window.elements;

import com.penzzly.engine.architecture.utilites.Clarifiers;
import io.reactivex.Observable;
import org.jetbrains.annotations.NotNull;

public interface Text<Type extends Text> extends Element<Type> {
	//--Text--
	@NotNull Type text(@Clarifiers.Text Object text);
	
	@NotNull Type text(@Clarifiers.Text Observable<Object> text);
	
	
	//--Subtext--
	@NotNull Type subtext(@Clarifiers.Text Object text);
	
	@NotNull Type subtext(@Clarifiers.Text Observable<Object> text);
}