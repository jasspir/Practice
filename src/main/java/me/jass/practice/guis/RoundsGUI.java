package me.jass.practice.guis;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.jass.practice.datatypes.GUI;
import me.jass.practice.datatypes.Goal;
import me.jass.practice.datatypes.RoundType;
import me.jass.practice.duels.Selection;

public class RoundsGUI extends DuelGUI {
	public RoundsGUI(final Player player) {
		final Inventory gui = Bukkit.createInventory(null, getSize(GUI.ROUNDS, null), centerTitle("&8Rounds"));
		setGUI(gui, player, GUI.ROUNDS);
	}

	@Override
	public void loadGUI() {
		fillBorders();

		inventory.setItem(11, createItem(Material.GOLD_INGOT, "#yellowFirst to", 1));
		inventory.setItem(13, createItem(Material.NETHERITE_INGOT, "&8Never Ending", 1));
		inventory.setItem(15, createItem(Material.IRON_INGOT, "&fBest of", 1));

		inventory.setItem(2, arrowUp());
		inventory.setItem(20, arrowDown());
		inventory.setItem(6, arrowUp());
		inventory.setItem(24, arrowDown());

		hideFlags();
	}

	@Override
	public void clicked(final ItemStack item, final int slot, final ItemStack with) {
		if (isBorder(item)) {
			return;
		}

		final Selection selection = guiManager.getSelection(player);
		final String name = item.getItemMeta().getDisplayName();
		final int rounds = 1;

		if (name.contains("First to")) {
			selection.setType(RoundType.FIRST_TO);
			selection.setRounds(item.getAmount());
		}

		else if (name.contains("Best of")) {
			selection.setType(RoundType.BEST_OF);
			selection.setRounds(item.getAmount());
		}

		else if (name.contains("Never Ending")) {
			selection.setType(RoundType.BEST_OF);
			selection.setRounds(99999);
		}

		else if (name.contains("More")) {
			int amount = inventory.getItem(slot + 9).getAmount();
			if (amount == 64) {
				return;
			}

			if (amount == 63) {
				amount = 99;
			}

			inventory.getItem(slot + 9).setAmount(amount + 1);
		}

		else if (name.contains("Less")) {
			final int amount = inventory.getItem(slot - 9).getAmount();
			if (amount == 1) {
				return;
			}

			inventory.getItem(slot - 9).setAmount(amount - 1);
		}

		final Goal goal = selection.getGoal();

		if (goal == Goal.DUEL && item.getType() != Material.COMPASS && item.getType() != Material.RECOVERY_COMPASS) {
			selection.sendRequest();
			close();
		}

		ding();
	}

	@Override
	public boolean canMove(final ItemStack item) {
		return false;
	}
}
