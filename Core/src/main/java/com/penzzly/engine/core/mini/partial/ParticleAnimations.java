package com.penzzly.engine.core.mini.partial;

import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

import static com.google.common.collect.Iterators.forArray;

public class ParticleAnimations {
	
	interface Path extends Iterable<Path> {
		default Iterable<Vector> points() {
			Iterator<Path> paths = iterator();
			return () -> new Iterator<Vector>() {
				Iterator<Vector> vectors = getNext();
				
				@Override
				public boolean hasNext() {
					return vectors != null;
				}
				
				@Override
				public Vector next() {
					Vector point = vectors.next();
					if (!vectors.hasNext())
						vectors = getNext();
					return point;
				}
				
				Iterator<Vector> getNext() {
					while (paths.hasNext()) {
						Iterator<Vector> points = paths.next()
								.points()
								.iterator();
						if (points.hasNext())
							return points;
					}
					return null;
				}
			};
		}
		
		static Path concat(Path... paths) {
			return () -> forArray(paths);
		}
		
		static Path concat(Iterable<Path> paths) {
			return paths::iterator;
		}
		
		static Path singleton(Iterable<Vector> points) {
			return new Path() {
				@Override
				public Iterable<Vector> points() {
					return points;
				}
				
				@NotNull
				@Override
				public Iterator<Path> iterator() {
					return () -> new Iterator<Path>() {
						@Override
						public boolean hasNext() {
							return false;
						}
						
						@Override
						public Path next() {
							return null;
						}
					};
				}
			};
		}
	}
	
	
	interface Runner {
		void display(Iterable<Path> points, Particles particles);
	}
	
}
