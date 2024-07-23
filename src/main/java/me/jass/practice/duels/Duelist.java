package me.jass.practice.duels;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

import lombok.Getter;
import lombok.Setter;
import me.jass.practice.PracticeAPI;
import me.jass.practice.datatypes.Destination;
import me.jass.practice.datatypes.QueueType;
import me.jass.practice.datatypes.Queues;
import me.jass.practice.datatypes.ScoreType;
import me.jass.practice.datatypes.Team;
import me.jass.practice.files.Arena;
import me.jass.practice.files.Kit;

@Getter
public class Duelist {
	private final Player player;
	@Setter
	private Team team;
	private final Kit kit;
	private final Arena arena;
	private Duel duel;
	private double health = 20;
	private int hits = 0;
	private int crits = 0;
	private int combo = 0;
	private int maxCombo = 0;
	private int score = 0;
	private boolean left = false;
	private boolean frozen = true;
	private ScoreType scoreType = ScoreType.NONE;
	private long lastHit = System.currentTimeMillis();
	private final Queues queue;
	private final DecimalFormat df = new DecimalFormat("#.#");

	public Duelist(final Player player, final Team team, final Kit kit, final Arena arena, final Queues queue) {
		this.player = player;
		this.team = team;
		this.kit = kit;
		this.arena = arena;
		this.queue = queue;

		if (kit != null) {
			scoreType = kit.getScoreType();
		}
	}

	public String getHealth() {
		return df.format(health / 2);
	}

	public void setHealth() {
		health = player.getHealth();
	}

	public void requeue() {
		leave();

		final QueueType type = duel.isCompetitive() ? QueueType.COMPETITIVE : QueueType.CASUAL;

		if (queue == Queues.SOLO) {
			PracticeAPI.INSTANCE.getQueueManager().joinSolo(player, type, kit);
		}

		else if (queue == Queues.SOLO) {
			PracticeAPI.INSTANCE.getQueueManager().joinDuo(player, type, kit);
		}
	}

	public void increaseScore() {
		score++;
		duel.increaseScore(team);
		duel.displayActionBar();
		checkScore();
	}

	public void descreaseScore() {
		score--;
		duel.decreaseScore(team);
	}

	public void checkScore() {
		if (duel.getScore(team) == kit.getRequiredScore()) {
			duel.endRound(team);
		}
	}

	public List<Duelist> getTeamMembers() {
		return duel.getTeam(team);
	}

	public int getTeamScore() {
		return duel.getScore(team);
	}

	public int getOpposingTeamScore() {
		return duel.getScore(getOpposingTeam());
	}

	public int getTeamWins() {
		return duel.getWins(team);
	}

	public int getOpposingTeamWins() {
		return duel.getWins(getOpposingTeam());
	}

	public void setView() {
		final Inventory view = Bukkit.createInventory(null, 54);
		view.setStorageContents(PracticeAPI.INSTANCE.getGuiManager().getFilled54().getStorageContents());

		for (int i = 0; i < 41; i++) {
			final ItemStack item = player.getInventory().getItem(i);
			int slot = i;
			if (i > 35 && i < 40) {
				slot -= 40;
			} else if (i == 40) {
				slot = 6;
			} else if (i >= 0 && i <= 8) {
				slot += 36;
			}

			slot = Math.abs(slot);

			view.setItem(slot, item);
		}

		final int pots = getPots();

		if (pots > 0) {
			final ItemStack pot = PracticeAPI.INSTANCE.getGuiManager().createItem(Material.SPLASH_POTION, "#pinkPots", pots);
			final PotionMeta meta = (PotionMeta) pot.getItemMeta();
			meta.setBasePotionData(new PotionData(PotionType.INSTANT_HEAL));
			pot.setItemMeta(meta);
			view.setItem(48, pot);
		}

		ItemStack stats;

		if (scoreType == ScoreType.NONE) {
			stats = PracticeAPI.INSTANCE.getGuiManager().createItem(Material.PAPER, "#aquaStats", 1, "&7Health: #aqua" + getHealth() + "&7/#aqua10", "&7Crits: #aqua" + crits, "&7Hits: #aqua" + hits,
					"&7Max Combo: #aqua" + maxCombo);
		} else {
			stats = PracticeAPI.INSTANCE.getGuiManager().createItem(Material.PAPER, "#aquaStats", 1, "&7Score: #aqua" + score + "&7/#aqua" + kit.getRequiredScore());
		}

		view.setItem(49, stats);

		PracticeAPI.INSTANCE.getDuelManager().setView(player, view);
	}

	public int getPots() {
		int pots = 0;
		if (player.getInventory().contains(Material.SPLASH_POTION)) {
			for (final ItemStack item : player.getInventory()) {
				if (item == null) {
					continue;
				}

				if (item.getType() == Material.SPLASH_POTION) {
					final PotionMeta potion = (PotionMeta) item.getItemMeta();
					if (potion.getBasePotionData().getType() != PotionType.INSTANT_HEAL) {
						continue;
					}

					pots++;
				}
			}
		}

		return pots;
	}

	public int getMaxPots() {
		int pots = 0;
		if (hasPots()) {
			for (final ItemStack item : kit.getItems()) {
				if (item == null) {
					continue;
				}

				if (item.getType() == Material.SPLASH_POTION) {
					final PotionMeta potion = (PotionMeta) item.getItemMeta();

					if (potion.getBasePotionData().getType() != PotionType.INSTANT_HEAL) {
						continue;
					}

					pots++;
				}
			}
		}

		return pots;
	}

	public boolean hasPots() {
		return kit.getItems().contains(Material.SPLASH_POTION);
	}

	public void join(final Duel duel) {
		this.duel = duel;
	}

	public void freeze() {
		frozen = true;
	}

	public void unfreeze() {
		frozen = false;
	}

	public void addHit() {
		hits++;
		lastHit = System.currentTimeMillis();
	}

	public void addCrit() {
		crits++;
	}

	public void endCombo() {
		if (combo > maxCombo) {
			maxCombo = combo;
		}
		combo = 0;
	}

	public void heal() {
		disableFlight();
		player.setGameMode(GameMode.SURVIVAL);
		player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
		player.setFoodLevel(20);
		player.setExhaustion(0);
		player.setSaturation(kit.isStartingSaturation() ? 20 : 0);
		player.setSaturatedRegenRate(kit.isNaturalRegeneration() ? 10 : 99999);
		player.setUnsaturatedRegenRate(kit.isNaturalRegeneration() ? 80 : 99999);
		player.setLevel(0);
		player.setExp(0);
		player.setArrowsInBody(0);
		player.setFireTicks(0);

		for (final PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}
	}

	public void giveKit() {
		player.getInventory().clear();
		player.setItemOnCursor(null);

		player.getInventory().setContents(kit.getFixedItems(player).toArray(new ItemStack[0]));

		final List<PotionEffect> effects = new ArrayList<PotionEffect>();

		for (final PotionEffect effect : kit.getEffects()) {
			effects.add(new PotionEffect(effect.getType(), effect.getDuration(), effect.getAmplifier(), effect.isAmbient(), false));
		}

		player.addPotionEffects(effects);
	}

	public void warp(final Destination destination) {
		if (destination == Destination.SPAWN) {
			player.teleport(player.getWorld().getSpawnLocation());
			reset();
			giveMenu();
			enableFlight();
		}

		if (destination == Destination.ARENA) {
			player.teleport(arena.getSpawn(team));
		}
	}

	public void prepare() {
		heal();
		giveKit();
		warp(Destination.ARENA);
	}

	public boolean isQueued() {
		return queue != Queues.NONE;
	}

	public int getElo() {
		return PracticeAPI.INSTANCE.getEloManager().get(getPlayer(), kit, queue).getElo();
	}

	public int getRank() {
		return PracticeAPI.INSTANCE.getEloManager().get(getPlayer(), kit, queue).getPosition();
	}

	public int getEloIncrement() {
		return PracticeAPI.INSTANCE.getEloManager().getWinnerIncrement(duel.getTeam(team), duel.getTeam(getOpposingTeam()));
	}

	public int getEloDecrement() {
		return PracticeAPI.INSTANCE.getEloManager().getLoserIncrement(duel.getTeam(getOpposingTeam()), duel.getTeam(team));
	}

	public Team getOpposingTeam() {
		if (team == Team.A) {
			return Team.B;
		} else {
			return Team.A;
		}
	}

	public void leave() {
		left = true;
		warp(Destination.SPAWN);
		duel.removeDuelist(this);
	}

	public void reset() {
		enableFlight();
		player.setGameMode(GameMode.ADVENTURE);
		player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(4);
		player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
		player.setFoodLevel(20);
		player.setExhaustion(0);
		player.setSaturation(0);
		player.setSaturatedRegenRate(0);
		player.setUnsaturatedRegenRate(0);
		player.setLevel(0);
		player.setExp(0);
		player.setArrowsInBody(0);
		player.setFireTicks(0);
	}

	public void disableFlight() {
		PracticeAPI.INSTANCE.getMenuManager().disableFlight(player);
	}

	public void enableFlight() {
		PracticeAPI.INSTANCE.getMenuManager().enableFlight(player);
	}

	public void giveMenu() {
		PracticeAPI.INSTANCE.getMenuManager().give(player);
	}

	@Override
	public int hashCode() {
		return player.hashCode();
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null) {
			return false;
		}

		if (o == this) {
			return true;
		}

		if (!(o instanceof Duelist)) {
			return false;
		}

		final Duelist t = (Duelist) o;

		return (player.equals(t.getPlayer()));
	}
}