package me.jass.practice.duels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;

import lombok.Getter;
import me.jass.practice.PracticeAPI;
import me.jass.practice.datatypes.Destination;
import me.jass.practice.datatypes.Message;
import me.jass.practice.datatypes.RoundType;
import me.jass.practice.datatypes.ScoreType;
import me.jass.practice.datatypes.Team;
import me.jass.practice.files.Arena;
import me.jass.practice.utils.Text;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

@Getter
public class Duel {
	private final UUID uuid = UUID.randomUUID();
	private final List<Duelist> duelists;
	private final List<Player> spectators = new ArrayList<Player>();
	private final List<Duelist> teamA = new ArrayList<Duelist>();
	private final List<Duelist> teamB = new ArrayList<Duelist>();
	private final List<Duelist> left = new ArrayList<Duelist>();
	private int teamAWins = 0;
	private int teamBWins = 0;
	private int teamAScore = 0;
	private int teamBScore = 0;
	private final int teamARequiredScore = 0;
	private final int teamBRequiredScore = 0;
	private final int size;
	private int round = 0;
	private int countdown = 0;
	private final int rounds;
	private final RoundType roundType;
	private Team roundWinner = Team.NONE;
	private Team roundLoser = Team.NONE;
	private final boolean competitive;
	private final long matchStartTime = System.currentTimeMillis();
	private long roundStartTime = System.currentTimeMillis();
	private final List<Entity> entities = new ArrayList<Entity>();
	private final Set<WorldDamage> worldDamage = new HashSet<WorldDamage>();
	private boolean ended = false;
	private boolean active = true;
	private Team winner = Team.NONE;
	private Team loser = Team.NONE;
	private final List<Duelist> dead = new ArrayList<Duelist>();
	private final List<Duelist> requestingRematch = new ArrayList<Duelist>();
	private final Map<Player, Duelist> duelistLookup = new HashMap<Player, Duelist>();
	private boolean rematch = false;
	private boolean frozen = false;
	private final List<Location> explosives = new ArrayList<Location>();
	final Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

	public Duel(final List<Duelist> duelists, final int rounds, final RoundType roundType, final boolean competitive) {
		this.duelists = duelists;
		this.size = duelists.size();
		this.competitive = competitive;
		this.rounds = rounds;
		this.roundType = roundType;

		setOccupied();
		balanceTeams();
		setTeams();
		loadLookup();
		loadDuel();
		nextRound();
		addNameColors();
		initiateActionBar();

		for (final Duelist duelist : duelists) {
			Text.tell(duelist.getPlayer(), Message.DUEL_START.formatDuelist(duelist));
		}
	}

	public void loadDuel() {
		PracticeAPI.INSTANCE.getDuelManager().add(this);
		for (final Duelist duelist : duelists) {
			PracticeAPI.INSTANCE.getDuelManager().addIndex(duelist.getPlayer(), this);
		}
	}

	public void unloadDuel() {
		PracticeAPI.INSTANCE.getDuelManager().remove(this);
		PracticeAPI.INSTANCE.getDuelManager().removeIdIndex(uuid);
		for (final Duelist duelist : duelists) {
			PracticeAPI.INSTANCE.getDuelManager().removeIndex(duelist.getPlayer());
		}
	}

	public void loadLookup() {
		for (final Duelist duelist : duelists) {
			duelistLookup.put(duelist.getPlayer(), duelist);
		}
	}

	public Set<Player> getAllPlayers() {
		final Set<Player> players = new HashSet<Player>();
		players.addAll(spectators);
		for (final Duelist duelist : duelists) {
			players.add(duelist.getPlayer());
		}
		return players;
	}

	public void setOccupied() {
		for (final Duelist duelist : duelists) {
			duelist.getArena().setOccupied(true);
		}
	}

	public void setUnoccupied() {
		for (final Duelist duelist : duelists) {
			duelist.getArena().setOccupied(false);
		}

		for (final Duelist duelist : left) {
			duelist.getArena().setOccupied(false);
		}
	}

	public void setTeams() {
		for (final Duelist duelist : duelists) {
			if (duelist.getTeam() == Team.A) {
				teamA.add(duelist);
			}

			else if (duelist.getTeam() == Team.B) {
				teamB.add(duelist);
			}

			duelist.join(this);
		}
	}

	public void balanceTeams() {
		if (duelists.size() == 2) {
			duelists.get(0).setTeam(Team.A);
			duelists.get(1).setTeam(Team.B);
			return;
		}

		final List<Duelist> unregistered = new ArrayList<Duelist>();

		for (final Duelist duelist : duelists) {
			if (duelist.getTeam() == Team.NONE) {
				unregistered.add(duelist);
			}
		}

		if (unregistered.isEmpty()) {
			return;
		}

		final int midIndex = ((unregistered.size() / 2) - (((unregistered.size() % 2) > 0) ? 0 : 1));

		final List<List<Duelist>> splitTeams = new ArrayList<List<Duelist>>(unregistered.stream().collect(Collectors.partitioningBy(s -> unregistered.indexOf(s) > midIndex)).values());

		for (final Duelist duelist : splitTeams.get(0)) {
			duelist.setTeam(Team.A);
		}

		for (final Duelist duelist : splitTeams.get(1)) {
			duelist.setTeam(Team.B);
		}
	}

	public void addNameColors() {
		final org.bukkit.scoreboard.Team a = scoreboard.registerNewTeam("a");
		final org.bukkit.scoreboard.Team b = scoreboard.registerNewTeam("b");
		a.setColor(ChatColor.getByChar(Text.color(PracticeAPI.INSTANCE.getMessageManager().getTeamAColor()).substring(1)));
		b.setColor(ChatColor.getByChar(Text.color(PracticeAPI.INSTANCE.getMessageManager().getTeamBColor()).substring(1)));
		a.setAllowFriendlyFire(PracticeAPI.INSTANCE.getConfigManager().isFriendlyFire());
		b.setAllowFriendlyFire(PracticeAPI.INSTANCE.getConfigManager().isFriendlyFire());

		for (final Duelist duelist : duelists) {
			if (duelist.getTeam() == Team.A) {
				a.addPlayer(duelist.getPlayer());
			}

			else if (duelist.getTeam() == Team.B) {
				b.addPlayer(duelist.getPlayer());
			}

			duelist.getPlayer().setScoreboard(scoreboard);
		}
	}

	public void removeNameColors() {
		final org.bukkit.scoreboard.Team a = scoreboard.getTeam("a");
		final org.bukkit.scoreboard.Team b = scoreboard.getTeam("b");

		for (final Duelist duelist : duelists) {
			if (duelist.getTeam() == Team.A) {
				a.removePlayer(duelist.getPlayer());
			}

			else if (duelist.getTeam() == Team.B) {
				b.removePlayer(duelist.getPlayer());
			}
		}

		for (final Duelist duelist : left) {
			if (duelist.getTeam() == Team.A) {
				a.removePlayer(duelist.getPlayer());
			}

			else if (duelist.getTeam() == Team.B) {
				b.removePlayer(duelist.getPlayer());
			}
		}
	}

	public void removeNameColor(final Duelist duelist) {
		final org.bukkit.scoreboard.Team a = scoreboard.getTeam("a");
		final org.bukkit.scoreboard.Team b = scoreboard.getTeam("b");
		if (duelist.getTeam() == Team.A) {
			a.removePlayer(duelist.getPlayer());
		}

		else if (duelist.getTeam() == Team.B) {
			b.removePlayer(duelist.getPlayer());
		}
	}

	public void end() {
		ended = true;
		removeNameColors();
		removeSpectators();
		removeExplosives();
		for (final Duelist duelist : duelists) {
			Text.tell(duelist.getPlayer(), Message.DUEL_END.formatDuelist(duelist));
		}

		if (!PracticeAPI.INSTANCE.getConfigManager().isRepairsPerRound()) {
			repairWorld();
		}

		addElo();
		startRematchPhase();
	}

	public void forceEnd() {
		removeNameColors();
		teleportDuelists(Destination.SPAWN);
		//PracticeAPI.INSTANCE.getDuelManager().addTempDamage(uuid, worldDamage);
		PracticeAPI.INSTANCE.getDuelManager().forceWorldRepair(this);
		unloadDuel();
	}

	public void startRematchPhase() {
		freezeDuelists();
		final ItemStack rematch = PracticeAPI.INSTANCE.getGuiManager().createItem(Material.GLASS_BOTTLE, "#aquaRematch", 1);
		final ItemStack requeue = PracticeAPI.INSTANCE.getGuiManager().createItem(Material.GLASS_BOTTLE, "#aquaRequeue", 1);

		for (final Duelist duelist : duelists) {
			duelist.getPlayer().getInventory().clear();
			if (duelist.isQueued()) {
				duelist.getPlayer().getInventory().setItem(4, requeue);
			} else {
				duelist.getPlayer().getInventory().setItem(4, rematch);
			}
		}

		Bukkit.getScheduler().runTaskLater(PracticeAPI.INSTANCE.getPlugin(), () -> {
			teleportDuelists(Destination.SPAWN);
			setUnoccupied();
			unloadDuel();
			checkForRematch();
		}, PracticeAPI.INSTANCE.getConfigManager().getRematchPhase() * 20);
	}

	public void checkForRematch() {
		if (rematch) {
			final List<Duelist> newDuelists = new ArrayList<Duelist>();

			for (final Duelist duelist : duelists) {
				newDuelists.add(new Duelist(duelist.getPlayer(), duelist.getTeam(), duelist.getKit(), duelist.getArena(), duelist.getQueue()));
			}

			PracticeAPI.INSTANCE.getDuelManager().start(newDuelists, rounds, roundType, competitive);
		}
	}

	public void requestRematch(final Duelist duelist) {
		if (rematch) {
			return;
		}

		if (!left.isEmpty()) {
			Text.tell(duelist.getPlayer(), PracticeAPI.INSTANCE.getMessageManager().getPlayerLeft());
			return;
		}

		if (requestingRematch.contains(duelist)) {
			Text.tell(duelist.getPlayer(), PracticeAPI.INSTANCE.getMessageManager().getAlreadyRequested());
			return;
		}

		requestingRematch.add(duelist);

		broadcast(Message.REQUEST_REMATCH.formatDuelist(duelist));

		if (requestingRematch.containsAll(duelists)) {
			rematch = true;
		}
	}

	public void addExplosive(final Location location) {
		explosives.add(location);
	}

	public void removeExplosives() {
		for (final Location location : explosives) {
			PracticeAPI.INSTANCE.getDuelManager().removeExplosive(location);
		}
	}

	public Set<Arena> getArenas() {
		final Set<Arena> arenas = new HashSet<Arena>();
		for (final Duelist duelist : duelists) {
			arenas.add(duelist.getArena());
		}
		return arenas;
	}

	public void addStats() {
		for (final Duelist duelist : duelists) {
			if (duelist.getTeam() == roundWinner) {
				PracticeAPI.INSTANCE.getStatManager().addWin(duelist.getPlayer(), duelist.getKit(), duelist.getQueue());
			}

			else if (duelist.getTeam() == roundLoser) {
				PracticeAPI.INSTANCE.getStatManager().addLoss(duelist.getPlayer(), duelist.getKit(), duelist.getQueue());
			}
		}
	}

	public void addElo() {
		if (competitive) {
			if (winner == Team.A) {
				PracticeAPI.INSTANCE.getEloManager().increment(teamA, teamB);
			}

			else if (winner == Team.B) {
				PracticeAPI.INSTANCE.getEloManager().increment(teamB, teamA);
			}
		}
	}

	public void repairWorld() {
		PracticeAPI.INSTANCE.getDuelManager().repairWorld(this);
	}

	public void freezeDuelists() {
		frozen = true;
		for (final Duelist duelist : duelists) {
			duelist.freeze();
		}
	}

	public void unfreezeDuelists() {
		frozen = false;
		for (final Duelist duelist : duelists) {
			duelist.unfreeze();
		}

		teleportDuelists(Destination.ARENA);
	}

	public void startCountdown() {
		final int length = PracticeAPI.INSTANCE.getConfigManager().getDuelCountdownLength();

		if (length < 1) {
			return;
		}

		freezeDuelists();

		for (int i = 0; i < length; i++) {
			new BukkitRunnable() {
				@Override
				public void run() {
					for (final Duelist duelist : duelists) {
						final String main = Message.COUNTDOWN_MAIN.formatDuelist(duelist);
						final String sub = Message.COUNTDOWN_SUB.formatDuelist(duelist);
						duelist.getPlayer().sendTitle(main, sub, 0, 21, 0);
					}
					ding();
					addCountdown();
				}
			}.runTaskLater(PracticeAPI.INSTANCE.getPlugin(), 20 * i);
		}

		new BukkitRunnable() {
			@Override
			public void run() {
				addCountdown();
				for (final Duelist duelist : duelists) {
					final String main = Message.END_COUNTDOWN_MAIN.formatDuelist(duelist);
					final String sub = Message.END_COUNTDOWN_SUB.formatDuelist(duelist);
					duelist.getPlayer().sendTitle(main, sub, 0, 20, 0);
				}
				endDing();
				resetCountdown();
				unfreezeDuelists();
			}
		}.runTaskLater(PracticeAPI.INSTANCE.getPlugin(), 20 * length);
	}

	public void ding() {
		for (final Player player : getAllPlayers()) {
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1, 1);
		}
	}

	public void endDing() {
		for (final Player player : getAllPlayers()) {
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1, 3);
		}
	}

	public void addCountdown() {
		countdown++;
	}

	public void resetCountdown() {
		countdown = 0;
	}

	public void addEntity(final Entity entity) {
		entities.add(entity);
	}

	public void clearEntities() {
		for (final Entity entity : entities) {
			entity.remove();
		}
	}

	public void increaseScore(final Team team) {
		if (team == Team.A) {
			teamAScore++;
		}

		else if (team == Team.B) {
			teamBScore++;
		}
	}

	public void decreaseScore(final Team team) {
		if (team == Team.A) {
			teamAScore--;
		}

		else if (team == Team.B) {
			teamBScore--;
		}
	}

	public int getScore(final Team team) {
		if (team == Team.A) {
			return teamAScore;
		}

		else if (team == Team.B) {
			return teamBScore;
		}

		return -1;
	}

	public void clearScore(final Team team) {
		if (team == Team.A) {
			teamAScore = 0;
		}

		else if (team == Team.B) {
			teamBScore = 0;
		}
	}

	public void clearScores() {
		teamAScore = 0;
		teamBScore = 0;
	}

	public int getWins(final Team team) {
		if (team == Team.A) {
			return teamAWins;
		}

		else if (team == Team.B) {
			return teamBWins;
		}

		return -1;
	}

	public List<String> getTeamNames(final Team team) {
		final List<String> names = new ArrayList<String>();
		for (final Duelist duelist : getTeam(team)) {
			names.add(duelist.getPlayer().getName());
		}

		final String subColor = PracticeAPI.INSTANCE.getMessageManager().getSubColor();

		for (final Duelist duelist : left) {
			if (duelist.getTeam() == team) {
				names.add(duelist.getPlayer().getName() + subColor + " (&cforfeit" + subColor + ")");
			}
		}

		return names;
	}

	public List<String> getArenaNames() {
		final List<String> names = new ArrayList<String>();
		for (final Arena arena : getArenas()) {
			names.add(arena.getDisplayName());
		}

		return names;
	}

	public List<Duelist> getTeam(final Team team) {
		if (team == Team.A) {
			return teamA;
		}

		else if (team == Team.B) {
			return teamB;
		}

		return new ArrayList<Duelist>();
	}

	public List<Duelist> getOpposingTeam(final Team team) {
		if (team == Team.A) {
			return teamB;
		}

		else if (team == Team.B) {
			return teamA;
		}

		return new ArrayList<Duelist>();
	}

	public void initiateActionBar() {
		new BukkitRunnable() {
			@Override
			public void run() {
				if (active) {
					displayActionBar();
				}

				if (ended) {
					cancel();
				}
			}
		}.runTaskTimer(PracticeAPI.INSTANCE.getPlugin(), 0, 20);
	}

	public void displayActionBar() {
		for (final Duelist duelist : duelists) {
			if (duelist.getScoreType() != ScoreType.NONE) {
				duelist.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Message.DUEL_BAR.formatDuelist(duelist)));
			}
		}
	}

	public void addWorldDamage(final BlockState block) {
		if (frozen) {
			return;
		}
		worldDamage.add(new WorldDamage(block));
	}

	public void nextRound() {
		active = true;
		roundStartTime = System.currentTimeMillis();
		round++;

		prepareDuelists();
		startCountdown();
	}

	public void prepareDuelists() {
		for (final Duelist duelist : duelists) {
			duelist.prepare();
		}
	}

	public void teleportDuelists(final Destination destination) {
		for (final Duelist duelist : duelists) {
			duelist.warp(destination);
		}
	}

	public Duelist getDuelist(final Player player) {
		return duelistLookup.get(player);
	}

	public void addDeath(final Duelist duelist) {
		dead.add(duelist);
		checkTeams();
	}

	public void addSpectator(final Player player) {
		PracticeAPI.INSTANCE.getDuelManager().addSpectator(player, this);
		spectators.add(player);
		player.setGameMode(GameMode.SPECTATOR);
		player.teleport(duelists.get(0).getPlayer());
		broadcast(Message.SPECTATOR_JOIN.formatPlayer(player));
	}

	public void removeSpectator(final Player player) {
		PracticeAPI.INSTANCE.getDuelManager().removeSpectator(player);
		spectators.remove(player);
		player.setGameMode(GameMode.ADVENTURE);
		player.teleport(player.getWorld().getSpawnLocation());
		broadcast(Message.SPECTATOR_LEAVE.formatPlayer(player));
	}

	public void removeSpectators() {
		for (final Player player : spectators) {
			PracticeAPI.INSTANCE.getDuelManager().removeSpectator(player);
			player.setGameMode(GameMode.ADVENTURE);
			player.teleport(player.getWorld().getSpawnLocation());
		}

		spectators.clear();
	}

	public void removeDuelist(final Duelist duelist) {
		removeNameColor(duelist);
		PracticeAPI.INSTANCE.getStatManager().addLoss(duelist.getPlayer(), duelist.getKit(), duelist.getQueue());
		PracticeAPI.INSTANCE.getDuelManager().removeIndex(duelist.getPlayer());
		left.add(duelist);
		duelists.remove(duelist);
		teamA.remove(duelist);
		teamB.remove(duelist);
		checkTeams();
	}

	public void checkTeams() {
		if (active) {
			if (dead.containsAll(teamA)) {
				endRound(Team.B);
			}

			else if (dead.containsAll(teamB)) {
				endRound(Team.A);
			}
		}
	}

	public void declareWinner() {
		if (winner != Team.NONE && loser != Team.NONE) {
			return;
		}

		if (teamA.size() == 0) {
			winner = Team.B;
			loser = Team.A;
		}

		else if (teamB.size() == 0) {
			winner = Team.A;
			loser = Team.B;
		}

		if (roundType == RoundType.BEST_OF) {
			if (teamAWins == rounds) {
				winner = Team.A;
				loser = Team.B;
			}

			else if (teamBWins == rounds) {
				winner = Team.B;
				loser = Team.A;
			}
		}

		else if (roundType == RoundType.FIRST_TO) {
			if (teamAWins > teamBWins && teamAWins == rounds) {
				winner = Team.A;
				loser = Team.B;
			}

			else if (teamBWins > teamAWins && teamBWins == rounds) {
				winner = Team.B;
				loser = Team.A;
			}
		}
	}

	public void clearDead() {
		dead.clear();
	}

	public void broadcast(final String message) {
		for (final Player player : getAllPlayers()) {
			Text.tell(player, message);
		}
	}

	public void sendViews(final List<Duelist> winners, final List<Duelist> losers) {
		final TextComponent view = new TextComponent("");
		for (final Duelist duelist : winners) {
			final TextComponent button = new TextComponent(Text.color(Message.VIEW_WINNER.formatDuelist(duelist)));
			button.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/view " + duelist.getPlayer().getName()));
			view.addExtra(button);
			view.addExtra(" ");
		}

		for (final Duelist duelist : losers) {
			final TextComponent button = new TextComponent(Text.color(Message.VIEW_LOSER.formatDuelist(duelist)));
			button.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/view " + duelist.getPlayer().getName()));
			view.addExtra(button);
			view.addExtra(" ");
		}

		for (final Player player : getAllPlayers()) {
			player.spigot().sendMessage(view);
		}
	}

	public void endRound(final Team winner) {
		active = false;
		roundWinner = winner;
		if (winner == Team.A) {
			teamAWins++;
			roundLoser = Team.B;
		}

		else if (winner == Team.B) {
			teamBWins++;
			roundLoser = Team.A;
		}

		addStats();
		broadcast(Message.ROUND_END.formatDuel(this));

		for (final Duelist duelist : duelists) {
			if (!dead.contains(duelist)) {
				duelist.setView();
				duelist.setHealth();
			}
		}

		clearDead();
		sendViews(getTeam(winner), getOpposingTeam(winner));
		clearEntities();
		clearScores();
		declareWinner();

		if (PracticeAPI.INSTANCE.getConfigManager().isRepairsPerRound()) {
			repairWorld();
		}

		if (this.winner != Team.NONE) {
			end();
			return;
		}

		nextRound();
	}
}
