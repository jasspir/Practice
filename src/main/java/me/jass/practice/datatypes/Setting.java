package me.jass.practice.datatypes;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import me.jass.practice.PracticeAPI;

public enum Setting {
	PING_RANGE, DUEL_REQUESTS, DUEL_CHAT;

	final PersistentDataType<Integer, Integer> type = PersistentDataType.INTEGER;

	public int get(final Player player) {
		final PersistentDataContainer data = player.getPersistentDataContainer();
		final NamespacedKey key = getKey();

		if (!data.has(key, type)) {
			return 1;
		}

		return data.get(key, type);
	}

	public void set(final Player player, final int value) {
		final PersistentDataContainer data = player.getPersistentDataContainer();
		data.set(getKey(), type, value);
	}

	private NamespacedKey getKey() {
		return new NamespacedKey(PracticeAPI.INSTANCE.getPlugin(), this.toString());
	}
}
