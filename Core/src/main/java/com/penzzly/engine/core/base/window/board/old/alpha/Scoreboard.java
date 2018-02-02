package com.penzzly.engine.core.base.window.board.old.alpha;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import org.javatuples.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

import static org.javatuples.Pair.with;

public class Scoreboard implements Board {
	private final BiMap<Integer, Line> lines = HashBiMap.create();
	private final Subject<Pair<Integer, String>> linesSubject = PublishSubject.create();
	private final Subject<String> titleSubject = BehaviorSubject.createDefault("Header");
	private Function<Line, Integer> numberingScheme = NUMBERING_SCHEME_DEFAULT;
	
	@NotNull
	public Observable<String> titleObservable() {
		return titleSubject;
	}
	
	@NotNull
	public Observable<Pair<Integer, String>> linesObservable() {
		return linesSubject;
	}
	
	@Nullable
	@Override
	public Line blank() {
		return null;
	}
	
	@NotNull
	@Override
	public BiMap<Integer, Line> lines() {
		return lines;
	}
	
	@NotNull
	@Override
	public Line line(Object defaultValue) {
		Line line = new Line() {
			@NotNull
			@Override
			public Line text(@NotNull Object text) {
				linesSubject.onNext(with(lines.inverse().get(this), text.toString()));
				return this;
			}
		};
		lines.put(numberingScheme.apply(line), line);
		line.accept(defaultValue);
		return line;
	}
	
	@NotNull
	@Override
	public Board remove(int index) {
		linesSubject.onNext(with(index, ""));
		return this;
	}
	
	@NotNull
	@Override
	public Board title(@NotNull Object title) {
		titleSubject.onNext(title.toString());
		return this;
	}
	
	@NotNull
	@Override
	public Board numberingScheme(Function<Line, Integer> numberingScheme) {
		this.numberingScheme = numberingScheme;
		return this;
	}
}