package me.jass.practice.managers;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import lombok.Getter;
import lombok.Setter;
import me.jass.practice.PracticeAPI;
import me.jass.practice.datatypes.Queues;
import me.jass.practice.datatypes.RoundType;
import me.jass.practice.datatypes.Team;
import me.jass.practice.duels.Duel;
import me.jass.practice.duels.Duelist;
import me.jass.practice.duels.WorldDamage;
import me.jass.practice.files.Arena;
import me.jass.practice.files.Kit;
import me.jass.practice.utils.Text;

public class DuelManager {
	private final Map<Duel, Duel> cache = new HashMap<Duel, Duel>();
	private final Map<Player, Duel> index = new HashMap<Player, Duel>();
	private final Map<UUID, Duel> idIndex = new HashMap<UUID, Duel>();
	private final Map<Player, Duel> spectators = new HashMap<Player, Duel>();
	private final Map<Location, Duel> explosives = new HashMap<Location, Duel>();
	private final Map<Player, Inventory> views = new HashMap<Player, Inventory>();
	private final Map<UUID, Set<WorldDamage>> repairTasks = new HashMap<UUID, Set<WorldDamage>>();
	private Location recentlyDamagedLocation;
	private Duel recentlyDamagedDuel;
	private UUID repair = null;
	@Getter
	@Setter
	private int repairsPerTick = 100;

	private final FileManager fileManager = PracticeAPI.INSTANCE.getFileManager();

	public Duel get(final UUID uuid) {
		return idIndex.get(uuid);
	}

	public Collection<Duel> getAll() {
		return cache.values();
	}

	public Duel getSpectating(final Player player) {
		return spectators.get(player);
	}

	public void addSpectator(final Player player, final Duel duel) {
		spectators.put(player, duel);
	}

	public void removeSpectator(final Player player) {
		spectators.remove(player);
	}

	public Inventory getView(final Player player) {
		return views.get(player);
	}

	public void removeView(final Player player) {
		views.remove(player);
	}

	public Duel getClosestDuel(final Location location) {
		if (cache.isEmpty()) {
			return null;
		}

		if (recentlyDamagedLocation != null) {
			if (recentlyDamagedLocation.distance(location) < 10) {
				recentlyDamagedLocation = location;
				return recentlyDamagedDuel;
			}
		}

		double distance = 99999;
		Duel closestDuel = null;

		for (final Duel duel : getAll()) {
			for (final Arena arena : duel.getArenas()) {
				if (arena.getSpawnA().distance(location) < distance) {
					closestDuel = duel;
					distance = arena.getSpawnA().distance(location);
					recentlyDamagedLocation = location;
					recentlyDamagedDuel = duel;
				}

				if (arena.getSpawnB().distance(location) < distance) {
					closestDuel = duel;
					distance = arena.getSpawnB().distance(location);
					recentlyDamagedLocation = location;
					recentlyDamagedDuel = duel;
				}
			}
		}

		return closestDuel;
	}

	public void add(final Duel duel) {
		cache.put(duel, duel);
		addIdIndex(duel);
	}

	public void remove(final Duel duel) {
		for (final Duelist duelist : duel.getDuelists()) {
			index.remove(duelist.getPlayer());
		}

		cache.remove(duel);
	}

	public void clear() {
		cache.clear();
		index.clear();
		idIndex.clear();
		explosives.clear();
	}

	public void removeIndex(final Player player) {
		index.remove(player);
	}

	public void removeIdIndex(final UUID uuid) {
		idIndex.remove(uuid);
	}

	public void addIndex(final Player player, final Duel duel) {
		index.put(player, duel);
	}

	public void addIdIndex(final Duel duel) {
		idIndex.put(duel.getUuid(), duel);
	}

	public Duel getIndex(final Player player) {
		return index.get(player);
	}

	public Duel getIdIndex(final UUID uuid) {
		return idIndex.get(uuid);
	}

	public boolean isDueling(final Player player) {
		return index.get(player) != null;
	}

	public void setView(final Player player, final Inventory view) {
		views.put(player, view);
	}

	public void start(final List<Duelist> duelists, final int rounds, final RoundType type, final boolean competitive) {
		new Duel(duelists, rounds, type, competitive);
	}

	public void start(final List<Player> players, final Kit kit, final Arena arena, final int rounds, final RoundType type, final boolean competitive, final Queues queue) {
		final List<Duelist> duelists = new ArrayList<Duelist>();
		for (final Player player : players) {
			duelists.add(new Duelist(player, Team.NONE, kit, arena, queue));
		}

		start(duelists, rounds, type, competitive);
	}

	public void start(final List<Player> teamA, final List<Player> teamB, final Kit kit, final Arena arena, final int rounds, final RoundType type, final boolean competitive, final Queues queue) {
		final List<Duelist> duelists = new ArrayList<Duelist>();
		for (final Player player : teamA) {
			duelists.add(new Duelist(player, Team.A, kit, arena, queue));
		}

		for (final Player player : teamB) {
			duelists.add(new Duelist(player, Team.B, kit, arena, queue));
		}

		start(duelists, rounds, type, competitive);
	}

	public void endAll() {
		final List<Duel> duels = new ArrayList<Duel>();
		duels.addAll(cache.values());
		for (final Duel duel : duels) {
			duel.forceEnd();
		}
	}

	public List<String> duelistNames() {
		final List<String> players = new ArrayList<String>();
		for (final Player player : index.keySet()) {
			players.add(player.getName());
		}
		return players;
	}

	public void addExplosive(final Location location, final Duel duel) {
		explosives.put(location, duel);
		duel.addExplosive(location);
	}

	public void removeExplosive(final Location location) {
		explosives.remove(location);
	}

	public Duel getExplosive(final Location location) {
		return explosives.get(location);
	}

	public boolean isExplosive(final Location location) {
		return explosives.get(location) != null;
	}

	public int availablePlayers() {
		int amount = 0;
		for (final World world : Bukkit.getWorlds()) {
			for (final Player player : world.getPlayers()) {
				if (!isDueling(player)) {
					amount++;
				}
			}
		}
		return amount;
	}

	public int unavailablePlayers() {
		return index.size();
	}

	public List<Player> getAvailablePlayers() {
		final List<Player> players = new ArrayList<Player>();
		for (final World world : Bukkit.getWorlds()) {
			for (final Player player : world.getPlayers()) {
				if (!isDueling(player)) {
					players.add(player);
				}
			}
		}
		return players;
	}

	public Set<Player> getUnavailablePlayers() {
		return index.keySet();
	}

	public void repairWorld(final Duel duel) {
		final UUID uuid = duel.getUuid();
		final Set<WorldDamage> worldDamage = duel.getWorldDamage();
		repairTasks.put(uuid, worldDamage);

		if (worldDamage == null || worldDamage.size() == 0) {
			return;
		}

		final int size = worldDamage.size();
		final Iterator<WorldDamage> damages = worldDamage.iterator();
		final String arenas = StringUtils.join(duel.getArenaNames(), ", ");

		if (repair != null) {
			Text.alert("&7Detected repairs already running, waiting for them to finish...");
		}

		new BukkitRunnable() {
			int ticks = 0;

			@Override
			public void run() {
				if (repair == null) {
					repair = uuid;
				}

				if (repair == uuid) {
					ticks++;
					boolean finished = false;

					for (int repair = 0; repair < repairsPerTick; repair++) {
						if (damages.hasNext()) {
							final WorldDamage damage = damages.next();
							damage.repair();
							damages.remove();

							if (isExplosive(damage.getLocation())) {
								removeExplosive(damage.getLocation());
							}
						} else {
							finished = true;
							break;
						}
					}

					if (finished) {
						final float time = (float) ticks / 20;
						Text.alert("&7Successfully repaired &e" + size + " &7damaged blocks &8(&7Map: &e" + arenas + " &7Time: &e" + new DecimalFormat("#.#").format(time) + "s&8)");
						repairTasks.remove(uuid);
						repair = null;
						cancel();
					}
				}
			}
		}.runTaskTimer(PracticeAPI.INSTANCE.getPlugin(), 0, 1);
	}

	public void forceWorldRepair(final Duel duel) {
		final UUID uuid = duel.getUuid();
		final Set<WorldDamage> worldDamage = duel.getWorldDamage();
		repairTasks.put(uuid, worldDamage);

		if (worldDamage == null || worldDamage.size() == 0) {
			return;
		}

		final Iterator<WorldDamage> damages = worldDamage.iterator();

		while (damages.hasNext()) {
			final WorldDamage damage = damages.next();
			damage.repair();
			damages.remove();

			if (isExplosive(damage.getLocation())) {
				removeExplosive(damage.getLocation());
			}
		}

	}

	//	public List<Set<WorldDamage>> getTempDamage() {
	//		if (fileManager.get(File.ARENAS) == null) {
	//			return null;
	//		}
	//
	//		final List<Set<WorldDamage>> damage = new ArrayList<Set<WorldDamage>>();
	//
	//		for (final String uuid : fileManager.get(File.ARENAS).getConfigurationSection("TempDamage").getKeys(false)) {
	//			final List<BlockState> tempDamage = (List<BlockState>) fileManager.get(File.ARENAS).getList("TempDamage." + uuid);
	//			final Set<WorldDamage> thisDamage = new HashSet<WorldDamage>(tempDamage.size());
	//
	//			if (tempDamage == null || tempDamage.size() == 0) {
	//				continue;
	//			}
	//
	//			for (final BlockState block : tempDamage) {
	//				thisDamage.add(new WorldDamage(block));
	//			}
	//
	//			damage.add(thisDamage);
	//		}
	//
	//		return damage;
	//	}
	//
	//	public void repairTempDamage() {
	//		final List<Set<WorldDamage>> tempDamage = getTempDamage();
	//		clearTempDamage();
	//
	//		if (tempDamage == null || tempDamage.size() == 0) {
	//			return;
	//		}
	//
	//		for (final Set<WorldDamage> damage : tempDamage) {
	//			if (damage == null || damage.size() == 0) {
	//				return;
	//			}
	//
	//			final UUID uuid = UUID.randomUUID();
	//
	//			final int size = damage.size();
	//			final Iterator<WorldDamage> damages = damage.iterator();
	//
	//			if (repair != null) {
	//				Text.alert("&7Detected repairs already running, waiting for them to finish...");
	//			}
	//
	//			new BukkitRunnable() {
	//				int ticks = 0;
	//
	//				@Override
	//				public void run() {
	//					if (repair == null) {
	//						repair = uuid;
	//					}
	//
	//					if (repair == uuid) {
	//						ticks++;
	//						boolean finished = false;
	//
	//						for (int repair = 0; repair < repairsPerTick; repair++) {
	//							if (damages.hasNext()) {
	//								final WorldDamage damage = damages.next();
	//								damage.repair();
	//
	//								damages.remove();
	//
	//								if (isExplosive(damage.getLocation())) {
	//									removeExplosive(damage.getLocation());
	//								}
	//							} else {
	//								finished = true;
	//								break;
	//							}
	//						}
	//
	//						if (finished) {
	//							final float time = (float) ticks / 20;
	//							Text.alert("&7Successfully repaired &e" + size + " &7damaged blocks via file &8(&7Time: &e" + new DecimalFormat("#.#").format(time) + "s&8)");
	//							repair = null;
	//							cancel();
	//						}
	//					}
	//				}
	//			}.runTaskTimer(PracticeAPI.INSTANCE.getPlugin(), 0, 1);
	//		}
	//	}
	//
	//	public void addTempDamage(final UUID uuid, final Set<WorldDamage> damage) {
	//		final List<BlockState> tempDamage = new ArrayList<BlockState>();
	//		for (final WorldDamage worldDamage : damage) {
	//			tempDamage.add(worldDamage.getState());
	//		}
	//
	//		PracticeAPI.INSTANCE.getFileManager().get(File.ARENAS).set("TempDamage." + uuid.toString(), tempDamage);
	//	}
	//
	//	public void clearTempDamage() {
	//		PracticeAPI.INSTANCE.getFileManager().get(File.ARENAS).set("TempDamage", null);
	//		PracticeAPI.INSTANCE.getFileManager().save(File.ARENAS);
	//	}
}
