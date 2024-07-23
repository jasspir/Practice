package me.jass.practice.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;

import me.jass.practice.PracticeAPI;
import me.jass.practice.datatypes.File;
import me.jass.practice.datatypes.QueueType;
import me.jass.practice.datatypes.Team;
import me.jass.practice.files.Arena;
import me.jass.practice.files.Kit;

public class ArenaManager {
	private final Map<Arena, Arena> cache = new HashMap<Arena, Arena>();

	private final FileManager fileManager = PracticeAPI.INSTANCE.getFileManager();

	public void loadAll() {
		for (final String name : fileManager.get(File.ARENAS).getConfigurationSection("Arenas").getKeys(false)) {
			if (name.matches(".*[0-9].*")) {
				continue;
			}

			load(name);
		}

		loadSubs();
	}

	public void load(final String name) {
		if (fileManager.get(File.ARENAS) == null) {
			return;
		}

		Location spawnA = null;
		Location spawnB = null;
		String display = "GRASS_BLOCK";
		String color = "§f";

		if (fileManager.get(File.ARENAS).contains("Arenas." + name + ".spawnA")) {
			spawnA = fileManager.get(File.ARENAS).getLocation("Arenas." + name + ".spawnA");
		}

		if (fileManager.get(File.ARENAS).contains("Arenas." + name + ".spawnB")) {
			spawnB = fileManager.get(File.ARENAS).getLocation("Arenas." + name + ".spawnB");
		}

		if (fileManager.get(File.ARENAS).contains("Arenas." + name + ".Display")) {
			display = fileManager.get(File.ARENAS).getString("Arenas." + name + ".Display");
		}

		if (fileManager.get(File.ARENAS).contains("Arenas." + name + ".Color")) {
			color = fileManager.get(File.ARENAS).getString("Arenas." + name + ".Color");
		}

		final Arena arena = new Arena(name);
		arena.setSpawn(Team.A, spawnA);
		arena.setSpawn(Team.B, spawnB);
		arena.setDisplay(Material.getMaterial(display));
		arena.setColor(ChatColor.getByChar(color.substring(1)));

		cache(arena);
	}

	public void loadSubs() {
		if (fileManager.get(File.ARENAS) == null) {
			return;
		}

		final List<Arena> subArenas = new ArrayList<Arena>();

		for (final String name : fileManager.get(File.ARENAS).getConfigurationSection("Arenas").getKeys(false)) {
			if (!name.matches(".*[0-9].*")) {
				continue;
			}

			Location spawnA = null;
			Location spawnB = null;

			if (fileManager.get(File.ARENAS).contains("Arenas." + name + ".spawnA")) {
				spawnA = fileManager.get(File.ARENAS).getLocation("Arenas." + name + ".spawnA");
			}

			if (fileManager.get(File.ARENAS).contains("Arenas." + name + ".spawnB")) {
				spawnB = fileManager.get(File.ARENAS).getLocation("Arenas." + name + ".spawnB");
			}

			final Arena subArena = new Arena(name);
			subArena.setSpawn(Team.A, spawnA);
			subArena.setSpawn(Team.B, spawnB);

			subArenas.add(subArena);
		}

		getAll().forEach(arena -> {
			for (final Arena subArena : subArenas) {
				if (!subArena.getName().matches("^" + arena.getName() + "[0-9]")) {
					continue;
				}

				arena.addSubArena(subArena);
			}
		});
	}

	public void giveSub(final Arena sub) {
		getAll().forEach(arena -> {
			if (sub.getName().startsWith(arena.getName())) {
				arena.addSubArena(sub);
			}
		});
	}

	public Arena get(final String name) {
		return cache.get(new Arena(name));
	}

	public Collection<Arena> getAll() {
		return cache.values();
	}

	public void cache(final Arena arena) {
		cache.put(arena, arena);
	}

	public void unload(final Arena arena) {
		cache.remove(arena);
	}

	public void save(final Arena arena) {
		PracticeAPI.INSTANCE.getFileManager().get(File.ARENAS).set("Arenas." + arena.getName() + ".Display", arena.getDisplay().toString());

		PracticeAPI.INSTANCE.getFileManager().get(File.ARENAS).set("Arenas." + arena.getName() + ".spawnA", arena.getSpawn(Team.A));

		PracticeAPI.INSTANCE.getFileManager().get(File.ARENAS).set("Arenas." + arena.getName() + ".spawnB", arena.getSpawn(Team.B));

		PracticeAPI.INSTANCE.getFileManager().get(File.ARENAS).set("Arenas." + arena.getName() + ".Color", arena.getColor().toString());
	}

	public void delete(final Arena arena) {
		unload(arena);
		fileManager.get(File.ARENAS).set("Arenas." + arena.getName(), null);
	}

	public void unloadAll() {
		cache.clear();
	}

	public Arena getRandom(final Kit kit, final QueueType type) {
		final List<Arena> avaliableArenas = new ArrayList<Arena>();
		for (final Arena arena : cache.values()) {
			if (kit == null || kit.hasArena(arena)) { //(type == null || arena.canBeUsed(type))
				avaliableArenas.add(arena);
			}
		}

		Collections.shuffle(avaliableArenas);

		for (final Arena arena : avaliableArenas) {
			if (!arena.isOccupied()) {
				return arena;
			}

			for (final Arena subArena : arena.getSubArenas()) {
				if (!subArena.isOccupied()) {
					return subArena;
				}
			}
		}

		return null;
	}

	public Arena getAvailableSub(final Arena arena) {
		for (final Arena subArena : arena.getSubArenas()) {
			if (!subArena.isOccupied()) {
				return subArena;
			}
		}

		return null;
	}

	public int getAmount(final QueueType type, final Kit kit) {
		int amount = 0;
		for (final Arena arena : cache.values()) {
			if (kit == null || kit.hasArena(arena)) { //(type == null || arena.canBeUsed(type))
				amount++;
			}
		}
		return amount;
	}

	public List<String> getNames() {
		final List<String> names = new ArrayList<String>();
		cache.values().forEach(arena -> {
			names.add(arena.getName());
		});
		return names;
	}
}
