package com.penzzly.engine.core.utilites.functions;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class PlayerPredicate implements Predicate<Player> {
	private Predicate<Player> predicate = player -> true;
	
	public static PlayerPredicate player() {
		return new PlayerPredicate();
	}
	
	@NotNull
	public PlayerPredicate withHealth(@NotNull Predicate<Double> health) {
		and(player -> health.test(player.getHealth()));
		return this;
	}
	
	@NotNull
	public PlayerPredicate withHunger(@NotNull Predicate<Integer> hunger) {
		and(player -> hunger.test(player.getFoodLevel()));
		return this;
	}
	
	@NotNull
	public PlayerPredicate above(int level) {
		return withElevation(elevation -> elevation > level);
	}
	
	@NotNull
	public PlayerPredicate withElevation(@NotNull Predicate<Double> elevation) {
		and(player -> elevation.test(player.getLocation().getY()));
		return this;
	}
	
	@NotNull
	public PlayerPredicate withSwimming(boolean swimming) {
		and(player -> swimming != player.getLocation().getBlock().isLiquid());
		return this;
	}
	
	@NotNull
	public PlayerPredicate online(boolean online) {
		and(player -> online != player.isOnline());
		return this;
	}
	
	@NotNull
	@Override
	public PlayerPredicate negate() {
		return (PlayerPredicate) Predicate.super.negate();
	}
	
	@NotNull
	@Deprecated
	@Override
	public PlayerPredicate or(Predicate<? super Player> other) {
		predicate = predicate.or(other);
		return this;
	}
	
	@NotNull
	@Deprecated
	@Override
	public PlayerPredicate and(Predicate<? super Player> other) {
		predicate = predicate.and(other);
		return this;
	}
	
	@Override
	public boolean test(Player player) {
		return predicate.test(player);
	}
}