package com.penzzly.engine.core.base.configuration.serialization;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ItemMetaTypeAdapter extends TypeAdapter<ItemMeta> {
	@Override
	public void write(JsonWriter writer, ItemMeta meta) throws IOException {
		writer.beginObject()
				.name("enchantments")
				.beginArray();
		
		for (Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
			writer.beginObject()
					.name("name")
					.value(entry.getKey().toString())
					.name("level")
					.value(entry.getValue());
		}
		writer.endArray()
				.endObject();
	}
	
	@Override
	public ItemMeta read(JsonReader reader) throws IOException {
		final Map<Enchantment, Integer> enchants = new HashMap<>();
		while (reader.hasNext()) {
			if (!reader.nextName().equals("enchantment"))
				continue;
			reader.beginArray();
			Enchantment type = null;
			int level = -1;
			while (reader.hasNext()) {
				String name = reader.nextName();
				if (name.equals("name"))
					type = Enchantment.getByName(reader.nextString()
							.replace(' ', '_')
							.toUpperCase()
					);
				else if (name.equals("level"))
					level = reader.nextInt();
				
				if (level == -1 || type == null)
					continue;
				
				enchants.put(type, level);
				type = null;
				level = -1;
			}
			reader.endArray();
		}
		
		return new CraftMetaItem()
	}
	
}