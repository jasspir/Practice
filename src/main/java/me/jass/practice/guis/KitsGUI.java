package me.jass.practice.guis;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.jass.practice.PracticeAPI;
import me.jass.practice.datatypes.GUI;
import me.jass.practice.datatypes.Goal;
import me.jass.practice.datatypes.QueueType;
import me.jass.practice.datatypes.Queues;
import me.jass.practice.duels.Selection;
import me.jass.practice.files.Elo;
import me.jass.practice.files.Kit;
import me.jass.practice.files.Stats;
import me.jass.practice.utils.Text;

public class KitsGUI extends DuelGUI {
	private final QueueType type;

	public KitsGUI(final Player player, final QueueType type) {
		this.type = type;

		GUI size = GUI.KITS;
		if (type == QueueType.CASUAL) {
			size = GUI.CASUAL_KITS;
		}

		else if (type == QueueType.COMPETITIVE) {
			size = GUI.COMPETITIVE_KITS;
		}

		final Inventory gui = Bukkit.createInventory(null, getSize(size, null), centerTitle("&8Kits"));
		setGUI(gui, player, GUI.KITS);
	}

	@Override
	public void loadGUI() {
		setBorders();
		final Selection selection = guiManager.getSelection(player);
		final Goal goal = selection.getGoal();

		if (goal == Goal.CASUAL_QUEUE || goal == Goal.COMPETITIVE_QUEUE) {
			addQueueKits();
		}

		else if (goal == Goal.STATS) {
			addStatKits();
		}

		else if (goal == Goal.LEADERBOARDS) {
			addLeaderboardKits();
		}

		else {
			addKits();
		}

		hideFlags();
	}

	@Override
	public void clicked(final ItemStack item, final int slot, final ItemStack with) {
		if (isBorder(item)) {
			return;
		}

		final Selection selection = guiManager.getSelection(player);
		final Kit kit = kitManager.get(rawName(item.getItemMeta().getDisplayName()));
		selection.setKit(kit);

		final Goal goal = selection.getGoal();

		if (goal == Goal.CASUAL_QUEUE || goal == Goal.COMPETITIVE_QUEUE) {
			if (selection.getQueue() == Queues.SOLO) {
				queueManager.joinSolo(player, type, kit);
			}

			else if (selection.getQueue() == Queues.DUO) {
				queueManager.joinDuo(player, type, kit);
			}

			close();
		}

		else if (goal == Goal.DUEL) {
			if (player.hasPermission("practice.arena")) {
				guiManager.open(player, new ArenasGUI(player, QueueType.CASUAL));
			} else {
				selection.setArena(arenaManager.getRandom(kit, type));
				if (selection.getArena() == null) {
					Text.staff("[debug] No arenas found please make more/assign them to your kits");
					close();
				}
				guiManager.open(player, new RoundsGUI(player));
			}
		} else if (goal == Goal.CHALLENGE) {
			selection.setArena(arenaManager.getRandom(kit, type));
			if (selection.getArena() == null) {
				Text.staff("[debug] No arenas found please make more/assign them to your kits");
				close();
			}
			guiManager.open(player, new ChallengeGUI(player));
		}

		else if (goal == Goal.EDIT_KIT) {
			guiManager.open(player, new KitEditorGUI(player));
		}

		else if (goal == Goal.EDIT_LAYOUT) {
			guiManager.open(player, new LayoutEditorGUI(player));
		} else {
			return;
		}

		ding();
	}

	@Override
	public boolean canMove(final ItemStack item) {
		return false;
	}

	public void addKits() {
		kitManager.getAll().forEach(kit -> {
			inventory.addItem(createItem(kit.getDisplay(), kit.getDisplayName(), 1));
		});
	}

	public void addQueueKits() {
		final Selection selection = guiManager.getSelection(player);
		kitManager.getAll().forEach(kit -> {
			//			if (kit.hasQueue(type)) {
			int queued = 0;

			if (selection.getQueue() == Queues.SOLO) {
				queued = queueManager.getSoloAmount(kit, type);
			}

			else if (selection.getQueue() == Queues.DUO) {
				queued = queueManager.getDuoAmount(kit, type);
			}

			inventory.addItem(createItem(kit.getDisplay(), kit.getDisplayName(), 1, "&7Queued: &f" + queued));
			//			}
		});
	}

	public void addStatKits() {
		final Selection selection = guiManager.getSelection(player);
		kitManager.getAll().forEach(kit -> {
			final Stats stats = statManager.get(player, kit, selection.getQueue());
			inventory
					.addItem(createItem(kit.getDisplay(), kit.getDisplayName(), 1, "&7Wins: #lime" + stats.getWins(), "&7Losses: #red" + stats.getLosses(), "&7Matches: #yellow" + stats.getMatches()));
		});
	}

	public void addLeaderboardKits() {
		final Selection selection = guiManager.getSelection(player);
		kitManager.getAll().forEach(kit -> {
			final List<Elo> leaderboard = PracticeAPI.INSTANCE.getEloManager().getLeaderboard(kit, selection.getQueue());

			if (leaderboard == null || leaderboard.isEmpty()) {
				inventory.addItem(createItem(kit.getDisplay(), kit.getDisplayName(), 1));
			} else {
				final ItemStack item = createItem(kit.getDisplay(), kit.getDisplayName(), 1);
				final ItemMeta meta = item.getItemMeta();
				final List<String> lore = new ArrayList<String>();
				for (int i = 0; i < leaderboard.size(); i++) {
					String color = "&7";
					if (i == 0) {
						color = "&#FFD700";
					}

					else if (i == 1) {
						color = "&#C0C0C0";
					}

					else if (i == 2) {
						color = "&#CD7F32";
					}

					lore.add(Text.color(color + "#" + (i + 1) + " " + Bukkit.getOfflinePlayer(leaderboard.get(i).getId()).getName() + " &7(&f" + leaderboard.get(i).getElo() + "&7)"));
				}

				meta.setLore(lore);
				item.setItemMeta(meta);
				inventory.addItem(item);
			}
		});
	}
}
