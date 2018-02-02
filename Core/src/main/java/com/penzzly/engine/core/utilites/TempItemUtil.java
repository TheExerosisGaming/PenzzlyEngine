package com.penzzly.engine.core.utilites;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TempItemUtil {
	
	public static void addEnchantment(@NotNull ItemStack stack, Enchantment enchantment, int levels) {
		levels = stack.getEnchantmentLevel(enchantment) + levels;
		stack.addUnsafeEnchantment(enchantment, levels);
	}
	
	public static void removeEnchantment(@NotNull ItemStack stack, Enchantment enchantment, int levels) {
		levels = stack.getEnchantmentLevel(enchantment) - levels;
		if (levels <= 0) {
			stack.removeEnchantment(enchantment);
		} else {
			stack.addUnsafeEnchantment(enchantment, levels);
		}
	}
}
