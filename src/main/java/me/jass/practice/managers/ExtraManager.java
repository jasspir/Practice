package me.jass.practice.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import me.jass.practice.PracticeAPI;
import me.jass.practice.datatypes.File;
import me.jass.practice.files.Extra;
import me.jass.practice.files.Kit;

public class ExtraManager {
	private final Map<Extra, Extra> cache = new HashMap<Extra, Extra>();

	private final FileManager fileManager = PracticeAPI.INSTANCE.getFileManager();

	public Extra get(final String name) {
		return cache.get(new Extra(name));
	}

	public Collection<Extra> getAll() {
		return cache.values();
	}

	public void loadAll() {
		for (final String name : fileManager.get(File.KITS).getConfigurationSection("Extras").getKeys(false)) {
			load(name);
		}
	}

	public void load(final String name) {
		if (fileManager.get(File.KITS) == null) {
			return;
		}

		List<ItemStack> items = new ArrayList<ItemStack>();
		String display = "NETHER_STAR";
		String color = "§f";

		if (fileManager.get(File.KITS).contains("Extras." + name + ".Items")) {
			items = (List<ItemStack>) fileManager.get(File.KITS).getList("Extras." + name + ".Items");
		}

		if (fileManager.get(File.KITS).contains("Extras." + name + ".Display")) {
			display = fileManager.get(File.KITS).getString("Extras." + name + ".Display");
		}

		if (fileManager.get(File.KITS).contains("Extras." + name + ".Color")) {
			color = fileManager.get(File.KITS).getString("Extras." + name + ".Color");
		}

		final Extra extra = new Extra(name);
		extra.setItems(items);
		extra.setDisplay(Material.getMaterial(display));
		extra.setColor(ChatColor.getByChar(color.substring(1)));

		cache(extra);
	}

	public void cache(final Extra extra) {
		cache.put(extra, extra);
	}

	public void unload(final Extra extra) {
		cache.remove(extra);
	}

	public void unloadAll() {
		cache.clear();
	}

	public void save(final Extra extra) {
		fileManager.get(File.KITS).set("Extras." + extra.getName() + ".Items", extra.getItems());

		fileManager.get(File.KITS).set("Extras." + extra.getName() + ".Display", extra.getDisplay().toString());

		fileManager.get(File.KITS).set("Extras." + extra.getName() + ".Color", extra.getColor().toString());
	}

	public void delete(final Extra extra) {
		unload(extra);
		fileManager.get(File.KITS).set("Extras." + extra.getName(), null);
	}

	public int getAmount(final Player player, final Kit kit) {
		int amount = 0;
		for (final Extra extra : cache.values()) {
			if ((player == null || player.hasPermission("practice.extras." + extra.getName())) && (kit == null || kit.hasExtra(extra))) {
				amount++;
			}
		}
		return amount;
	}

	public List<Extra> getSet(final Player player, final Kit kit) {
		final List<Extra> extras = new ArrayList<Extra>();

		for (final Extra extra : getAvailable(player, kit)) {
			if (isSet(player, extra)) {
				extras.add(extra);
			}
		}

		return extras;
	}

	public boolean isSet(final Player player, final Extra extra) {
		final PersistentDataContainer data = player.getPersistentDataContainer();
		final NamespacedKey key = new NamespacedKey(PracticeAPI.INSTANCE.getPlugin(), "EXTRA_SET_" + extra.getName().toUpperCase());
		final PersistentDataType<Integer, Integer> type = PersistentDataType.INTEGER;

		if (!data.has(key, type)) {
			return false;
		}

		return data.get(key, type) > 0 ? true : false;
	}

	public void set(final Player player, final Extra extra, final boolean enabled) {
		final PersistentDataContainer data = player.getPersistentDataContainer();
		final NamespacedKey key = new NamespacedKey(PracticeAPI.INSTANCE.getPlugin(), "EXTRA_SET_" + extra.getName().toUpperCase());
		final PersistentDataType<Integer, Integer> type = PersistentDataType.INTEGER;

		final int set = enabled ? 1 : 0;

		data.set(key, type, set);
	}

	public List<String> getNames() {
		final List<String> names = new ArrayList<String>();
		cache.values().forEach(extra -> {
			names.add(extra.getName());
		});
		return names;
	}

	public List<Extra> getAvailable(final Player player, final Kit kit) {
		final List<Extra> extras = new ArrayList<Extra>();
		for (final Extra extra : kit.getExtras()) {
			if (player.hasPermission("jduels.extra" + extra.getName())) {
				extras.add(extra);
			}
		}
		return extras;
	}
}
