package com.penzzly.engine.core.base.configuration.serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.yaml.snakeyaml.Yaml;

import static com.google.gson.FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES;

public interface Serializers {
	Gson GSON = new GsonBuilder()
			.registerTypeAdapter(OfflinePlayer.class, new OfflinePlayerTypeAdapter())
			.registerTypeHierarchyAdapter(Entity.class, new EntityTypeAdapter())
			.registerTypeAdapter(Player.class, new PlayerTypeAdapter())
			.registerTypeAdapter(Material.class, new MaterialTypeAdapter())
			.setPrettyPrinting()
			.enableComplexMapKeySerialization()
			.setFieldNamingPolicy(LOWER_CASE_WITH_UNDERSCORES)
			.create();
	
	Yaml YAML = new Yaml();
}