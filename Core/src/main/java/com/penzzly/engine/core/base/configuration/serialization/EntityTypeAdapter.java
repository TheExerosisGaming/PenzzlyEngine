package com.penzzly.engine.core.base.configuration.serialization;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

import static java.util.UUID.fromString;
import static org.bukkit.Bukkit.getWorlds;

public class EntityTypeAdapter extends TypeAdapter<Entity> {
	@Override
	public void write(@NotNull JsonWriter writer, @NotNull Entity entity) throws IOException {
		writer.value(entity.getUniqueId().toString());
	}
	
	@Override
	public Entity read(@NotNull JsonReader reader) throws IOException {
		UUID uuid = fromString(reader.nextString());
		return getWorlds().stream()
				.map(World::getEntities)
				.flatMap(Collection::stream)
				.filter(entity -> entity.getUniqueId().equals(uuid))
				.findAny()
				.orElseThrow(() -> new RuntimeException("Entity with ID " + uuid + " not found!"));
	}
}
