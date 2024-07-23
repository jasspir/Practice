package me.jass.practice.guis;

import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import me.jass.practice.PracticeAPI;
import me.jass.practice.datatypes.GUI;
import me.jass.practice.datatypes.Goal;
import me.jass.practice.datatypes.QueueType;
import me.jass.practice.duels.Selection;
import me.jass.practice.utils.Text;

public class PlayersGUI extends DuelGUI {
	public PlayersGUI(final Player player, final GUI type) {
		final Inventory gui = Bukkit.createInventory(null, getSize(type, null), centerTitle("&8" + WordUtils.capitalize(type.toString().toLowerCase().replaceAll("_", " "))));
		setGUI(gui, player, type);
	}

	@Override
	public void loadGUI() {
		setBorders();
		addPlayers();
		hideFlags();
	}

	@Override
	public void clicked(final ItemStack item, final int slot, final ItemStack with) {
		if (isBorder(item)) {
			return;
		}

		final Selection selection = guiManager.getSelection(player);
		final Player opponent = Bukkit.getPlayer(rawName(item.getItemMeta().getDisplayName()));
		selection.setPlayer(opponent);

		final Goal goal = selection.getGoal();

		if (goal == Goal.DUEL || goal == Goal.CHALLENGE) {
			guiManager.open(player, new KitsGUI(player, QueueType.CASUAL));
		}

		else if (goal == Goal.VIEW) {
			guiManager.open(player, new ViewGUI(player));
		}

		else if (goal == Goal.SPECTATE) {
			PracticeAPI.INSTANCE.getDuelManager().getIndex(opponent).addSpectator(player);
			close();
		}

		ding();
	}

	@Override
	public boolean canMove(final ItemStack item) {
		return false;
	}

	public void addPlayers() {
		final Selection selection = guiManager.getSelection(player);
		if (selection.getGoal() == Goal.SPECTATE) {
			duelManager.getUnavailablePlayers().forEach(player -> {
				addPlayer(player);
			});
		} else {
			duelManager.getAvailablePlayers().forEach(player -> {
				if (this.player != player) {
					addPlayer(player);
				}
			});
		}
	}

	public void addPlayer(final Player player) {
		final ItemStack head = createItem(Material.PLAYER_HEAD, Text.color("&7" + player.getName()), 1);
		final SkullMeta meta = (SkullMeta) head.getItemMeta();
		meta.setOwningPlayer(player);
		head.setItemMeta(meta);
		inventory.addItem(head);
	}
}
