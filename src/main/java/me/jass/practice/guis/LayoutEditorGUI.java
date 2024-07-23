package me.jass.practice.guis;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.jass.practice.datatypes.GUI;
import me.jass.practice.duels.Selection;

public class LayoutEditorGUI extends DuelGUI {
	public LayoutEditorGUI(final Player player) {
		final Inventory gui = Bukkit.createInventory(null, getSize(GUI.LAYOUT_EDITOR, null), centerTitle("&8Editor"));
		setGUI(gui, player, GUI.LAYOUT_EDITOR);
	}

	public void setDefaultItems() {
		final Selection selection = guiManager.getSelection(player);
		final List<ItemStack> items = selection.getKit().getDefaultItems(player);
		for (int i = 0; i < 41; i++) {
			final ItemStack item = items.get(i);
			int slot = i;
			if (i > 35 && i < 40) {
				slot -= 40;
			} else if (i == 40) {
				slot = 6;
			} else if (i >= 0 && i <= 8) {
				slot += 36;
			}

			slot = Math.abs(slot);

			inventory.setItem(slot, item);
		}
	}

	@Override
	public void loadGUI() {
		final Selection selection = guiManager.getSelection(player);
		final List<ItemStack> items = selection.getKit().getFixedItems(player);

		fillBorders();

		for (int i = 0; i < 41; i++) {
			final ItemStack item = items.get(i);
			int slot = i;
			if (i > 35 && i < 40) {
				slot -= 40;
			} else if (i == 40) {
				slot = 6;
			} else if (i >= 0 && i <= 8) {
				slot += 36;
			}

			slot = Math.abs(slot);

			inventory.setItem(slot, item);
		}

		inventory.setItem(8, createItem(Material.WHITE_CANDLE, "&7Reset to default", 1));
		inventory.setItem(49, createItem(Material.NETHER_STAR, "#aquaExtras", 1));
	}

	@Override
	public void clicked(final ItemStack item, final int slot, final ItemStack with) {
		if (isBorder(item)) {
			return;
		}

		final String name = item.getItemMeta().getDisplayName();

		if (name.contains("Reset to default")) {
			setDefaultItems();
		}

		if (name.contains("Extras")) {
			guiManager.open(player, new ExtrasGUI(player));
		}
	}

	@Override
	public boolean canMove(final ItemStack item) {
		if (isBorder(item)) {
			return false;
		}

		final String name = item.getItemMeta().getDisplayName();

		if (name.contains("Reset to default") || name.contains("Extras")) {
			return false;
		}

		return true;
	}
}