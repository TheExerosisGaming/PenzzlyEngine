package com.penzzly.engine.core.base.configuration.serialization;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import static java.util.UUID.fromString;
import static org.bukkit.Bukkit.getOfflinePlayer;

public class OfflinePlayerTypeAdapter extends TypeAdapter<OfflinePlayer> {
	@Override
	public void write(@NotNull JsonWriter writer, @Nullable OfflinePlayer player) throws IOException {
		if (player == null) {
			writer.nullValue();
		} else {
			writer.value(player.getUniqueId().toString());
		}
	}
	
	@Nullable
	@Override
	public OfflinePlayer read(@NotNull JsonReader reader) throws IOException {
		return getOfflinePlayer(fromString(reader.nextString()));
	}
}