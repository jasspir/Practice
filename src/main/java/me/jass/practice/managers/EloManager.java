package me.jass.practice.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.jass.practice.PracticeAPI;
import me.jass.practice.datatypes.File;
import me.jass.practice.datatypes.Queues;
import me.jass.practice.duels.Duelist;
import me.jass.practice.files.Elo;
import me.jass.practice.files.Kit;

public class EloManager {
	private final Map<Elo, Elo> cache = new HashMap<Elo, Elo>();
	private final Map<Kit, List<Elo>> soloLeaderboards = new HashMap<Kit, List<Elo>>();
	private final Map<Kit, List<Elo>> duoLeaderboards = new HashMap<Kit, List<Elo>>();

	private final FileManager fileManager = PracticeAPI.INSTANCE.getFileManager();

	public void load(final Player player) {
		if (isCached(player)) {
			return;
		}

		final UUID uuid = player.getUniqueId();

		if (fileManager.get(File.ELO) == null) {
			return;
		}

		PracticeAPI.INSTANCE.getKitManager().getAll().forEach(kit -> {
			//			if (kit.hasQueue(QueueType.COMPETITIVE)) {
			cache(getFromFile(uuid, kit, Queues.SOLO));
			cache(getFromFile(uuid, kit, Queues.SOLO));
			//			}
		});
	}

	public void unload(final UUID id) {
		PracticeAPI.INSTANCE.getKitManager().getAll().forEach(kit -> {
			remove(new Elo(id, kit, 0, Queues.SOLO));
			remove(new Elo(id, kit, 0, Queues.DUO));
		});
	}

	public void unloadAll() {
		cache.clear();
	}

	public void unloadOfflinePlayers() {
		final Set<Elo> elos = new HashSet<Elo>(cache.size());
		elos.addAll(cache.values());

		for (final Elo elo : elos) {
			if (!Bukkit.getPlayer(elo.getId()).isOnline()) {
				unload(elo.getId());
			}
		}
	}

	public void updateLeaderboard(final Kit kit, final Queues queue) {
		final Map<String, Elo> temp = new HashMap<String, Elo>();

		final String queueType = StringUtils.capitalize(queue.toString().toLowerCase());

		for (final String uuid : fileManager.get(File.ELO).getConfigurationSection("Elo." + kit.getName()).getKeys(false)) {

			if (fileManager.get(File.ELO).contains("Elo." + kit.getName() + "." + uuid + "." + queueType + ".Elo")) {
				final int elo = fileManager.get(File.ELO).getInt("Elo." + kit.getName() + "." + uuid + "." + queueType + ".Elo");
				temp.put(uuid, new Elo(UUID.fromString(uuid), kit, elo, queue));
			}
		}

		final List<Elo> sort = new ArrayList<Elo>(temp.values());
		Collections.sort(sort, Collections.reverseOrder());

		final List<Elo> elo = new ArrayList<Elo>(10);
		for (int i = 0; i < sort.size(); i++) {
			final int position = i + 1;
			fileManager.get(File.ELO).set("Elo." + kit.getName() + "." + sort.get(i).getId().toString() + "." + queueType + ".Pos", position);

			if (i < 10) {
				elo.add(sort.get(i));
			}
		}

		setLeaderboard(kit, queue, elo);
	}

	public List<Elo> getLeaderboard(final Kit kit, final Queues queue) {
		if (queue == Queues.SOLO) {
			return soloLeaderboards.get(kit);
		}

		else if (queue == Queues.DUO) {
			return duoLeaderboards.get(kit);
		}

		return null;
	}

	public void setLeaderboard(final Kit kit, final Queues queue, final List<Elo> elo) {
		if (queue == Queues.SOLO) {
			soloLeaderboards.put(kit, elo);
		}

		else if (queue == Queues.DUO) {
			duoLeaderboards.put(kit, elo);
		}
	}

	public void updateLeaderboards() {
		Bukkit.getServer().getScheduler().runTaskAsynchronously(PracticeAPI.INSTANCE.getPlugin(), () -> {
			PracticeAPI.INSTANCE.getKitManager().getAll().forEach(kit -> {
				//				if (kit.hasQueue(QueueType.COMPETITIVE)) {
				updateLeaderboard(kit, Queues.SOLO);
				updateLeaderboard(kit, Queues.DUO);
				//				}
			});

			fileManager.save(File.ELO);
		});
	}

	public Elo get(final Player player, final Kit kit, final Queues queue) {
		final Elo tempKey = new Elo(player.getUniqueId(), kit, 0, queue);
		return cache.get(tempKey);
	}

	public Elo getFromFile(final UUID uuid, final Kit kit, final Queues queue) {
		int elo = 1000;
		int pos = -1;

		final String queueType = StringUtils.capitalize(queue.toString().toLowerCase());

		if (fileManager.get(File.ELO).contains("Elo." + kit.getName() + "." + uuid.toString() + "." + queueType + ".Elo")) {
			elo = fileManager.get(File.ELO).getInt("Elo." + kit.getName() + "." + uuid.toString() + "." + queueType + ".Elo");
		}

		if (fileManager.get(File.ELO).contains("Elo." + kit.getName() + "." + uuid.toString() + "." + queueType + ".Pos")) {
			pos = fileManager.get(File.ELO).getInt("Elo." + kit.getName() + "." + uuid.toString() + "." + queueType + ".Pos");
		}

		final Elo file = new Elo(uuid, kit, elo, queue);
		file.setPosition(pos);

		return file;
	}

	public boolean isCached(final Player player) {
		for (final Kit kit : PracticeAPI.INSTANCE.getKitManager().getAll()) {
			return cache.get(new Elo(player.getUniqueId(), kit, 0, Queues.SOLO)) != null;
		}
		return false;
	}

	public Collection<Elo> getAll() {
		return cache.values();
	}

	public void cache(final Elo elo) {
		cache.put(elo, elo);
	}

	public void remove(final Elo elo) {
		cache.remove(elo);
	}

	public void updatePositions() {
		getAll().forEach(elo -> {
			final String queueType = StringUtils.capitalize(elo.getQueue().toString().toLowerCase());
			int position = 1000;
			if (fileManager.get(File.ELO).contains("Elo." + elo.getKit().getName() + "." + elo.getId().toString() + "." + queueType + ".Pos")) {
				position = fileManager.get(File.ELO).getInt("Elo." + elo.getKit().getName() + "." + elo.getId().toString() + "." + queueType + ".Pos");
			}
			elo.setPosition(position);
		});
	}

	public void outdateAll() {
		getAll().forEach(elo -> {
			elo.outdate();
		});
	}

	public double getAverage(final List<Duelist> duelists) {
		final List<Integer> elo = new ArrayList<Integer>();

		for (final Duelist duelist : duelists) {
			elo.add(get(duelist.getPlayer(), duelist.getKit(), Queues.DUO).getElo());
		}

		return elo.stream().mapToInt(val -> val).average().orElse(0.0);
	}

	public void increment(final List<Duelist> winner, final List<Duelist> loser) {
		double eloA;
		double eloB;

		if (winner.size() > 1) {
			eloA = getAverage(winner);
		} else {
			eloA = get(winner.get(0).getPlayer(), winner.get(0).getKit(), Queues.SOLO).getElo();
		}

		if (loser.size() > 1) {
			eloB = getAverage(loser);
		} else {
			eloB = get(loser.get(0).getPlayer(), loser.get(0).getKit(), Queues.SOLO).getElo();
		}

		final double predictionA = 1 / (1 + Math.pow(10, ((eloB - eloA) / 400)));
		final double predictionB = 1 / (1 + Math.pow(10, ((eloA - eloB) / 400)));

		Queues queue;

		if (winner.size() > 1) {
			queue = Queues.DUO;
		} else {
			queue = Queues.SOLO;
		}

		final String queueType = StringUtils.capitalize(queue.toString().toLowerCase());

		for (final Duelist duelist : winner) {
			final Elo elo = get(duelist.getPlayer(), duelist.getKit(), queue);
			final Elo newElo = new Elo(duelist.getPlayer().getUniqueId(), duelist.getKit(), (int) Math.round(elo.getElo() + 32 * (1 - predictionA)), queue);
			newElo.setPosition(elo.getPosition());
			fileManager.get(File.ELO).set("Elo." + newElo.getKit().getName() + "." + newElo.getId().toString() + "." + queueType + ".Elo", newElo.getElo());
			cache(newElo);
		}

		if (loser.size() > 1) {
			queue = Queues.DUO;
		} else {
			queue = Queues.SOLO;
		}

		for (final Duelist duelist : loser) {
			final Elo elo = get(duelist.getPlayer(), duelist.getKit(), queue);
			final Elo newElo = new Elo(duelist.getPlayer().getUniqueId(), duelist.getKit(), (int) Math.round(elo.getElo() + 32 * (0 - predictionB)), queue);
			newElo.setPosition(elo.getPosition());
			fileManager.get(File.ELO).set("Elo." + newElo.getKit().getName() + "." + newElo.getId().toString() + "." + queueType + ".Elo", newElo.getElo());
			cache(newElo);
		}
	}

	public int getWinnerIncrement(final List<Duelist> winner, final List<Duelist> loser) {
		double eloA;
		double eloB;

		if (winner.size() > 1) {
			eloA = getAverage(winner);
		} else {
			eloA = get(winner.get(0).getPlayer(), winner.get(0).getKit(), Queues.SOLO).getElo();
		}

		if (loser.size() > 1) {
			eloB = getAverage(loser);
		} else {
			eloB = get(loser.get(0).getPlayer(), loser.get(0).getKit(), Queues.SOLO).getElo();
		}

		final double prediction = 1 / (1 + Math.pow(10, ((eloB - eloA) / 400)));
		final int newElo = (int) Math.round(eloA + 32 * (1 - prediction));
		return (int) (newElo - eloA);
	}

	public int getLoserIncrement(final List<Duelist> winner, final List<Duelist> loser) {
		double eloA;
		double eloB;

		if (winner.size() > 1) {
			eloA = getAverage(winner);
		} else {
			eloA = get(winner.get(0).getPlayer(), winner.get(0).getKit(), Queues.SOLO).getElo();
		}

		if (loser.size() > 1) {
			eloB = getAverage(loser);
		} else {
			eloB = get(loser.get(0).getPlayer(), loser.get(0).getKit(), Queues.SOLO).getElo();
		}

		final double prediction = 1 / (1 + Math.pow(10, ((eloA - eloB) / 400)));
		final int newElo = (int) Math.round(eloB + 32 * (0 - prediction));
		return (int) (newElo - eloB);
	}
}
