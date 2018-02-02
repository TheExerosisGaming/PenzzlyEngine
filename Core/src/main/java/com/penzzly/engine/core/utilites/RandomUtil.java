package com.penzzly.engine.core.utilites;

import com.google.common.collect.ImmutableMap;
import com.penzzly.engine.core.utilites.color.Palette;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;

import static com.penzzly.engine.architecture.utilites.Extensions.range;
import static org.bukkit.ChatColor.*;

//TODO make this not depend on bukkit
public final class RandomUtil {
	private static final Random RANDOM = new Random();
	
	@NotNull
	public static Random getRandom() {
		return RANDOM;
	}
	
	private RandomUtil() {
	
	}
	
	private static final Map<ChatColor, Color> CHAT_COLOR_MAP = ImmutableMap.<ChatColor, Color>builder()
			.put(BLACK, Color.fromRGB(0, 0, 0))
			.put(DARK_BLUE, Color.fromRGB(0, 0, 170))
			.put(DARK_GREEN, Color.fromRGB(0, 170, 0))
			.put(DARK_AQUA, Color.fromRGB(0, 170, 170))
			.put(DARK_RED, Color.fromRGB(170, 0, 0))
			.put(DARK_PURPLE, Color.fromRGB(170, 0, 170))
			.put(GOLD, Color.fromRGB(255, 170, 0))
			.put(GRAY, Color.fromRGB(170, 170, 170))
			.put(DARK_GRAY, Color.fromRGB(85, 85, 85))
			.put(BLUE, Color.fromRGB(85, 85, 255))
			.put(GREEN, Color.fromRGB(85, 255, 85))
			.put(AQUA, Color.fromRGB(85, 255, 255))
			.put(RED, Color.fromRGB(255, 85, 85))
			.put(LIGHT_PURPLE, Color.fromRGB(255, 85, 255))
			.put(YELLOW, Color.fromRGB(255, 255, 85))
			.put(WHITE, Color.fromRGB(255, 255, 255)).build();
	
	public static Color toColor(ChatColor chatColor) {
		Color color = CHAT_COLOR_MAP.get(chatColor);
		if (color != null) {
			return color;
		} else {
			return Color.WHITE;
		}
	}
	
	public static boolean randomBoolean() {
		return RANDOM.nextBoolean();
	}
	
	public static Color randomColor() {
		return random(Color.AQUA, Color.BLACK, Color.BLUE, Color.FUCHSIA, Color.GRAY, Color.GREEN, Color.LIME, Color.MAROON, Color.NAVY,
				Color.OLIVE, Color.ORANGE, Color.PURPLE, Color.RED, Color.SILVER, Color.TEAL, Color.WHITE, Color.YELLOW);
	}
	
	@SafeVarargs
	public static <T> T random(@NotNull T... items) {
		return items[random(items.length - 1)];
	}
	
	@NotNull
	public static ChatColor[] random(@NotNull Palette palette, int amount) {
		ChatColor[] result = new ChatColor[amount];
		range(amount - 1, i -> result[i] = random(palette));
		return result;
	}
	
	public static ChatColor random(@NotNull Palette palette) {
		switch (random(4)) {
			case 0:
				return palette.getSecondary();
			case 1:
				return palette.getAccent();
			case 2:
				return palette.getDarkShader();
			case 3:
				return palette.getLightShader();
			default:
				return palette.getPrimary();
		}
	}
	
	public static <T extends Enum<?>> T random(@NotNull Class<T> clazz) {
		return random(clazz.getEnumConstants());
	}
	
	public static <T extends Enum<?>> T random(@NotNull Class<T> type, @NotNull Predicate<T> filter) {
		T result;
		do result = random(type.getEnumConstants());
		while (!filter.test(result));
		return result;
	}
	
	public static int random(int max) {
		return RANDOM.nextInt(max + 1);
	}
	
	public static int random(int min, int max) {
		return RANDOM.nextInt(max - min + 1) + min;
	}
	
	public static double randomDouble(double min, double max) {
		return min + (getRandom().nextDouble() * Math.abs(max - min));
	}
	
	public static <T> T randomElement(@NotNull List<T> list) {
		return list.get(getRandom().nextInt(list.size()));
	}
	
	public static <T> T randomElement(@NotNull Set<T> set) {
		int item = getRandom().nextInt(set.size());
		int i = 0;
		for (T obj : set) {
			if (i == item) {
				return obj;
			}
			i++;
		}
		//Should never happen!
		return null;
	}
	
	public static <T> T randomElement(@NotNull T[] array) {
		return array[getRandom().nextInt(array.length)];
	}
	
}
