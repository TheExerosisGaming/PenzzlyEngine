package com.penzzly.engine.core.components.unorganized;

import com.penzzly.engine.architecture.base.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.material.MaterialData;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.bukkit.Bukkit.*;
import static org.bukkit.Material.SKULL_ITEM;
import static org.bukkit.SkullType.PLAYER;

public class HeadGodAppleComponent extends Component {
	
	public HeadGodAppleComponent() {
		ItemStack godApple = new ItemStack(Material.GOLDEN_APPLE, 1, (short) 1);
		ShapedRecipe recipe = new ShapedRecipe(godApple);
		recipe.shape("AAA", "ABA", "AAA");
		recipe.setIngredient('A', Material.GOLD_BLOCK);
		recipe.setIngredient('B', new MaterialData(SKULL_ITEM, (byte) PLAYER.ordinal()));
		
		onEnable(() -> addRecipe(recipe));
		onDisable(() -> {
			//There has got to be a reasonable way to do this lol
			List<Recipe> recipes = newArrayList(recipeIterator());
			clearRecipes();
			recipes.remove(recipe);
			recipes.forEach(Bukkit::addRecipe);
		});
	}
}
