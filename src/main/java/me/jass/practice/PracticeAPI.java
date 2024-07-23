package me.jass.practice;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import me.jass.practice.managers.ArenaManager;
import me.jass.practice.managers.CommandManager;
import me.jass.practice.managers.ConfigManager;
import me.jass.practice.managers.DuelManager;
import me.jass.practice.managers.EloManager;
import me.jass.practice.managers.EventManager;
import me.jass.practice.managers.ExtraManager;
import me.jass.practice.managers.FileManager;
import me.jass.practice.managers.GUIManager;
import me.jass.practice.managers.KitManager;
import me.jass.practice.managers.MenuManager;
import me.jass.practice.managers.MessageManager;
import me.jass.practice.managers.QueueManager;
import me.jass.practice.managers.RequestManager;
import me.jass.practice.managers.StatManager;
import me.jass.practice.utils.Text;

@Getter
public enum PracticeAPI {
	INSTANCE;

	private JavaPlugin plugin;
	private ExternalAPI externalAPI;
	private FileManager fileManager;
	private ConfigManager configManager;
	private MessageManager messageManager;
	private DuelManager duelManager;
	private ArenaManager arenaManager;
	private KitManager kitManager;
	private ExtraManager extraManager;
	private QueueManager queueManager;
	private RequestManager requestManager;
	private EloManager eloManager;
	private StatManager statManager;
	private GUIManager guiManager;
	private MenuManager menuManager;
	private CommandManager commandManager;
	private EventManager eventManager;

	public void start(final JavaPlugin plugin) {
		this.plugin = plugin;
		assert plugin != null : "Error while starting Practice";
		externalAPI = new ExternalAPI();

		Bukkit.getServicesManager().register(ExternalAPI.class, externalAPI, plugin, ServicePriority.Normal);

		load();
	}

	public void stop() {
		assert plugin != null : "Error while stopping Practice";
		duelManager.endAll();
		fileManager.saveAll();
		Bukkit.getScheduler().cancelTasks(plugin);
		plugin = null;
	}

	public void load() {
		fileManager = new FileManager();
		configManager = new ConfigManager();
		messageManager = new MessageManager();
		duelManager = new DuelManager();
		arenaManager = new ArenaManager();
		kitManager = new KitManager();
		extraManager = new ExtraManager();
		queueManager = new QueueManager();
		requestManager = new RequestManager();
		eloManager = new EloManager();
		statManager = new StatManager();
		guiManager = new GUIManager();
		menuManager = new MenuManager();
		commandManager = new CommandManager();
		eventManager = new EventManager();

		fileManager.load();
		configManager.load();
		messageManager.load();
		arenaManager.loadAll();
		extraManager.loadAll();
		kitManager.loadAll();
		queueManager.load();
		guiManager.loadDefault();
		menuManager.load();

		Text.load();

		commandManager.register();
		eventManager.registerAll();

		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
			new PracticeExpansion(plugin).register();
		}

		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			commandManager.registerCompletions();
		}, 5);

		fileManager.saveAll();
		eloManager.outdateAll();
		statManager.outdateAll();
		eloManager.unloadOfflinePlayers();
		statManager.unloadOfflinePlayers();
		eloManager.updatePositions();
		eloManager.updateLeaderboards();

		initiateTimers();
		fixPlayers();
	}

	public void reload() {
		Bukkit.getScheduler().cancelTasks(plugin);
		configManager.load();
		messageManager.load();
		initiateTimers();
	}

	public void initiateTimers() {
		final int saveInterval = configManager.getSaveInterval() < 1 ? 1 : configManager.getSaveInterval();
		final int leaderboardUpdateInterval = configManager.getLeaderboardUpdateInterval() < 1 ? 1 : configManager.getLeaderboardUpdateInterval();

		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
			Text.alert("&7Saving Files");
			fileManager.saveAll();
			eloManager.outdateAll();
			statManager.outdateAll();
			eloManager.unloadOfflinePlayers();
			statManager.unloadOfflinePlayers();
			eloManager.updatePositions();
		}, 0, saveInterval * 1200);

		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
			Text.alert("&7Updating Leaderboards");
			eloManager.updateLeaderboards();
		}, 600, leaderboardUpdateInterval * 1200);
	}

	public void fixPlayers() {
		for (final Player player : Bukkit.getOnlinePlayers()) {
			if (PracticeAPI.INSTANCE.getConfigManager().isJoinToSpawn()) {
				player.teleport(player.getLocation().getWorld().getSpawnLocation());
			}

			player.setGameMode(GameMode.ADVENTURE);
			player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
			player.setFoodLevel(20);
			player.setExhaustion(0);
			player.setSaturation(0);
			player.setSaturatedRegenRate(0);
			player.setUnsaturatedRegenRate(0);
			player.setLevel(0);
			player.setExp(0);

			PracticeAPI.INSTANCE.getEloManager().load(player);
			PracticeAPI.INSTANCE.getStatManager().load(player);

			Bukkit.getScheduler().runTaskLater(PracticeAPI.INSTANCE.getPlugin(), () -> menuManager.give(player), 5);
			Bukkit.getScheduler().runTaskLater(PracticeAPI.INSTANCE.getPlugin(), () -> menuManager.enableFlight(player), 20);
		}
	}
}
