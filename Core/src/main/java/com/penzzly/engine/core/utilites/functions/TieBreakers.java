package com.penzzly.engine.core.utilites.functions;

import com.penzzly.engine.core.utilites.RandomUtil;
import org.bukkit.entity.Player;

import java.util.Comparator;

public final class TieBreakers {
	public static final Comparator<Player> HIGHEST_PLAYER = Comparator.comparingDouble(p -> p.getLocation().getY());
	
	private TieBreakers() {
	}
	
	public static <T> Comparator<T> random() {
		return Comparator.comparing(object -> RandomUtil.randomBoolean());
	}
	
	
}
