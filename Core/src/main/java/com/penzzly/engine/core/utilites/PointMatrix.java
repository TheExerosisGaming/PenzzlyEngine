package com.penzzly.engine.core.utilites;

import com.google.common.collect.Lists;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author Exerosis
 */
public class PointMatrix implements Iterable<Vector> {
	private final List<Vector> points = new LinkedList<>();
	
	/**
	 * Create a {@link LinkedList} backed container of points along a path.
	 * @param density The {@link Number} of points spread between each {@link Vector} point in the path.
	 * @param path The {@link Location} points between which more points will be added.
	 */
	public PointMatrix(int density, Vector... path) {
		addPoints(density, path);
	}
	
	/**
	 * Create a {@link LinkedList} backed container of points along a path.
	 * @param density The {@link Number} of points spread between each {@link Vector} point in the path.
	 * @param path The {@link Vector} points between which more points will be added.
	 */
	public PointMatrix(int density, @NotNull Iterable<Vector> path) {
		addPoints(density, path);
	}
	
	/**
	 * @param density The {@link Number} of points spread between each {@link Vector} point in the path.
	 * @param path The {@link Vector} points between which more points will be added.
	 */
	public PointMatrix(int density, @NotNull List<Vector> path) {
		addPoints(density, path);
	}
	
	
	/**
	 * @param density The {@link Number} of points spread between each {@link Vector} point in the path.
	 * @param path The {@link Vector} points between which more points will be added.
	 */
	@NotNull
	public PointMatrix addPoints(int density, Vector... path) {
		return addPoints(density, Arrays.asList(path));
	}
	
	/**
	 * @param density The {@link Number} of points spread between each {@link Vector} point in the path.
	 * @param path The {@link Vector} points between which more points will be added.
	 */
	@NotNull
	public PointMatrix addPoints(int density, @NotNull Iterable<Vector> path) {
		return addPoints(density, Lists.newArrayList(path));
	}
	
	/**
	 * @param density The {@link Number} of points spread between each {@link Vector} point in the path.
	 * @param path The {@link Vector} points between which more points will be added.
	 */
	@NotNull
	public PointMatrix addPoints(int density, @NotNull List<Vector> path) {
		if (path.size() < 2) {
			throw new IllegalArgumentException("Must imput at least two vectors!");
		}
		int pointsPerLine = density / (path.size() / 2);
		for (int i = 1; i < path.size(); i++)
			addPoints(path.get(i - 1), path.get(i), pointsPerLine);
		return this;
	}
	
	//TODO replace this with something faster I guess...
	private void addPoints(@NotNull Vector start, @NotNull Vector end, int points) {
		Vector increment = start.clone().subtract(end).divide(new Vector(points, points, points));
		Vector last = start;
		for (int i = 0; i < points + 1; i++) {
			this.points.add(last);
			last = last.clone().subtract(increment);
		}
	}
	
	
	public int size() {
		return points.size();
	}
	
	/**
	 * @return The backing {@link List} containing all the points in the matrix.
	 */
	@NotNull
	public List<Vector> getPoints() {
		return points;
	}
	
	@Override
	public Spliterator<Vector> spliterator() {
		return points.spliterator();
	}
	
	@NotNull
	@Override
	public Iterator<Vector> iterator() {
		return points.iterator();
	}
}