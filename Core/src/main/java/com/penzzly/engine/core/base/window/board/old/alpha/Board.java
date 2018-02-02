package com.penzzly.engine.core.base.window.board.old.alpha;

import com.google.common.collect.BiMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

public interface Board {
	Function<Line, Integer> NUMBERING_SCHEME_DEFAULT = new Function<Line, Integer>() {
		int count = 100;
		
		@NotNull
		@Override
		public Integer apply(Line line) {
			return count--;
		}
	};
	
	default Optional<Integer> indexOf(Line line) {
		return Optional.ofNullable(lines().inverse().get(line));
	}
	
	@NotNull
	default Board remove(Line line) {
		indexOf(line).ifPresent(this::remove);
		return this;
	}
	
	BiMap<Integer, Line> lines();
	
	@Nullable Line blank();
	
	Line line(Object text);
	
	Board remove(int index);
	
	Board title(Object title);
	
	Board numberingScheme(Function<Line, Integer> numberingScheme);
}