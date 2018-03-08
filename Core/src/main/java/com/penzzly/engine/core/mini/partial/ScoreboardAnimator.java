package com.penzzly.engine.core.mini.partial;

import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.generate;
import static java.util.stream.IntStream.range;
import static org.bukkit.Bukkit.getScheduler;

public class ScoreboardAnimator {
	private final List<Runnable> animations = new ArrayList<>();
	
	public ScoreboardAnimator(Plugin plugin, Number period, TimeUnit unit) {
		getScheduler().runTaskTimer(plugin, () -> {
			animations.forEach(Runnable::run);
		}, 0, unit.toMillis(period.longValue()) / 50);
	}
	
	public void title(MiniScoreboards.Scoreboard board, String title) {
		line(board, null, title);
	}
	
	public void title(MiniScoreboards.Scoreboard board, Iterable<String> states) {
		line(board, null, states);
	}
	
	public void title(MiniScoreboards.Scoreboard board, Supplier<String> states) {
		animate(board, null, states);
	}
	
	public void line(MiniScoreboards.Scoreboard board, Number index, String line) {
		int[] codePoints = line.codePoints().toArray();
		IntSupplier chars = new IntSupplier() {
			int index = 0;
			
			@Override
			public int getAsInt() {
				if (index >= codePoints.length)
					index = 0;
				return codePoints[index++];
			}
		};
		line(board, index, range(0, line.length())
				.mapToObj(i -> new String(generate(chars).skip(i).limit(16).toArray(), 0, 16))
				.collect(toList()));
	}
	
	public void line(MiniScoreboards.Scoreboard board, Number index, Iterable<String> states) {
		animate(board, index, new Supplier<String>() {
			Iterator<String> iterator = states.iterator();
			
			@Override
			public String get() {
				if (!iterator.hasNext())
					iterator = states.iterator();
				return iterator.next();
			}
		});
	}
	
	public void line(MiniScoreboards.Scoreboard board, Number index, Supplier<String> states) {
		animate(board, index, states);
	}
	
	private void animate(MiniScoreboards.Scoreboard board, Number index, Supplier<String> states) {
		animations.add(() -> {
			if (index == null)
				board.title(states.get());
			else
				board.line(index.intValue(), states.get());
		});
	}
}