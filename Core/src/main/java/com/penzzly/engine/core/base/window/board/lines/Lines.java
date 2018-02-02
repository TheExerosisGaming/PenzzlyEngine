package com.penzzly.engine.core.base.window.board.lines;

import com.penzzly.engine.core.base.window.elements.Element;
import io.reactivex.Observable;
import org.jetbrains.annotations.NotNull;

import static com.penzzly.engine.architecture.utilites.Clarifiers.Text;

public interface Lines extends Element<Lines> {
	@NotNull Lines line(@Text Object line);
	
	@NotNull Lines line(@Text Observable<Object> line);
}