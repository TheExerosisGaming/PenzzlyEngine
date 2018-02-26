package com.penzzly.engine.architecture;

import com.penzzly.engine.architecture.base.Component;
import com.penzzly.engine.architecture.holder.Holder;
import com.penzzly.engine.architecture.holder.mutable.CachedHolder;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import org.javatuples.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.*;
import java.util.function.Predicate;

import static com.google.common.collect.Iterators.size;
import static java.lang.Integer.compare;
import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.sleep;
import static java.util.Collections.asLifoQueue;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.stream.IntStream.range;
import static org.javatuples.Pair.with;

public class Main {
	private static long averageStepTime = 0;
	private static long lastStepTime;
	
	public static void main(String[] args) {
		CachedHolder<String> strings = new CachedHolder<>();
		strings.add("stringywingy");
		strings.onAdd(System.out::println);
		strings.add("wingystringums");
	}
	
	public static void main4(String[] args) throws InterruptedException {
		Test test = new Test();
		
		Transaction one = new Transaction("one", 0, true, true);
		test.init(one);
		Transaction two = new Transaction("two", 0, true, false);
		test.init(two);
		Transaction three = new Transaction("three", 0, false, true);
		test.init(three);
		Transaction four = new Transaction("four", 0, true, true);
		test.init(four);
		Transaction five = new Transaction("five", 0, true, true);
		test.init(five);
		Transaction six = new Transaction("six", 1, true, true);
		test.init(six);
		Transaction seven = new Transaction("seven", 0, true, true);
		test.init(seven);
		
		//Totally normal
		test.commit(one);
		s();
		
		//Skips 2 on backstack.
		test.commit(two);
		s();
		
		//Doesn't disable 3 on backstack.
		test.commit(three);
		s();
		
		//Totally normal.
		test.commit(four);
		s();
		
		//Go back to three.
		test.popBack();
		s();
		
		//Now to 5.
		test.commit(five);
		s();
		
		//Back to three.
		test.popBack();
		s();
		
		//Back to one.
		test.popBack();
		s();
		
		//Show six and seven.
		test.commit(six);
		test.commit(seven);
		s();
		
		//Pop back to one.
		test.popBack(2);
		
		sleep(60_000);
	}
	
	@OverridingMethodsMustInvokeSuper
	private static void s() throws InterruptedException {
		Thread.sleep(3000);
		System.out.println();
		System.out.println("-------------Next-Operations----------");
		System.out.println();
	}
	
	static class Transaction extends Component implements Comparable<Transaction> {
		private final String id;
		private int level;
		private boolean disable;
		private boolean backstack;
		private Runnable runnable;
		
		public Transaction(String id, int level, boolean disable, boolean backstack) {
			this.id = id;
			this.level = level;
			this.disable = disable;
			this.backstack = backstack;
			onEnable(() -> System.out.println("Enabled transaction: " + this.id), () -> {
			
			});
			onDisable(() -> System.out.println("Disabled transaction: " + this.id));
		}
		
		public void hide() {
			System.out.println("Hid transaction: " + id);
		}
		
		public void show() {
			System.out.println("Showed transaction: " + id);
			new Thread(() -> {
				try {
					sleep(level == 1 ? 1000 : 1500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				done();
			}).start();
		}
		
		public void done() {
			System.out.println("Completed transaction: " + this.id);
			runnable.run();
		}
		
		public void onDone(Runnable runnable) {
			this.runnable = runnable;
		}
		
		
		public String getId() {
			return id;
		}
		
		@Override
		public int compareTo(Transaction transaction) {
			return compare(level, transaction.level);
		}
		
		public boolean willDisableEagerly() {
			return disable;
		}
		
		public boolean willAddToBackstack() {
			return backstack;
		}
	}
	
	static class Test {
		private final Queue<Transaction> backstack = asLifoQueue(new LinkedList<>());
		private final Queue<Transaction> stack = new PriorityQueue<>();
		private final Queue<Transaction> forestack = new PriorityQueue<>();
		@Nullable
		private Transaction current;
		
		public void popBack() {
			popBack(1);
		}
		
		public void popBack(int times) {
			for (int i = 0; i < times; i++)
				backstack.poll().disable();
			setTop(backstack.peek());
		}
		
		public void popFore() {
			commit(forestack.poll());
		}
		
		public void commit(@NotNull Transaction transaction) {
			if (current == null || transaction.compareTo(current) > 0) {
				setTop(transaction);
			} else {
				transaction.enable();
				stack.offer(transaction);
			}
		}
		
		public void setTop(@Nullable Transaction transaction) {
			//Hide and maybe disable the current transaction.
			if (current != null) {
				current.hide();
				if (current.willDisableEagerly() || backstack.contains(transaction)) {
					current.disable();
				}
				//If we aren't backing up then maybe add the transaction to the backstack.
			}
			if (transaction != null) {
				//Enable and show the transaction and make it the current one.
				//Likely to already be enabled if it came off the backstack and WON'ViewModel disable eagerly.
				//if(!transaction.isEnabled()) <- remember this is within enable method.
				transaction.enable();
				transaction.show();
				if (transaction.willAddToBackstack() && !backstack.contains(transaction)) {
					backstack.offer(transaction);
				}
			}
			current = transaction;
		}
		
		private void init(@NotNull Transaction transaction) {
			transaction.onDone(() -> {
				if (!stack.isEmpty()) {
					setTop(stack.poll());
				} else if (!forestack.isEmpty()) {
					setTop(forestack.poll());
				} else {
					setTop(null);
				}
			});
		}
	}
	
	public static void main3(String[] args) {
		BiObservableSubject<String, Integer> test = new BiObservableSubject<>();
		test
				.first().map(String::length)
				.second().map(length -> "Should Be: " + length)
				.first().filter(size -> size >= 10)
				.subscribe((first, second) ->
						System.out.println("First: " + first + " Second: " + second));
		
		
		test.onNext("My Long Value", 13);
		test.onNext("My Longer Value", 15);
		test.onNext("My Shorter", 10);
		test.onNext("My Short", 8);
	}
	
	public static class BiObservableSubject<First, Second> extends BiObservable<First, Second> {
		private final Subject<Pair<First, Second>> subject = PublishSubject.create();
		
		@NotNull
		public BiObservableSubject<First, Second> onNext(First first, Second second) {
			subject.onNext(with(first, second));
			return this;
		}
		
		@Override
		protected void subscribeActual(@NotNull Observer<? super Pair<First, Second>> observer) {
			subject.subscribe(observer);
		}
	}
	
	public abstract static class BiObservable<First, Second> extends Observable<Pair<First, Second>> {
		@NotNull
		public FirstSideObservable<First, Second> first() {
			return new FirstSideObservable<>(this);
		}
		
		@NotNull
		public SecondSideObservable<First, Second> second() {
			return new SecondSideObservable<>(this);
		}
		
		@NotNull
		public Disposable subscribe(@NotNull BiConsumer<First, Second> observer) {
			return subscribe(pair -> observer.accept(pair.getValue0(), pair.getValue1()));
		}
	}
	
	public static class FirstSideObservable<First, Second> {
		private BiObservable<First, Second> source;
		
		public FirstSideObservable(BiObservable<First, Second> source) {
			this.source = source;
		}
		
		@NotNull
		public Disposable subscribe(@NotNull Consumer<First> observer) {
			return source.subscribe(pair -> observer.accept(pair.getValue0()));
		}
		
		@NotNull
		public BiObservable<First, Second> filter(@NotNull Predicate<First> filter) {
			return new BiObservable<First, Second>() {
				@Override
				protected void subscribeActual(@NotNull Observer<? super Pair<First, Second>> observer) {
					source.subscribe(pair -> {
						if (filter.test(pair.getValue0())) {
							observer.onNext(pair);
						}
					}, observer::onError, observer::onComplete);
				}
			};
		}
		
		@NotNull
		public <To> BiObservable<To, Second> map(@NotNull Function<First, To> mapper) {
			return new BiObservable<To, Second>() {
				@Override
				protected void subscribeActual(@NotNull Observer<? super Pair<To, Second>> observer) {
					source.subscribe(pair ->
							observer.onNext(with(mapper.apply(pair.getValue0()),
									pair.getValue1())));
				}
			};
		}
	}
	
	public static class SecondSideObservable<First, Second> {
		private BiObservable<First, Second> source;
		
		public SecondSideObservable(BiObservable<First, Second> source) {
			this.source = source;
		}
		
		@NotNull
		public Disposable subscribe(@NotNull Consumer<Second> observer) {
			return source.subscribe(pair -> observer.accept(pair.getValue1()));
		}
		
		@NotNull
		public BiObservable<First, Second> filter(@NotNull Predicate<Second> filter) {
			return new BiObservable<First, Second>() {
				@Override
				protected void subscribeActual(@NotNull Observer<? super Pair<First, Second>> observer) {
					source.subscribe(pair -> {
						if (filter.test(pair.getValue1())) {
							observer.onNext(pair);
						}
					}, observer::onError, observer::onComplete);
				}
			};
		}
		
		@NotNull
		public <To> BiObservable<First, To> map(@NotNull Function<Second, To> mapper) {
			return new BiObservable<First, To>() {
				@Override
				protected void subscribeActual(@NotNull Observer<? super Pair<First, To>> observer) {
					source.subscribe(pair ->
									observer.onNext(with(pair.getValue0(),
											mapper.apply(pair.getValue1()))),
							observer::onError, observer::onComplete);
				}
			};
		}
	}
	
	public static void main2(String[] args) {
	
	/*
		int blockSize = 10_000;
		int times = 1000;
		int interval = 50;
		List<Long> stepTimes = new ArrayList<>();
		lastStepTime = currentTimeMillis();
		range(0, times).forEach(step -> {
			stepTimes.add(currentTimeMillis() - lastStepTime);
			lastStepTime = currentTimeMillis();
			long sum = 0;
			for (Long stepTime : stepTimes) {
				sum += stepTime;
			}
			averageStepTime = sum / stepTimes.size();
			holder.add(range(0, blockSize).boxed().collect(toList()));
			if (step % interval == 0) {
				System.out.println("Step " + (step + 1) + "/" + times);
				System.out.println("Estimated time left " + MILLISECONDS.toSeconds(averageStepTime * (times - step)) + " seconds");
			}
		});*/
		System.out.println("Starting generation.");
		long generatingStart = currentTimeMillis();
		CachedHolder<Integer> holder = new CachedHolder<>();
		List<Integer> ints = new ArrayList<>();
		range(0, 1_000_000).boxed().forEach(test -> {
			if (!ints.contains(test)) {
				ints.add(test);
			}
		});
		System.out.println("Elements: " + holder.size());
		System.out.println("Generating took " + MILLISECONDS.toSeconds(currentTimeMillis() - generatingStart) + " seconds");
		System.out.println();
		System.out.println();
		System.out.println("Starting partitioning.");
		long partitionStart = currentTimeMillis();
		Holder<Integer> partition = holder.live().partition(j -> j % 2 == 1);
		System.out.println("Partitioning took " + (currentTimeMillis() - partitionStart) + " milliseconds");
		
		System.out.println("Starting iteration.");
		long iterationStart = currentTimeMillis();
		System.out.println("Elements: " + size(partition.iterator()));
		System.out.println("Iteration took " + (currentTimeMillis() - iterationStart) + " milliseconds");
	}
}
