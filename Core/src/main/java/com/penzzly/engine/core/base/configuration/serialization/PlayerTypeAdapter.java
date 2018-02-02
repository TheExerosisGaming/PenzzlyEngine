package com.penzzly.engine.core.base.configuration.serialization;

import com.google.gson.stream.JsonReader;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class PlayerTypeAdapter extends OfflinePlayerTypeAdapter {
	@Nullable
	@Override
	public Player read(JsonReader reader) throws IOException {
		OfflinePlayer player = super.read(reader);
		if (player.isOnline()) {
			return player.getPlayer();
		}
		return null;
	}
}
