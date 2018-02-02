package com.penzzly.engine.core.base.window.page;

import com.google.common.collect.BiMap;
import com.penzzly.engine.architecture.functions.Optional;
import com.penzzly.engine.core.base.window.elements.Element;
import io.reactivex.Observable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.penzzly.engine.architecture.functions.Optional.empty;
import static com.penzzly.engine.architecture.functions.Optional.of;

public interface Page extends Element<Page> {
	//--Displayable--
	@NotNull
	default Item element() {
		for (int i = 0; i < size(); i++)
			if (!elements().containsKey(i)) {
				return element(i);
			}
		throw new IndexOutOfBoundsException("There is no room left in this page!");
	}
	
	@NotNull
	default Item element(int row, int column) {
		return element((column * 9) + row);
	}
	
	@NotNull Item element(int index);
	
	//--Remove--
	@Nullable
	default Page remove(int row, int column) {
		return remove((column * 9) + row);
	}
	
	@Nullable
	default Page remove(int index) {
		return remove(elements().get(index));
	}
	
	@Nullable Page remove(Item item);
	
	
	//--Displayable At--
	@NotNull
	default Optional<Item> elementAt(int index) {
		if (elements().containsKey(index)) {
			return of(elements().get(index));
		}
		return empty();
	}
	
	@NotNull
	default Optional<Item> elementAt(int row, int column) {
		return elementAt((column * 9) + row);
	}
	
	
	//--Size--
	@NotNull
	default Page size(@NotNull Observable<Number> size) {
		size.subscribe(this::size);
		return this;
	}
	
	@NotNull Page size(Number size);
	
	int size();
	
	
	//--Title---
	@NotNull Page title(Object title);
	
	@NotNull Page title(Observable<Object> title);
	
	//--Close--
	Runnable onClose(Runnable listener);
	
	@NotNull List<Runnable> getCloseListeners();
	
	//--Elements--
	@NotNull BiMap<Integer, Item> elements();
}
