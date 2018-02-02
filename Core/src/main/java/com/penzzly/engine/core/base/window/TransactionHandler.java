package com.penzzly.engine.core.base.window;

import com.penzzly.engine.core.base.window.elements.Element;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.UUID;
import java.util.function.Consumer;

import static com.penzzly.engine.core.base.window.Transaction.CLEAR;
import static com.penzzly.engine.core.base.window.Transaction.QUEUE;
import static java.util.Collections.asLifoQueue;

public abstract class TransactionHandler<Type extends Element> {
	private final Queue<Transaction<Type>> forestack = new PriorityQueue<>();
	private final Queue<Transaction<Type>> stack = new PriorityQueue<>();
	private final Queue<Transaction<Type>> backstack = asLifoQueue(new LinkedList<>());
	private @Nullable Transaction<Type> current;
	protected final Player player;
	
	public TransactionHandler(Player player) {
		this.player = player;
	}
	
	@NotNull
	protected abstract BaseElement<Type> create();
	
	@SuppressWarnings("unchecked")
	@NotNull
	public Transaction<Type> transaction(@NotNull Consumer<Type> description) {
		BaseElement<Type> element = create();
		element.onComplete(() -> {
			if (current == null || element.equals(current.getElement())) {
				if (!stack.isEmpty()) {
					stack.poll().commitNow();
				} else if (!forestack.isEmpty()) {
					forestack.poll().commitNow();
				} else {
					remove(current);
					current = null;
				}
			}
		});
		description.accept((Type) element);
		return new Transaction<Type>(element) {
			@NotNull
			@Override
			public UUID commit(Boolean mode) {
				setTop(mode, this);
				return getId();
			}
		};
	}
	
	//--Back--
	public Transaction<Type> popBack() {
		return popBack(1);
	}
	
	public Transaction<Type> popBack(int times) {
		return dig(backstack, times);
	}
	
	public Transaction<Type> popBack(UUID id) {
		return find(backstack, id);
	}
	
	//--Fore--
	public Transaction<Type> popFore() {
		return popFore(1);
	}
	
	public Transaction<Type> popFore(int times) {
		return dig(forestack, times);
	}
	
	public Transaction<Type> popFore(UUID id) {
		return find(forestack, id);
	}
	
	private Transaction<Type> find(@NotNull Queue<Transaction<Type>> queue, UUID id) {
		while (!queue.isEmpty()) {
			Transaction<Type> pair = queue.peek();
			if (pair.getId().equals(id)) {
				return pair;
			} else {
				pair.getElement().disable();
				queue.poll();
			}
		}
		throw new RuntimeException("Could not locate an element with id: " + id);
	}
	
	private Transaction<Type> dig(@NotNull Queue<Transaction<Type>> queue, int times) {
		for (int i = 0; i < times; i++)
			queue.poll().getElement().disable();
		return queue.peek();
	}
	
	private void remove(@Nullable Transaction<Type> holder) {
		if (holder != null) {
			if (holder.willDisableEagerly() || !backstack.contains(holder)) {
				holder.getElement().disable();
			} else {
				holder.getElement().conceal();
			}
		}
	}
	
	private void add(@NotNull Transaction<Type> element) {
		//Enable and show the transaction and make it the current one.
		//Likely to already be enabled if it came off the backstack and WON'T task eagerly.
		//if(!transaction.isEnabled()) <- remember this is within enable method.
		//if(!transaction.isDisplayed()) <- remember this is within display method.
		element.getElement().enable();
		element.getElement().reveal();
		element.getElement().show();
		//TODO this might be a problem.
		if (element.willAddToBackstack() && !backstack.contains(element)) {
			backstack.offer(element);
		}
		current = element;
	}
	
	@NotNull
	private UUID setTop(Boolean mode, @NotNull Transaction<Type> element) {
		if (mode == CLEAR) {
			stack.forEach(this::remove);
			stack.clear();
			remove(current);
			add(element);
		} else if (current == null || current.getElement().isComplete()) {
			remove(current);
			add(element);
		} else if (current.getPriority() < element.getPriority()) {
			current.getElement().hide();
			stack.offer(current);
			add(element);
		} else if (mode == QUEUE) {
			forestack.offer(element);
		} else {
			element.getElement().enable();
			element.getElement().reveal();
			stack.offer(element);
		}
		if (current != null) {
			return current.getId();
		}
		throw new IllegalStateException("Something went terribly wrong!");
	}
}