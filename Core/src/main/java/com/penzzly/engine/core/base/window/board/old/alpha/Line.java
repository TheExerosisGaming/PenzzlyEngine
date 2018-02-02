package com.penzzly.engine.core.base.window.board.old.alpha;

import com.penzzly.engine.architecture.functions.compat.Consumer;
import org.jetbrains.annotations.NotNull;

public interface Line extends Consumer<Object> {
	@NotNull Line text(Object text);
	
	@Override
	default void accept(Object text) {
		text(text);
	}
}