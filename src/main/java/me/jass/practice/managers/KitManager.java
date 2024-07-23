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
import org.bukkit.potion.PotionEffect;

import me.jass.practice.PracticeAPI;
import me.jass.practice.datatypes.File;
import me.jass.practice.datatypes.QueueType;
import me.jass.practice.datatypes.ScoreType;
import me.jass.practice.files.Arena;
import me.jass.practice.files.Extra;
import me.jass.practice.files.Kit;

public class KitManager {
	private final Map<Kit, Kit> cache = new HashMap<Kit, Kit>();

	private final FileManager fileManager = PracticeAPI.INSTANCE.getFileManager();

	public void loadAll() {
		for (final String name : fileManager.get(File.KITS).getConfigurationSection("Kits").getKeys(false)) {
			load(name);
		}
	}

	public void load(final String name) {
		if (fileManager.get(File.KITS) == null) {
			return;
		}

		List<ItemStack> items = new ArrayList<ItemStack>();
		List<PotionEffect> effects = new ArrayList<PotionEffect>();
		List<String> arenaNames = new ArrayList<String>();
		List<String> extraNames = new ArrayList<String>();
		String display = "IRON_SWORD";
		String color = "§f";
		String scoreType = "NONE";
		int requiredScore = 0;
		boolean itemDrops = false;
		boolean itemPickups = false;
		boolean deathDrops = false;
		boolean hungerDepletion = false;
		boolean naturalRegeneration = false;
		boolean startingSaturation = false;
		boolean blockBreaking = false;
		boolean blockPlacing = false;
		boolean attacking = false;

		if (fileManager.get(File.KITS).contains("Kits." + name + ".Items")) {
			items = (List<ItemStack>) fileManager.get(File.KITS).getList("Kits." + name + ".Items");
		}

		if (fileManager.get(File.KITS).contains("Kits." + name + ".Effects")) {
			effects = (List<PotionEffect>) fileManager.get(File.KITS).getList("Kits." + name + ".Effects");
		}

		if (fileManager.get(File.KITS).contains("Kits." + name + ".Arenas")) {
			arenaNames = (List<String>) fileManager.get(File.KITS).getList("Kits." + name + ".Arenas");
		}

		if (fileManager.get(File.KITS).contains("Kits." + name + ".Extras")) {
			extraNames = (List<String>) fileManager.get(File.KITS).getList("Kits." + name + ".Extras");
		}

		if (fileManager.get(File.KITS).contains("Kits." + name + ".Display")) {
			display = fileManager.get(File.KITS).getString("Kits." + name + ".Display");
		}

		if (fileManager.get(File.KITS).contains("Kits." + name + ".Color")) {
			color = fileManager.get(File.KITS).getString("Kits." + name + ".Color");
		}

		if (fileManager.get(File.KITS).contains("Kits." + name + ".ScoreType")) {
			scoreType = fileManager.get(File.KITS).getString("Kits." + name + ".ScoreType");
		}

		if (fileManager.get(File.KITS).contains("Kits." + name + ".RequiredScore")) {
			requiredScore = fileManager.get(File.KITS).getInt("Kits." + name + ".RequiredScore");
		}

		if (fileManager.get(File.KITS).contains("Kits." + name + ".ItemDrops")) {
			itemDrops = fileManager.get(File.KITS).getBoolean("Kits." + name + ".ItemDrops");
		}

		if (fileManager.get(File.KITS).contains("Kits." + name + ".ItemPickups")) {
			itemPickups = fileManager.get(File.KITS).getBoolean("Kits." + name + ".ItemPickups");
		}

		if (fileManager.get(File.KITS).contains("Kits." + name + ".DeathDrops")) {
			deathDrops = fileManager.get(File.KITS).getBoolean("Kits." + name + ".DeathDrops");
		}

		if (fileManager.get(File.KITS).contains("Kits." + name + ".HungerDepletion")) {
			hungerDepletion = fileManager.get(File.KITS).getBoolean("Kits." + name + ".HungerDepletion");
		}

		if (fileManager.get(File.KITS).contains("Kits." + name + ".NaturalRegeneration")) {
			naturalRegeneration = fileManager.get(File.KITS).getBoolean("Kits." + name + ".NaturalRegeneration");
		}

		if (fileManager.get(File.KITS).contains("Kits." + name + ".StartingSaturation")) {
			startingSaturation = fileManager.get(File.KITS).getBoolean("Kits." + name + ".StartingSaturation");
		}

		if (fileManager.get(File.KITS).contains("Kits." + name + ".BlockBreaking")) {
			blockBreaking = fileManager.get(File.KITS).getBoolean("Kits." + name + ".BlockBreaking");
		}

		if (fileManager.get(File.KITS).contains("Kits." + name + ".BlockPlacing")) {
			blockPlacing = fileManager.get(File.KITS).getBoolean("Kits." + name + ".BlockPlacing");
		}

		if (fileManager.get(File.KITS).contains("Kits." + name + ".Attacking")) {
			attacking = fileManager.get(File.KITS).getBoolean("Kits." + name + ".Attacking");
		}

		final List<Arena> arenas = new ArrayList<Arena>();
		final List<Extra> extras = new ArrayList<Extra>();

		for (final String arenaName : arenaNames) {
			arenas.add(PracticeAPI.INSTANCE.getArenaManager().get(arenaName));
		}

		for (final String extraName : extraNames) {
			extras.add(PracticeAPI.INSTANCE.getExtraManager().get(extraName));
		}

		final Kit kit = new Kit(name);
		kit.setItems(items);
		kit.setEffects(effects);
		kit.setArenas(arenas);
		kit.setExtras(extras);
		kit.setDisplay(Material.getMaterial(display));
		kit.setColor(ChatColor.getByChar(color.substring(1)));
		kit.setScoreType(ScoreType.valueOf(scoreType));
		kit.setRequiredScore(requiredScore);
		kit.setItemDrops(itemDrops);
		kit.setItemPickups(itemPickups);
		kit.setDeathDrops(deathDrops);
		kit.setHungerDepletion(hungerDepletion);
		kit.setNaturalRegeneration(naturalRegeneration);
		kit.setStartingSaturation(startingSaturation);
		kit.setBlockBreaking(blockBreaking);
		kit.setBlockPlacing(blockPlacing);
		kit.setAttacking(attacking);

		cache(kit);
	}

	public Kit get(final String name) {
		return cache.get(new Kit(name));
	}

	public Collection<Kit> getAll() {
		return cache.values();
	}

	public void cache(final Kit kit) {
		cache.put(kit, kit);
	}

	public void unload(final Kit kit) {
		cache.remove(kit);
	}

	public void save(final Kit kit) {
		fileManager.get(File.KITS).set("Kits." + kit.getName() + ".Items", kit.getItems());

		fileManager.get(File.KITS).set("Kits." + kit.getName() + ".Effects", kit.getEffects());

		fileManager.get(File.KITS).set("Kits." + kit.getName() + ".Arenas", kit.getArenaNames());

		fileManager.get(File.KITS).set("Kits." + kit.getName() + ".Extras", kit.getExtraNames());

		fileManager.get(File.KITS).set("Kits." + kit.getName() + ".Display", kit.getDisplay().toString());

		fileManager.get(File.KITS).set("Kits." + kit.getName() + ".Color", kit.getColor().toString());

		fileManager.get(File.KITS).set("Kits." + kit.getName() + ".ScoreType", kit.getScoreType().toString());

		fileManager.get(File.KITS).set("Kits." + kit.getName() + ".RequiredScore", kit.getRequiredScore());

		fileManager.get(File.KITS).set("Kits." + kit.getName() + ".ItemDrops", kit.isItemDrops());

		fileManager.get(File.KITS).set("Kits." + kit.getName() + ".ItemPickups", kit.isItemPickups());

		fileManager.get(File.KITS).set("Kits." + kit.getName() + ".DeathDrops", kit.isDeathDrops());

		fileManager.get(File.KITS).set("Kits." + kit.getName() + ".HungerDepletion", kit.isHungerDepletion());

		fileManager.get(File.KITS).set("Kits." + kit.getName() + ".NaturalRegeneration", kit.isNaturalRegeneration());

		fileManager.get(File.KITS).set("Kits." + kit.getName() + ".StartingSaturation", kit.isStartingSaturation());

		fileManager.get(File.KITS).set("Kits." + kit.getName() + ".BlockBreaking", kit.isBlockBreaking());

		fileManager.get(File.KITS).set("Kits." + kit.getName() + ".BlockPlacing", kit.isBlockPlacing());

		fileManager.get(File.KITS).set("Kits." + kit.getName() + ".Attacking", kit.isAttacking());
	}

	public void delete(final Kit kit) {
		unload(kit);
		fileManager.get(File.KITS).set("Kits." + kit.getName(), null);
	}

	public void unloadAll() {
		cache.clear();
	}

	public int getAmount(final QueueType type) {
		int amount = 0;
		for (final Kit kit : cache.values()) {
			//			if (type == null || kit.hasQueue(type)) {
			amount++;
			//			}
		}
		return amount;
	}

	public List<String> getNames(final QueueType type) {
		final List<String> list = new ArrayList<String>();
		cache.values().forEach(kit -> {
			//			if (type == null || kit.hasQueue(type)) {
			list.add(kit.getName());
			//			}
		});
		return list;
	}

	public int[] getLayout(final Player player, final Kit kit) {
		final PersistentDataContainer data = player.getPersistentDataContainer();
		final NamespacedKey key = new NamespacedKey(PracticeAPI.INSTANCE.getPlugin(), "KIT_LAYOUT_" + kit.getName().toUpperCase());
		final PersistentDataType<int[], int[]> type = PersistentDataType.INTEGER_ARRAY;

		if (!data.has(key, type)) {
			return getDefaultLayout();
		}

		return data.get(key, type);
	}

	public void setLayout(final Player player, final Kit kit, final int[] layout) {
		final PersistentDataContainer data = player.getPersistentDataContainer();
		final NamespacedKey key = new NamespacedKey(PracticeAPI.INSTANCE.getPlugin(), "KIT_LAYOUT_" + kit.getName().toUpperCase());
		final PersistentDataType<int[], int[]> type = PersistentDataType.INTEGER_ARRAY;
		data.set(key, type, layout);
	}

	public int[] getDefaultLayout() {
		final int[] layout = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40 };
		return layout;
	}
}
