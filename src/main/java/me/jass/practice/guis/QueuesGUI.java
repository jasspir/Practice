package me.jass.practice.guis;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.jass.practice.datatypes.GUI;
import me.jass.practice.datatypes.Goal;
import me.jass.practice.datatypes.QueueType;
import me.jass.practice.datatypes.Queues;
import me.jass.practice.duels.Selection;
import me.jass.practice.files.Stats;
import me.jass.practice.utils.Text;

public class QueuesGUI extends DuelGUI {
	private final QueueType type;

	public QueuesGUI(final Player player, final QueueType type) {
		this.type = type;
		final Inventory gui = Bukkit.createInventory(null, getSize(GUI.QUEUES, null), centerTitle("&8Queues"));
		setGUI(gui, player, GUI.QUEUES);
	}

	@Override
	public void loadGUI() {
		fillBorders();

		final Selection selection = guiManager.getSelection(player);
		if (selection.getGoal() == Goal.STATS) {
			Stats stats = statManager.get(player, null, null);
			final List<String> selected = Arrays.asList(Text.color("&7Wins: #lime" + stats.getWins()), Text.color("&7Losses: #red" + stats.getLosses()),
					Text.color("&7Matches: #yellow" + stats.getMatches()));
			for (int i = 0; i < inventory.getSize(); i++) {
				final ItemStack item = inventory.getItem(i);
				final ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(Text.color("&7Overall"));
				meta.setLore(selected);
				item.setItemMeta(meta);
				inventory.setItem(i, item);
			}

			stats = statManager.get(player, null, Queues.SOLO);
			inventory.setItem(11, createItem(Material.IRON_SWORD, "&fSolo", 1, "&7Wins: #lime" + stats.getWins(), "&7Losses: #red" + stats.getLosses(), "&7Matches: #yellow" + stats.getMatches()));
			stats = statManager.get(player, null, Queues.NONE);
			inventory.setItem(13,
					createItem(Material.WOODEN_SWORD, "#brownDuel", 1, "&7Wins: #lime" + stats.getWins(), "&7Losses: #red" + stats.getLosses(), "&7Matches: #yellow" + stats.getMatches()));
			stats = statManager.get(player, null, Queues.DUO);
			inventory.setItem(15,
					createItem(Material.GOLDEN_SWORD, "#yellowDuo", 1, "&7Wins: #lime" + stats.getWins(), "&7Losses: #red" + stats.getLosses(), "&7Matches: #yellow" + stats.getMatches()));
		} else {
			inventory.setItem(12, createItem(Material.IRON_SWORD, "&fSolo", 1));
			inventory.setItem(14, createItem(Material.GOLDEN_SWORD, "#yellowDuo", 1));
		}

		hideFlags();
	}

	@Override
	public void clicked(final ItemStack item, final int slot, final ItemStack with) {
		if (isBorder(item)) {
			return;
		}

		final Selection selection = guiManager.getSelection(player);
		final String name = item.getItemMeta().getDisplayName();

		if (name.contains("Solo")) {
			selection.setQueue(Queues.SOLO);
		}

		else if (name.contains("Duo")) {
			selection.setQueue(Queues.DUO);
		}

		else if (name.contains("Duel")) {
			selection.setQueue(Queues.NONE);
		}

		guiManager.open(player, new KitsGUI(player, type));

		ding();
	}

	@Override
	public boolean canMove(final ItemStack item) {
		return false;
	}
}
