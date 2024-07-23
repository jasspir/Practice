package me.jass.practice.managers;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.jass.practice.PracticeAPI;
import me.jass.practice.utils.Text;

public class FileManager {
	private File kitsFile;
	private FileConfiguration kits;

	private File arenasFile;
	private FileConfiguration arenas;

	private File eloFile;
	private FileConfiguration elo;

	private File statsFile;
	private FileConfiguration stats;

	private final File dataFolder = PracticeAPI.INSTANCE.getPlugin().getDataFolder();

	public void load() {
		kitsFile = new File(dataFolder, "Kits.yml");
		kits = YamlConfiguration.loadConfiguration(kitsFile);

		arenasFile = new File(dataFolder, "Arenas.yml");
		arenas = YamlConfiguration.loadConfiguration(arenasFile);

		eloFile = new File(dataFolder, "Elo.yml");
		elo = YamlConfiguration.loadConfiguration(eloFile);

		statsFile = new File(dataFolder, "Stats.yml");
		stats = YamlConfiguration.loadConfiguration(statsFile);

		setup();
		saveAll();
	}

	public FileConfiguration get(final me.jass.practice.datatypes.File file) {
		switch (file) {
		case KITS:
			return kits;
		case ARENAS:
			return arenas;
		case STATS:
			return stats;
		case ELO:
			return elo;
		default:
			return null;
		}
	}

	public void save(final me.jass.practice.datatypes.File file) {
		switch (file) {
		case KITS:
			try {
				kits.save(kitsFile);
			} catch (final Exception e) {
				Text.alert("#redError while saving kits");
			}
			break;
		case ARENAS:
			try {
				arenas.save(arenasFile);
			} catch (final Exception e) {
				Text.alert("#redError while saving arenas");
			}
			break;
		case STATS:
			try {
				stats.save(statsFile);
			} catch (final Exception e) {
				Text.alert("#redError while saving stats");
			}
			break;
		case ELO:
			try {
				elo.save(eloFile);
			} catch (final Exception e) {
				Text.alert("#redError while saving elo");
			}
			break;
		}
	}

	public void setup() {
		if (!kits.contains("Kits.")) {
			kits.createSection("Kits");
		}

		if (!kits.contains("Extras.")) {
			kits.createSection("Extras");
		}

		if (!arenas.contains("Arenas.")) {
			arenas.createSection("Arenas");
		}

		if (!stats.contains("Stats.")) {
			stats.createSection("Stats");
		}

		if (!elo.contains("Elo.")) {
			elo.createSection("Elo");
		}
	}

	public void saveAll() {
		save(me.jass.practice.datatypes.File.KITS);
		save(me.jass.practice.datatypes.File.ARENAS);
		save(me.jass.practice.datatypes.File.STATS);
		save(me.jass.practice.datatypes.File.ELO);
	}
}
