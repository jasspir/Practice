package me.jass.practice.managers;

import org.bukkit.configuration.file.FileConfiguration;

import lombok.Getter;
import me.jass.practice.PracticeAPI;

@Getter
public class ConfigManager {
	FileConfiguration config = PracticeAPI.INSTANCE.getPlugin().getConfig();
	private int repairsPerTick;
	private int saveInterval;
	private int leaderboardUpdateInterval;
	private int duelCountdownLength;
	private int requestTimeLimit;
	private int rematchPhase;
	private boolean joinToSpawn;
	private boolean repairsPerRound;
	private boolean unsafeExplosives;
	private boolean unsafeInteractions;
	private boolean hexColors;
	private boolean duelChat;
	private boolean friendlyFire;

	public void load() {
		PracticeAPI.INSTANCE.getPlugin().saveDefaultConfig();
		repairsPerTick = config.getInt("Repairs Per Tick");
		saveInterval = config.getInt("Save Interval");
		leaderboardUpdateInterval = config.getInt("Leaderboard Update Interval");
		duelCountdownLength = config.getInt("Duel Countdown Length");
		requestTimeLimit = config.getInt("Request Time Limit");
		rematchPhase = config.getInt("Rematch Phase");
		joinToSpawn = config.getBoolean("Join To Spawn");
		repairsPerRound = config.getBoolean("Repairs Per Round");
		unsafeExplosives = config.getBoolean("Unsafe Explosives");
		unsafeInteractions = config.getBoolean("Unsafe Interactions");
		hexColors = config.getBoolean("Hex Colors");
		duelChat = config.getBoolean("Duel Chat");
		friendlyFire = config.getBoolean("Friendly Fire");
	}
}