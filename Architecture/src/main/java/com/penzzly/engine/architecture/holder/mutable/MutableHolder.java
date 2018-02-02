package com.penzzly.engine.architecture.holder.mutable;

import com.penzzly.engine.architecture.holder.Holder;
import com.penzzly.engine.architecture.holder.Operators;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import static com.penzzly.engine.architecture.holder.Iterators.filter;
import static com.penzzly.engine.architecture.holder.Iterators.merge;
import static io.reactivex.Observable.timer;
import static io.reactivex.schedulers.Schedulers.io;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.stream.Collectors.toList;

//TODO add post event.
public interface MutableHolder<Type> extends Holder<Type>, Augmentable<Type>, Reduceable<Type> {
	Observable<Type> onAdd();
	
	Observable<Type> onRemove();
	
	@NotNull
	default MutableHolder<Type> onAdd(@NotNull Consumer<Type> listener) {
		onAdd().subscribe(listener);
		return this;
	}
	
	@NotNull
	default MutableHolder<Type> onRemove(@NotNull Consumer<Type> listener) {
		onRemove().subscribe(listener);
		return this;
	}
	
	@NotNull
	@Override
	default Operators<Type, MutableHolder<Type>> live() {
		return new Operators<Type, MutableHolder<Type>>() {
			@NotNull
			@Override
			public MutableHolder<Type> partition(@NotNull Predicate<Type> filter) {
				return new MutableHolder<Type>() {
					@Override
					public boolean remove(Object element) {
						return MutableHolder.this.remove(element);
					}
					
					@Override
					public boolean add(Type element) {
						return MutableHolder.this.add(element);
					}
					
					@Override
					public Observable<Type> onAdd() {
						return MutableHolder.this.onAdd().filter(filter::test);
					}
					
					@Override
					public Observable<Type> onRemove() {
						return MutableHolder.this.onRemove().filter(filter::test);
					}
					
					@NotNull
					@Override
					public Iterator<Type> iterator() {
						return filter(MutableHolder.this.iterator(), filter);
					}
					
					@Override
					public boolean test(Type element) {
						return filter.and(MutableHolder.this).test(element);
					}
				};
			}
			
			@NotNull
			@Override
			public MutableHolder<Type> union(@NotNull MutableHolder<Type> holder) {
				return new MutableHolder<Type>() {
					@Override
					public boolean remove(Object element) {
						return MutableHolder.this.remove(element);
					}
					
					@Override
					public boolean add(Type element) {
						return MutableHolder.this.add(element);
					}
					
					@Override
					public Observable<Type> onAdd() {
						return MutableHolder.this.onAdd().mergeWith(holder.onAdd().filter(MutableHolder.this::test));
					}
					
					@Override
					public Observable<Type> onRemove() {
						return MutableHolder.this.onRemove().mergeWith(holder.onRemove().filter(MutableHolder.this::test));
					}
					
					@NotNull
					@Override
					public Iterator<Type> iterator() {
						return merge(holder.iterator(), filter(MutableHolder.this.iterator(), holder.negate()));
					}
					
					@Override
					public boolean test(Type element) {
						return holder.or(MutableHolder.this).test(element);
					}
				};
			}
			
			@NotNull
			@Override
			public MutableHolder<Type> difference(@NotNull MutableHolder<Type> holder) {
				return new MutableHolder<Type>() {
					@Override
					public boolean remove(Object element) {
						return MutableHolder.this.remove(element);
					}
					
					@Override
					public boolean add(Type element) {
						return MutableHolder.this.add(element);
					}
					
					@Override
					public Observable<Type> onAdd() {
						return MutableHolder.this.onAdd().filter(holder.negate()::test);
					}
					
					@Override
					public Observable<Type> onRemove() {
						return MutableHolder.this.onRemove().filter(holder.negate()::test);
					}
					
					@NotNull
					@Override
					public Iterator<Type> iterator() {
						return filter(MutableHolder.this.iterator(), holder.negate());
					}
					
					@Override
					public boolean test(Type element) {
						return holder.or(MutableHolder.this).test(element);
					}
				};
			}
			
			@NotNull
			@Override
			public MutableHolder<Type> intersection(@NotNull MutableHolder<Type> holder) {
				return new MutableHolder<Type>() {
					@Override
					public boolean remove(Object element) {
						return MutableHolder.this.remove(element);
					}
					
					@Override
					public boolean add(Type element) {
						return MutableHolder.this.add(element);
					}
					
					@Override
					public Observable<Type> onAdd() {
						return MutableHolder.this.onAdd().filter(holder::test);
					}
					
					@Override
					public Observable<Type> onRemove() {
						return MutableHolder.this.onRemove().filter(holder::test);
					}
					
					@NotNull
					@Override
					public Iterator<Type> iterator() {
						return filter(MutableHolder.this.iterator(), holder);
					}
					
					@Override
					public boolean test(Type element) {
						return holder.or(MutableHolder.this).test(element);
					}
				};
			}
		};
	}
	
	@NotNull
	default MutableOperators<Type> cached() {
		return new MutableOperators<>(this);
	}
	
	
	class MutableOperators<Type> implements Operators<Type, MutableHolder<Type>> {
		private MutableHolder<Type> parent;
		
		public MutableOperators(MutableHolder<Type> parent) {
			this.parent = parent;
		}
		
		@NotNull
		@Deprecated
		public MutableHolder<Type> hybridPartition(@NotNull Predicate<Type> filter) {
			CachedHolder<Type> cachedHolder = new CachedHolder<>();
			for (Type element : parent) {
				if (filter.test(element)) {
					cachedHolder.getCache().add(element);
				}
			}
			timer(10, MILLISECONDS, io()).subscribe(tick -> {
				List<Type> newValues = parent.stream().filter(filter).collect(toList());
				for (Type element : newValues) {
					if (!cachedHolder.test(element)) {
						cachedHolder.add(element);
					}
				}
				if (newValues.size() != cachedHolder.size()) {
					List<Type> toRemove = new ArrayList<>();
					for (Type element : cachedHolder) {
						if (!newValues.contains(element)) {
							toRemove.add(element);
						}
					}
					for (Type element : toRemove) {
						cachedHolder.remove(element);
					}
				}
			});
			parent.onAdd().filter(filter::test).subscribe(cachedHolder::add);
			parent.onRemove().filter(filter::test).subscribe(cachedHolder::remove);
			return cachedHolder;
		}
		
		@NotNull
		@Override
		public MutableHolder<Type> partition(@NotNull Predicate<Type> filter) {
			CachedHolder<Type> cachedHolder = new CachedHolder<>();
			for (Type element : parent) {
				if (filter.test(element)) {
					cachedHolder.getCache().add(element);
				}
			}
			parent.onAdd().filter(filter::test).subscribe(cachedHolder::add);
			parent.onRemove().filter(filter::test).subscribe(cachedHolder::remove);
			return cachedHolder;
		}
		
		@NotNull
		@Override
		public MutableHolder<Type> union(@NotNull MutableHolder<Type> holder) {
			CachedHolder<Type> cachedHolder = new CachedHolder<>();
			for (Type element : parent) {
				cachedHolder.getCache().add(element);
			}
			for (Type element : holder) {
				if (!parent.test(element)) {
					cachedHolder.getCache().add(element);
				}
			}
			parent.onAdd().mergeWith(holder.onAdd()).subscribe(cachedHolder::add);
			parent.onRemove().mergeWith(holder.onRemove()).subscribe(cachedHolder::remove);
			return cachedHolder;
		}
		
		@NotNull
		@Override
		public MutableHolder<Type> difference(@NotNull MutableHolder<Type> holder) {
			CachedHolder<Type> cachedHolder = new CachedHolder<>();
			for (Type element : parent) {
				if (!holder.test(element)) {
					cachedHolder.getCache().add(element);
				}
			}
			holder.onAdd()
					.filter(cachedHolder::test)
					.subscribe(cachedHolder::remove);
			holder.onRemove()
					.filter(parent::test)
					.subscribe(cachedHolder::add);
			parent.onAdd()
					.filter(holder.negate()::test)
					.subscribe(cachedHolder::add);
			parent.onRemove()
					.subscribe(cachedHolder::remove);
			return cachedHolder;
		}
		
		@NotNull
		@Override
		public MutableHolder<Type> intersection(@NotNull MutableHolder<Type> holder) {
			CachedHolder<Type> cachedHolder = new CachedHolder<>();
			for (Type element : parent) {
				if (holder.test(element)) {
					cachedHolder.getCache().add(element);
				}
			}
			holder.onAdd()
					.filter(parent::test)
					.subscribe(cachedHolder::add);
			holder.onRemove()
					.filter(parent::test)
					.subscribe(cachedHolder::remove);
			parent.onAdd()
					.filter(holder::test)
					.subscribe(cachedHolder::add);
			parent.onRemove()
					.subscribe(cachedHolder::remove);
			return cachedHolder;
		}
	}
	
	
}
