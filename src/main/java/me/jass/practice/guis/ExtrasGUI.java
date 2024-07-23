package me.jass.practice.guis;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.jass.practice.PracticeAPI;
import me.jass.practice.datatypes.GUI;
import me.jass.practice.datatypes.Goal;
import me.jass.practice.duels.Selection;
import me.jass.practice.files.Extra;
import me.jass.practice.files.Kit;

public class ExtrasGUI extends DuelGUI {
	public ExtrasGUI(final Player player) {
		final Inventory gui = Bukkit.createInventory(null, getSize(GUI.EXTRAS, null), centerTitle("&8Extras"));
		setGUI(gui, player, GUI.EXTRAS);
	}

	public void setSelected() {
		final Selection selection = guiManager.getSelection(player);

		for (int i = 0; i < inventory.getSize(); i++) {
			ItemStack item = inventory.getItem(i);
			final int slot = i;

			if (isBorder(item) || item == null) {
				continue;
			}

			if (selection.getGoal() == Goal.EDIT_LAYOUT) {
				if (extraManager.isSet(player, new Extra(rawName(item.getItemMeta().getDisplayName())))) {
					item = select(item);
				} else {
					item = unselect(item);
				}
			}

			else if (selection.getGoal() == Goal.EDIT_KIT) {
				if (selection.getKit().getExtras().contains(new Extra(rawName(item.getItemMeta().getDisplayName())))) {
					item = select(item);
				} else {
					item = unselect(item);
				}
			}

			inventory.setItem(slot, item);
		}

	}

	@Override
	public void loadGUI() {
		setBorders();

		final Selection selection = guiManager.getSelection(player);

		if (selection.getGoal() == Goal.EDIT_LAYOUT) {
			addExtras(selection.getKit());
		}

		else if (selection.getGoal() == Goal.EDIT_EXTRA) {
			addExtras(null);
			return;
		}

		else {
			addExtras(null);
		}

		setSelected();

		hideFlags();
	}

	@Override
	public void clicked(final ItemStack item, final int slot, final ItemStack with) {
		if (isBorder(item)) {
			return;
		}

		final Selection selection = guiManager.getSelection(player);
		final String name = rawName(item.getItemMeta().getDisplayName());
		selection.setExtra(extraManager.get(name));

		final Goal goal = selection.getGoal();

		inventory.setItem(slot, select(item));

		if (goal == Goal.EDIT_LAYOUT) {
			if (item.containsEnchantment(Enchantment.ARROW_INFINITE)) {
				PracticeAPI.INSTANCE.getExtraManager().set(player, extraManager.get(name), true);
			} else {
				PracticeAPI.INSTANCE.getExtraManager().set(player, extraManager.get(name), false);
			}
		}

		else if (goal == Goal.EDIT_KIT) {
			if (item.containsEnchantment(Enchantment.ARROW_INFINITE)) {
				selection.getKit().addExtra(extraManager.get(name));
			} else {
				selection.getKit().removeExtra(extraManager.get(name));
			}
		}

		else if (goal == Goal.EDIT_EXTRA) {
			guiManager.open(player, new ExtraEditorGUI(player));
		}

		ding();
	}

	public void addExtras(final Kit kit) {
		if (kit == null) {
			extraManager.getAll().forEach(extra -> {
				inventory.addItem(createItem(extra.getDisplay(), extra.getDisplayName(), 1));
			});
		} else {
			for (final Extra extra : kit.getExtras()) {
				inventory.addItem(createItem(extra.getDisplay(), extra.getDisplayName(), 1));
			}
		}
	}

	@Override
	public boolean canMove(final ItemStack item) {
		return false;
	}
}
