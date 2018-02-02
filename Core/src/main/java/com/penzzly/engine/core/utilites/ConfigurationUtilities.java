package com.penzzly.engine.core.utilites;

import com.penzzly.engine.core.base.configuration.MapConfiguration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.bukkit.ChatColor.translateAlternateColorCodes;

public final class ConfigurationUtilities {
	private ConfigurationUtilities() {
	
	}
	
	@SuppressWarnings("unchecked")
	public static Vector getVector(@NotNull MapConfiguration config) {
		String[] xyz = config.getOrSet("Location", "").replaceAll(" ", "").split(",");
		if (xyz.length < 3) {
			return null;
		}
		Double x = Double.parseDouble(xyz[0]);
		Double y = Double.parseDouble(xyz[1]);
		Double z = Double.parseDouble(xyz[2]);
		return new Vector(x, y, z);
	}
	
	public static ItemStack getItem(@NotNull MapConfiguration config) {
		if (!config.contains("Type")) {
			return null;
		}
		return getItem(config, (ItemStack) null);
	}
	
	@Nullable
	public static ItemStack getItem(@NotNull MapConfiguration config, @NotNull Material defaultValue) {
		return getItem(config, new ItemStack(defaultValue));
	}
	
	@Nullable
	public static ItemStack getItem(@NotNull MapConfiguration config, @Nullable ItemStack defaultValue) {
		MaterialData data = getData(config);
		if (data != null) {
			if (defaultValue == null) {
				defaultValue = new ItemStack(data.getItemType());
			}
			defaultValue.setData(data);
		}
		defaultValue.setAmount(config.getOrSet("Amount", defaultValue.getAmount()));
		ItemMeta meta = defaultValue.getItemMeta();
		String name = config.get("Name");
		if (name != null) {
			meta.setDisplayName(translateAlternateColorCodes('&', name));
		}
		List<String> lore = config.<List<String>>getOrSet("Text", new ArrayList<>()).stream().map(line -> translateAlternateColorCodes('&', line)).collect(toList());
		if (!lore.isEmpty()) {
			meta.setLore(lore);
		}
		defaultValue.setItemMeta(meta);
		return defaultValue;
	}
	
	public static PotionEffect getEffect(@NotNull MapConfiguration config) {
		PotionEffectType type = PotionEffectType.getByName(config.<String>get("Type").toLowerCase().replace(" ", "_"));
		if (type != null) {
			return new PotionEffect(type, config.getOrSet("Length", 1), 0);
		}
		return null;
	}
	
	@Nullable
	public static MaterialData getData(@NotNull MapConfiguration config, MaterialData defaultValue) {
		MaterialData data = getData(config);
		if (data == null) {
			return defaultValue;
		}
		return data;
	}
	
	public static MaterialData getData(@NotNull MapConfiguration config) {
		Material type = getType(config);
		if (type != null) {
			return new MaterialData(type, config.getOrSet("Data", 0).byteValue());
		}
		return null;
	}
	
	@Nullable
	public static Material getType(@NotNull MapConfiguration config) {
		return getType(config.<String>get("Type"));
	}
	
	@Nullable
	public static Material getType(@NotNull MapConfiguration config, Material defaultValue) {
		Material type = getType(config);
		if (type == null) {
			return defaultValue;
		}
		return type;
	}
	
	@Nullable
	public static Material getType(@NotNull String string, Material defaultValue) {
		Material type = getType(string);
		if (type == null) {
			return defaultValue;
		}
		return type;
	}
	
	@Nullable
	public static Material getType(@Nullable String string) {
		if (string == null) {
			return null;
		}
		return Material.valueOf(string.toUpperCase().replaceAll(" ", "_"));
	}
}