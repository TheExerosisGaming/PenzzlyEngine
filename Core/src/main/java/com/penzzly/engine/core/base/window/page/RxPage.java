package com.penzzly.engine.core.base.window.page;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.penzzly.engine.core.base.window.BaseElement;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.javatuples.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static io.reactivex.subjects.BehaviorSubject.create;
import static io.reactivex.subjects.BehaviorSubject.createDefault;
import static org.bukkit.Material.AIR;
import static org.bukkit.Material.GRASS;
import static org.javatuples.Pair.with;

public abstract class RxPage extends BaseElement<Page> implements Page {
	private final BiMap<Integer, Item> elements = HashBiMap.create();
	protected final BehaviorSubject<Pair<Integer, ItemStack>> items = create();
	protected final BehaviorSubject<Object> titleSubject = createDefault("Inventory");
	protected final ArrayListMultimap<Integer, Consumer<ClickType>> clickListeners = ArrayListMultimap.create();
	protected final List<Runnable> closeListeners = new ArrayList<>();
	private int size = 9;
	
	public RxPage() {
	
	}
	
	//--Displayable--
	@NotNull
	@Override
	public Item element(int index) {
		Item item = new Item() {
			private ItemStack stack = new ItemStack(GRASS);
			
			
			@NotNull
			@Override
			public Item item(ItemStack stack) {
				this.stack = stack;
				items.onNext(with(index, stack));
				return this;
			}
			
			@NotNull
			@Override
			public Item item(@NotNull Observable<ItemStack> stacks) {
				stacks.map(stack -> with(index, stack)).subscribe(items);
				return this;
			}
			
			@Override
			public Consumer<ClickType> onClick(Consumer<ClickType> listener) {
				clickListeners.put(index, listener);
				return listener;
			}
			
			@NotNull
			@Override
			public ItemStack itemStack() {
				return stack;
			}
			
			@Override
			public List<Consumer<ClickType>> clickListeners() {
				return clickListeners.get(index);
			}
		};
		elements.put(index, item);
		return item;
	}
	
	
	//--Remove--
	@Nullable
	@Override
	public Page remove(@NotNull Item item) {
		item.item(new ItemStack(AIR));
		elements.inverse().remove(item);
		return null;
	}
	
	
	//--Size--
	@NotNull
	@Override
	public Page size(@NotNull Number size) {
		this.size = size.intValue();
		titleSubject.onNext(titleSubject.getValue());
		return this;
	}
	
	@Override
	public int size() {
		return size;
	}
	
	
	//--Title--
	@NotNull
	@Override
	public Page title(Object title) {
		titleSubject.onNext(title);
		return this;
	}
	
	@NotNull
	@Override
	public Page title(@NotNull Observable<Object> title) {
		title.subscribe(titleSubject);
		return this;
	}
	
	
	//--Elements--
	@NotNull
	@Override
	public BiMap<Integer, Item> elements() {
		return elements;
	}
	
	
	//--Close--
	@Override
	public Runnable onClose(Runnable listener) {
		closeListeners.add(listener);
		return listener;
	}
	
	@NotNull
	@Override
	public List<Runnable> getCloseListeners() {
		return closeListeners;
	}
}