package com.penzzly.engine.core.utilites;

import org.apache.commons.lang.text.StrLookup;
import org.apache.commons.lang.text.StrSubstitutor;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.lang.Math.max;
import static java.util.regex.Pattern.compile;
import static java.util.stream.Collector.of;
import static org.bukkit.Bukkit.getScheduler;

public class StringUtil {
	
	public static Collector<Integer, ?, String> codePointsToString() {
		return of(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append, StringBuilder::toString);
	}
	
	public static Collector<Character, ?, String> charsToString() {
		return of(StringBuilder::new, StringBuilder::append, StringBuilder::append, StringBuilder::toString);
	}
	
	public static int LCSDistance(@NotNull String first, @NotNull String second) {
		int m = first.length();
		int n = second.length();
		int[][] dp = new int[m + 1][n + 1];
		
		for (int i = 0; i <= m; i++) {
			for (int j = 0; j <= n; j++) {
				if (i == 0 || j == 0) {
					dp[i][j] = 0;
				} else if (first.charAt(i - 1) == second.charAt(j - 1)) {
					dp[i][j] = 1 + dp[i - 1][j - 1];
				} else {
					dp[i][j] = max(dp[i - 1][j], dp[i][j - 1]);
				}
			}
		}
		return dp[m][n];
	}
	
	public static double levenshteinDistance(@NotNull String first, @NotNull String second) {
		final int sLen = first.length(), tLen = second.length();
		if (sLen == 0) {
			return tLen;
		}
		if (tLen == 0) {
			return sLen;
		}
		int[] costsPrev = new int[sLen + 1];
		int[] costs = new int[sLen + 1];
		int[] tmpArr;
		int sIndex, tIndex;
		int cost;
		char tIndexChar;
		for (sIndex = 0; sIndex <= sLen; sIndex++)
			costsPrev[sIndex] = sIndex;
		for (tIndex = 1; tIndex <= tLen; tIndex++) {
			tIndexChar = second.charAt(tIndex - 1);
			costs[0] = tIndex;
			for (sIndex = 1; sIndex <= sLen; sIndex++) {
				cost = (first.charAt(sIndex - 1) == tIndexChar) ? 0 : 1;
				costs[sIndex] = Math.min(Math.min(costs[sIndex - 1] + 1,
						costsPrev[sIndex] + 1),
						costsPrev[sIndex - 1] + cost);
			}
			tmpArr = costsPrev;
			costsPrev = costs;
			costs = tmpArr;
		}
		return costsPrev[sLen] / (double) max(first.length(), second.length());
	}
	
	public static <Type> Type closest(@NotNull String query, @NotNull Collection<Type> values) {
		return closest(query, values, Object::toString);
	}
	
	public static <Type> Type closest(@NotNull String query, @NotNull Collection<Type> values, @NotNull Function<Type, String> mapper) {
		return values.stream()
				.sorted(Comparator.<Type>comparingDouble(value -> levenshteinDistance(mapper.apply(value), query))
						.thenComparingDouble(value -> (double) LCSDistance(mapper.apply(value), query)))
				.findFirst()
				.orElseThrow(NoSuchElementException::new);
	}
	
	
	public static Runnable schedule(Plugin plugin, Supplier<Boolean> task, Number ticks) {
		return schedule(plugin, bukkitTask -> {
			if (task.get()) {
				bukkitTask.cancel();
			}
		}, ticks);
	}
	
	public static Runnable schedule(Plugin plugin, Consumer<BukkitTask> task, Number ticks) {
		return new Runnable() {
			BukkitTask bukkitTask = getScheduler()
					.runTaskTimer(plugin, this, ticks.longValue(), ticks.longValue());
			
			@Override
			public void run() {
				task.accept(bukkitTask);
			}
		};
	}
	
	public static void main(String[] args) {
		Data data = new Data();
	}
	
	private static final Pattern PATTERN = compile(",");
	private static final String CLEAR = "clear";
	private static final StrSubstitutor FORMAT = new StrSubstitutor(new StrLookup() {
		@Override
		public String lookup(String value) {
			return PATTERN.splitAsStream(value)
					.map(prefix -> {
						if (!CLEAR.startsWith(prefix)) {
							for (ChatColor color : ChatColor.values())
								if (color.toString().startsWith(prefix)) {
									return color.toString();
								}
						}
						return "§r";
					})
					.collect(Collectors.joining());
		}
	});
	
	public static String color(Object text) {
		return FORMAT.replace(text);
	}
	
	
	public static void setField(Object caller, Object from, Object to) {
//		getUnsafe().ifPresent(unsafe -> {
//			try {
//				for (Field field : caller.getClass().getDeclaredFields()) {
//					if (field.get(caller).equals(ofZip)) {
//						unsafe.compareAndSwapObject(caller, unsafe.objectFieldOffset(field), ofZip, to);
//					}
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		});
	}
	
	static class Data {
		@NotNull Test test = new Test();
		
		public Data() {
			System.out.println(test.value);
			
			Test newVal = new Test();
			newVal.value = "loaded";
			setField(this, test, newVal);
			
			System.out.println(test.value);
		}
	}
	
	static class Test {
		@NotNull String value = "default";
	}
}
