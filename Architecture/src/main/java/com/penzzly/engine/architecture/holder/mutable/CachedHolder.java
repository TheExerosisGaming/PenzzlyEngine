package com.penzzly.engine.architecture.holder.mutable;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;

public class CachedHolder<Type> implements MutableHolder<Type> {
	@NotNull
	private final Set<Type> cache;
	private final Subject<Type> addSubject = PublishSubject.create();
	private final Subject<Type> removeSubject = PublishSubject.create();
	
	public CachedHolder() {
		this(null);
	}
	
	public CachedHolder(@Nullable Set<Type> elements) {
		cache = elements == null ? new HashSet<>() : elements;
	}
	
	@NotNull
	public Set<Type> getCache() {
		return cache;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean remove(Object element) {
		if (cache.contains(element)) {
			removeSubject.onNext((Type) element);
		}
		return cache.remove(element);
	}
	
	@Override
	public boolean add(Type element) {
		addSubject.onNext(element);
		return cache.add(element);
	}
	
	@Override
	public boolean add(@NotNull Iterable<? extends Type> elements) {
		for (Type element : elements) {
			if (!test(element)) {
				addSubject.onNext(element);
			}
		}
		return cache.addAll(newArrayList(elements));
	}
	
	@NotNull
	@Override
	public Observable<Type> onAdd() {
		return addSubject;
	}
	
	@NotNull
	@Override
	public Observable<Type> onRemove() {
		return addSubject;
	}
	
	@NotNull
	@Override
	public Iterator<Type> iterator() {
		return cache.iterator();
	}
	
	@Override
	public int size() {
		return cache.size();
	}
	
	@Override
	public boolean test(Type element) {
		return cache.contains(element);
	}
}
