package me.jass.practice.guis;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.jass.practice.datatypes.GUI;
import me.jass.practice.datatypes.RoundType;
import me.jass.practice.duels.Selection;

public class ChallengeGUI extends DuelGUI {
	public ChallengeGUI(final Player player) {
		final Inventory gui = Bukkit.createInventory(null, getSize(GUI.CHALLENGE, null), centerTitle("&8Challenge"));
		setGUI(gui, player, GUI.CHALLENGE);
	}

	@Override
	public void loadGUI() {
		fillBorders();

		inventory.setItem(11, createItem(Material.DIAMOND_SWORD, "#aquaSingle Match", 1));
		inventory.setItem(13, createItem(Material.IRON_SWORD, "&fBest of 3", 1));
		inventory.setItem(15, createItem(Material.GOLDEN_SWORD, "#yellowFirst to 10", 1));
		hideFlags();
	}

	@Override
	public void clicked(final ItemStack item, final int slot, final ItemStack with) {
		if (isBorder(item)) {
			return;
		}

		final Selection selection = guiManager.getSelection(player);
		final String name = item.getItemMeta().getDisplayName();

		if (name.contains("Single Match")) {
			selection.setType(RoundType.BEST_OF);
			selection.setRounds(1);
		}

		else if (name.contains("Best of 3")) {
			selection.setType(RoundType.BEST_OF);
			selection.setRounds(3);
		}

		else if (name.contains("First to 10")) {
			selection.setType(RoundType.FIRST_TO);
			selection.setRounds(10);
		}

		selection.sendRequest();
		close();

		ding();
	}

	@Override
	public boolean canMove(final ItemStack item) {
		return false;
	}
}
