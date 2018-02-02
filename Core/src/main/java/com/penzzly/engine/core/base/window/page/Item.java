package com.penzzly.engine.core.base.window.page;

import io.reactivex.Observable;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;

public interface Item {
	//--Item--
	@NotNull Item item(ItemStack stack);
	
	@NotNull Item item(Observable<ItemStack> stacks);
	
	//--Icon--
	@NotNull
	default Item icon(@NotNull Material icon) {
		ItemStack stack = itemStack();
		stack.setType(icon);
		return item(stack);
	}
	
	@NotNull
	default Item icon(@NotNull Observable<Material> icon) {
		return item(icon.map(material -> {
			ItemStack stack = itemStack();
			stack.setType(material);
			return stack;
		}));
	}
	
	//--Title--
	@NotNull
	default Item title(String title) {
		ItemStack stack = itemStack();
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(title);
		stack.setItemMeta(meta);
		return item(stack);
	}
	
	@NotNull
	default Item title(@NotNull Observable<String> title) {
		return item(title.map(string -> {
			ItemStack stack = itemStack();
			ItemMeta meta = stack.getItemMeta();
			meta.setDisplayName(string);
			stack.setItemMeta(meta);
			return stack;
		}));
	}
	
	//--Amount--
	@NotNull
	default Item amount(@NotNull Number amount) {
		ItemStack stack = itemStack();
		stack.setAmount(amount.intValue());
		return item(stack);
	}
	
	@NotNull
	default Item amount(@NotNull Observable<Number> amount) {
		return item(amount.map(number -> {
			ItemStack stack = itemStack();
			stack.setAmount(number.intValue());
			return stack;
		}));
	}
	
	//--Data--
	@NotNull
	default Item data(@NotNull Number data) {
		ItemStack stack = itemStack();
		stack.setData(new MaterialData(data.byteValue()));
		return item(stack);
	}
	
	@NotNull
	default Item data(@NotNull Observable<Number> data) {
		return item(data.map(number -> {
			ItemStack stack = itemStack();
			stack.setData(new MaterialData(number.byteValue()));
			return stack;
		}));
	}
	
	//--Text--
	@NotNull
	default Item text(@NotNull Iterable<String> text) {
		ItemStack stack = itemStack();
		ItemMeta meta = stack.getItemMeta();
		meta.setLore(newArrayList(text));
		stack.setItemMeta(meta);
		return item(stack);
	}
	
	@NotNull
	default Item text(String... text) {
		ItemStack stack = itemStack();
		ItemMeta meta = stack.getItemMeta();
		meta.setLore(asList(text));
		stack.setItemMeta(meta);
		return item(stack);
	}
	
	@NotNull
	default Item text(@NotNull Observable<Iterable<String>> text) {
		return item(text.map(strings -> {
			ItemStack stack = itemStack();
			ItemMeta meta = stack.getItemMeta();
			meta.setLore(newArrayList(strings));
			stack.setItemMeta(meta);
			return stack;
		}));
	}
	
	//--Click--
	default Consumer<ClickType> onClick(ClickType type, @NotNull Runnable runnable) {
		return onClick(clickType -> {
			if (type == clickType) {
				runnable.run();
			}
		});
	}
	
	default Consumer<ClickType> onClick(@NotNull Runnable runnable) {
		return onClick(type -> runnable.run());
	}
	
	Consumer<ClickType> onClick(Consumer<ClickType> listener);
	
	//--Getter--
	@NotNull ItemStack itemStack();
	
	List<Consumer<ClickType>> clickListeners();
}