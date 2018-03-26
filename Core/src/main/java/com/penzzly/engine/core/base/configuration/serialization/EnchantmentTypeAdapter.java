package com.penzzly.engine.core.base.configuration.serialization;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bukkit.enchantments.Enchantment;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.bukkit.enchantments.Enchantment.getByName;

public class EnchantmentTypeAdapter extends TypeAdapter<Map<Enchantment, Integer>> {
	@Override
	public void write(JsonWriter write, Map<Enchantment, Integer> enchants) throws IOException {
		write.beginArray();
		for (Map.Entry<Enchantment, Integer> enchant : enchants.entrySet())
			write
					.beginObject()
					.name("name").value(enchant.getKey().toString())
					.name("level").value(enchant.getValue())
					.endObject();
		write.endArray();
	}
	
	@Override
	public Map<Enchantment, Integer> read(JsonReader read) throws IOException {
		final Map<Enchantment, Integer> enchants = new HashMap<>();
		while (read.hasNext())
			enchants.put(getByName(read.nextString()), read.nextInt());
		return enchants;
	}
}
