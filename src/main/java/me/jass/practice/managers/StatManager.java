package me.jass.practice.managers;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.jass.practice.PracticeAPI;
import me.jass.practice.datatypes.File;
import me.jass.practice.datatypes.Queues;
import me.jass.practice.datatypes.Stat;
import me.jass.practice.files.Kit;
import me.jass.practice.files.Stats;

public class StatManager {
	private final Map<Stats, Stats> cache = new HashMap<Stats, Stats>();

	private final FileManager fileManager = PracticeAPI.INSTANCE.getFileManager();

	public void load(final Player player) {
		final String uuid = player.getUniqueId().toString();

		if (isCached(player)) {
			return;
		}

		if (fileManager.get(File.STATS) == null) {
			return;
		}

		PracticeAPI.INSTANCE.getKitManager().getAll().forEach(kit -> {
			final Stats soloStats = new Stats(player.getUniqueId(), kit, Queues.SOLO);
			final Stats duoStats = new Stats(player.getUniqueId(), kit, Queues.DUO);
			final Stats noneStats = new Stats(player.getUniqueId(), kit, Queues.NONE);
			int soloWins = 0;
			int soloLosses = 0;
			int duoWins = 0;
			int duoLosses = 0;
			int noneWins = 0;
			int noneLosses = 0;

			if (fileManager.get(File.STATS).contains("Stats." + kit.getName() + "." + uuid + ".Solo.Wins")) {
				soloWins = fileManager.get(File.STATS).getInt("Stats." + kit.getName() + "." + uuid + ".Solo.Wins");
			}

			if (fileManager.get(File.STATS).contains("Stats." + kit.getName() + "." + uuid + ".Solo.Losses")) {
				soloLosses = fileManager.get(File.STATS).getInt("Stats." + kit.getName() + "." + uuid + ".Solo.Losses");
			}

			if (fileManager.get(File.STATS).contains("Stats." + kit.getName() + "." + uuid + ".Duo.Wins")) {
				duoWins = fileManager.get(File.STATS).getInt("Stats." + kit.getName() + "." + uuid + ".Duo.Wins");
			}

			if (fileManager.get(File.STATS).contains("Stats." + kit.getName() + "." + uuid + ".Duo.Losses")) {
				duoLosses = fileManager.get(File.STATS).getInt("Stats." + kit.getName() + "." + uuid + ".Duo.Losses");
			}

			if (fileManager.get(File.STATS).contains("Stats." + kit.getName() + "." + uuid + ".None.Wins")) {
				noneWins = fileManager.get(File.STATS).getInt("Stats." + kit.getName() + "." + uuid + ".None.Wins");
			}

			if (fileManager.get(File.STATS).contains("Stats." + kit.getName() + "." + uuid + ".None.Losses")) {
				noneLosses = fileManager.get(File.STATS).getInt("Stats." + kit.getName() + "." + uuid + ".None.Losses");
			}

			soloStats.set(Stat.WINS, soloWins);
			soloStats.set(Stat.LOSSES, soloLosses);
			duoStats.set(Stat.WINS, duoWins);
			duoStats.set(Stat.LOSSES, duoLosses);
			noneStats.set(Stat.WINS, noneWins);
			noneStats.set(Stat.LOSSES, noneLosses);

			cache(soloStats);
			cache(duoStats);
			cache(noneStats);
		});

		final Stats totalStats = new Stats(player.getUniqueId(), null, null);
		final Stats soloStats = new Stats(player.getUniqueId(), null, Queues.SOLO);
		final Stats duoStats = new Stats(player.getUniqueId(), null, Queues.DUO);
		final Stats noneStats = new Stats(player.getUniqueId(), null, Queues.NONE);
		int soloWins = 0;
		int soloLosses = 0;
		int duoWins = 0;
		int duoLosses = 0;
		int noneWins = 0;
		int noneLosses = 0;

		if (fileManager.get(File.STATS).contains("Stats.Stats-Total." + uuid + ".Solo.Wins")) {
			soloWins = fileManager.get(File.STATS).getInt("Stats.Stats-Total." + uuid + ".Solo.Wins");
		}

		if (fileManager.get(File.STATS).contains("Stats.Stats-Total." + uuid + ".Solo.Losses")) {
			soloLosses = fileManager.get(File.STATS).getInt("Stats.Stats-Total." + uuid + ".Solo.Losses");
		}

		if (fileManager.get(File.STATS).contains("Stats.Stats-Total." + uuid + ".Duo.Wins")) {
			duoWins = fileManager.get(File.STATS).getInt("Stats.Stats-Total." + uuid + ".Duo.Wins");
		}

		if (fileManager.get(File.STATS).contains("Stats.Stats-Total." + uuid + ".Duo.Losses")) {
			duoLosses = fileManager.get(File.STATS).getInt("Stats.Stats-Total." + uuid + ".Duo.Losses");
		}

		if (fileManager.get(File.STATS).contains("Stats.Stats-Total." + uuid + ".None.Wins")) {
			noneWins = fileManager.get(File.STATS).getInt("Stats.Stats-Total." + uuid + ".None.Wins");
		}

		if (fileManager.get(File.STATS).contains("Stats.Stats-Total." + uuid + ".None.Losses")) {
			noneLosses = fileManager.get(File.STATS).getInt("Stats.Stats-Total." + uuid + ".None.Losses");
		}

		soloStats.set(Stat.WINS, soloWins);
		soloStats.set(Stat.LOSSES, soloLosses);
		duoStats.set(Stat.WINS, duoWins);
		duoStats.set(Stat.LOSSES, duoLosses);
		noneStats.set(Stat.WINS, noneWins);
		noneStats.set(Stat.LOSSES, noneLosses);
		totalStats.set(Stat.WINS, soloWins + duoWins + noneWins);
		totalStats.set(Stat.LOSSES, soloLosses + duoLosses + noneLosses);

		cache(soloStats);
		cache(duoStats);
		cache(noneStats);
		cache(totalStats);
	}

	public boolean isCached(final Player player) {
		return get(player, null, null) != null;
	}

	public void unload(final UUID id) {
		remove(new Stats(id, null, Queues.SOLO));
		remove(new Stats(id, null, Queues.DUO));
		remove(new Stats(id, null, Queues.NONE));
		remove(new Stats(id, null, null));
		PracticeAPI.INSTANCE.getKitManager().getAll().forEach(kit -> {
			remove(new Stats(id, kit, Queues.SOLO));
			remove(new Stats(id, kit, Queues.DUO));
			remove(new Stats(id, kit, Queues.NONE));
		});
	}

	public void addWin(final Player player, final Kit kit, final Queues queue) {
		final Stats stats = get(player, kit, queue);
		final Stats totalStats = get(player, null, queue);
		final Stats fullStats = get(player, null, null);
		stats.increase(Stat.WINS);
		totalStats.increase(Stat.WINS);
		fullStats.increase(Stat.WINS);
		final String queueType = StringUtils.capitalize(queue.toString().toLowerCase());
		fileManager.get(File.STATS).set("Stats." + kit.getName() + "." + player.getUniqueId().toString() + "." + queueType + ".Wins", stats.getWins());
		fileManager.get(File.STATS).set("Stats.Stats-Total." + player.getUniqueId().toString() + "." + queueType + ".Wins", totalStats.getWins());
		cache(stats);
		cache(totalStats);
		cache(fullStats);
	}

	public void addLoss(final Player player, final Kit kit, final Queues queue) {
		final Stats stats = get(player, kit, queue);
		final Stats totalStats = get(player, null, queue);
		final Stats fullStats = get(player, null, null);
		stats.increase(Stat.LOSSES);
		totalStats.increase(Stat.LOSSES);
		fullStats.increase(Stat.LOSSES);
		final String queueType = StringUtils.capitalize(queue.toString().toLowerCase());
		fileManager.get(File.STATS).set("Stats." + kit.getName() + "." + player.getUniqueId().toString() + "." + queueType + ".Losses", stats.getLosses());
		fileManager.get(File.STATS).set("Stats.Stats-Total." + player.getUniqueId().toString() + "." + queueType + ".Losses", totalStats.getLosses());
		cache(stats);
		cache(totalStats);
		cache(fullStats);
	}

	public Stats get(final Player player, final Kit kit, final Queues queue) {
		Stats stats = cache.get(new Stats(player.getUniqueId(), kit, queue));

		if (stats == null) {
			stats = cache(new Stats(player.getUniqueId(), kit, queue));
		}

		return stats;
	}

	public Stats getOverall(final Player player, final Queues queue) {
		return get(player, null, queue);
	}

	public Stats getOverall(final Player player) {
		return get(player, null, null);
	}

	public void unloadOfflinePlayers() {
		final Set<Stats> stats = new HashSet<Stats>(cache.size());
		stats.addAll(cache.values());

		for (final Stats stat : stats) {
			if (!Bukkit.getPlayer(stat.getId()).isOnline()) {
				unload(stat.getId());
			}
		}
	}

	public void outdateAll() {
		getAll().forEach(stats -> {
			stats.outdate();
		});
	}

	public Collection<Stats> getAll() {
		return cache.values();
	}

	public Stats cache(final Stats stats) {
		return cache.put(stats, stats);
	}

	public void remove(final Stats stats) {
		cache.remove(stats);
	}

	public void unloadAll() {
		cache.clear();
	}
}
