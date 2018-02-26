package com.penzzly.engine.core.base;

import com.penzzly.engine.architecture.base.Phase;
import com.penzzly.engine.architecture.base.Toggleable;
import com.penzzly.engine.core.utilites.time.Duration;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.penzzly.engine.core.utilites.bukkit.ServerUtil.getPlugin;
import static com.penzzly.engine.core.utilites.functions.Functions.FALSE;
import static com.penzzly.engine.core.utilites.functions.Functions.TRUE;
import static com.penzzly.engine.core.utilites.time.Duration.toMillis;
import static java.lang.Runtime.getRuntime;
import static java.lang.System.currentTimeMillis;
import static java.util.concurrent.TimeUnit.*;
import static org.bukkit.Bukkit.getScheduler;

public class Scheduler {
	//--Duration--
	
	//--Provided--
	public static TaskConditionBuilder<Task> every(@NotNull Number number, @NotNull TimeUnit unit) {
		
		return (task, condition) -> new Task(toMillis(number, unit), task, condition);
	}
	
	public static TaskConditionBuilder<Task> every(@NotNull Supplier<Number> number, @NotNull TimeUnit unit) {
		return (task, condition) -> new Task(toMillis(number, unit), task, condition);
	}
	
	public static TaskConditionBuilder<Task> every(@NotNull Number number, @NotNull Supplier<TimeUnit> unit) {
		return (task, condition) -> new Task(toMillis(number, unit), task, condition);
	}
	
	public static TaskConditionBuilder<Task> every(@NotNull Supplier<Number> number, @NotNull Supplier<TimeUnit> unit) {
		return (task, condition) -> new Task(toMillis(number, unit), task, condition);
	}
	
	//--Plural--
	public static UnitBuilder.Plural<TaskConditionBuilder<Task>> every(@NotNull Number number) {
		return unit -> every(number, unit);
	}
	
	public static UnitBuilder.Plural<TaskConditionBuilder<Task>> every(@NotNull Supplier<Number> number) {
		return unit -> every(number, unit);
	}
	
	//--Singular--
	public static UnitBuilder.Singular<TaskConditionBuilder<Task>> every() {
		return unit -> every(1, unit);
	}
	
	
	//--In--
	public static TaskBuilder<Task> in(@NotNull Number number, @NotNull TimeUnit unit) {
		return task -> new Task(toMillis(number, unit), task, TRUE);
	}
	
	public static TaskBuilder<Task> in(@NotNull Supplier<Number> number, @NotNull Supplier<TimeUnit> unit) {
		return task -> new Task(toMillis(number, unit), task, TRUE);
	}
	
	public static TaskBuilder<Task> in(@NotNull Supplier<Number> number, @NotNull TimeUnit unit) {
		return task -> new Task(toMillis(number, unit), task, TRUE);
	}
	
	public static TaskBuilder<Task> in(@NotNull Number number, @NotNull Supplier<TimeUnit> unit) {
		return task -> new Task(toMillis(number, unit), task, TRUE);
	}
	
	
	//--Plural--
	public static UnitBuilder.Plural<TaskBuilder<Task>> in(@NotNull Number number) {
		return unit -> in(number, unit);
	}
	
	public static UnitBuilder.Plural<TaskBuilder<Task>> in(@NotNull Supplier<Number> number) {
		return unit -> in(number, unit);
	}
	
	
	public static TaskBuilder<Task> schedule(@NotNull Duration duration) {
		return in(duration.time(), duration.unit());
	}
	
	//--Singular--
	//TODO maybe remove this?
	public static TaskBuilder<Task> inAnHour() {
		return in(1, HOURS);
	}
	
	public static UnitBuilder.Singular<TaskBuilder<Task>> inA() {
		return unit -> in(1, unit);
	}
	
	
	public interface TaskConditionBuilder<Return> extends TaskBuilder<ConditionBuilder<Return>>, ConditionBuilder<TaskBuilder<Return>> {
		@NotNull Return build(BiConsumer<Long, Task> runnable, Supplier<Boolean> condition);
		
		@NotNull
		@Override
		default ConditionBuilder<Return> build(BiConsumer<Long, Task> task) {
			return condition -> build(task, condition);
		}
		
		@NotNull
		@Override
		default TaskBuilder<Return> build(Supplier<Boolean> condition) {
			return runnable -> build(runnable, condition);
		}
	}
	
	public interface TaskBuilder<Return> {
		@NotNull Return build(BiConsumer<Long, Task> task);
		
		@NotNull
		default Return task(@NotNull Toggleable... toggleables) {
			for (Toggleable toggleable : toggleables)
				toggleable.enable();
			return run(() -> {
				for (Toggleable toggleable : toggleables)
					toggleable.disable();
			});
		}
		
		@NotNull
		default Return run(@NotNull Consumer<Long> task) {
			return run((ticks, $) -> task.accept(ticks));
		}
		
		@NotNull
		default Return run(@NotNull Consumer<Long>... tasks) {
			return build((ticks, $) -> {
				for (Consumer<Long> task : tasks)
					task.accept(ticks);
			});
		}
		
		@NotNull
		default Return run(BiConsumer<Long, Task> task) {
			return build(task);
		}
		
		@NotNull
		default Return run(@NotNull BiConsumer<Long, Task>... tasks) {
			return build((ticks, component) -> {
				for (BiConsumer<Long, Task> task : tasks)
					task.accept(ticks, component);
			});
		}
		
		@NotNull
		default Return run(@NotNull Runnable runnable) {
			return run($ -> runnable.run());
		}
		
		@NotNull
		default Return run(@NotNull Runnable... tasks) {
			return build(($, $_) -> {
				for (Runnable task : tasks)
					task.run();
			});
		}
	}
	
	public interface ConditionBuilder<Return> {
		@NotNull Return build(Supplier<Boolean> condition);
		
		@NotNull
		default Return forever() {
			return build(FALSE);
		}
		
		//--Singular--
		//TODO Maybe remove one or more of these?
		@NotNull
		default Return forAnHour() {
			return forTheNext(1, HOURS);
		}
		
		@NotNull
		default UnitBuilder.Singular<Return> forA() {
			return forTheNext();
		}
		
		@NotNull
		default UnitBuilder.Singular<Return> forTheNext() {
			return unit -> forTheNext(1, unit);
		}
		
		//--Plural--
		@NotNull
		default Plural<Return> forTheNext(Number number) {
			return forTheNext(() -> number);
		}
		
		@NotNull
		default Plural<Return> forTheNext(@NotNull Supplier<Number> number) {
			return unit -> build(unit == null ? timesCondition(number) : durationCondition(toMillis(number, unit)));
		}
		
		//--Provided--
		@NotNull
		default Return forTheNext(@NotNull Number number, @NotNull TimeUnit unit) {
			long end = currentTimeMillis() + unit.toMillis(number.longValue());
			return build(() -> end > currentTimeMillis());
		}
		
		@NotNull
		default Return forTheNext(@NotNull Supplier<Number> number, @NotNull Supplier<TimeUnit> unit) {
			return build(durationCondition(toMillis(number, unit)));
		}
		
		@NotNull
		default Return forTheNext(@NotNull Supplier<Number> number, @NotNull TimeUnit unit) {
			return build(durationCondition(toMillis(number, unit)));
		}
		
		@NotNull
		default Return forTheNext(@NotNull Number number, @NotNull Supplier<TimeUnit> unit) {
			return build(durationCondition(toMillis(number, unit)));
		}
		
		@NotNull
		default Return until(Supplier<Boolean> condition) {
			return build(condition);
		}
		
		interface Plural<Return> extends UnitBuilder.Plural<Return> {
			@NotNull
			default Return times() {
				return build(null);
			}
		}
		
		@NotNull
		default Supplier<Boolean> timesCondition(@NotNull Supplier<Number> runs) {
			return new Supplier<Boolean>() {
				long times = 0;
				
				@NotNull
				@Override
				public Boolean get() {
					return ++times >= runs.get().longValue();
				}
			};
		}
		
		@NotNull
		default Supplier<Boolean> durationCondition(@NotNull Supplier<Number> duration) {
			final long start = currentTimeMillis();
			return () -> currentTimeMillis() >= start + duration.get().longValue();
		}
	}
	
	public interface UnitBuilder<Return> {
		@NotNull Return build(TimeUnit unit);
		
		interface Singular<Return> extends UnitBuilder<Return> {
			@NotNull
			default Return millisecond() {
				return build(MILLISECONDS);
			}
			
			@NotNull
			default Return second() {
				return build(SECONDS);
			}
			
			@NotNull
			default Return minute() {
				return build(MINUTES);
			}
			
			@NotNull
			default Return hour() {
				return build(HOURS);
			}
			
			@NotNull
			default Return day() {
				return build(DAYS);
			}
		}
		
		interface Plural<Return> extends UnitBuilder<Return> {
			@NotNull
			default Return milliseconds() {
				return build(MILLISECONDS);
			}
			
			@NotNull
			default Return seconds() {
				return build(SECONDS);
			}
			
			@NotNull
			default Return minutes() {
				return build(MINUTES);
			}
			
			@NotNull
			default Return hours() {
				return build(HOURS);
			}
			
			@NotNull
			default Return days() {
				return build(DAYS);
			}
		}
	}
	
	
	public static class Task extends Phase {
		private static final ScheduledExecutorService EXECUTOR = new ScheduledThreadPoolExecutor(getRuntime().availableProcessors());
		@NotNull
		private final Runnable runnable;
		private long times = 0;
		private boolean sync = false;
		private Predicate<Long> filter = $ -> true;
		
		public Task(Number period, @NotNull BiConsumer<Long, Task> task, @NotNull Supplier<Boolean> condition) {
			this(() -> period, task, condition);
		}
		
		public Task(@NotNull Supplier<Number> period, @NotNull BiConsumer<Long, Task> task, @NotNull Supplier<Boolean> condition) {
			runnable = new Runnable() {
				@Override
				public void run() {
					if (filter.test(times)) {
						if (sync) {
							getScheduler().runTask(getPlugin(), () -> {
								task.accept(times, Task.this);
							});
						} else {
							task.accept(times, Task.this);
						}
						if (condition.get()) {
							if (sync) {
								getScheduler().runTask(getPlugin(), Task.this::complete);
							} else {
								Task.this.complete();
							}
						} else {
							times++;
						}
					}
					if (isEnabled() && !isComplete()) {
						long time = period.get().longValue();
						if (time == 0) {
							complete();
						} else {
							EXECUTOR.schedule(this, time, MILLISECONDS);
						}
					}
				}
			};
			autoDisable();
		}
		
		@NotNull
		public Scheduler.Task synchronously() {
			sync ^= true;
			return this;
		}
		
		@NotNull
		public Scheduler.Task filter(Predicate<Long> filter) {
			this.filter = filter;
			return this;
		}
		
		@NotNull
		@Override
		public Scheduler.Task enable() {
			times = 1;
			super.enable();
			runnable.run();
			return this;
		}
		
		@NotNull
		@Override
		public Scheduler.Task disable() {
			complete();
			return this;
		}
	}
}