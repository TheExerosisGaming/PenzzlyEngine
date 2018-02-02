package com.penzzly.engine.core.base.configuration.serialization;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static org.bukkit.Material.values;

public class MaterialTypeAdapter extends TypeAdapter<Material> {
	@Override
	public void write(@NotNull JsonWriter writer, @NotNull Material value) throws IOException {
		writer.value(value.ordinal());
	}
	
	@Override
	public Material read(@NotNull JsonReader reader) throws IOException {
		return values()[reader.nextInt()];
	}
}
